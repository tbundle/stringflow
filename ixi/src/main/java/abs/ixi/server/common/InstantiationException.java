package abs.ixi.server.common;

/**
 * An exception which is thrown from a constructor to indicate the failure of
 * object instantiation.
 * 
 * @author Yogi
 *
 */
public class InstantiationException extends Exception {
	private static final long serialVersionUID = -8360322135069933074L;

	public InstantiationException(Exception cause) {
		super(cause);
	}

	public InstantiationException(String msg) {
		super(msg);
	}

	public InstantiationException(String msg, Exception cause) {
		super(msg, cause);
	}
}
