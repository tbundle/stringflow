package abs.ixi.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.common.InstantiationException;
import abs.ixi.server.etc.conf.Configurations;
import abs.ixi.server.packet.JID;

/**
 * This is a basic implementation of a {@link ServerComponent} It defines basic
 * policies and properties required to function {@link ServerComponent}
 * 
 * In future, it would dictate statistics generation, admin control,
 * Initialization sequence as well as shutdown sequence
 */
/**
 * Default implementation of {@link ServerComponent} interface.
 * 
 * @author Yogi
 *
 */
public abstract class BasicComponent implements ServerComponent {
	private static final Logger LOGGER = LoggerFactory.getLogger(BasicComponent.class);

	/**
	 * Name of this server component
	 */
	protected String name;

	/**
	 * Unique {@link JID} of this component; {@link JID} must be unique for each
	 * of the server component loaded.
	 */
	protected JID jid;

	/**
	 * Flag to indicate if this component has received command to stop.
	 */
	protected volatile boolean stopping = false;

	/**
	 * Flag to indicate if this component has stopped running.
	 */
	protected volatile boolean stopped = false;

	public BasicComponent(String name, Configurations conf) throws InstantiationException {
		this.name = name;
	}

	@Override
	public void init() {
		LOGGER.info("Initializing {} server component", this.name);
	}

	@Override
	public void start() throws Exception {
		LOGGER.info("Starting {} server component", this.name);
		this.jid = new JID(this.name, Stringflow.runtime().domain());
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public JID getJID() {
		return this.jid;
	}

	@Override
	public void shutdown() throws Exception {
		this.stopping = true;
	}

}
