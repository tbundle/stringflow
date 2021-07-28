package abs.ixi.server.protocol;

import abs.ixi.server.io.PacketCollector;
import abs.ixi.server.io.StreamContext;
import abs.ixi.server.io.StreamContext.StreamState;
import abs.ixi.server.packet.Packet;
import abs.ixi.server.packet.Packet.PacketXmlElement;
import abs.ixi.server.packet.xmpp.SASLAuthPacket;
import abs.ixi.server.packet.xmpp.SASLSuccess;
import abs.ixi.server.session.LocalSession;
import abs.ixi.server.sys.secure.sasl.SaslEngineFactory;

public class SASLAuthPreProcessor implements PacketPreProcessor<SASLAuthPacket, Packet> {

	@Override
	public void preProcess(SASLAuthPacket authPacket, LocalSession ls, StreamContext context,
			PacketCollector<Packet> collector) throws Exception {

		context.setState(StreamState.SASL_STARTED);

		authPacket.setSourceId(context.getStreamId());

		Packet saslResponse;

		saslResponse = SaslEngineFactory.getSaslEngine().processPacket(authPacket);

		if (saslResponse.getXmlElementName() == PacketXmlElement.SASL_SUCCESS) {
			collector.collectSASLSuccess((SASLSuccess) saslResponse);
		}

		collector.collectOutbound(saslResponse);
	}

}
