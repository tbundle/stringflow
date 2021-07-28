package abs.ixi.server;

import abs.ixi.server.CoreComponent.ServerFunction;
import abs.ixi.server.etc.conf.Configurations;
import abs.ixi.server.etc.conf.ProcessConfigAware;
import abs.ixi.server.etc.conf.SystemConfigAware;
import abs.ixi.server.io.InputHandler;
import abs.ixi.server.packet.JID;
import abs.ixi.server.router.Router;

/**
 * It's a facade interface for Stringflow process which restricts process's
 * interface to other classes.
 * 
 * @author Yogi
 *
 */
public interface ServerRuntime extends ProcessConfigAware, SystemConfigAware {
	/**
	 * @return String value of node from ther server {@link JID}.
	 */
	public String node();

	/**
	 * @return domain name of the server. The domain name is applied at cluster
	 *         level.
	 */
	public String domain();

	/**
	 * @return {@link JID}
	 */
	public JID jid();

	/**
	 * @return server configurations loaded. Configurations are immutable.
	 */
	public Configurations configurations();

	/**
	 * Discover the Jid of a core component within server for a given server
	 * function.
	 * 
	 * @param function
	 * @return
	 * @throws ComponentNotFoundException
	 */
	public String getCoreComponentJid(ServerFunction function);

	/**
	 * Print loaded core components
	 */
	public void printLoadedCoreComponents();

	/**
	 * Get the router component
	 * 
	 * @return
	 */
	public Router getRouter();

	/**
	 * Shutdown Stringflow process with a status. Non-zero ststus is abnormal
	 * termination.
	 * 
	 * @param status
	 */
	public void shutdown(int status);

	// TODO This is heck; must be removed once signal/event bus is introduced in
	// which case IOController itself will instantiate IOServices. Below method
	// is used by IOService to discover InputHandler
	public InputHandler getInputHandler();

}
