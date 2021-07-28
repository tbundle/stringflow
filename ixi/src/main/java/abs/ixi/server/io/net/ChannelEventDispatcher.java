package abs.ixi.server.io.net;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.util.SystemPropertyUtils;

/**
 * {@code ChannelEventDispatcher} is a continuously running loop which polls
 * {@link SelectableChannel}s for events using {@link Selector#select()}.
 * <p>
 * Selectors are themselves safe for use by multiple concurrent threads; their
 * key sets, however, are not. The selection operations synchronize on the
 * selector itself, on the key set, and on the selected-key set, in that order.
 * They also synchronize on the cancelled-key set before while manipulating
 * cancelled-key set. above. For more information see <a href=
 * "https://docs.oracle.com/javase/7/docs/api/java/nio/channels/Selector.html">
 * java doc </a>. The {@link ChannelEventDispatcher} implementation notes the
 * selector concurrency mechanism and optimizes its working accrodingly.
 * </p>
 * {@code ChannelEventDispatcher} follows below steps in order-
 * <ul>
 * <li>Selector synchronizes on various keysetsa and hence manipulation of
 * keysets may block if the selector is inside <i>select</i> operation;
 * therefore the channel event loop takes a guard before firing a select
 * operation. The guard ensures that there is no other thread manipulating the
 * selector keysets. The guard is not a typicall lock therefore there is still a
 * possibility that some other thread is able to acquire a lock on keysets
 * between taking a guard and firing a select operation</li>
 * <li>Fire a select operation with a predefined timeout. The implementation
 * ensures that no ther thread manipulates the selector keysets as it may get
 * blocked for the time selector is performing above step</li>
 * <li>In order to avoid synchronization blocks among threads, {@link IOPort}
 * instances for which status has chnaged are placed on a queue which is
 * processed for each run of the loop</li>
 * <li>Selected keys are dispatched for processing. The keys are processed
 * inside a thread pool</li>
 * <li>selected keysets is cleared</li>
 * </ul>
 * <p>
 * {@link Selector#wakeup()} is a very expensive operation; therefore every
 * attempt is made to minimize the number of wakeup calls.
 * </p>
 * <P>
 * Once shutdown, loop can <b>not</b> be restarted.
 * </P>
 * 
 * @author Yogi
 *
 */
public class ChannelEventDispatcher implements IOPortStateChangeListener, Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(ChannelEventDispatcher.class);

	/**
	 * Number of premature selector returns after which selector will deemed as
	 * frozen/spinning; which will trigger selector rebuild
	 */
	private static final short PREMATURE_SELECTOR_RETURN_THRESHOLD = 5;

	/**
	 * Timeout in millis for {@link Selector#select()} operation
	 */
	private static final long SELECT_TIMEOUT = Duration.ofSeconds(10).toMillis();

	/**
	 * NIO Selector instance used by this loop
	 */
	private Selector selector;

	/**
	 * Default selector provider instance
	 */
	private final SelectorProvider provider;

	/**
	 * lock object used by Selector thread to synchronize selection
	 */
	private final ReadWriteLock selectorGuard = new ReentrantReadWriteLock();

	/**
	 * A blocking queue which holds all the {@link IOPort} instances. Each
	 * {@link IOPort} instance is put on this queue each time it finishes its
	 * execution within a worker thread
	 */
	private final BlockingQueue<IOPort> ioPortStateChangeQ;

	/**
	 * Executer used to dispatch events
	 */
	private ExecutorService executor;

	/**
	 * Flag to indicate if this loop has received a shutdown command
	 */
	private volatile boolean shuttingdown = false;

	/**
	 * Flag to indicate if this loop has been shutdown
	 */
	private volatile boolean shutdown = false;

	static {

		// Workaround for Java Bug
		// http://bugs.sun.com/view_bug.do?bug_id=6427854
		final String key = "sun.nio.ch.bugLevel";
		final String bugLevel = SystemPropertyUtils.get(key);
		if (bugLevel == null) {
			try {
				AccessController.doPrivileged(new PrivilegedAction<Void>() {
					@Override
					public Void run() {
						System.setProperty(key, "");
						return null;
					}
				});
			} catch (final SecurityException e) {
				LOGGER.debug("Unable to get/set System Property: " + key, e);
			}
		}
	}

	/**
	 * Constructor to instantiate ChannelEventDispatcher
	 * 
	 * @param corePoolSize
	 * @param maxPoolSize
	 * @param keepAliveTime
	 */
	public ChannelEventDispatcher(int corePoolSize, int maxPoolSize, Duration keepAliveTime) {
		this(SelectorProvider.provider(), corePoolSize, maxPoolSize, keepAliveTime);
	}

	/**
	 * Constructor which allows to inject a selector provider which loop will
	 * use to instantiate a selector.
	 * 
	 * @param provider selector provider instance
	 */
	public ChannelEventDispatcher(SelectorProvider provider, int corePoolSize, int maxPoolSize,
			Duration keepAliveTime) {
		this.provider = provider;
		this.ioPortStateChangeQ = new LinkedBlockingQueue<>();

		this.selector = openSelector();

		this.executor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime.toMinutes(), TimeUnit.MINUTES,
				new LinkedBlockingQueue<>());
	}

	/**
	 * Open a selector using the provider
	 */
	private Selector openSelector() {
		try {
			return this.provider.openSelector();
		} catch (IOException e) {
			throw new ChannelException("Failed to open a selector", e);
		}
	}

	@Override
	public void run() {
		LOGGER.debug("Starting Channel Event loop");

		while (Thread.currentThread().isInterrupted() || !this.shuttingdown) {
			try {

				/**
				 * Take guard so that all the selected keys are processed before
				 * invoking another select on the selector
				 */
				selectorGuardBarrier();

				executeSelect();

				// Checking it here gives dead adaptors a chance to
				// participate in selection process in order to flush
				// the write data, if any
				processStatusChangeQueue();

				Set<SelectionKey> keys = this.selector.selectedKeys();

				for (SelectionKey key : keys) {
					dispatch((IOPort) key.attachment());
				}

				keys.clear();

			} catch (IOException e) {
				LOGGER.debug("Unexpected exception in channel event loop", e);
				rebuildSelector();
			} catch (Throwable t) {
				LOGGER.error("Caught throwable in channel event loop", t);
				break;
			}
		}

		this.shutdownLoop();
	}

	/**
	 * Execute select command on selector. The method returns only when there is
	 * at least one key selected. If selector returns with zero keys for
	 * {@link ChannelEventDispatcher#PREMATURE_SELECTOR_RETURN_THRESHOLD} times
	 * in a row without selecting anything, selector rebuild is triggered.
	 * 
	 * @throws IOException
	 */
	private void executeSelect() throws IOException {
		int psrCount = 0;

		while (true) {
			long timeMillis = System.currentTimeMillis();

			int selectedKeys = this.selector.select(SELECT_TIMEOUT);

			LOGGER.trace("{} keys were selected", selectedKeys);

			if (selectedKeys != 0) {
				break;
			} else {
				if ((System.currentTimeMillis() - timeMillis) < SELECT_TIMEOUT) {
					psrCount++;

					if (psrCount > PREMATURE_SELECTOR_RETURN_THRESHOLD) {
						rebuildSelector();
						this.selector.select(SELECT_TIMEOUT);
						break;
					}
				}
			}
		}
	}

	/**
	 * Replace the old selector with new one. All the keys will be added to the
	 * new selector.
	 */
	private void rebuildSelector() {
		LOGGER.debug("Rebuilding selector");

		Selector oldSelector = this.selector;
		this.selector = openSelector();

		int nChannels = 0;

		for (SelectionKey key : oldSelector.keys()) {
			IOPort ioPort = (IOPort) key.attachment();
			try {
				if (!key.isValid() || key.channel().keyFor(this.selector) != null) {
					continue;
				}

				int interestOps = key.interestOps();
				key.cancel();
				SelectionKey newKey = key.channel().register(this.selector, interestOps, ioPort);
				ioPort.setKey(newKey);
				nChannels++;
			} catch (Exception e) {
				LOGGER.warn("Failed to re-register channel to new Selector.", e);
				ioPort.close();
			}
		}

		LOGGER.debug("Migrated {} channels to new selector", nChannels);

		try {
			oldSelector.close();
		} catch (Throwable e) {
			LOGGER.debug("Failed to close old selector", e);
		}
	}

	/**
	 * Called to acquire and then immediately release a write lock on the
	 * selectorGuard object. This method is only called by the selection thread
	 * and it has the effect of making that thread wait until all read locks
	 * have been released.
	 */
	private void selectorGuardBarrier() {
		this.selectorGuard.writeLock().lock();
		this.selectorGuard.writeLock().unlock();
	}

	/**
	 * Grab a read lock on the selectorGuard object. The method is called
	 * whenever you want to mutate the state of the Selector. It must call
	 * releaserSelectorGuard when it is finished, because selection will not
	 * resume until all read locks have been released.
	 */
	private void acquireSelectorGuard(boolean wakeUp) {
		this.selectorGuard.readLock().lock();

		if (wakeUp) {
			this.selector.wakeup();
		}
	}

	/**
	 * Undo a previous call to acquireSelectorGuard to indicate that the calling
	 * thread no longer needs access to the Selector object.
	 */
	private void releaseSelectorGuard() {
		this.selectorGuard.readLock().unlock();
	}

	/**
	 * In each selector loop, after processing is completed, {@link IOPort}
	 * instance is placed on status change queue. After each <i>select</i> call,
	 * status change queue is processed to unregister dead channels and resume
	 * selection for valid keys.
	 */
	private void processStatusChangeQueue() {
		IOPort port = null;

		while ((port = ioPortStateChangeQ.poll()) != null) {
			if (port.isDead()) {
				unregisterChannel(port);
			} else {
				resumeSelection(port);
			}
		}
	}

	/**
	 * Cancel a key attached to given {@link IOPort}. As we know selector
	 * implemetations are synchronized on selected keyset, cancelled key set and
	 * selector itself. Therefore, cancelling a key may block if selector is
	 * inside select call.
	 * 
	 * @param ioPort {@link IOPort} instance
	 */
	protected void unregisterChannel(IOPort ioPort) {
		LOGGER.debug("unregistering socket channel from selector");

		SelectionKey selectionKey = ioPort.key();

		// this method is called only during stausChangeQ processing; and at
		// that time selector will not be inside select operation; therefore no
		// need to wakeup selector
		acquireSelectorGuard(false);

		try {
			selectionKey.cancel();
			LOGGER.debug("Cancellled key {}", selectionKey);

		} finally {
			releaseSelectorGuard();
		}

		LOGGER.debug("finished unregistering socket channel from selector");
	}

	/**
	 * Register a {@link SelectableChannel} with the selector. If the
	 * registration fails, it marks the port dead. Remember channel registration
	 * process synchronizes on keyset of the selector therefore, registraion
	 * call may block if another registration or selection process is in
	 * progress.
	 * 
	 * @param channel
	 * @param connector
	 * @throws IOException
	 */
	void registerChannel(SelectableChannel channel, IOPort port) {
		try {
			acquireSelectorGuard(true);

			SelectionKey key = channel.register(this.selector, SelectionKey.OP_READ, port);
			port.setKey(key);

		} catch (IOException e) {
			LOGGER.warn("Failed to register channel with selector", e);
			port.die();
		} finally {
			releaseSelectorGuard();
		}
	}

	/**
	 * Set the interested operations back into the {@link SelectionKey} so that
	 * the selection thread can trap the fresh events on the
	 * {@link SocketChannel}
	 * 
	 * @param ioPort {@link IOPort} instance
	 */
	private void resumeSelection(IOPort ioPort) {
		SelectionKey key = ioPort.key();

		if (key.isValid()) {
			key.interestOps(ioPort.interestOps());
		} else {
			LOGGER.warn("Unexpected: key was not cancelled yet it was found invalid");
		}
	}

	/**
	 * Dispatch event received on a {@link SocketChannel} for execution
	 */
	private void dispatch(IOPort ioPort) {
		ioPort.prepareToRun();
		ioPort.key().interestOps(0);

		this.executor.execute(new HandlerFutureTask(ioPort));
	}

	@Override
	public boolean signalStateChange(IOPort port) {
		boolean success = this.ioPortStateChangeQ.offer(port);

		if (!success) {
			LOGGER.error("Failed to add IOPort {} instance to state change queue", port);
		}

		return success;
	}

	/**
	 * Issue a shutdown command to the Selector Loop. The method simply sets a
	 * flag; the loop will be shutdown once loop completes the current run.
	 */
	public void shutdown() {
		this.shuttingdown = true;
	}

	/**
	 * Check if the loop has been shutdown
	 * 
	 * @return
	 */
	public boolean isShutdown() {
		return this.shutdown;
	}

	/**
	 * Check if shutdown command has been issued to this loop
	 * 
	 * @return
	 */
	public boolean isShuttingdown() {
		return this.shuttingdown;
	}

	/**
	 * Shutdown the loop
	 */
	private synchronized void shutdownLoop() {
		LOGGER.info("Shutting down Channel event loop");

		this.shuttingdown = true;
		this.executor.shutdownNow();
		this.shutdown = true;

		try {
			LOGGER.info("Unregistering channels from selector");
			Set<SelectionKey> keys = selector.selectedKeys();

			for (SelectionKey key : keys) {
				IOPort port = (IOPort) key.attachment();
				unregisterChannel(port);
			}

			LOGGER.debug("Closing selector");
			this.selector.close();

		} catch (Exception e) {
			LOGGER.warn("Failed to close selector. Ignoring...", e);
		}
	}

	/**
	 * Wraps a {@link IOPort} within a {@link FutureTask}. After the
	 * {@link IOPort} completes processing, it is placed onto status change
	 * queue (Blocking queue) for selection thread to examine
	 */
	private class HandlerFutureTask extends FutureTask<Boolean> {
		private final IOPort ioPort;

		public HandlerFutureTask(IOPort ioPort) {
			super(ioPort, null);
			this.ioPort = ioPort;
		}

		protected void done() {
			enqueueStatusChange(ioPort);

			try {
				// Get result returned by call(), or cause
				// deferred exception to be thrown. We know
				// the result will be the IOPort instance
				// stored above, so we ignore it.
				get();

			} catch (ExecutionException e) {
				// Seems like the execution task within executer service has
				// thrown exception
				ioPort.die();
				LOGGER.warn("Handler died", e.getCause());
			} catch (InterruptedException e) {
				Thread.interrupted();
				LOGGER.warn("Handler interrupted", e);
			}
		}

		/**
		 * Place the {@link IOPort} instance on the status change queue. The
		 * loop and nested try/catch blocks have been added to properly handle
		 * the InterruptedException that might be thrown when adding to the
		 * completion queue. The infinite loop enforces that we exit only when
		 * we add the {@link IOPort} instance to queue. We need to check the
		 * impact of {@link Selector#wakeup()}. This might cause
		 * {@link ServerIO} dispatcher to quit its first selection by the
		 * {@link Selector}
		 */
		public void enqueueStatusChange(IOPort ioPort) {
			boolean interrupted = false;

			try {
				while (true) {
					try {
						ioPortStateChangeQ.put(ioPort);
						// This method is exlusively used by workers running
						// HandlerFutureTask (processing dispatched events from
						// selector). As selector takes guard before calling
						// select op, there is no need to wake up selector.

						// selector.wakeup();
						return;
					} catch (InterruptedException e) {
						interrupted = true;
					}
				}
			} finally {
				if (interrupted)
					Thread.currentThread().interrupt();
			}
		}
	}

}
