package abs.ixi.server;

import abs.ixi.server.common.NameAware;
import abs.ixi.server.etc.conf.ProcessConfigAware;
import abs.ixi.server.packet.JID;
import abs.ixi.server.packet.Packet;

/**
 * Root interface for all the {@link ServerComponent}
 */
public interface ServerComponent extends NameAware<String>, ProcessConfigAware {
	/**
	 * {@link ServerComponent} unique name in the system
	 */
	public String getName();

	/**
	 * {@link ServerComponent} JID in the System. JID is used as component
	 * identification while routing {@link Packet}
	 */
	public JID getJID();

	/**
	 * Initialize the server component. This method is invoked as part of server
	 * bootstrap process therefore, {@link ServerRuntime} will be unavailable.
	 */
	public void init();

	/**
	 * Component start command method; this method is invoked after server
	 * bootstrap has finished execution. Therefore components can access
	 * {@link ServerRuntime}.
	 */
	public void start() throws Exception;

	/**
	 * Component shutdown command
	 */
	public void shutdown() throws Exception;

}
