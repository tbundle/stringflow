package abs.ixi.server.io.net;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An IO Channel to accept TCP/IP connections. Its a wrapper around
 * {@link ServerSocketChannel}
 * 
 * @author Yogi
 *
 */
public class NioServerChannel extends AbstractServerChannel {
	private static final Logger LOGGER = LoggerFactory.getLogger(NioServerChannel.class);

	public NioServerChannel(ChannelConfig config, ServerSocketChannel channel) {
		super(config);
		this.channel = channel;
	}

	/**
	 * @return {@link SocketAddress} instance for the local adress of the socket
	 *         associated with this Channel. Will return null if socket is not
	 *         bound a local address.
	 */
	@Override
	public SocketAddress localAddress() {
		try {
			return this.channel.getLocalAddress();
		} catch (Throwable e) {
			LOGGER.error("Failed to retrieve socket local address :" + e.getMessage());
		}

		return null;
	}

	/**
	 * @return Null as there is no remote address bound to a server channel
	 */
	@Override
	public SocketAddress remoteAddress() {
		return null;
	}

	@Override
	public void printConfig() {
		LOGGER.info("ServerChannel Config:", this.config);
	}

	protected void printSupportedOptions() {
		requireNonNull(this.channel, "Channel instance can not be null");

		for (SocketOption<?> option : this.channel.supportedOptions()) {
			LOGGER.debug("Server Socket Channel supported options : {}", option);
		}
	}

	@Override
	public SocketChannel accept() throws IOException {
		return this.channel.accept();
	}

	@Override
	public void close() {
		try {
			this.channel.close();
		} catch (IOException e) {
			LOGGER.debug("Failed to close {} :", this, e.getMessage());
		}
	}

	@Override
	public String toString() {
		return "ServerChannel[" + this.localAddress() + "]";
	}

}
