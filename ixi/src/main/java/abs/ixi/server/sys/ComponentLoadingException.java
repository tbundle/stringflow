package abs.ixi.server.sys;

/**
 * An exception to indicate component loading failure. Component loading
 * failures are fail-fast in nature; therefore as soon component loading fails
 * for first component, the process exits.
 * 
 * @author Yogi
 *
 */
public class ComponentLoadingException extends Exception {
	private static final long serialVersionUID = 1L;

	public ComponentLoadingException(Exception cause) {
		super(cause);
	}

	public ComponentLoadingException(String msg) {
		super(msg);
	}

	public ComponentLoadingException(String msg, Exception cause) {
		super(msg, cause);
	}

}
