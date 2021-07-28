package abs.ixi.server.protocol;

import abs.ixi.server.etc.PersistenceService;
import abs.ixi.server.io.PacketCollector;
import abs.ixi.server.io.StreamContext;
import abs.ixi.server.packet.xmpp.AckPacket;
import abs.ixi.server.packet.xmpp.Message;
import abs.ixi.server.packet.xmpp.XMPPPacket;
import abs.ixi.server.session.LocalSession;

public class MessagePreProcessor implements PacketPreProcessor<Message, XMPPPacket> {

	@Override
	public void preProcess(Message message, LocalSession ls, StreamContext context,
			PacketCollector<XMPPPacket> collector) throws Exception {

		if (ls.isStreamManagementEnabled()) {
			ls.increaseHandledStanzaCount();

			AckPacket ack = new AckPacket(ls.getHandledStanzaCount());
			ack.setSourceId(context.getStreamId());

			collector.collectOutbound(ack);
		}

		message.setFrom(context.getFrom());

		PersistenceService.getInstance().persistStanzaPacket(message);
		collector.collectInbound(message);
	}

}
