package abs.ixi.server.protocol;

import abs.ixi.server.io.PacketCollector;
import abs.ixi.server.io.StreamContext;
import abs.ixi.server.packet.xmpp.AckPacket;
import abs.ixi.server.packet.xmpp.XMPPPacket;
import abs.ixi.server.session.LocalSession;

public class AckPreProcessor implements PacketPreProcessor<AckPacket, XMPPPacket> {

	@Override
	public void preProcess(AckPacket ack, LocalSession ls, StreamContext context,
			PacketCollector<XMPPPacket> packetCollector) throws Exception {

		ls.setAcknowledgedStanzaCount(ack.getHandledPacketCount());

	}

}
