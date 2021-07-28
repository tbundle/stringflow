package abs.ixi.server.protocol;

import java.util.List;

import abs.ixi.server.io.StreamContext;
import abs.ixi.server.io.StreamContext.StreamState;
import abs.ixi.server.packet.Packet;
import abs.ixi.server.packet.xmpp.StreamFeature;
import abs.ixi.server.packet.xmpp.StreamFeaturePacket;
import abs.ixi.server.packet.xmpp.StreamFeature.StreamFeatureType;
import abs.ixi.util.CollectionUtils;

public class StreamFeaturePostProcessor implements PacketPostProcessor {
	@Override
	public void postProcess(Packet packet, StreamContext context) throws Exception {
		StreamFeaturePacket featurePacket = (StreamFeaturePacket) packet;

		List<StreamFeature> features = featurePacket.getFeatures();

		boolean bindAdvertised = false;
		boolean smAdvertised = false;

		if (!CollectionUtils.isNullOrEmpty(features)) {
			for (StreamFeature feature : features) {
				if (feature.getFeatureType() == StreamFeatureType.TLS)
					context.setState(StreamState.TLS_ADVERTISED);

				else if (feature.getFeatureType() == StreamFeatureType.SASL)
					context.setState(StreamState.SASL_ADVERTISED);

				else if (feature.getFeatureType() == StreamFeatureType.BIND)
					bindAdvertised = true;

				else if (feature.getFeatureType() == StreamFeatureType.SM)
					smAdvertised = true;
			}
		}

		if (bindAdvertised && smAdvertised) {
			context.setState(StreamState.RESOURCE_BIND_AND_SM_ADVERTISED);
		}
	}

}
