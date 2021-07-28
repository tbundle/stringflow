package abs.ixi.server.protocol;

import abs.ixi.server.io.StreamContext;
import abs.ixi.server.io.StreamContext.StreamState;
import abs.ixi.server.packet.Packet;

public class StreamResumedPostProcessor implements PacketPostProcessor {

	@Override
	public void postProcess(Packet packet, StreamContext context) throws Exception {
		context.setState(StreamState.OPEN);
	}

}
