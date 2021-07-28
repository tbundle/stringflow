package abs.ixi.server.app;

/**
 * Represents an error in an {@link Application}. The errors are unrecoverable.
 * Therefore, server may destroy the application instance if it encounters an
 * error.
 */
public class ApplicationError extends Error{
	private static final long serialVersionUID = 1L;
	
	public ApplicationError(Throwable cause) {
		super(cause);
	}
	
	public ApplicationError(String msg) {
		super(msg);
	}
	
	public ApplicationError(String msg, Throwable cause) {
		super(msg, cause);
	}
	
}