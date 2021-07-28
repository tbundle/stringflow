package abs.ixi.server.io.net;

import static abs.ixi.server.etc.conf.Configurations.Bundle.PROCESS;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.BasicComponent;
import abs.ixi.server.CoreComponent;
import abs.ixi.server.common.BufferFactory;
import abs.ixi.server.common.InstantiationException;
import abs.ixi.server.etc.conf.Configurations;

/**
 * {code NetworkTransport} is responsible for managing network input/ouput. It's
 * the governing entity of all the network sockets and IO on them.
 * 
 * @author Yogi
 *
 */
public final class NetworkTransport extends BasicComponent implements CoreComponent, NetworkEventDispatcher {
	private static final Logger LOGGER = LoggerFactory.getLogger(NetworkTransport.class);

	/**
	 * Default name of this server component.
	 */
	private static final String NAME = "netTransport";

	/**
	 * Default value of core pool size of the executor used for channel event
	 * processing inside {@link ChannelEventDispatcher}
	 */
	private static final int CORE_POOL_SIZE = 3;

	/**
	 * Default value of max pool size of the executor used for channel event
	 * processing inside {@link ChannelEventDispatcher}
	 */
	private static final int MAX_POOL_SIZE = 5;

	/**
	 * Default keep alive time for workers of the executor used for channel
	 * event processing inside {@link ChannelEventDispatcher}
	 */
	private static final int KEEP_ALLIVE_TIME_MINUTES = 5;

	/**
	 * All the running instances of Connection Managers
	 */
	private List<ConnectionManager> connectionManagers;

	/**
	 * {@link BufferFactory} instance
	 */
	private BufferFactory bufFactory;

	/**
	 * {@link ChannelEventDispatcher} instance
	 */
	private ChannelEventDispatcher dispatcher;

	/**
	 * {@link ChannelEventDispatcher} thread
	 */
	private Thread dispatcherThread;

	public NetworkTransport(Configurations conf) throws InstantiationException {
		super(NAME, conf);

		int cps = conf.getOrDefaultInteger(_NET_TRANSPORT_CORE_THREDAPOOL_SIZE, CORE_POOL_SIZE, PROCESS);
		if (cps < 0) {
			LOGGER.warn("Invalid NetTransport core threadpool size;defaulting to {}", CORE_POOL_SIZE);
			cps = CORE_POOL_SIZE;
		}

		int mps = conf.getOrDefaultInteger(_NET_TRANSPORT_MAX_THREADPOOL_SIZE, MAX_POOL_SIZE, PROCESS);
		if (mps < 0) {
			LOGGER.warn("Invalid NetTransport core threadpool size;defaulting to {}", MAX_POOL_SIZE);
			mps = MAX_POOL_SIZE;
		}

		int keepAliveMins = conf.getOrDefaultInteger(_NET_TRANSPORT_THREADPOOL_KEEPALIVE_MINS, KEEP_ALLIVE_TIME_MINUTES,
				PROCESS);
		if (keepAliveMins < 0) {
			LOGGER.info("Invalid NetTransport threadpool keepAlive mins;defaulting to {}", KEEP_ALLIVE_TIME_MINUTES);
			keepAliveMins = KEEP_ALLIVE_TIME_MINUTES;
		}

		this.dispatcher = new ChannelEventDispatcher(cps, mps, Duration.ofMinutes(keepAliveMins));
		this.dispatcherThread = new Thread(this.dispatcher);

		this.connectionManagers = new ArrayList<ConnectionManager>();

		if (conf.getBoolean(_XMPP_CONNECTION_MANAGER_ACTIVE, PROCESS)) {
			this.connectionManagers.add(new XMPPConnectionManager(this, conf));
			LOGGER.info("Instantiated XMPP Connection manager");
		}

		if (conf.getBoolean(_BOSH_CONNECTION_MANAGER_ACTIVE, PROCESS)) {
			this.connectionManagers.add(new BoshConnectionManager(this, conf));
			LOGGER.info("Instantiated BOSH connection manager");
		}

		// JmxRegistrar.registerBeanSilently("abs.ixi.server.jmx:type=ServerIOJmxBean",
		// new NetworkTransportJmxBean(this));
	}

	/**
	 * Instantiate buffer factory
	 * 
	 * @throws InstantiationException
	 */
	private void instantiateBufferFactory() throws InstantiationException {
		LOGGER.debug("Acquiring buffer factory instance");
		this.bufFactory = CachedBufferFactory.getInstance();
	}

	/**
	 * This is not thread-safe; however by design, there has to be just one
	 * instance running in the server at a time
	 * 
	 * @throws Exception
	 */
	@Override
	public void start() throws Exception {
		super.start();

		this.instantiateBufferFactory();

		LOGGER.info("Starting connection managers");
		for (ConnectionManager cm : this.connectionManagers) {
			cm.start();
			LOGGER.info("Started {}", cm);
		}

		LOGGER.info("Starting channel event dispacther thread");
		dispatcherThread.start();
	}

	@Override
	public void registerChannel(SelectableChannel ch, IOPortConnector connector) throws IOException {
		LOGGER.info("Registering channel with selector");
		ch.configureBlocking(false);

		IOPort ioPort = new IOPort(new LocalSocket((SocketChannel) ch), connector, this.bufFactory, this.dispatcher);
		connector.attachIOPort(ioPort);

		this.dispatcher.registerChannel(ch, ioPort);
		this.dispatcher.signalStateChange(ioPort);

		LOGGER.debug("Registered channel with selector");
	}

	@Override
	public ServerFunction getServerFunction() {
		return ServerFunction.NETWORK_TRANSPORT;
	}

	@Override
	public void shutdown() throws Exception {
		LOGGER.info("shutting down {} component", getName());
		this.dispatcher.shutdown();
		super.shutdown();
	}

}
