package abs.ixi.server;

import abs.ixi.server.common.UnrecoverableException;

/**
 * {@code ServerStartupException} indicates an unrecoverable error at server
 * startup time. At any stage of server startup hook, if
 * {@link ServerStartupException} is thrown, it means server has failed to start
 * and it cant recover. In such scnearios, server will also fail to shutdown
 * from the error point.
 * 
 * @author Yogi
 *
 */
public class ServerStartupException extends UnrecoverableException {
	private static final long serialVersionUID = 1L;

	public ServerStartupException(String msg) {
		super(msg);
	}

	public ServerStartupException(Exception e) {
		super(e);
	}

	public ServerStartupException(String msg, Exception cause) {
		super(msg, cause);
	}
}
