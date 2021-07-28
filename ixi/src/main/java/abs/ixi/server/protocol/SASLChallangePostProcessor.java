package abs.ixi.server.protocol;

import abs.ixi.server.io.StreamContext;
import abs.ixi.server.packet.Packet;

public class SASLChallangePostProcessor implements PacketPostProcessor {
	@Override
	public void postProcess(Packet packet, StreamContext context) throws Exception {
		// SASLChallenge challenge = (SASLChallenge) packet;

	}
}
