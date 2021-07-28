package abs.ixi.server.io.net;

import java.io.IOException;
import java.nio.channels.SocketChannel;

/**
 * Interface for {@link IOChannel}s which are actually server channels and will
 * accept inbound connections.
 * 
 * @author Yogi
 *
 */
public interface ServerChannel extends IOChannel {
	/**
	 * Delegate method for accept method of a server socket
	 * 
	 * @return {@link SocketChannel} instance
	 * @throws IOException
	 */
	public SocketChannel accept() throws IOException;

}
