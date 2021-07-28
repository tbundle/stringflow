package abs.ixi.server.io.net;

/**
 * A listener interface which receives signal if there is a state change for any
 * {@link IOPort}.
 * 
 * @author Yogi
 *
 */
public interface IOPortStateChangeListener {
	/**
	 * Signal change in state for given {@link IOPort} to this
	 * {@link IOPortStateChangeListener}
	 * 
	 * @param port {@link IOPort} whose state has changed
	 */
	public boolean signalStateChange(IOPort port);
}
