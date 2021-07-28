package abs.ixi.server.io.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.io.ChannelFacade;
import abs.ixi.server.io.IOSignalReceiver;
import abs.ixi.server.io.multipart.ByteStreamer;
import abs.ixi.server.io.multipart.Streamable;
import abs.ixi.server.packet.Packet;

public class IOPortConnector {
	private static final Logger LOGGER = LoggerFactory.getLogger(IOPortConnector.class);

	/**
	 * It's layer-7 byte stream
	 */
	private ByteStream byteStream;

	/**
	 * A socket channel facade instance which is connected to this connection
	 */
	private ChannelFacade channelFacade;

	/**
	 * A IO signal receiver for this connection
	 */
	private IOSignalReceiver ioSignalReceiver;

	/**
	 * Time when this connection was started; we capture the time when this
	 * connection object was instantiated
	 */
	private Calendar startTime;

	public IOPortConnector(ByteStream streamType) {
		this.byteStream = streamType;
		this.lastActivityTime = System.currentTimeMillis();
		this.startTime = Calendar.getInstance();
	}

	/**
	 * Time of most recent activity on this connection; any inbound/outbound
	 * byte transmission is an activity on the connection
	 */
	protected long lastActivityTime;

	public long getLastActivityTime() {
		return this.lastActivityTime;
	}

	public Calendar getStartTime() {
		return this.startTime;
	}

	public ByteStream getByteStream() {
		return byteStream;
	}

	public void attachIOPort(IOPort ioPort) {
		this.channelFacade = ioPort;
	}

	public void attachIOSignalReceiver(IOSignalReceiver ioSignalReceiver) {
		this.ioSignalReceiver = ioSignalReceiver;
	}

	public boolean isConnected() {
		return channelFacade.isConnected();
	}

	public InetSocketAddress getRemoteAddress() throws IOException {
		return (InetSocketAddress) this.channelFacade.getRemoteAddress();
	}

	public byte[] readAllBytes() {
		return this.channelFacade.readAllBytes();
	}

	public byte[] readAllBytes(byte[] unprocessedBytes) {
		return this.channelFacade.readAllBytes(unprocessedBytes);
	}

	public void write(byte[] bytes) throws IOException {
		this.channelFacade.write(bytes);
		this.lastActivityTime = System.currentTimeMillis();
	}

	public void write(Packet packet) throws IOException {
		this.channelFacade.write(packet);
		this.lastActivityTime = System.currentTimeMillis();
	}

	/**
	 * Write a packet on to the channel. The method also accepts a boolean flag
	 * indicating if the packet should be streamed. The boolean flag is just a
	 * hint; the object must be {@link Streamable} for {@link IOPortConnector}
	 * to stream it.
	 * 
	 * @param packet
	 * @param stream
	 * @throws IOException
	 */
	public void write(Packet packet, boolean stream) throws IOException {
		if (stream && packet instanceof Streamable) {
			ByteStreamer.getInstance().stream((Streamable) packet, this.channelFacade);
			this.channelFacade.write(packet);
			this.lastActivityTime = System.currentTimeMillis();

		} else {
			write(packet);
		}
	}

	public void bytesArrived(int readBytes) {
		this.ioSignalReceiver.bytesRead(readBytes);
	}

	/**
	 * Network layers signals that channel is dead. And this signal propagated
	 * to IO layer. IO layer manage itself according itself.
	 */
	public void signalIOPortClosed() {
		ioSignalReceiver.channelDead();
	}

	public void channelRegistered() {
		ioSignalReceiver.channelRegistered();
	}

	/**
	 * IO layers instruct to network layer to disconnect connection by calling
	 * this method.
	 */
	public void close() {
		channelFacade.close();
		LOGGER.error("Not implemented");
	}

	@Override
	protected void finalize() throws Throwable {
		LOGGER.debug("Finalizing {}", this);
	}

}
