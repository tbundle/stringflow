package abs.ixi.server.protocol;

import abs.ixi.server.io.PacketCollector;
import abs.ixi.server.io.StreamContext;
import abs.ixi.server.packet.xmpp.StreamError;
import abs.ixi.server.packet.xmpp.StreamHeader;
import abs.ixi.server.packet.xmpp.XMPPPacket;
import abs.ixi.server.session.LocalSession;

public class StreamErrorPreProcessor implements PacketPreProcessor<StreamError, XMPPPacket> {
    @Override
    public void preProcess(StreamError packet, LocalSession ls, StreamContext context, PacketCollector<XMPPPacket> packetCollector)
	    throws Exception {
	StreamHeader closeStream = new StreamHeader(true);

	packetCollector.collectOutbound(packet);
	packetCollector.collectOutbound(closeStream);
    }

}
