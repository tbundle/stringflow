package abs.ixi.server.app;

/**
 * Represents an error while starting up the application during deployment. As
 * this is an error, server will destroy the application instance and will mark
 * it as "deployment failed"
 */
public class ApplicationStartupError extends ApplicationError {
	private static final long serialVersionUID = 1L;

	public ApplicationStartupError(Throwable cause) {
		super(cause);
	}

	public ApplicationStartupError(String msg) {
		super(msg);
	}

	public ApplicationStartupError(String msg, Throwable cause) {
		super(msg, cause);
	}

}
