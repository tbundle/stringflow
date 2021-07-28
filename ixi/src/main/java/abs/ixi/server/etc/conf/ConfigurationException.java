package abs.ixi.server.etc.conf;

/**
 * A high-level exception class to wrap all the detailed exceptions during
 * configuration processing.
 * 
 * @author Yogi
 *
 */
public class ConfigurationException extends Exception {
	private static final long serialVersionUID = -2348002718869190247L;

	public ConfigurationException(String msg) {
		super(msg);
	}

	public ConfigurationException(Exception cause) {
		super(cause);
	}

	public ConfigurationException(String msg, Exception cause) {
		super(msg, cause);
	}
}
