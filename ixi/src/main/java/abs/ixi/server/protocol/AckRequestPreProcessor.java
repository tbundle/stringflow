package abs.ixi.server.protocol;

import abs.ixi.server.io.PacketCollector;
import abs.ixi.server.io.StreamContext;
import abs.ixi.server.packet.xmpp.AckPacket;
import abs.ixi.server.packet.xmpp.AckRequestPacket;
import abs.ixi.server.packet.xmpp.XMPPPacket;
import abs.ixi.server.session.LocalSession;

public class AckRequestPreProcessor implements PacketPreProcessor<AckRequestPacket, XMPPPacket> {

	@Override
	public void preProcess(AckRequestPacket packet, LocalSession ls, StreamContext context,
			PacketCollector<XMPPPacket> collector) throws Exception {
		
		AckPacket ack = new AckPacket(ls.getHandledStanzaCount());
		ack.setSourceId(context.getStreamId());

		collector.collectOutbound(ack);
	}

}
