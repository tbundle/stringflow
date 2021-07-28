package abs.ixi.server.protocol;

import abs.ixi.server.etc.PersistenceService;
import abs.ixi.server.io.PacketCollector;
import abs.ixi.server.io.StreamContext;
import abs.ixi.server.packet.xmpp.AckPacket;
import abs.ixi.server.packet.xmpp.Presence;
import abs.ixi.server.packet.xmpp.XMPPPacket;
import abs.ixi.server.session.LocalSession;

public class PresencePreProcessor implements PacketPreProcessor<Presence, XMPPPacket> {

	@Override
	public void preProcess(Presence presence, LocalSession ls, StreamContext context,
			PacketCollector<XMPPPacket> collector) throws Exception {

		if (ls.isStreamManagementEnabled()) {
			ls.increaseHandledStanzaCount();

			AckPacket ack = new AckPacket(ls.getHandledStanzaCount());
			ack.setSourceId(context.getStreamId());

			collector.collectOutbound(ack);
		}

		presence.setFrom(context.getFrom());

		PersistenceService.getInstance().persistStanzaPacket(presence);

		collector.collectInbound(presence);

	}

}
