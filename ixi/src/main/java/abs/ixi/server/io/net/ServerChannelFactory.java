package abs.ixi.server.io.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketOption;
import java.nio.channels.ServerSocketChannel;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A factory to instantiate Server socket. For a server node, there will be just
 * one server socket created. 5222 is the XMPP recommended default port for XMPP
 * servers. The factory reads server configuration to retrieve server port.
 * 
 * @author Yogi
 *
 */
public class ServerChannelFactory {
	private static final Logger LOGGER = LoggerFactory.getLogger(ServerChannelFactory.class);

	/**
	 * Max port number is 65535
	 */
	private static final int MAX_PORT = 0xFFFF;

	public static NioServerChannel newServerChannel(int port, Map<SocketOption<?>, Object> options)
			throws SocketSetupFailureException {
		if (port <= 0 || port > MAX_PORT) {
			throw new IllegalArgumentException("Invalid server socket port " + port);
		}

		ChannelConfig config = new DefaultChannelConfig(options);

		try {
			ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

			if (serverSocketChannel.isOpen()) {
				// print the supported options on to console
				printSupportedSocketOptions(serverSocketChannel);

				setSocketOptions(serverSocketChannel, options);

				LOGGER.info("Binding server socket listener port {}", port);
				bindServerPort(port, serverSocketChannel);

				return new NioServerChannel(config, serverSocketChannel);

			} else {
				LOGGER.warn("Failed to open Server Socket Channel");
				throw new SocketSetupFailureException("Could not open Server Socket");
			}

		} catch (IOException e) {
			LOGGER.warn("Exception occurred while initiliazing connetion request listener");
			throw new SocketSetupFailureException("Could not initilize Server Socket", e);
		}
	}

	/**
	 * Bind server port
	 * 
	 * @param port
	 * @param serverSocketChannel
	 * @throws IOException
	 */
	private static void bindServerPort(int port, ServerSocketChannel serverSocketChannel) throws IOException {
		try {
			serverSocketChannel.bind(new InetSocketAddress(port));
		} catch (Exception e) {
			LOGGER.error("Could not bind port {}", port);
			throw e;
		}
	}

	/**
	 * Set server socket options
	 * 
	 * @param channel
	 * @param options
	 */
	@SuppressWarnings("unchecked")
	private static <T> void setSocketOptions(ServerSocketChannel channel, Map<SocketOption<?>, Object> options) {
		if (options != null && options.size() > 0) {
			for (Entry<SocketOption<?>, Object> oe : options.entrySet()) {
				try {
					setSocketOption(channel, (SocketOption<Object>) oe.getKey(), oe.getValue());
				} catch (IOException e) {
					LOGGER.warn("Could not set socket option {}", oe.getKey(), e);
				}
			}
		}
	}

	/**
	 * Set server socket option if underlying infrastructure supports it
	 * 
	 * @param channel
	 * @param option
	 * @param value
	 * @throws IOException
	 */
	protected static void setSocketOption(ServerSocketChannel channel, SocketOption<Object> option, Object value)
			throws IOException {
		if (channel.supportedOptions().contains(option)) {
			channel.setOption(option, value);
		} else {
			LOGGER.warn("Option {} is not supported", option);
		}
	}

	/**
	 * Prints all the supported socket options
	 */
	protected static void printSupportedSocketOptions(ServerSocketChannel channel) {
		for (SocketOption<?> option : channel.supportedOptions()) {
			LOGGER.debug("Server Socket Channel supports option : {}", option);
		}
	}

}
