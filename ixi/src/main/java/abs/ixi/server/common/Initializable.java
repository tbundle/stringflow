package abs.ixi.server.common;

public interface Initializable {
	/**
	 * Initiate initialization sequence
	 * 
	 * @throws InitializationException
	 */
	public void init() throws InitializationException;

	/**
	 * Check if the initialization sequence has been executed successfully
	 */
	public boolean isInitialized();
}
