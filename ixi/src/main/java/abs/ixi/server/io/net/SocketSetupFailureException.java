package abs.ixi.server.io.net;

import abs.ixi.server.common.UnrecoverableException;

public class SocketSetupFailureException extends UnrecoverableException {
	private static final long serialVersionUID = -7595885534067626590L;

	public SocketSetupFailureException(String msg) {
		super(msg);
	}

	public SocketSetupFailureException(Exception cause) {
		super(cause);
	}

	public SocketSetupFailureException(String msg, Exception cause) {
		super(msg, cause);
	}

}
