package abs.ixi.server;

/**
 * Root interface for all the Listeners within server
 */
public interface ServerListener {

	/**
	 * Listener instance starts listening to the events
	 * 
	 * @throws Exception
	 */
	public void start() throws Exception;

	/**
	 * Stops listening to the events. Once listening has been stopped, it can't
	 * be restarted. The associated listener thread would be marked as inactive.
	 */
	public void stop() throws Exception;

}