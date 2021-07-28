package abs.ixi.server.io.net;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientChannel implements IOChannel {
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientChannel.class);

	/**
	 * {@link ChannelConfig} instance. It's static as all the client channel
	 * instances share the same config.
	 */
	private static ChannelConfig config;

	static {
		config = new DefaultChannelConfig();
	}

	/**
	 * Underlying {@link SelectableChannel} insatance
	 */
	private SocketChannel channel;

	@Override
	public SocketAddress localAddress() {
		try {
			return this.channel.getLocalAddress();
		} catch (IOException e) {
			LOGGER.info("Failed to get local address for {}", this);
		}

		return null;
	}

	@Override
	public SocketAddress remoteAddress() {
		try {
			return this.channel.getRemoteAddress();
		} catch (IOException e) {
			LOGGER.info("Failed to get remote address for {}", this);
		}

		return null;
	}

	@Override
	public void printConfig() {
		LOGGER.info("ServerChannel Config:", config);
	}

	@Override
	public void close() {
		try {
			this.close();
		} catch (Exception e) {
			LOGGER.info("Failed to close client channel {} : {}", this, e.getMessage());
		}
	}

}
