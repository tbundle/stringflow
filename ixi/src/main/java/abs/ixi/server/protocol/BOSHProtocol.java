package abs.ixi.server.protocol;

import static abs.ixi.server.protocol.PacketPreProcessorFactory.getPreProcessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.io.BOSHStreamProcessor;
import abs.ixi.server.io.BoshStreamContext;
import abs.ixi.server.io.InputStreamProcessor;
import abs.ixi.server.io.PacketCollector;
import abs.ixi.server.packet.BoshPacketValidator;
import abs.ixi.server.packet.Packet;
import abs.ixi.server.packet.PacketValidatorFactory;
import abs.ixi.server.packet.xmpp.BOSHBody;
import abs.ixi.server.packet.xmpp.BoshHttpPacket;
import abs.ixi.server.packet.xmpp.IQ;
import abs.ixi.server.packet.xmpp.XMPPPacket;
import abs.ixi.server.packet.xmpp.BOSHBody.Type;
import abs.ixi.server.session.LocalSession;
import abs.ixi.util.CollectionUtils;
import abs.ixi.util.UUIDGenerator;

public class BOSHProtocol implements Protocol<BOSHBody> {
	private static final Logger LOGGER = LoggerFactory.getLogger(BOSHProtocol.class);

	private BoshStreamContext context;
	private BoshPacketValidator boshValidator;
	private PacketCollector<BOSHBody> packetCollector;

	public BOSHProtocol() {
		this(new BoshStreamContext(UUIDGenerator.uuid()));
	}

	public BOSHProtocol(BoshStreamContext context) {
		this.context = context;
		this.boshValidator = PacketValidatorFactory.getBoshPacketValidator();
	}

	@Override
	public void enforceInbound(BOSHBody boshBody, LocalSession ls) throws Exception {
		boshValidator.validate(boshBody, null, new Object[] { context });

		if (!CollectionUtils.isNullOrEmpty(boshBody.getXmppPackets())) {
			for (XMPPPacket xmppPacket : boshBody.getXmppPackets()) {
				preProcessInboundPacket(xmppPacket, ls);
			}

		} else {
			preProcessInboundPacket(boshBody, ls);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void preProcessInboundPacket(Packet packet, LocalSession ls) throws Exception {
		PacketPreProcessor preprocessor = getPreProcessor(packet);

		if (preprocessor != null) {
			preprocessor.preProcess(packet, ls, this.context, this.packetCollector);

		} else {
			LOGGER.info("No pre processor found for packet {}", packet);
		}
	}

	@Override
	public BOSHBody enforceOutbound(Packet packet) throws Exception {
		postProcessOutboundPacket(packet);
		return packet.isBoshBodyPacket() ? (BoshHttpPacket) packet : wrapToBoshBody((XMPPPacket) packet);
	}

	private BoshHttpPacket wrapToBoshBody(XMPPPacket xmppPacket) {
		BoshHttpPacket boshBody = new BoshHttpPacket();

		if (xmppPacket.isCloseStream()) {
			boshBody.setType(Type.TERMINATE);

		} else {
			boshBody.addXmppPacket(xmppPacket);
		}

		return boshBody;
	}

	private void postProcessOutboundPacket(Packet packet) throws Exception {
		PacketPostProcessor processor = PacketPostProcessorFactory.getPostProcessor(packet);

		if (processor != null) {
			processor.postProcess(packet, this.context);

		} else {
			LOGGER.info("No post processor found for packet {}", packet);
		}
	}

	@Override
	public void addPacketCollector(PacketCollector<BOSHBody> packetCollector) {
		this.packetCollector = packetCollector;
	}

	@Override
	public InputStreamProcessor<BOSHBody> getInputStreamProcessor() {
		return new BOSHStreamProcessor(this.packetCollector);
	}

	@Override
	public boolean isPingable() {
		return false;
	}

	@Override
	public IQ getPingRequestIQ() {
		return null;
	}

	/**
	 * This is an utility method which wraps a {@link BOSHBody} inside Http
	 * Response. As part of BOSH protocol specifications, BOSH is XMPP over
	 * HTTP. Therefore all outbound packets are wrapped inside HTTP Response.
	 */
	public static void wrapWithinHttpResponse(BOSHBody booshBody) {

	}

}
