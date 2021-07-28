package abs.ixi.server.protocol;

import abs.ixi.server.io.StreamContext;
import abs.ixi.server.io.StreamContext.StreamState;
import abs.ixi.server.packet.Packet;

public class TLSProceedPostProcessor implements PacketPostProcessor {

	@Override
	public void postProcess(Packet packet, StreamContext context) throws Exception {
		// TLSProceed tlsProceed = (TLSProceed) packet;
		context.setState(StreamState.TLS_DONE);
	}

}
