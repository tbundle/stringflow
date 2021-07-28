package abs.ixi.server.common;

import abs.ixi.server.ServerException;

/**
 * A checked exception to indicate failure of initialization sequence for an
 * {@link Initializable}
 * 
 * @author Yogi
 *
 */
public class InitializationException extends ServerException {
	private static final long serialVersionUID = -9160499318177936959L;

	public InitializationException(String msg) {
		super(msg);
	}

	public InitializationException(Exception cause) {
		super(cause);
	}

	public InitializationException(String msg, Exception cause) {
		super(msg, cause);
	}

}
