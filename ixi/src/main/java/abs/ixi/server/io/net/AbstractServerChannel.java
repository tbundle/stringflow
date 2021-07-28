package abs.ixi.server.io.net;

import java.nio.channels.ServerSocketChannel;

public abstract class AbstractServerChannel implements ServerChannel {
	/**
	 * Underlying channel
	 */
	protected ServerSocketChannel channel;

	/**
	 * Stringflow does not allow different config for each {@link IOChannel}
	 */
	protected ChannelConfig config;

	public AbstractServerChannel(ChannelConfig conf) {
		this.config = conf;
	}

	public ChannelConfig getConfig() {
		return config;
	}
}
