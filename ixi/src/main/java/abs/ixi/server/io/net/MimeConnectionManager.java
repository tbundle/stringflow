package abs.ixi.server.io.net;

import static abs.ixi.server.etc.conf.Configurations.Bundle.PROCESS;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.common.InstantiationException;
import abs.ixi.server.etc.conf.Configurations;

/**
 * An implementation of {@link ConnectionManager} to accept/register connections
 * with MIME transport. Unless overrriden explicitly, MIME tranport is used for
 * file transport only in server.
 * 
 * <p>
 * XMPP is not a suitable protocol for large file transfer; XMPP is designed to
 * handle small XMLs having small sized user conversation data. Threfore,
 * standard protocols such as HTTP, SOCKS5 are plugged on to XMPP to support
 * file transfer. Stringflow has a proprietry content model to transfer files
 * which uses MIME as mechanism to transport files.
 * </p>
 * 
 * <p>
 * MIME connections do not support MIME as stated in MIME RFC 2045/2046; instead
 * it extracts the simplest format out of it. The support for MIME is very
 * similar to what HTTP protocol has adopted. We do not support nested body
 * parts.
 * 
 * <pre>
 * {@code
 * MIME-Version 1.0
 * Content-Type: text;boundary=boundary-string
 * 
 * --boundary-string
 * Content-Type:text
 * 
 * this is body of the message
 * --boundary-string
 * Content-Type:text;Encoding:Base64
 * 
 * --boundary-string--
 * }
 * </pre>
 * 
 * Morever, we do not process all the mime headers; there is a finite list of
 * headers that we support. However, unsupported headers in MIME message will
 * not result in message transport failure.
 * </p>
 * 
 * <p>
 * Stringflow supports many technologies such as SOCKS5 bytestream for file
 * transport; Every transport mechanism has its pros and cons. Therefore, one
 * shoud carefully evaluate the environment conditions to decide the optimal
 * transport mechanism.
 * </p>
 * 
 * @author Yogi
 *
 */
public class MimeConnectionManager extends ConnectionManager {
	public static final Logger LOGGER = LoggerFactory.getLogger(MimeConnectionManager.class);

	/**
	 * Default port on which {@link MimeConnectionManager} listens for incoming
	 * connections.
	 */
	// TODO Ideally defaulting logic should be part of Server Configuration
	// management. Here in this class, we should directly get the port number at
	// which server socket to be setup.
	private static final int DEFAULT_PORT = 5234;

	public MimeConnectionManager(NetworkEventDispatcher dispatcher, Configurations conf) throws InstantiationException {
		super(dispatcher, conf);

		// TODO Register jmx bean for this connection manager
	}

	@Override
	public void start() throws Exception {
		super.start();
		LOGGER.info("Started MIME Connection Manager");
	}

	@Override
	protected int getServerSocketPort(Configurations conf) {
		int port = conf.getOrDefaultInteger(_MIME_CONNECTION_MANAGER_PORT, DEFAULT_PORT, PROCESS);

		if (port < 0 || port > MAX_PORT_VAL) {
			LOGGER.info("Defaulting XMPP port to to {}", DEFAULT_PORT);
			port = DEFAULT_PORT;
		}

		return port;
	}

	@Override
	protected ByteStream getByteStreamType() {
		return ByteStream.MIME;
	}

	@Override
	public void shutdown() throws Exception {
		LOGGER.info("Shutting down MimeConnectionManager");
		super.shutdown();
	}

}
