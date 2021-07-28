package abs.ixi.server.sys.admin;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.ServerComponent;
import abs.ixi.server.Stringflow;
import abs.ixi.server.common.Initializable;
import abs.ixi.server.common.InitializationException;
import abs.ixi.server.io.net.SocketListener;

/**
 * A {@link SocketListener} implementation which listens on server control port
 * for external control commands.
 * <p>
 * Server control port can receive a set of command to both administrate server
 * and also to compile server state information. For example, server can be
 * queried for a list of loaded components, to print runtime state of a
 * particular {@link ServerComponent} etc.
 * </p>
 * <p>
 * Server control port is accessible from localhost only; the restricted access
 * is to secure server from hijacking it from a remote machine. This means that
 * server admin scripts can connect to server control port from same host where
 * server is running.
 * </p>
 * <p>
 * The server socket created to monitor server control port has a backlog size
 * of 1 as we don't expect multiple connections to server originating from same
 * machine for administartion. This may limit the use of server control port in
 * certain cases; however at this point concurrent processing of multiple
 * control commands from multiple connections may result in unpredictable server
 * behaviour.
 * </p>
 * 
 * @author Yogi
 *
 */
public class AdminCommandListener implements SocketListener, Runnable, Initializable {
	private static final Logger LOGGER = LoggerFactory.getLogger(AdminCommandListener.class);

	private static final String SHOW_COMMAND_KEY_LOADED_COMPONENT = "-loadedcomponents";

	/**
	 * Port at which listerner will listen for control commands. Control port
	 * value can be changed in server configurations.
	 */
	private int port;

	/**
	 * {@link ServerSocket} instance for {@code ControlCommandListener}
	 */
	private ServerSocket controlSocket;

	/**
	 * Flag to indicate if {@code ControlSocketListener} insatnce has been
	 * initialized
	 */
	private volatile boolean initialized = false;

	/**
	 * Flag to indicate if listener is listening currently; and also to control
	 * the listening process from outside.
	 */
	private volatile boolean listening = false;

	/**
	 * Thread instance for this listener instance
	 */
	private Thread listenerThread;

	public AdminCommandListener(int port) {
		this.port = port;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() throws InitializationException {
		LOGGER.info("Initializing Server Control Listener");

		try {
			// loopback address to restrict remote access of the control
			// port; we may require to inspect origin header in TCP to block
			// Javascript making a connection on localhost.
			InetAddress address = InetAddress.getByName(null);

			// the backlog on the server socket has been kept 1 to avoid
			// multiple administrative access at the same time;
			this.controlSocket = new ServerSocket(this.port, 1, address);

			LOGGER.debug("Created server socket on control port {}", this.port);

			if (this.controlSocket.isBound()) {
				LOGGER.info("Control listener socket bounded sucessfully");
				this.initialized = true;
			} else {
				LOGGER.error("Failed to bound control socket; Control Command Listener could not be initialized");
				this.controlSocket.close();
			}
		} catch (IOException e) {
			throw new InitializationException("Failed to initialize AdminCommandListener", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isInitialized() {
		return this.initialized;
	}

	@Override
	public void start() throws Exception {
		this.listenerThread = new Thread(this);
		this.listening = true;

		this.listenerThread.start();
		LOGGER.info("Started Admin command listener thread");
	}

	@Override
	public void run() {
		if (!this.initialized) {
			LOGGER.error("Control command listener startup failed; it was not initialized properly");
			return;
		}

		while (this.listening && !Thread.interrupted()) {

			try (Socket clientSocket = this.controlSocket.accept()) {
				LOGGER.debug("Accepted a connection on server control port");

				ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());

				LOGGER.debug("instance of OOS is created");

				AdminCommand ac = null;

				while ((ac = (AdminCommand) ois.readObject()) != null) {
					switch (ac.name()) {
					case START_SERVER:
						handleStartServer(ac);
						break;
					case STOP_SERVER:
						handleStopServer(ac);
						break;
					case SHOW:
						handleShowCommand(ac);
					}
				}

			} catch (IOException e) {
				// Ignoring; assuming socket is closed by client

			} catch (ClassNotFoundException e) {
				LOGGER.error("Unexpected error occured", e);
			}
		}
	}

	/**
	 * Show command is a general {@link AdminCommand} which allows administrator
	 * to server runtime state. With the help of arguments, the command can show
	 * runtime state of a {@link ServerComponent} or any other entity in server.
	 * 
	 * @param ac
	 */
	private void handleShowCommand(AdminCommand ac) {
		System.out.println("Executing admin show command");
		Map<String, String> args = ac.args();

		if (args.containsKey(SHOW_COMMAND_KEY_LOADED_COMPONENT)) {
			showAllLoadedComponents();

		} else {
			System.out.println("No valid arguments found");
		}

	}

	private void showAllLoadedComponents() {
		Stringflow.runtime().printLoadedCoreComponents();
	}

	private void handleStopServer(AdminCommand ac) {
		System.out.println("Invoking server shutdown hook");
		Stringflow.runtime().shutdown(0);
	}

	private void handleStartServer(AdminCommand ac) {
		System.out.println("Not implemeneted currently");
	}

	@Override
	public void stop() throws Exception {
		listening = false;
	}

	public int getPort() {
		return port;
	}

}
