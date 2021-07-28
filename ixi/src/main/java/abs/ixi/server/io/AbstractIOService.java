package abs.ixi.server.io;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.Stringflow;
import abs.ixi.server.ValidationError;
import abs.ixi.server.io.net.IOPortConnector;
import abs.ixi.server.packet.Packet;
import abs.ixi.server.packet.XMPPError;
import abs.ixi.server.packet.XMPPUtil;
import abs.ixi.server.packet.xmpp.BareJID;
import abs.ixi.server.packet.xmpp.IQ;
import abs.ixi.server.packet.xmpp.SASLSuccess;
import abs.ixi.server.packet.xmpp.StreamError;
import abs.ixi.server.packet.xmpp.StreamHeader;
import abs.ixi.server.protocol.Protocol;
import abs.ixi.server.session.LocalSession;
import abs.ixi.util.StringUtils;

/**
 * Abstract implementation of {@link IOService} interface. It offers core
 * processing byte-to-packet and vice-versa.
 * 
 * @author Yogi
 *
 * @param <PROTOCOL>
 * @param <PACKET>
 */
public abstract class AbstractIOService<PROTOCOL extends Protocol<PACKET>, PACKET extends Packet>
		implements IOService<PROTOCOL, PACKET> {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractIOService.class);

	/**
	 * {@link BareJID} of the connected user
	 */
	protected BareJID bareJID;

	/**
	 * Represents underlying protocol for this stream
	 */
	protected PROTOCOL protocol;

	/**
	 * Stream processor instance to process incoming bytes and generate packets
	 */
	protected InputStreamProcessor<PACKET> streamProcessor;

	/**
	 * Number of bytes waiting to get processed
	 */
	protected AtomicLong bytesWaiting = new AtomicLong(0);

	/**
	 * flag to indicate if this {@link IOService} instance is being executed
	 * inside a thread (IOService implements {@link Runnable})
	 */
	protected volatile boolean executing;

	/**
	 * Id of the {@link IQ} packet inside which a Ping is sent to the client.
	 */
	private String pingId;

	/**
	 * Flag to indicate if a ping response (pong) is awaited on this connection
	 */
	private boolean awaitingPong;

	/**
	 * {@link LocalSession} instance to represent this user session.
	 */
	protected LocalSession localSession;

	/**
	 * InputHandler instance
	 */
	protected InputHandler inputHandler;

	public AbstractIOService(PROTOCOL protocol) {
		this.protocol = protocol;

		// TODO This must be removed once event/signal bus is introduced
		this.inputHandler = Stringflow.runtime().getInputHandler();

		LOGGER.debug("Instantiated {}", this);
	}

	@Override
	public void bytesRead(int bytesRead) {
		this.bytesArrived(bytesRead);
	}

	@Override
	public void channelDisconnected() {
		this.channelDead();
	}

	@Override
	public void bytesArrived() {
		execute();
	}

	@Override
	public void bytesArrived(int bytesCount) {
		this.bytesWaiting.addAndGet(bytesCount);
		execute();
	}

	@Override
	public void channelDead() {
		LOGGER.debug("Received channel dead signal; Unregistering...");
		this.closeStream();
	}

	@Override
	public void run() {
		try {
			LOGGER.debug("Running {}", this);

			byte[] bytes = readBytes();

			long remaining = 0;

			if (bytes != null) {
				long readBytes = bytes.length;
				remaining = this.bytesWaiting.addAndGet(readBytes * -1);

				this.process(bytes);
			}

			if (remaining > 0 || this.bytesWaiting.get() > 0) {
				execute0();

			} else {
				synchronized (this.bytesWaiting) {
					if (this.bytesWaiting.get() > 0) {
						execute0();
					} else {
						this.executing = false;
					}
				}
			}

		} catch (Throwable e) {
			LOGGER.error("Unexpected error occured", e);
		}
	}

	protected void execute() {
		if (!this.executing) {
			execute1();
		} else {
			synchronized (this.bytesWaiting) {
				if (!this.executing && this.bytesWaiting.get() > 0) {
					execute0();
				}
			}
		}
	}

	private void execute1() {
		synchronized (this.bytesWaiting) {
			if (!this.executing) {
				execute0();
			}
		}
	}

	private void execute0() {
		this.executing = true;
		this.inputHandler.execute(this);
	}

	public void process(byte[] networkData) {
		try {
			LOGGER.debug("Received data {}", new String(networkData, StandardCharsets.UTF_8));
			this.streamProcessor.process(networkData);

		} catch (Throwable t) {
			LOGGER.error("Error while processing packet in IOService {}", this.getId(), t);
			// TODO: stop processing
			sendStreamErrorAndStreamClose(XMPPError.INTERNAL_SERVER);
		}
	}

	@Override
	public BareJID getBareJID() {
		return this.bareJID;
	}

	@Override
	public String getNode() {
		return this.bareJID.getNode();
	}

	@Override
	public String getDomain() {
		return this.bareJID.getDomain();
	}

	@Override
	public String getResourceID() {
		return localSession.getSessionId();
	}

	protected void handleGeneratedPacket(PACKET packet) {
		try {
			this.localSession.setLastActivityTime(System.currentTimeMillis());
			protocol.enforceInbound(packet, localSession);

		} catch (ValidationError e) {
			LOGGER.error("Packet validation Error on validation of packet {}", packet, e);
			sendStreamErrorAndStreamClose(e.getError());

		} catch (Throwable t) {
			LOGGER.error("Server Error while enforcing Inbound protocaol on generated packets {}", packet, t);
			sendStreamErrorAndStreamClose(XMPPError.INTERNAL_SERVER);
		}

	}

	protected void handleSASLSuccess(SASLSuccess saslSuccess) {
		this.bareJID = saslSuccess.getUserJID().getBareJID();
		this.localSession.setUserJID(this.bareJID);
	}

	protected void handleInboundPacket(Packet packet) {
		try {
			this.inputHandler.handle(packet);

		} catch (Throwable e) {
			LOGGER.error("Error while handaling inbound packet {}", packet, e);
			sendStreamErrorAndStreamClose(XMPPError.INTERNAL_SERVER);
		}
	}

	@Override
	public boolean handleOutboundPacket(Packet packet) {
		try {
			Packet writablePacket = protocol.enforceOutbound(packet);
			boolean written = writePacket(writablePacket);

			this.localSession.setLastActivityTime(System.currentTimeMillis());

			if (packet.isCloseStream()) {
				closeStream();
			}

			return written;

		} catch (IOException e) {
			LOGGER.error("IO error while handaling outbound packet{}", packet, e);

		} catch (Exception e) {
			LOGGER.error("Error while handaling outbound for  packet {}", packet, e);
			sendStreamErrorAndStreamClose(XMPPError.INTERNAL_SERVER);
		}

		return false;
	}

	/**
	 * If ResourceBinding is done it will destroy through Session manager.
	 * Otherwise it will destroy directly.
	 */
	@Override
	public void closeStream() {
		LOGGER.info("Closing connection for bareJID {} and resourceID {}", this.bareJID, getId());
		this.localSession.archive();
	}

	private void sendStreamErrorAndStreamClose(XMPPError error) {
		LOGGER.info("Sending Stream Error and close stream");
		StreamError streamError = new StreamError(XMPPUtil.getXMPPErrorResponse(error));
		this.handleOutboundPacket(streamError);

		StreamHeader closeStream = new StreamHeader(true);
		this.handleOutboundPacket(closeStream);

	}

	@Override
	public boolean sendPing() {
		if (protocol.isPingable()) {
			IQ pingRequest = protocol.getPingRequestIQ();
			try {
				handleOutboundPacket(pingRequest);
				this.pingId = pingRequest.getId();
				this.awaitingPong = true;

				return true;
			} catch (Exception e) {
				LOGGER.info("Failed to ping", e);
				// TODO: after implementation check we may need to close this
				// session here
			}
		}

		return false;
	}

	@Override
	public boolean hasPingTimeout() {
		return this.awaitingPong;
	}

	/**
	 * Notify this ioService on the ping response(Pong). This will turn off the
	 * <b>awaitingPong</b> flag.
	 * 
	 * @param pongId id of the pong
	 * 
	 */
	protected void pong(String pongId) {
		if (this.awaitingPong && StringUtils.safeEquals(this.pingId, pongId)) {
			this.awaitingPong = false;
			this.pingId = null;
		}
	}

	@Override
	public long getLastActivityTime() {
		return localSession.getLastActivityTime();
	}

	@Override
	public String getId() {
		return "io-svc-" + this.localSession.getSessionId();
	}

	/**
	 * read bytes from {@link IOPortConnector}.
	 */
	protected abstract byte[] readBytes();

}
