package abs.ixi.server.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.io.multipart.MimePacket;
import abs.ixi.server.io.net.IOPort;
import abs.ixi.server.io.net.IOPortConnector;
import abs.ixi.server.packet.Packet;
import abs.ixi.server.packet.xmpp.IQ;
import abs.ixi.server.packet.xmpp.SASLSuccess;
import abs.ixi.server.protocol.Protocol;
import abs.ixi.server.session.LocalSession;

/**
 * An abstract implementation of {@link IOService} interface. This class offers
 * basic ability to interface with a {@link IOPort} and the server
 * router/application
 */
public class BasicIOService<PROTOCOL extends Protocol<PACKET>, PACKET extends Packet>
		extends AbstractIOService<PROTOCOL, PACKET> implements PacketCollector<PACKET> {

	private static final Logger LOGGER = LoggerFactory.getLogger(BasicIOService.class);

	private IOPortConnector connector;

	public BasicIOService(IOPortConnector connector, PROTOCOL protocol) throws IOException {
		super(protocol);

		this.protocol.addPacketCollector(this);
		this.connector = connector;
		this.connector.attachIOSignalReceiver(this);
		this.streamProcessor = this.protocol.getInputStreamProcessor();
		this.localSession = new LocalSession(this);

		LOGGER.debug("Instantiated IOServie {}", this);
	}

	@Override
	protected byte[] readBytes() {
		if (this.streamProcessor.hasUnprocessedBytes()) {
			return this.connector.readAllBytes(this.streamProcessor.getUnprocessedBytes());

		} else {
			return this.connector.readAllBytes();
		}
	}

	@Override
	public boolean writePacket(Packet packet) {
		try {
			if (!this.connector.isConnected()) {
				closeStream();

			} else {
				this.writePacket0(packet);
				return true;
			}

		} catch (Exception e) {
			LOGGER.error("Failed to write packet {} ", packet, e);
			closeStream();
		}

		return false;
	}

	public boolean writePacket0(Packet packet) throws Exception {
		LOGGER.info("Writting packet {} onto {}", packet, this);

		this.connector.write(packet);
		return true;
	}

	@Override
	public boolean writeData(String data) throws Exception {
		LOGGER.debug("Writing data {}", data);
		return this.writeData(data.getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public boolean writeData(byte[] data) throws Exception {
		LOGGER.debug("Sent Data {}", new String(data)); // very expensive op
		this.connector.write(data);

		return true;
	}

	@Override
	public void channelRegistered() {
		LOGGER.info("Channel Registered Event generated");
		if (protocol.isMimeStream()) {
			sendSIDResponse();
		}

	}

	private void sendSIDResponse() {
		try {
			LOGGER.info("Sending mime sid : {} Resoponse for mime ioService", localSession.getSessionId());

			MimePacket mimePacket = new MimePacket(localSession.getSessionId());
			writeData(mimePacket.getSidResponse());

		} catch (Exception e) {
			LOGGER.error("Failed to send mime sid response for sessionID : " + localSession.getSessionId());
			closeStream();
		}
	}

	protected InetSocketAddress getRemoteAddress() throws IOException {
		return this.connector.getRemoteAddress();
	}

	@Override
	public void collect(PACKET packet) {
		handleGeneratedPacket(packet);
	}

	@Override
	public void collectInbound(PACKET packet) {
		handleInboundPacket(packet);
	}

	@Override
	public void collectOutbound(PACKET packet) {
		handleOutboundPacket(packet);
	}

	@Override
	public void collectSASLSuccess(SASLSuccess saslSuccess) {
		handleSASLSuccess(saslSuccess);
	}

	@Override
	public void collectPongIQ(IQ pongIQ) {
		pong(pongIQ.getId());
	}

	@Override
	public void collectMimePacket(MimePacket mimePacket) {
		try {
			LOGGER.info("Sending mime sid : {} Resoponse for mime ioService", localSession.getSessionId());

			if (mimePacket.isMediaAckResponse()) {
				// TODO: Do not send media receive ack on mime connection. send
				// Ack on xmpp stream
				// writeData(mimePacket.getMediaAckResponse());

			} else {
				// TODO: Check for failure nd send failure response
			}

		} catch (Exception e) {
			LOGGER.error("Failed to send mime sid response for sessionID : " + localSession.getSessionId());
			this.closeStream();
		}
	}

	@Override
	public void destroy() {
		LOGGER.info("Destroying ioService {} with resource ID {}", this, this.getResourceID());
		this.connector.close();

		// TODO: Later may not to do this thing. mime cache may also removed
		// from IOController. Think about it later.
		if (protocol.isMimeStream()) {
			IOController.removeFromMimeIosCache(this);
		}

	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		LOGGER.debug("Finalizing {}", this);
	}
}
