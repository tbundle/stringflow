package abs.ixi.server.io.net;

import static abs.ixi.server.etc.conf.Configurations.Bundle.PROCESS;
import static java.nio.ByteBuffer.allocate;
import static java.nio.ByteBuffer.allocateDirect;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.Stringflow;
import abs.ixi.server.common.BufferFactory;
import abs.ixi.server.etc.conf.Configurations;
import abs.ixi.server.etc.conf.ProcessConfigAware;

/**
 * {@link CachedBufferFactory} maintains a pool of buffers which entities can
 * borrow from the factory. The buffer borrower is expected to return the buffer
 * back to factory after the use. The factory is initialized with number of
 * buffers = CAPACITY. If the factory exhausts the pool of buffers, it starts
 * creating buffers as and when it receives a borrow request. The factory can be
 * configured to generate buffer either in JVM heap or DIRECT buffers.
 */
public final class CachedBufferFactory implements BufferFactory, ProcessConfigAware {
	private static final Logger LOGGER = LoggerFactory.getLogger(CachedBufferFactory.class);

	/**
	 * Default size of a buffer in this factory.
	 */
	public static final int BUFFER_SIZE = 4096; // 4KB

	/**
	 * Number of buffers that this factory would be initialized with.
	 */
	public static final int CAPACITY = 10000;

	/**
	 * Size of the each buffer.
	 */
	private int bufSize;

	/**
	 * Number of buffers in this factory.
	 */
	private int capacity;

	/**
	 * Type of the buffer this factory offers (DIRECT/INDIRECT).
	 */
	private BufferType bufType;

	/**
	 * Number of buffers which are loaned at given point of time.
	 */
	private int loanedBufCount;

	/**
	 * Blocking queue which keeps all the free buffers.
	 */
	private BlockingQueue<ByteBuffer> bufs;

	private static CachedBufferFactory instance;

	private CachedBufferFactory() {
		this(BUFFER_SIZE, CAPACITY, BufferType.DIRECT);
	}

	private CachedBufferFactory(int bufferSize, int capacity, BufferType bufferType) {
		this.bufSize = bufferSize;
		this.capacity = capacity;
		this.bufType = bufferType;

		this.bufs = new LinkedBlockingQueue<>(this.capacity);

		LOGGER.info("Buffer size : {} factory capacity : {} and BufferType : {}", bufferSize, capacity, bufferType);
	}

	public static synchronized CachedBufferFactory getInstance() {
		if (instance == null) {
			LOGGER.debug("Instantiating Cached buffer factory");

			Configurations conf = Stringflow.runtime().configurations();
			int bufSize = conf.getOrDefaultInteger(_CACHED_BUF_FACTORY_BUFFER_SIZE, BUFFER_SIZE, PROCESS);
			if (bufSize <= 0) {
				LOGGER.warn("Invalid buffer size;defaulting to {}", BUFFER_SIZE);
				bufSize = BUFFER_SIZE;
			}

			int capacity = conf.getOrDefaultInteger(_CACHED_BUF_FACTORY_CAPACITY, CAPACITY, PROCESS);
			if (capacity <= 0) {
				LOGGER.warn("Invalid capacity; defaulting to {}", CAPACITY);
				capacity = CAPACITY;
			}

			BufferType bufType = null;
			try {
				String bType = conf.getOrDefault(_CACHED_BUF_FACTORY_BUFFER_TYPE, BufferType.DIRECT.val(), PROCESS);
				bufType = BufferType.from(bType);

			} catch (IllegalArgumentException e) {
				LOGGER.warn("Invalid Buffer type {}; defaulting to {}", BufferType.DIRECT);
				LOGGER.error(e.getMessage());
				bufType = BufferType.DIRECT;
			}

			instance = new CachedBufferFactory(bufSize, capacity, bufType);
		}

		return instance;
	}

	@Override
	public synchronized ByteBuffer borrowBuffer() {
		ByteBuffer buf = null;

		if (this.bufs.isEmpty()) {
			LOGGER.debug("Allocating buffer for lending");
			buf = this.allocateBuffer(this.bufType);

		} else {
			buf = this.bufs.poll();
			if (buf == null) {
				LOGGER.error("something has gone wrong; buffer queue was determined as non-empty");
			}
		}

		this.loanedBufCount++;
		LOGGER.debug("Number of buffers loaned: {}", this.loanedBufCount);

		return buf;
	}

	/**
	 * Allocate a {@link ByteBuffer} of given type;
	 * 
	 * @param type type of the buffer
	 * @return {@link ByteBuffer} instance
	 */
	private ByteBuffer allocateBuffer(BufferType type) {
		ByteBuffer b = BufferType.DIRECT == type ? allocateDirect(this.bufSize) : allocate(this.bufSize);
		b.order(ByteOrder.BIG_ENDIAN);
		return b;
	}

	@Override
	public synchronized void returnBuffer(ByteBuffer buf) {
		if (isValidBufferType(buf)) {
			buf.clear();
			boolean success = bufs.offer(buf);
			if (!success) {
				LOGGER.error("Returned buffer count exceeded queue size. This is unusual.");
			} else {
				this.loanedBufCount--;
			}
		}
	}

	private boolean isValidBufferType(ByteBuffer buf) {
		return (this.bufType == BufferType.DIRECT && buf.isDirect())
				|| (this.bufType == BufferType.DEFAULT && !buf.isDirect());
	}

	@Override
	public int bufferSize() {
		return this.bufSize;
	}

	@Override
	public synchronized int size() {
		return this.loanedBufCount + this.bufs.size();
	}

	/**
	 * As the return type is integer; we don't need to synchronize this.
	 */
	@Override
	public int loanedBufferCount() {
		return this.loanedBufCount;
	}

}
