package abs.ixi.server.etc;

/**
 * A high-level exception to indicate database related errors.
 * 
 * @author Yogi
 *
 */
public class DatabaseException extends Exception {
	private static final long serialVersionUID = 3864693641672046378L;

	public DatabaseException(Exception cause) {
		super(cause);
	}

	public DatabaseException(String msg, Exception e) {
		super(msg, e);
	}

}
