package abs.ixi.server.io.net;

/**
 * A runtime exception to indicate channel related exceptions.
 * 
 * @author Yogi
 *
 */
public class ChannelException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ChannelException(String msg) {
		super(msg);
	}

	public ChannelException(Exception cause) {
		super(cause);
	}

	public ChannelException(String msg, Exception cause) {
		super(msg, cause);
	}
}
