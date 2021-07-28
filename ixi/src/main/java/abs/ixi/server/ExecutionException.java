package abs.ixi.server;

import abs.ixi.server.couplet.Couplet;
import abs.ixi.server.packet.JID;

/**
 * An exception which is thrown by a {@link ServerComponent} when it fails to
 * process a packet.
 * 
 * @author Yogi
 *
 */
public class ExecutionException extends Exception {
	private static final long serialVersionUID = -602337377814151177L;

	/**
	 * {@link JID} of the component which is raising this exception.
	 */
	private String executorId;

	public ExecutionException(String executor, String msg) {
		super(msg);
		this.executorId = executor;
	}

	public ExecutionException(String executor, Exception cause) {
		super(cause);
		this.executorId = executor;
	}

	public ExecutionException(String executor, String msg, Exception cause) {
		super(msg, cause);
		this.executorId = executor;
	}

	/**
	 * @return {@link JID} of the component which failed to process a
	 *         {@link Couplet} and raised this exception.
	 */
	public String getExecutorId() {
		return executorId;
	}

}
