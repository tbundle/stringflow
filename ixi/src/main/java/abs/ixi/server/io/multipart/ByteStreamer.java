package abs.ixi.server.io.multipart;

import java.nio.ByteBuffer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.EntityState;
import abs.ixi.server.StateEmitter;
import abs.ixi.server.io.ChannelFacade;
import abs.ixi.server.io.net.IOPort;

/**
 * Loading Large objects such as {@link MimePacket} into memory limits server's
 * scalibility. It's never a good idea to load them into memory fully; instead
 * these messages must be loaded into memory in chunks to optimize resource
 * utilization. Moreover, Java as technology does not allow bytes to be drained
 * onto a socket without wrapping them into a {@link ByteBuffer}. And draing
 * large number of bytes onto a network socket may result socket buffer getting
 * full; causing write operation to take long time to finsih the writting.
 * Therefore {@code ByteStreamer} allows draining bytes from a large objects in
 * chunks.
 * <P>
 * This class is part of server input/output layer; allowing efficient byte
 * transmission for large objects such as MIME. It should strictly be used for
 * transmitting large objects. In most cases, XMPP packets are small sized
 * therefore this class should be used to transmit MIME packets only.
 * </p>
 * 
 * @author Yogi
 *
 */
public final class ByteStreamer implements StateEmitter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ByteStreamer.class);

    /**
     * Number of worker threads within executor service.This is just the core
     * pool size; based on nature of executor the actual number of workers may
     * vary at a given time.
     */
    private static final int DEFAULT_POOL_SIZE = 10;

    /**
     * Number of threads in executer service core pool size
     */
    private int poolSize;

    /**
     * An exector service to execute file transmission tasks
     */
    private ExecutorService executor;

    /**
     * @return Singleton instance of {@link ByteStreamer}
     */
    public static ByteStreamer getInstance() {
	return null;
    }

    /**
     * Submit a large object for Streaming on to a destination {@link IOPort}.
     * 
     * @param largeObj
     * @param port
     */
    public void stream(Streamable largeObj, ChannelFacade channel) {
	LOGGER.debug("Submitting {} for Streaming", largeObj);
    }

    @Override
    public EntityState emit() {
	EntityState es = new EntityState();
	es.put(EntityState.NAME, "ByteStreamer");
	es.put(EntityState.CLASS, ByteStreamer.class.getSimpleName());

	return es;
    }

    /**
     * Worker class which streams a Large objects onto an {@link IOPort}
     * instance. It implements {@link Callable} and is used by
     * {@link ByteStreamer} as a worker class for its {@link ExecutorService}
     * 
     * @author Yogi
     *
     */
    class Streamer implements Callable<Long> {
	/**
	 * Object to be streamed
	 */
	private Streamable largeObj;

	/**
	 * {@link IOPort} on which the bytes will be drained
	 */
	private IOPort port;

	/**
	 * Offset counter
	 */
	private long offset;

	public Streamer(Streamable largeObj, IOPort port) {
	    this.largeObj = largeObj;
	    this.port = port;
	}

	@Override
	public Long call() throws Exception {
	    return null;
	}
    }

}
