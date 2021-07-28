package abs.ixi.server.io.net;

import static abs.ixi.server.io.IOController.instantiateIOService;
import static abs.ixi.server.io.net.ServerChannelFactory.newServerChannel;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketOption;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.AbstractServerListener;
import abs.ixi.server.common.InstantiationException;
import abs.ixi.server.etc.conf.Configurations;
import abs.ixi.server.etc.conf.ProcessConfigAware;

/**
 * Base class for all the connection managers in server. It offers basic
 * features such as setting up the server socket, accepting incoming
 * connections, creating local connections and registering them with the
 * {@link NetworkTransport}
 */
public abstract class ConnectionManager implements ProcessConfigAware {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionManager.class);

	/**
	 * Max value permiited for a server socket port
	 */
	public static final int MAX_PORT_VAL = 0xFFFF;

	// Default buffer size of socket receive buffer
	private static final int SO_RCV_BUFFER_SIZE = 2048;

	/**
	 * Default buffer size of socket send buffer
	 */
	private static final int SO_SND_BUFFER_SIZE = 2048;

	/**
	 * Default value of the flag to indicate socket address reuse
	 */
	private static final boolean DEFAULT_SO_REUSEADDR = true;

	/**
	 * Server socket instance for this connection manager.
	 */
	protected NioServerChannel serverChannel;

	/**
	 * {@link NetworkTransport} instance which accepts
	 * {@link SelectableChannel}s for network operations.
	 */
	protected NetworkEventDispatcher dispatcher;

	// Blocking queue for incoming connections
	protected BlockingQueue<SocketChannel> incomingConnQ;

	// entity which accepts incoming connection requests
	protected ConnectionAcceptor connAcceptor;

	// Listener thread for incoming tcp connection requests
	protected Thread connAcceptorThread;

	public ConnectionManager(NetworkEventDispatcher dispatcher, Configurations conf) throws InstantiationException {
		try {
			this.serverChannel = newServerChannel(getServerSocketPort(conf), getChannelOptions());
			this.connAcceptor = new ConnectionAcceptor();
			this.dispatcher = dispatcher;

		} catch (SocketSetupFailureException e) {
			throw new InstantiationException(e);
		}
	}

	public void start() throws Exception {
		this.connAcceptor.init();
		this.connAcceptor.start();
		this.connAcceptorThread = new Thread(connAcceptor);

		LOGGER.info("Starting connection acceptor thread");
		this.connAcceptorThread.start();
	}

	protected void registerWithEventDispatcher(SocketChannel channel, ByteStream streamType) {
		try {
			LOGGER.debug("Preparing connection object for :" + channel.getRemoteAddress());

			if (this.dispatcher != null) {
				IOPortConnector connector = new IOPortConnector(streamType);
				instantiateIOService(connector);
				this.dispatcher.registerChannel(channel, connector);
				connector.channelRegistered();

			} else {
				LOGGER.error("Could not retrieve Channel event dispatcher");
				LOGGER.error("Will stop receiving connection requests. Closing connection received");

				channel.close();
			}

		} catch (IOException e) {
			LOGGER.warn("seems like client socket has disconnected. Will close the channel");
			try {
				channel.close();

			} catch (IOException e1) {
				LOGGER.warn("failed to close channel. Ignoring...");
			}
		}
	}

	protected Map<SocketOption<?>, Object> getChannelOptions() {
		Map<SocketOption<?>, Object> map = new HashMap<>();

		map.put(StandardSocketOptions.SO_RCVBUF, SO_RCV_BUFFER_SIZE);
		map.put(StandardSocketOptions.SO_SNDBUF, SO_SND_BUFFER_SIZE);

		// Disabling Naggle's Algorithm
		map.put(StandardSocketOptions.TCP_NODELAY, true);

		// making socket is reusable
		map.put(StandardSocketOptions.SO_REUSEADDR, DEFAULT_SO_REUSEADDR);

		// Disable keep alive on the socket
		map.put(StandardSocketOptions.SO_KEEPALIVE, false);

		return map;
	}

	/**
	 * {@link ConnectionAcceptor} initializes {@link ServerSocket} with
	 * specified port. When the socket is ready, it listens to incoming tcp
	 * connections. Accepted connections are added to a blocking queue for
	 * further processing by Registrar thread
	 */
	public class ConnectionAcceptor extends AbstractServerListener implements Runnable {
		@Override
		public void start() throws Exception {
			super.start();
		}

		@Override
		public void run() {
			LOGGER.info("Waiting for connections...");

			while (!Thread.currentThread().isInterrupted() && listening) {
				try {
					SocketChannel ch = serverChannel.accept();
					LOGGER.trace("Received inbound connection {}", ch);
					registerWithEventDispatcher(ch, getByteStreamType());

				} catch (IOException e) {
					LOGGER.warn("Could not connect to client socket after accepting connection request", e);
				}
			}
		}
	}

	public void shutdown() throws Exception {
		LOGGER.info("Stopping Connection Acceptor");
		this.connAcceptor.stop();

		LOGGER.info("Closing server socket");
		this.serverChannel.close();
	}

	/**
	 * @return port to which server channel will be bound
	 */
	protected abstract int getServerSocketPort(Configurations conf);

	/**
	 * Get {@link ByteStream} type that sockets accepted by this connection
	 * manager will exchange.
	 * 
	 * @return
	 */
	protected abstract ByteStream getByteStreamType();

}
