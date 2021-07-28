package abs.ixi.server.protocol;

import abs.ixi.server.io.StreamContext;
import abs.ixi.server.packet.Packet;
import abs.ixi.server.packet.xmpp.SASLSuccess;

public class SASLSuccessPostProcessor implements PacketPostProcessor {
	@Override
	public void postProcess(Packet packet, StreamContext context) throws Exception {
		SASLSuccess saslSuccess = (SASLSuccess) packet;
		context.saslDone(true, saslSuccess.getUserJID());
	}

}
