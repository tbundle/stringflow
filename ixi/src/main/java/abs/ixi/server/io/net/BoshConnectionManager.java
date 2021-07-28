package abs.ixi.server.io.net;

import static abs.ixi.server.etc.conf.Configurations.Bundle.PROCESS;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.common.InstantiationException;
import abs.ixi.server.etc.conf.Configurations;

/**
 * An implementation of {@link ConnectionManager} which manages user connections
 * using BOSH protocol(XEP-0124 & XEP-0206). The connection manager sets up the
 * server socket and starts a {@link ConnectionAcceptor} which accepts incoming
 * connections and adds them into a blocking queue.
 */
public class BoshConnectionManager extends ConnectionManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(BoshConnectionManager.class);

	// Default BOSH server port
	private static final int DEFAULT_PORT = 5280;

	public BoshConnectionManager(NetworkEventDispatcher dispatcher, Configurations conf) throws InstantiationException {
		super(dispatcher, conf);
	}

	@Override
	public void start() throws Exception {
		LOGGER.debug("Starting BOSH Connection Manager");
		super.start();
	}

	@Override
	protected int getServerSocketPort(Configurations conf) {
		int port = conf.getOrDefaultInteger(_BOSH_CONNECTION_MANAGER_PORT, DEFAULT_PORT, PROCESS);

		if (port < 0 || port > MAX_PORT_VAL) {
			LOGGER.info("Defaulting XMPP port to to {}", DEFAULT_PORT);
			port = DEFAULT_PORT;
		}

		return port;
	}

	@Override
	protected ByteStream getByteStreamType() {
		return ByteStream.BOSH;
	}

	@Override
	public void shutdown() throws Exception {
		super.shutdown();
		LOGGER.info("Bosh Connection Manager has shutdown");
	}

}
