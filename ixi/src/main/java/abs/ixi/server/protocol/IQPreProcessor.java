
package abs.ixi.server.protocol;

import abs.ixi.server.Stringflow;
import abs.ixi.server.common.InstantiationException;
import abs.ixi.server.etc.PersistenceService;
import abs.ixi.server.io.PacketCollector;
import abs.ixi.server.io.StreamContext;
import abs.ixi.server.io.StreamContext.StreamState;
import abs.ixi.server.packet.XMPPNamespaces;
import abs.ixi.server.packet.xmpp.AckPacket;
import abs.ixi.server.packet.xmpp.BareJID;
import abs.ixi.server.packet.xmpp.IQ;
import abs.ixi.server.packet.xmpp.IQErrorContent;
import abs.ixi.server.packet.xmpp.IQQuery;
import abs.ixi.server.packet.xmpp.IQResourceBind;
import abs.ixi.server.packet.xmpp.UserRegistrationData;
import abs.ixi.server.packet.xmpp.XMPPPacket;
import abs.ixi.server.packet.xmpp.IQ.IQType;
import abs.ixi.server.packet.xmpp.IQContent.IQContentType;
import abs.ixi.server.packet.xmpp.IQErrorContent.IQError;
import abs.ixi.server.session.LocalSession;
import abs.ixi.server.session.SessionManager;
import abs.ixi.util.StringUtils;

public class IQPreProcessor implements PacketPreProcessor<IQ, XMPPPacket> {

	@Override
	public void preProcess(IQ packet, LocalSession ls, StreamContext context, PacketCollector<XMPPPacket> collector)
			throws Exception {

		if (ls.isStreamManagementEnabled()) {
			ls.increaseHandledStanzaCount();

			AckPacket ack = new AckPacket(ls.getHandledStanzaCount());
			ack.setSourceId(context.getStreamId());

			collector.collectOutbound(ack);
		}

		packet.setFrom(context.getFrom());

		PersistenceService.getInstance().persistStanzaPacket(packet);

		if (packet.getContent() == null) {
			if (packet.getType() == IQType.RESULT) {
				collector.collectPongIQ(packet);
			}

		} else if (packet.getContent().getType() == IQContentType.QUERY) {
			IQQuery query = (IQQuery) packet.getContent();
			String xmlns = query.getXmlns();

			if (StringUtils.safeEquals(xmlns, XMPPNamespaces.USER_REGISTER_NAMESPACE)
					&& context.getState() == StreamState.INITIATED) {
				processUserRegistration(packet, collector);

			} else {

				collector.collectInbound(packet);
			}

		} else if (packet.getContent().getType() == IQContentType.SESSION) {
			IQ successIQ = new IQ(packet.getId(), IQType.RESULT);
			successIQ.setFrom(Stringflow.runtime().jid());
			successIQ.setTo(context.getFrom());

			collector.collectOutbound(successIQ);

		} else if (packet.getContent().getType() == IQContentType.BIND) {
			context.setUserResourceId(ls.getSessionId());
			ls.setSessionStreamId(context.getStreamId());
			SessionManager.getInstance().bindSession(ls);

			IQResourceBind bind = new IQResourceBind(XMPPNamespaces.RESOURCE_BIND_NAMESPACE);
			bind.setUserJID(context.getFrom());

			IQ bindIQ = new IQ(packet.getId(), IQType.RESULT, bind);
			bindIQ.setFrom(Stringflow.runtime().jid());
			bindIQ.setTo(context.getFrom());
			bindIQ.setContent(bind);

			collector.collectOutbound(bindIQ);

		} else if (packet.getContent().getType() == IQContentType.PING) {
			IQ pong = new IQ(packet.getId(), IQType.RESULT);
			pong.setFrom(Stringflow.runtime().jid());
			pong.setTo(context.getFrom());
			collector.collectOutbound(pong);

		} else if (packet.getContent().getType() == IQContentType.JINGLE) {
			collector.collectInbound(packet);

		} else if (packet.getContent().getType() == IQContentType.DATA
				|| packet.getContent().getType() == IQContentType.CLOSE
				|| packet.getContent().getType() == IQContentType.OPEN
				|| packet.getContent().getType() == IQContentType.PUSH_REGISTRATION
				|| packet.getContent().getType() == IQContentType.VCARD) {

			collector.collectInbound(packet);

		}
	}

	private void processUserRegistration(IQ requestIQ, PacketCollector<XMPPPacket> collector)
			throws InstantiationException {
		if (requestIQ.getType() == IQType.SET) {

			IQQuery query = (IQQuery) requestIQ.getContent();

			UserRegistrationData userData = query.getUserRegistrationData();

			registerNewUser(requestIQ, userData, collector);

		} else if (requestIQ.getType() == IQType.GET) {

			sendUserRegistrationAttribute(requestIQ.getId(), collector);
		}

	}

	private void sendUserRegistrationAttribute(String iqId, PacketCollector<XMPPPacket> collector) {
		IQQuery query = new IQQuery(XMPPNamespaces.USER_REGISTER_NAMESPACE);

		IQ iq = new IQ(iqId, IQType.RESULT, query);
		iq.setFrom(Stringflow.runtime().jid());

		collector.collectOutbound(iq);
	}

	private void registerNewUser(IQ iq, UserRegistrationData userRegistrationData,
			PacketCollector<XMPPPacket> collector) throws InstantiationException {

		if (StringUtils.isNullOrEmpty(userRegistrationData.getUserName())
				|| StringUtils.isNullOrEmpty(userRegistrationData.getPassword())
				|| StringUtils.isNullOrEmpty(userRegistrationData.getEmail())) {

			sendErrorIQ(iq.getId(), IQError.NOT_ACCEPTABLE_ERROR, collector);

		} else {
			BareJID newUserJID = new BareJID(userRegistrationData.getUserName(), Stringflow.runtime().domain());

			if (isUserJIDConflict(newUserJID)) {
				sendErrorIQ(iq.getId(), IQError.CONFLICT_ERROR, collector);

			} else {
				PersistenceService.getInstance().registerNewUser(newUserJID, userRegistrationData.getPassword(),
						userRegistrationData.getEmail());

				sendSuccessIQ(iq.getId(), collector);
			}
		}

	}

	private boolean isUserJIDConflict(BareJID userJID) throws InstantiationException {
		return PersistenceService.getInstance().isUserExist(userJID);
	}

	private void sendErrorIQ(String iqId, IQError error, PacketCollector<XMPPPacket> collector) {
		IQ iq = new IQ(iqId, IQType.ERROR);
		iq.setFrom(Stringflow.runtime().jid());

		IQErrorContent errorContent = new IQErrorContent(error);
		iq.setContent(errorContent);

		collector.collectOutbound(iq);

	}

	private void sendSuccessIQ(String iqId, PacketCollector<XMPPPacket> collector) {
		IQ iq = new IQ(iqId, IQType.RESULT);
		iq.setFrom(Stringflow.runtime().jid());

		collector.collectOutbound(iq);
	}
}
