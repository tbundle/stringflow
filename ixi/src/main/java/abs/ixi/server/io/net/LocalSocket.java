package abs.ixi.server.io.net;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import abs.ixi.server.common.SafeCloseable;

/**
 * {@code LocalSocket} is a wrapper around {@link SocketChannel}. Most of the
 * network operations are delegated to underlying {@link SocketChannel}.
 * Additionally, it keeps track of inbound and outbound bytes. This may help us
 * in avoid DDos attacks.
 *
 * @author Yogi
 *
 */
public class LocalSocket implements SafeCloseable {
	private static long counter;

	private SocketChannel channel;

	private boolean inputShutdown;
	private boolean outputShutdown;

	private long inBytes;
	private long outBytes;

	public LocalSocket(SocketChannel channel) {
		this.channel = channel;

		synchronized (LocalSocket.class) {
			counter++;
		}
	}

	public int write(ByteBuffer src) throws IOException {
		int rb = channel.write(src);
		outBytes += rb;
		return rb;
	}

	public int write(String ch) throws Exception {
		ByteBuffer buffer = ByteBuffer.wrap(ch.getBytes());
		return write(buffer);
	}

	public int read(ByteBuffer buffer) throws IOException {
		int rc = this.channel.read(buffer);

		if (rc != -1) {
			this.inBytes += rc;
		}

		return rc;
	}

	/**
	 * Checks if the underlying socket is connected. It simply delegates to the
	 * underlying {@link SocketChannel}. A broken connection may not be detected
	 * here knowing the nature of TCP protocol. This is just an indicator of the
	 * state held by underlying {@link Socket}
	 * 
	 * For more information, please look at Java docs
	 */
	public boolean isConnected() {
		return this.channel.isConnected();
	}

	/**
	 * Shutsdown input on the underlying channel. After input is closed, bytes
	 * can not be read from this channel. This implementation is thread-safe.
	 * 
	 * @throws IOException
	 */
	public synchronized void shutdownInput() throws IOException {
		this.inputShutdown = true;
		this.channel.shutdownInput();
	}

	public synchronized void shutdownOutput() throws IOException {
		this.outputShutdown = true;
		this.channel.shutdownInput();
	}

	public SelectionKey getKey(Selector selctor) {
		return this.channel.keyFor(selctor);
	}

	public SocketAddress getRemoteAddress() throws IOException {
		return this.channel.getRemoteAddress();
	}

	public long getInbounBytesCount() {
		return this.inBytes;
	}

	public long getOutboundByteCount() {
		return outBytes;
	}

	public boolean isInputShutdown() {
		return inputShutdown;
	}

	public boolean isOutputShutdown() {
		return outputShutdown;
	}

	@Override
	public void close() {
		LOGGER.debug("Closing {}", this);
		safeClose(channel);
	}

	/**
	 * @return current value of stattically stored socket counter. The counter
	 *         indicates number os {@link LocalSocket} instantiations since the
	 *         server started
	 */
	public static long socketCounter() {
		return counter;
	}

}
