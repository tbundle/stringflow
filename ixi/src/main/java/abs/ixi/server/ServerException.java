package abs.ixi.server;

/**
 * Root exception for all the exceptions defined in the server.
 */
public class ServerException extends Exception {
	private static final long serialVersionUID = 1L;

	public ServerException() {
		super();
	}

	public ServerException(String msg) {
		super(msg);
	}

	public ServerException(Exception cause) {
		super(cause);
	}

	public ServerException(String msg, Exception cause) {
		super(msg, cause);
	}
}
