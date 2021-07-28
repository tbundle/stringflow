package abs.ixi.server.io.net;

import static abs.ixi.server.etc.conf.Configurations.Bundle.PROCESS;

import java.net.SocketOption;
import java.net.StandardSocketOptions;
import java.util.Map;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.common.InstantiationException;
import abs.ixi.server.etc.conf.Configurations;
import abs.ixi.server.sys.monitor.JmxRegistrar;
import abs.ixi.server.sys.monitor.XMPPConnMgrJmxBean;

/**
 * {@link XMPPConnectionManager} manages incoming connections to the server.
 * {link ConnectionAcceptor} accepts the incoming TCP connection requests.
 * Accepted connections are added to a queue which has registrar thread as its
 * consumer. Registrar picks up local bound socket channels and registers them
 * with {@link ChannelEventDispatcher}
 */
public class XMPPConnectionManager extends ConnectionManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(XMPPConnectionManager.class);

	// Default XMPP server port as mentioned in RFC-6120
	private static final int DEFAULT_PORT = 5222;

	// Re-use the socket address
	private static final boolean DEFAULT_SO_REUSEADDR = true;

	// Default buffer size of socket receive buffer
	private static final int SO_RCV_BUFFER_SIZE = 4096;

	/**
	 * Default buffer size of socket send buffer
	 */
	private static final int SO_SND_BUFFER_SIZE = 4096;

	public XMPPConnectionManager(NetworkEventDispatcher dispatcher, Configurations conf) throws InstantiationException {
		super(dispatcher, conf);

		try {
			JmxRegistrar.registerBean("abs.ixi.server.jmx:type=XMPPConnMgrJmxBean",
					new XMPPConnMgrJmxBean(this, this.incomingConnQ));
		} catch (MalformedObjectNameException | InstanceAlreadyExistsException | MBeanRegistrationException
				| NotCompliantMBeanException e) {
			LOGGER.error("Failed to register jmx bean for xmpp connection manager", e);
		}
	}

	@Override
	public void start() throws Exception {
		LOGGER.info("Starting XMPP Connection Manager");
		super.start();
	}

	@Override
	protected Map<SocketOption<?>, Object> getChannelOptions() {
		Map<SocketOption<?>, Object> map = super.getChannelOptions();

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

	@Override
	protected int getServerSocketPort(Configurations conf) {
		int port = conf.getOrDefaultInteger(_XMPP_CONNECTION_MANAGER_PORT, DEFAULT_PORT, PROCESS);

		if (port < 0 || port > MAX_PORT_VAL) {
			LOGGER.info("Defaulting XMPP port to to {}", DEFAULT_PORT);
			port = DEFAULT_PORT;
		}

		return port;
	}

	@Override
	protected ByteStream getByteStreamType() {
		return ByteStream.XMPP;
	}

	@Override
	public void shutdown() throws Exception {
		super.shutdown();
		LOGGER.info("Has shutdown XMPPConnection Manager");
	}

}
