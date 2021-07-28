package abs.ixi.server.io.net;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.common.BufferFactory;
import abs.ixi.server.common.ChannelStream;
import abs.ixi.server.io.ChannelFacade;
import abs.ixi.server.io.IOService;
import abs.ixi.server.packet.Packet;

/**
 * This class acts as an adaptor between Network transport and
 * {@link IOService}. The adaptor is not thread-safe because by design each
 * {@link SelectableChannel} will be wrapped within one {@link IOPort} instance;
 * therefore we don't see a possible race condition.
 * 
 * Each {@link IOPort} is connected to one {@link IOService} instance; and the
 * connected instance is responsible for processing the data received through
 * his {@link IOPort}
 */
public class IOPort implements Runnable, ChannelFacade {
	private static final Logger LOGGER = LoggerFactory.getLogger(IOPort.class);

	private LocalSocket socket;

	private ChannelStream inStream;
	private ChannelStream outStream;

	private SelectionKey key = null;
	private volatile int interestOps = 0;
	private volatile int readyOps = 0;

	private volatile boolean shuttingDown;
	private volatile boolean dead;

	private final Object stateChangeLock = new Object();

	private IOPortConnector connector;

	/**
	 * {@link IOPortStateChangeListener} instance which receives signals
	 */
	private IOPortStateChangeListener stateChangeListener;

	/**
	 * Counter to track consequitive OP_READ signals for which 0 bytes were
	 * read; this is an indicator of selector Spin.
	 */
	private volatile byte zeroReadCounter;

	/**
	 * Flag to indicate if the previous read was a zero byte read
	 */
	private volatile boolean prevZeroRead;

	/**
	 * Counter to track consequitive zero bytes write opeartions on the socket
	 */
	private volatile byte zeroWriteCounter;

	public IOPort(LocalSocket socket, IOPortConnector connector, BufferFactory bufFactory,
			ChannelEventDispatcher dispacther) {
		this.socket = socket;
		this.connector = connector;
		this.stateChangeListener = dispacther;

		this.inStream = new ChannelStream(bufFactory);
		this.outStream = new ChannelStream(bufFactory);
	}

	@Override
	public void run() {
		try {
			int readBytes = readInboundBytes();

			if (readBytes > 0) {
				this.connector.bytesArrived(readBytes);

			} else if (readBytes == 0) {
				// If 0 bytes were read in previous read, increment the counter
				// by one; otherwise initialize it with 1. We could also have
				// always incremented the counter in this condition (readBytes
				// == 0); however in that case we must have reset it to 0 as
				// soon as we read non-zero bytes. Threfore, it's been
				// implemented in such a way that counter is not manipulated
				// until we ecnounter readBytes == 0 condition.
				if (prevZeroRead) {
					this.zeroReadCounter++;
				} else {
					this.zeroReadCounter = 1;
				}

				this.prevZeroRead = true;
				LOGGER.debug("ZeroBytesReadCounter {} for IOPort {}", this.zeroReadCounter, this);
			}

			// TODO: dharmu why we r not handaling readByte -1 senario

		} catch (Throwable e) {
			LOGGER.error("Caught exception while reading inbound bytes", e);
			// Exception does not mean we should die; it's too aggresive; we may
			// have to see which exception has been raised and we may need to
			// wait until next time before we really die. Can keep a counter to
			// track how many failures have been encountered.
			die();
		}

		try {
			drainOutput();
		} catch (Throwable e) {
			LOGGER.info("Caught exception whiling draining output on socket {}", this.socket);
			// Exception does not mean we should die; it's too aggresive; we may
			// have to see which exception has been raised and we may need to
			// wait until next time before we really die. Can keep a counter to
			// track how many failures have been encountered.
			die();
		}
	}

	@Override
	public byte[] readAllBytes() {
		return this.inStream.dequeueBytes();
	}

	@Override
	public byte[] readAllBytes(byte[] partial) {
		return this.inStream.dequeueBytes(partial);
	}

	@Override
	public void write(byte[] data) throws IOException {
		// TODO We should directly drain data on to channel followed by byte
		// writes received in this method. There is no point of enqueue data and
		// then drain it.
		fillOutStream(data);

		drain();
	}

	@Override
	public void write(Packet packet) throws IOException {
		try {
			packet.writeTo(this.outStream);

		} catch (IOException e) {
			// TODO Do some thing better. For now just logging it.
			LOGGER.warn("Failed to write packet to channel stream : {}", packet);
		}

		drain();
	}

	private void drain() throws IOException {
		try {

			drainNow();

			if (!this.outStream.isEmpty()) {
				enableWriteSelection();
			}

		} catch (Exception e) {
			LOGGER.warn("Exception caught whiling draining output on socket {}", this.socket, e);
			die();
			throw e;
		}
	}

	/**
	 * Read as much content as possible from the network socket and transfer it
	 * to {@link IOService}. If end-of-stream is reached, stop read selection
	 * and shutdown the input side of the channel
	 * 
	 * @throws IOException
	 */
	private int readInboundBytes() {
		if (this.shuttingDown || this.socket.isInputShutdown()) {
			return 0;
		}

		int rc = 0;

		try {
			rc = inStream.fillFrom(socket);
			LOGGER.trace("Read {} bytes from {}", rc, this.socket);

			if (rc == -1) {
				LOGGER.debug("Encountered end of stream; disabling read selection");
				disableReadSelection();

				if (this.socket.isConnected()) {
					try {
						this.socket.shutdownInput();

					} catch (SocketException e) {
						LOGGER.debug("Failed to shut down input on socket. Ignoring...");
					}
				}

				this.shuttingDown = true;

				// enable write selection so that output can be drained post
				// which the socket will be closed
				enableWriteSelection();
			}

		} catch (IOException e) {
			LOGGER.debug("Caught exception while reading from channel", e);
			die();
		}

		return rc;
	}

	void setKey(SelectionKey key) {
		this.key = key;
		this.interestOps = key.interestOps();
	}

	boolean isDead() {
		return dead;
	}

	SelectionKey key() {
		return key;
	}

	public int interestOps() {
		return interestOps;
	}

	/**
	 * Local modification to key sets. these modifications are later propagated
	 * to the key
	 */
	private void enableWriteSelection() {
		modifyInterestOps(SelectionKey.OP_WRITE, 0);
	}

	private void disableWriteSelection() {
		modifyInterestOps(0, SelectionKey.OP_WRITE);
	}

	private void disableReadSelection() {
		modifyInterestOps(0, SelectionKey.OP_READ);
	}

	public void modifyInterestOps(int opsToSet, int opsToReset) {
		synchronized (stateChangeLock) {
			interestOps = (interestOps | opsToSet) & (~opsToReset);

			if (!(dead || shuttingDown)) {
				stateChangeListener.signalStateChange(this);
			}
		}
	}

	/**
	 * Write as much data as we can from {@link ChannelStream}
	 * 
	 * @throws IOException
	 */
	private void drainOutput() {
		try {
			if (((this.readyOps & SelectionKey.OP_WRITE) != 0) && (!outStream.isEmpty())) {
				drainNow();
			}

			// Write selection is turned on when output data in enqueued,
			// turn it off when the queue becomes empty.
			if (outStream.isEmpty()) {
				disableWriteSelection();

				if (this.shuttingDown) {
					this.socket.shutdownOutput();
					die();
				}
			}
		} catch (IOException e) {
			die();
		}
	}

	/**
	 * Drains output {@link ChannelStream} bytes onto the channel. The method
	 * does not look at the selection key ready operations status assuming
	 * OP_WRITE is always enabled on channel
	 * 
	 * @throws IOException
	 */
	private void drainNow() throws IOException {
		int bytesWritten = this.outStream.drainTo(socket);

		if (bytesWritten == 0) {
			// This needs to be tracked for selector freez detection. Current
			// implementation has untidy write operations; first tide them up
			// and then track it.
			this.zeroWriteCounter++;
			LOGGER.debug("ZeroByteWriteCounter value is {} for IOPort {}", this.zeroWriteCounter, this);
		}
	}

	@Override
	public boolean isConnected() {
		return socket.isConnected() && !this.dead;
	}

	void prepareToRun() {
		synchronized (stateChangeLock) {
			interestOps = key.interestOps();
			readyOps = key.readyOps();
		}
	}

	public void fillOutStream(byte[] data) {
		this.outStream.enqueue(data);
	}

	/**
	 * This is internal method with default access level; meaning it can only be
	 * invoked by network layer. The method is invokoked when an IO error is
	 * encountered while data read/write from the underlying socket/channel. As
	 * the method is invoked from network layer, we signal the IO layer that
	 * channel has died so that user level states can be cleaned-up.
	 */
	void die() {
		if (!dead) {
			this.connector.signalIOPortClosed();
			close();
		}
	}

	@Override
	public synchronized void close() {
		try {
			if (!dead) {
				this.shuttingDown = true;
				this.dead = true;

				this.connector = null;

				inStream.returnBuffers();
				outStream.returnBuffers();

				this.socket.close();

				this.stateChangeListener.signalStateChange(this);

			} else {
				LOGGER.debug("IOPort {} is already closed", this);
			}

		} catch (Throwable e) {
			LOGGER.info("Exception caught. Probably failed to close socket", e);
		}

	}

	@Override
	public SocketAddress getRemoteAddress() throws IOException {
		return this.socket.getRemoteAddress();
	}

	@Override
	public void disableWrite() {
		this.disableWriteSelection();
	}

	@Override
	public String toString() {
		return "port-" + this.socket.toString();
	}

	@Override
	protected void finalize() throws Throwable {
		LOGGER.debug("Finalizing {}", this);
	}
}
