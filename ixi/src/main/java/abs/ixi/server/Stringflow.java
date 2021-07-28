package abs.ixi.server;

import static abs.ixi.server.etc.conf.Configurations.Bundle.SYSTEM;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.CoreComponent.ServerFunction;
import abs.ixi.server.etc.conf.ConfigurationException;
import abs.ixi.server.etc.conf.Configurations;
import abs.ixi.server.etc.conf.ServerConfigurations;
import abs.ixi.server.io.InputHandler;
import abs.ixi.server.packet.InvalidJabberId;
import abs.ixi.server.packet.JID;
import abs.ixi.server.router.Router;
import abs.ixi.server.sys.Bootstrap;
import abs.ixi.server.sys.RuntimeUnavailableException;
import abs.ixi.util.StringUtils;

/**
 * Stringflow process class.
 * 
 * @author Yogi
 *
 */
public final class Stringflow implements ServerRuntime {
	private static final Logger LOGGER = LoggerFactory.getLogger(Stringflow.class);

	/**
	 * Server Node
	 */
	private String node;

	/**
	 * Domain in which current server instance is running
	 */
	private String domain;

	/**
	 * Cluster {@link JID}
	 */
	private JID jid;

	/**
	 * Server Configuration manager
	 */
	private ServerConfigurations configurations;

	/**
	 * Cache to hold loaded server components
	 */
	private Map<String, ServerComponent> components;

	/**
	 * Flag to indicate if the server bootstrap has completed
	 */
	private boolean bootstraped = false;

	/**
	 * Server instance
	 */
	private static Stringflow instance;

	/**
	 * Private constructor
	 */
	private Stringflow() {
		instance = this;
	}

	/**
	 * Get {@link ServerRuntime} instance. Server runtime is not available for
	 * access until server bootstrap process is complete.
	 * 
	 * @return
	 */
	public static ServerRuntime runtime() {
		if (instance.bootstraped) {
			return instance;
		}

		LOGGER.error("Bootstrap process is not complete; server runtime is unavailable");
		throw new RuntimeUnavailableException();
	}

	public static void main(String[] args) throws IOException {
		LOGGER.info("Starting Stringflow process...");

		try {
			Stringflow sf = new Stringflow();

			sf.registerShutdownHook();
			sf.loadServerConfigurations();
			sf.setProcessIdentifiers();
			sf.bootstrapServer();
			sf.bootstraped = true;
			sf.printLoadedCoreComponents();
			sf.startComponents();

			LOGGER.info("**************** Server has started ****************");

		} catch (Throwable t) {
			LOGGER.error("Server startup failed", t);
			System.exit(1);
		}
	}

	/**
	 * Start all the loaded server components. The method fails-fast (the method
	 * quits as soon as a component failed to start).
	 * 
	 * @throws Exception
	 */
	private void startComponents() throws Exception {
		for (ServerComponent c : this.components.values()) {
			try {
				c.start();
			} catch (Exception e) {
				LOGGER.error("Failed to start component {}", c);
				throw e;
			}
		}
	}

	/**
	 * Set process identifiers such as domain name and node id.
	 * 
	 * @throws ConfigurationException
	 */
	private void setProcessIdentifiers() throws ConfigurationException {
		String jidString = this.configurations.get(SYS_CLUSTER_JID, SYSTEM);

		if (StringUtils.isNullOrEmpty(jidString)) {
			LOGGER.error("server cluster jid is missing");
			throw new ConfigurationException("cluster jid can't be null");
		}

		try {
			JID jid = new JID(jidString);
			this.domain = jid.getDomain();
			this.node = jid.getNode();

			// TODO: validate domain and node Strings. domain must have correct
			// formatting and node needs to be validated for valid String; look
			// for the protocol whch enforces String rules.

		} catch (InvalidJabberId e) {
			LOGGER.error("Cluster JID is invalid");
			throw new ConfigurationException(e);
		}

		LOGGER.info("Server has been registered in domain {}", this.domain);
	}

	/**
	 * Load all the server configurations.
	 * 
	 * @throws ConfigurationException
	 */
	private void loadServerConfigurations() throws ConfigurationException {
		LOGGER.info("Loading server configurations");

		this.configurations = new ServerConfigurations();
		this.configurations.loadServerConfig();
	}

	/**
	 * Initiate server bootstrap process
	 * 
	 * @throws Exception
	 */
	private void bootstrapServer() throws Exception {
		LOGGER.debug("Bootstraping server with following configurations:");

		this.configurations.printConfigs();

		Bootstrap bootstrapHoook = new Bootstrap(this.configurations);
		this.components = bootstrapHoook.initiate();

		if (this.components == null || this.components.size() == 0) {
			throw new ServerStartupException("None of the server components were loaded");
		}
	}

	/**
	 * Register shutdown hook
	 */
	private void registerShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new ServerShutdownHook());
	}

	/**
	 * @return server configurations
	 */
	public Configurations configurations() {
		return this.configurations;
	}

	@Override
	public String node() {
		return this.node;
	}

	@Override
	public String domain() {
		return this.domain;
	}

	/**
	 * {@link JID} represents the clsuter instead of a node in the server.
	 */
	@Override
	public JID jid() {
		if (this.jid == null) {
			this.jid = new JID(this.node, this.domain);
		}

		return this.jid;
	}

	@Override
	public String getCoreComponentJid(ServerFunction function) {
		for (ServerComponent comp : this.components.values()) {
			if (CoreComponent.class.isAssignableFrom(comp.getClass())
					&& (function == ((CoreComponent) comp).getServerFunction())) {
				return comp.getJID().toString();
			}
		}

		return null;
	}

	@Override
	public void printLoadedCoreComponents() {
		LOGGER.info("=================== Loaded Server Components ====================");

		for (Entry<String, ServerComponent> map : components.entrySet()) {
			LOGGER.info("{} : {}", map.getKey(), map.getValue());
		}

		LOGGER.info("================================================================");
	}

	@Override
	public InputHandler getInputHandler() {
		for (ServerComponent comp : this.components.values()) {
			if (InputHandler.class.isAssignableFrom(comp.getClass())
					&& (ServerFunction.IO_CONTROL == ((CoreComponent) comp).getServerFunction())) {
				return (InputHandler) comp;
			}
		}

		return null;
	}

	@Override
	public Router getRouter() {
		for (ServerComponent comp : this.components.values()) {
			if (Router.class.isAssignableFrom(comp.getClass())
					&& (ServerFunction.PACKET_ROUTING == ((CoreComponent) comp).getServerFunction())) {
				return (Router) comp;
			}
		}

		return null;
	}

	public void shutdown(int code) {
		LOGGER.info("Shutting down server with exit code {}", code);
		System.exit(code);
	}

	public class ServerShutdownHook extends Thread {
		@Override
		public void run() {
			LOGGER.info("************* Executing Server ShutdownHook ***************");
		}
	}

}
