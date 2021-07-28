package abs.ixi.server.io;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Packet generation is a multi-step process starting from reading bytes from
 * network socket, converting bytes to chars, generating packet and validating
 * them for structural correctness packet as well as semnatic validations.
 * <i>IOBus</i> provides governance to those steps. Additionally, it propogates
 * network events to application layer and vice-versa.
 * 
 * @author Yogi
 *
 */
public final class IOBus {
	private static final Logger LOGGER = LoggerFactory.getLogger(IOBus.class);

	private Executor executor;

	public IOBus(Executor executor) {
		// TODO Tune the thread pool parameters
		this.executor = Executors.newCachedThreadPool();
	}

	
	
}
