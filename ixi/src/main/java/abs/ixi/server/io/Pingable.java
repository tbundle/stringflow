package abs.ixi.server.io;

import abs.ixi.server.io.net.IOPortConnector;

/**
 * A contract for entities which support Ping mechanism. This interface is part
 * of server I/O layer.
 * 
 * @author Yogi
 *
 */
public interface Pingable {
	/**
	 * Not all protocols support ping messages; therefore if the associated
	 * protocol supports ping, {@code IOService} will send the ping message on
	 * underlying connection.
	 * 
	 * @return true if the ping message was sent on the underlying
	 *         {@link IOPortConnector} otherwise false
	 */
	public boolean sendPing();

	/**
	 * Check if the last sent ping has timeout. It returns false for both cases
	 * when pong has NOT timed out and when there was no ping sent.
	 */
	public boolean hasPingTimeout();

}
