package abs.ixi.server.protocol;

import abs.ixi.server.io.PacketCollector;
import abs.ixi.server.io.StreamContext;
import abs.ixi.server.io.StreamContext.StreamState;
import abs.ixi.server.packet.XMPPNamespaces;
import abs.ixi.server.packet.xmpp.FailurePacket;
import abs.ixi.server.packet.xmpp.StartTlsPacket;
import abs.ixi.server.packet.xmpp.TLSProceed;
import abs.ixi.server.packet.xmpp.XMPPPacket;
import abs.ixi.server.session.LocalSession;

public class StartTLSPreProcessor implements PacketPreProcessor<StartTlsPacket, XMPPPacket> {

    @Override
    public void preProcess(StartTlsPacket startTLS, LocalSession ls, StreamContext context, PacketCollector<XMPPPacket> packetCollector)
	    throws Exception {
	context.setState(StreamState.TLS_STARTED);

	// TODO : enable TLS
	boolean tlsResult = true;

	if (tlsResult) {
	    TLSProceed proceed = new TLSProceed();
	    proceed.setSourceId(context.getStreamId());

	    packetCollector.collectOutbound(proceed);

	} else {
	    FailurePacket failure = new FailurePacket(XMPPNamespaces.TLS_NAMESPACE);
	    failure.setSourceId(context.getStreamId());

	    packetCollector.collectOutbound(failure);
	}

    }

}
