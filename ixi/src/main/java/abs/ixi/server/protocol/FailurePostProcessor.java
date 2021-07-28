package abs.ixi.server.protocol;

import abs.ixi.server.io.StreamContext;
import abs.ixi.server.io.StreamContext.StreamState;
import abs.ixi.server.packet.Packet;
import abs.ixi.server.packet.XMPPNamespaces;
import abs.ixi.server.packet.xmpp.FailurePacket;
import abs.ixi.util.StringUtils;

public class FailurePostProcessor implements PacketPostProcessor {
	@Override
	public void postProcess(Packet packet, StreamContext context) throws Exception {
		FailurePacket failure = (FailurePacket) packet;

		if (StringUtils.safeEquals(failure.getXmlns(), XMPPNamespaces.TLS_NAMESPACE)) {

			context.setState(StreamState.UNKNOWN);

		} else if (StringUtils.safeEquals(failure.getXmlns(), XMPPNamespaces.SASL_NAMESPACE)) {

			context.saslDone(false, null);
		}
	}

}
