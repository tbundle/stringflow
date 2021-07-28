package abs.ixi.server;

/**
 * Base class of all runtime exceptions in server.
 * 
 * @author Yogi
 *
 */
public class StringflowRuntimeException extends RuntimeException {
	private static final long serialVersionUID = -1816276562488859064L;

	public StringflowRuntimeException(String msg) {
		super(msg);
	}

	public StringflowRuntimeException(Exception cause) {
		super(cause);
	}

	public StringflowRuntimeException(String msg, Exception cause) {
		super(msg, cause);
	}

}
