package abs.ixi.server.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.io.multipart.MimePacket;
import abs.ixi.server.io.net.IOPortConnector;
import abs.ixi.server.packet.Packet;
import abs.ixi.server.packet.xmpp.IQ;
import abs.ixi.server.packet.xmpp.SASLSuccess;
import abs.ixi.server.protocol.Protocol;
import abs.ixi.server.session.LocalSession;

/**
 * An {@link IOService} implementation which long polling on multiple
 * connections from same client. <i>LongPolling</i> is a mechanism in which a
 * HTTP client makes multiple connections with the server allowing server to
 * push response at will. For more information see
 * <a href="https://xmpp.org/extensions/xep-0124.html">XEP-0124</a> and
 * <a href="https://xmpp.org/extensions/xep-0206.html">XEP-0206</a>
 * 
 * @author YogiR
 *
 */
public class LongPollingIOService<PROTOCOL extends Protocol<PACKET>, PACKET extends Packet>
		extends AbstractIOService<PROTOCOL, PACKET> implements PacketCollector<PACKET> {

	private static final Logger LOGGER = LoggerFactory.getLogger(LongPollingIOService.class);

	/**
	 * Connection which is not active at the momemnt; it may timeout at some
	 * point and will be cleaned up.
	 */
	@SuppressWarnings("unused")
	private IOPortConnector abandonedConnector;

	/**
	 * Connection which is being long polled at the moment.
	 */
	private IOPortConnector longPolledConnector;

	public LongPollingIOService(IOPortConnector connection, PROTOCOL protocol) {
		super(protocol);

		this.protocol.addPacketCollector(this);
		this.longPolledConnector = connection;
		this.longPolledConnector.attachIOSignalReceiver(this);
		this.streamProcessor = this.protocol.getInputStreamProcessor();
		this.localSession = new LocalSession(this);

		LOGGER.debug("instantiated {}", this);
	}

	@Override
	protected byte[] readBytes() {
		if (this.streamProcessor.hasUnprocessedBytes()) {
			return this.longPolledConnector.readAllBytes(this.streamProcessor.getUnprocessedBytes());

		} else {
			return this.longPolledConnector.readAllBytes();
		}
	}

	@Override
	public boolean writePacket(Packet packet) {
		try {
			if (!this.longPolledConnector.isConnected()) {
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

		this.longPolledConnector.write(packet);
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
		this.longPolledConnector.write(data);

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
		return this.longPolledConnector.getRemoteAddress();
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
				writeData(mimePacket.getMediaAckResponse());
			} else {
				// TODO: Check for failure nd send failure response
			}

		} catch (Exception e) {
			LOGGER.error("Failed to send mime sid response for sessionID : " + localSession.getSessionId());
			closeStream();
		}
	}

	@Override
	public void destroy() {
		LOGGER.info("Destroying ioService {}", this);
		this.longPolledConnector.close();
		// TODO: unregister this ioService from ioService cache in IOController.
	}

}
