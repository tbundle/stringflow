package abs.ixi.server.io.net;

import java.net.SocketOption;
import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation of {@link ChannelConfig}.
 * 
 * @author Yogi
 *
 */
public class DefaultChannelConfig implements ChannelConfig {
	private Map<SocketOption<?>, Object> socketOptions;

	public DefaultChannelConfig() {
		this(new HashMap<>());
	}

	public DefaultChannelConfig(Map<SocketOption<?>, Object> socketOptions) {
		this.socketOptions = socketOptions;
	}

	@Override
	public Map<SocketOption<?>, Object> getSocketOptions() {
		return this.socketOptions;
	}

	@Override
	public void setSocketOptions(Map<SocketOption<?>, Object> options) {
		this.socketOptions = options;
	}

}
