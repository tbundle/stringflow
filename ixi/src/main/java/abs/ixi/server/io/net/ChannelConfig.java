package abs.ixi.server.io.net;

import java.net.SocketOption;
import java.nio.channels.Channel;
import java.util.Map;

/**
 * A map structure to hold a set of configurations for a {@link Channel}
 * 
 * @author Yogi
 *
 */
public interface ChannelConfig {
	/**
	 * @return {@link SocketOption}s and their values
	 */
	public Map<SocketOption<?>, Object> getSocketOptions();

	/**
	 * Set {@link SocketOption}s for socket. Socket options are set at the time
	 * of server socket setup.
	 * 
	 * @param options
	 */
	public void setSocketOptions(Map<SocketOption<?>, Object> options);

}
