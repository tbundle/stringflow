package abs.ixi.server.protocol;

import abs.ixi.server.io.StreamContext;
import abs.ixi.server.io.StreamContext.StreamState;
import abs.ixi.server.packet.Packet;
import abs.ixi.server.packet.xmpp.IQ;
import abs.ixi.server.packet.xmpp.IQContent.IQContentType;

public class IQPostProcessor implements PacketPostProcessor {

	@Override
	public void postProcess(Packet packet, StreamContext context) throws Exception {
		IQ iq = (IQ) packet;

		if (iq.getContent() != null && iq.getContent().getType() == IQContentType.BIND) {
			context.setState(StreamState.OPEN);
		}
	}

}
