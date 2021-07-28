package abs.ixi.server;

import abs.ixi.server.common.Initializable;
import abs.ixi.server.common.InitializationException;

/**
 * This implementation is not thread-safe. Further implementations needs with
 * thread-safety on their own.
 */
public abstract class AbstractServerListener implements ServerListener, Initializable {
	protected volatile boolean listening = false;
	protected volatile boolean initialized = false;

	@Override
	public void init() throws InitializationException {
		this.initialized = true;
	}

	@Override
	public boolean isInitialized() {
		return this.initialized;
	}

	@Override
	public void start() throws Exception {
		this.listening = true;
	}

	@Override
	public void stop() throws Exception {
		this.listening = false;
	}

}
