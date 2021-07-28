package abs.ixi.server.protocol;

import abs.ixi.server.io.PacketCollector;
import abs.ixi.server.io.StreamContext;
import abs.ixi.server.packet.Packet;
import abs.ixi.server.packet.xmpp.SASLChallengeResponse;
import abs.ixi.server.session.LocalSession;

public class SASLChallengeResponsePreProcessor implements PacketPreProcessor<SASLChallengeResponse, Packet> {

    @Override
    public void preProcess(SASLChallengeResponse packet, LocalSession ls, StreamContext context, PacketCollector<Packet> packetCollector)
	    throws Exception {
	// TODO : handle Challenge response
    }

}
