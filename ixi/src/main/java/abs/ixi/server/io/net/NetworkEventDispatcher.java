package abs.ixi.server.io.net;

import java.io.IOException;
import java.nio.channels.SelectableChannel;

/**
 * Facade interface for {@link NetworkTransport} server component which allows
 * interface to register netwrok channels
 * 
 * @author Yogi
 *
 */
public interface NetworkEventDispatcher {
	/**
	 * Register a channel with {@link NetworkEventDispatcher}. Once registration
	 * is complete, any event on the channel will be reported for handling.
	 * 
	 * @param ch
	 * @param connector
	 * @throws IOException
	 */
	public void registerChannel(SelectableChannel ch, IOPortConnector connector) throws IOException;
}
