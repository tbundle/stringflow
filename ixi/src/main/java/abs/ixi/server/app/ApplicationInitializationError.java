package abs.ixi.server.app;

/**
 * Error encountered while initializing an application during application
 * deployment. As this is an error, server will destroy the application instance
 * and will marked as "deployment failed"
 */
public class ApplicationInitializationError extends ApplicationError {
	private static final long serialVersionUID = 1L;

	public ApplicationInitializationError(Throwable cause) {
		super(cause);
	}

	public ApplicationInitializationError(String msg) {
		super(msg);
	}

	public ApplicationInitializationError(String msg, Throwable cause) {
		super(msg, cause);
	}

}
