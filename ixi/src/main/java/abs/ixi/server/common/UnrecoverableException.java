package abs.ixi.server.common;

import abs.ixi.server.ServerException;

/**
 * A generic {@link ServerException} to capture details of a situation from
 * which Server can not recover.
 * 
 * @author Yogi
 *
 */
public abstract class UnrecoverableException extends ServerException {
	private static final long serialVersionUID = 1L;

	public UnrecoverableException(String msg) {
		super(msg);
	}

	public UnrecoverableException(Exception e) {
		super(e);
	}

	public UnrecoverableException(String msg, Exception cause) {
		super(msg, cause);
	}
}
