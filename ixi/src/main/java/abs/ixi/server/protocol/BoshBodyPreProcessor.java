package abs.ixi.server.protocol;

import static abs.ixi.server.etc.conf.Configurations.Bundle.PROCESS;

import abs.ixi.server.Stringflow;
import abs.ixi.server.io.BoshStreamContext;
import abs.ixi.server.io.PacketCollector;
import abs.ixi.server.io.StreamContext;
import abs.ixi.server.io.StreamContext.StreamState;
import abs.ixi.server.packet.xmpp.BOSHBody;
import abs.ixi.server.packet.xmpp.BoshHttpPacket;
import abs.ixi.server.packet.xmpp.ResourceBindFeature;
import abs.ixi.server.packet.xmpp.SASLFeature;
import abs.ixi.server.packet.xmpp.StreamFeaturePacket;
import abs.ixi.server.packet.xmpp.StreamMenagementFeature;
import abs.ixi.server.packet.xmpp.TLSFeature;
import abs.ixi.server.packet.xmpp.XMPPPacket;
import abs.ixi.server.packet.xmpp.BOSHBody.Type;
import abs.ixi.server.session.LocalSession;
import abs.ixi.server.sys.secure.sasl.SASLMechanismName;
import abs.ixi.util.StringUtils;

public class BoshBodyPreProcessor implements PacketPreProcessor<BOSHBody, XMPPPacket> {
	private static final String TRUE = "true";

	@Override
	public void preProcess(BOSHBody boshBody, LocalSession ls, StreamContext context,
			PacketCollector<XMPPPacket> collector) throws Exception {

		BoshStreamContext boshContext = (BoshStreamContext) context;

		if (boshBody.isCloseStream()) {
			BoshHttpPacket responseBody = new BoshHttpPacket();
			responseBody.setRid(boshBody.getRid());
			responseBody.setType(Type.TERMINATE);

			collector.collectOutbound(responseBody);

		} else if (boshBody.isStreamStartRequest()) {
			updateBoshStreamContest(boshBody, boshContext);

			BoshHttpPacket responseBody = getCreationResponseBody(boshContext);

			collector.collectOutbound(responseBody);

			StreamFeaturePacket featurePacket = new StreamFeaturePacket();

			if (StringUtils.safeEquals(Stringflow.runtime().configurations().get(_TLS_SUPPORT, PROCESS), TRUE)) {

				// TODO: read from configuration isTLs required or not
				TLSFeature tlsFeature = new TLSFeature(true);
				featurePacket.addFeature(tlsFeature);

			} else {
				SASLFeature saslFeature = getSASLFeature();
				featurePacket.addFeature(saslFeature);
			}

			collector.collectOutbound(featurePacket);

		} else if (boshBody.isStreamRestart()) {
			StreamFeaturePacket featurePacket = new StreamFeaturePacket();

			if (boshContext.getState() == StreamState.TLS_DONE) {
				SASLFeature saslFeature = getSASLFeature();
				featurePacket.addFeature(saslFeature);

			} else if (boshContext.getState() == StreamState.SASL_DONE) {
				ResourceBindFeature bindFeature = new ResourceBindFeature();
				featurePacket.addFeature(bindFeature);

				// TODO: Check SM supported by server or not from configuration
				StreamMenagementFeature smFeature = new StreamMenagementFeature();
				featurePacket.addFeature(smFeature);
			}

			collector.collectOutbound(featurePacket);
		}

	}

	private BoshHttpPacket getCreationResponseBody(BoshStreamContext ctx) {
		BoshHttpPacket responseBody = new BoshHttpPacket();

		responseBody.setSid(ctx.getStreamId());
		responseBody.setWait(ctx.getWait());
		responseBody.setHold(ctx.getHold());
		responseBody.setInactivityTime(ctx.getInactivityTime());
		responseBody.setMaxpause(ctx.getMaxpause());
		responseBody.setPollingTime(ctx.getPollingTime());
		responseBody.setRequests(ctx.getRequests());
		responseBody.setFrom(Stringflow.runtime().jid());
		responseBody.setVer(ctx.getVersion());

		return responseBody;
	}

	private void updateBoshStreamContest(BOSHBody boshBody, BoshStreamContext ctx) {
		if (boshBody.getAck() != 0) {
			ctx.setAck(boshBody.getAck());
		}

		ctx.setWait(boshBody.getWait());
		ctx.setHold(boshBody.getHold());

		if (!StringUtils.isNullOrEmpty(boshBody.getContentType())) {
			ctx.setContentType(boshBody.getContentType());
		}

	}

	private SASLFeature getSASLFeature() {
		SASLFeature saslFeature = new SASLFeature();

		// TODO: read All supported sasl mechanisms from configuration and add
		// them
		saslFeature.addMechnism(SASLMechanismName.PLAIN);

		return saslFeature;
	}

}
