package abs.ixi.server.common;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * It's just a LinkedBlockingQueue for now; however I need to build ability to
 * tune its concurrency based on number of consumer threads running in system
 * Might introduce a collection of queues to reduce the contention among threads
 */
public class ConcurrentQueue<E> extends LinkedBlockingQueue<E> implements SynchronizedQueue<E> {
	private static final long serialVersionUID = 1L;

}
