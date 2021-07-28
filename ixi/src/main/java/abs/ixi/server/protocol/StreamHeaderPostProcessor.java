package abs.ixi.server.protocol;

import abs.ixi.server.io.StreamContext;
import abs.ixi.server.io.StreamContext.StreamState;
import abs.ixi.server.packet.Packet;
import abs.ixi.server.packet.xmpp.StreamHeader;

public class StreamHeaderPostProcessor implements PacketPostProcessor {

	@Override
	public void postProcess(Packet packet, StreamContext context) throws Exception {
		StreamHeader header = (StreamHeader) packet;

		if (header.isCloseStream()) {
			context.setState(StreamState.CLOSED);
		}
	}

}
