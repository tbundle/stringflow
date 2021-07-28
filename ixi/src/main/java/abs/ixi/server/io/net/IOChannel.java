package abs.ixi.server.io.net;

import java.net.SocketAddress;

/**
 * Contract for all input output channel implementations.
 * 
 * @author Yogi
 *
 */
public interface IOChannel {
	/**
	 * @return Local address of the associated socket
	 */
	public SocketAddress localAddress();

	/**
	 * @return remote addresss of the associated socket
	 */
	public SocketAddress remoteAddress();

	/**
	 * Print all the configirations for this {@link IOChannel}
	 */
	public void printConfig();
	
	/**
	 * Close this Channel; The method swallows any exception caught during
	 * close.
	 */
	public void close();

}
