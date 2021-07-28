package abs.ixi.server.sys.admin;

import static abs.ixi.server.etc.conf.Configurations.Bundle.PROCESS;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.BasicComponent;
import abs.ixi.server.ServerComponent;
import abs.ixi.server.Stringflow;
import abs.ixi.server.common.InitializationException;
import abs.ixi.server.common.InstantiationException;
import abs.ixi.server.etc.conf.Configurations;
import abs.ixi.util.ObjectUtils;

/**
 * {@link ServerComponent} which listens for control commands
 */
public class ServerControl extends BasicComponent {
	private static final Logger LOGGER = LoggerFactory.getLogger(ServerControl.class);

	public static final String COMPONENT_NAME = "server-control";

	/**
	 * Default port on which server listens for control commands
	 */
	private static final int DEFAULT_CONTROL_PORT = 9001;

	private int controlPort;

	private AdminCommandListener adminCommandListener;

	public ServerControl(Configurations config) throws InstantiationException {
		super(COMPONENT_NAME, config);

		String s = Stringflow.runtime().configurations().get(_SERVER_CONTROL_PORT, PROCESS);
		this.controlPort = ObjectUtils.parseToInt(s);

		if (this.controlPort == 0) {
			LOGGER.warn("Defaulting server control port to {}", DEFAULT_CONTROL_PORT);
			this.controlPort = DEFAULT_CONTROL_PORT;
		}

		try {
			this.adminCommandListener = new AdminCommandListener(this.controlPort);
			this.adminCommandListener.init();
		} catch (InitializationException e) {
			throw new InstantiationException(e);
		}
	}

	@Override
	public void start() throws Exception {
		LOGGER.info("Starting Server control component");
		this.adminCommandListener.start();
	}

	@Override
	public void shutdown() throws Exception {
		LOGGER.info("Shutting down Server control component");
		this.adminCommandListener.stop();
	}

}
