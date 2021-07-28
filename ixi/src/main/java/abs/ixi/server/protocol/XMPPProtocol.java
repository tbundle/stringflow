package abs.ixi.server.protocol;

import static abs.ixi.server.protocol.PacketPreProcessorFactory.getPreProcessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.io.InputStreamProcessor;
import abs.ixi.server.io.PacketCollector;
import abs.ixi.server.io.StreamContext;
import abs.ixi.server.io.XmppStreamProcessor;
import abs.ixi.server.packet.Packet;
import abs.ixi.server.packet.PacketValidatorFactory;
import abs.ixi.server.packet.XMPPPacketValidator;
import abs.ixi.server.packet.xmpp.IQ;
import abs.ixi.server.packet.xmpp.XMPPPacket;
import abs.ixi.server.session.LocalSession;
import abs.ixi.util.UUIDGenerator;

/**
 * XMPP protocol implementation. All the Protocol knowledge has been
 * encapsulated in this class. This implementation is not thread-safe. By
 * design, a protocol instance is associated with one user stream.
 */
public class XMPPProtocol implements Protocol<XMPPPacket> {
	private static final Logger LOGGER = LoggerFactory.getLogger(XMPPProtocol.class);

	/**
	 * {@link StreamContext} instance; it hold associated XMPP stream state
	 */
	private StreamContext context;

	/**
	 * Validator to validate inbound and outbound packets to ensure that packet
	 * flow complies to protocol rules.
	 */
	private XMPPPacketValidator validator;

	/**
	 * A collector for generated packets from this protocol
	 */
	private PacketCollector<XMPPPacket> packetCollector;

	public XMPPProtocol() {
		this(new StreamContext(UUIDGenerator.uuid()));
	}

	public XMPPProtocol(StreamContext context) {
		this.context = context;
		this.validator = PacketValidatorFactory.getXmppPacketValidator();
	}

	@Override
	public void enforceInbound(XMPPPacket packet, LocalSession ls) throws Exception {
		validator.validate(packet, null, new Object[] { context });
		preProcessInboundPacket(packet, ls);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void preProcessInboundPacket(XMPPPacket packet, LocalSession ls) throws Exception {
		PacketPreProcessor preprocessor = getPreProcessor(packet);

		if (preprocessor != null)
			preprocessor.preProcess(packet, ls, this.context, this.packetCollector);

		else
			LOGGER.info("No pre processor found for packet {}", packet);
	}

	@Override
	public XMPPPacket enforceOutbound(Packet packet) throws Exception {
		postProcessOutboundPacket(packet);
		return (XMPPPacket) packet;
	}

	private void postProcessOutboundPacket(Packet packet) throws Exception {
		PacketPostProcessor processor = PacketPostProcessorFactory.getPostProcessor(packet);

		if (processor != null)
			processor.postProcess(packet, this.context);

		else
			LOGGER.info("No post processor found for packet {}", packet);

	}

	@Override
	public void addPacketCollector(PacketCollector<XMPPPacket> collector) {
		this.packetCollector = collector;
	}

	@Override
	public InputStreamProcessor<XMPPPacket> getInputStreamProcessor() {
		return new XmppStreamProcessor(this.packetCollector);
	}

	@Override
	public boolean isPingable() {
		return true;
	}

	@Override
	public IQ getPingRequestIQ() {
		return IQ.getPingPacket(context.getFrom());
	}

}
