package abs.ixi.server.sys;

import abs.ixi.server.ServerRuntime;

/**
 * A runtime exception to indicate {@link ServerRuntime} unavailability.
 * 
 * @author Yogi
 *
 */
public class RuntimeUnavailableException extends RuntimeException {
	private static final long serialVersionUID = 3522539206754605917L;

	public RuntimeUnavailableException() {
		super();
	}
}
