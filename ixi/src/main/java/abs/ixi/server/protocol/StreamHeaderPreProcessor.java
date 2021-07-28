package abs.ixi.server.protocol;

import static abs.ixi.server.etc.conf.Configurations.Bundle.PROCESS;
import static abs.ixi.server.etc.conf.Configurations.Bundle.SYSTEM;
import static java.lang.Boolean.FALSE;

import abs.ixi.server.Stringflow;
import abs.ixi.server.etc.conf.Configurations.Bundle;
import abs.ixi.server.io.PacketCollector;
import abs.ixi.server.io.StreamContext;
import abs.ixi.server.io.StreamContext.StreamState;
import abs.ixi.server.packet.xmpp.ResourceBindFeature;
import abs.ixi.server.packet.xmpp.SASLFeature;
import abs.ixi.server.packet.xmpp.StreamFeaturePacket;
import abs.ixi.server.packet.xmpp.StreamHeader;
import abs.ixi.server.packet.xmpp.StreamMenagementFeature;
import abs.ixi.server.packet.xmpp.TLSFeature;
import abs.ixi.server.packet.xmpp.XMPPPacket;
import abs.ixi.server.session.LocalSession;
import abs.ixi.server.sys.secure.sasl.SASLMechanismName;
import abs.ixi.util.StringUtils;

public class StreamHeaderPreProcessor implements PacketPreProcessor<StreamHeader, XMPPPacket> {
	private static final String TRUE = "true";

	@Override
	public void preProcess(StreamHeader header, LocalSession ls, StreamContext context,
			PacketCollector<XMPPPacket> collector) throws Exception {
		if (header.isCloseStream()) {
			collector.collectOutbound(new StreamHeader(true));

		} else {
			StreamFeaturePacket featurePacket = new StreamFeaturePacket();
			featurePacket.setSourceId(context.getStreamId());

			if (context.getState() == StreamState.INITIATED) {

				if (Stringflow.runtime().configurations().getBoolean(_TLS_SUPPORT, PROCESS)) {

					// TODO: read from configuration isTLs required or not
					TLSFeature tlsFeature = new TLSFeature(true);
					featurePacket.addFeature(tlsFeature);

				} else {
					SASLFeature saslFeature = getSASLFeature();
					featurePacket.addFeature(saslFeature);
				}

			} else if (context.getState() == StreamState.TLS_DONE) {
				SASLFeature saslFeature = getSASLFeature();
				featurePacket.addFeature(saslFeature);

			} else if (context.getState() == StreamState.SASL_DONE) {
				ResourceBindFeature bindFeature = new ResourceBindFeature();
				featurePacket.addFeature(bindFeature);

				// TODO: Check SM supported by server or not from configuration
				StreamMenagementFeature smFeature = new StreamMenagementFeature();
				featurePacket.addFeature(smFeature);
			}

			modifyStreamHeader(header, context);

			collector.collectOutbound(header);

			collector.collectOutbound(featurePacket);
		}
	}

	private SASLFeature getSASLFeature() {
		SASLFeature saslFeature = new SASLFeature();

		// TODO: read All supported sasl mechanisms from configuration and add
		// them
		saslFeature.addMechnism(SASLMechanismName.PLAIN);

		return saslFeature;
	}

	private void modifyStreamHeader(StreamHeader header, StreamContext context) {
		header.setFrom(Stringflow.runtime().jid());
		header.setStreamId(context.getStreamId());
	}
}
