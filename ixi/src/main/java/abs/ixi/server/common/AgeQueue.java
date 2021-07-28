package abs.ixi.server.common;

import java.time.Duration;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A queue data structure which drops objects automatically after a predefined
 * period of time known as <i>object life</i>. {@code AgeQueue} accepts a
 * {@link Duration} which is the time each element is allowed to spent within
 * the queue (<i>Object Life</i>); after which queue automatically removes the
 * element. {@code AgeQueue} is a living collection-<i>a collection which is a
 * running process in itself</i>.
 * <p>
 * The implementation is customized to suit Stringflow server requirements; it
 * takes many assumptions to boost queue performance. Here are few-
 * <ul>
 * <li>AgeQueue size calculations do not lock the queue store; resulting
 * possibility of incorrect size if there are concurrent add/remove operations
 * in progress</li>
 * <li>Similarly <i>contains</i> operation on queue does not lock the queue
 * which may result incorrect result for elements getting removed or added into
 * the queue concurrently</li>
 * <li>isEmpty operation also has the similar behavior as above two</li>
 * <li>Operations such as add/remove has been synchronized though</li>
 * </ul>
 * </p>
 * 
 * @author Yogendra Sharma
 *
 * @param <OBJECT>
 */
public class AgeQueue<OBJECT> {
	private static final Logger LOGGER = LoggerFactory.getLogger(AgeQueue.class);

	/**
	 * Default object life in the queue
	 */
	private static final Duration DEFAULT_ELEMENT_LIFE = Duration.ofSeconds(30);

	/**
	 * Default slice duration
	 */
	private static final Duration DEFAULT_SLICE_DURATION = Duration.ofSeconds(2);

	/**
	 * Number of slices this queue will have. This is calculated based on the
	 * max-element-life and the slice-duration for this queue
	 */
	private int sliceCount;

	/**
	 * Each slice within the queue represents a duration. All the slices are of
	 * same duration.
	 */
	private Duration sliceDuration;

	/**
	 * Max duration an element can spent in this queue. After this duration, the
	 * element will be dropped out of the queue.
	 */
	private Duration elmMaxLife;

	/**
	 * Collection of all the {@link TimeSlice}s in this queue.
	 */
	private Queue<TimeSlice<OBJECT>> timeSlices;

	/**
	 * Ongoing time-slice. Any element which is added to the queue will be
	 * placed in this time-slice.
	 */
	private TimeSlice<OBJECT> runningSlice;

	/**
	 * A common death handlder for queue elements. Death handler is invoked for
	 * each of the elements which dies through aging.
	 */
	private DeathHandler<OBJECT> deathHandler;

	/**
	 * {@link TimeSliceGenerator} thread instance
	 */
	private TimeSliceGenerator timeSlicer;

	public AgeQueue() {
		this(DEFAULT_ELEMENT_LIFE, DEFAULT_SLICE_DURATION);
	}

	public AgeQueue(Duration elmLife) {
		this(elmLife, DEFAULT_SLICE_DURATION);
	}

	public AgeQueue(Duration elmLife, Duration sliceDuration) {
		this(elmLife, sliceDuration, null);
	}

	public AgeQueue(DeathHandler<OBJECT> deathHandler) {
		this(DEFAULT_ELEMENT_LIFE, DEFAULT_SLICE_DURATION, deathHandler);
	}

	public AgeQueue(Duration elmLife, Duration sliceDuration, DeathHandler<OBJECT> deathHandler) {
		this.timeSlices = new ConcurrentLinkedQueue<>();
		this.elmMaxLife = elmLife;
		this.sliceDuration = sliceDuration;
		this.deathHandler = deathHandler;

		this.sliceCount = calcSliceCount();
	}

	/**
	 * Calculate the number of time slices in this {@link AgeQueue}
	 */
	private int calcSliceCount() {
		long secs = this.elmMaxLife.getSeconds() / this.sliceDuration.getSeconds();

		return (int) secs;
	}

	/**
	 * {@link AgeQueue} is a live collection; this method starts the collection
	 * by setting up various objects and threads. Time slice generator thread is
	 * started here.
	 */
	public void start() {
		setupTimeSlicerThread();
	}

	/**
	 * Setup {@link TimeSliceGenerator} thread for this queue
	 */
	private void setupTimeSlicerThread() {
		LOGGER.info("Setting up TimeSlicer thread");
		this.timeSlicer = new TimeSliceGenerator(this);

		this.timeSlicer.start();
		LOGGER.info("Started TimeSlicer thread");
	}

	public void add(OBJECT key) {
		this.runningSlice.addOrIncrement(key);
	}

	/**
	 * Push a new {@link TimeSlice} instance on to the queue. This is likely to
	 * push another time slice out of the queue due to aging; and if so,
	 * {@link DeathHandler} will be invoked for all the objects being dropped
	 * from the queue.
	 * <p>
	 * The method has default access and is expected to be used by
	 * {@link TimeSliceGenerator} only.
	 * </p>
	 */
	void pushTimeSlice() {
		this.runningSlice = new TimeSlice<>();
		this.timeSlices.add(this.runningSlice);

		while (this.timeSlices.size() > this.sliceCount) {
			TimeSlice<OBJECT> slice = this.timeSlices.poll();

			if (slice != null && this.deathHandler != null) {
				slice.invoke(this.deathHandler);
			}
		}
	}

	public int size() {
		return this.timeSlices.size() > 0 ? this.timeSlices.stream().mapToInt(v -> v.size()).sum() : 0;
	}

	public boolean isEmpty() {
		if (this.timeSlices.size() > 0) {
			for (TimeSlice<OBJECT> ts : this.timeSlices) {
				if (ts.size() > 0) {
					return false;
				}
			}
		}

		return true;
	}

	public boolean contains(OBJECT o) {
		if (this.timeSlices.size() > 0) {
			for (TimeSlice<OBJECT> ts : this.timeSlices) {
				if (ts.contains(o)) {
					return true;
				}
			}
		}

		return false;
	}

	public void dropObjects(OBJECT o) {
		if (this.timeSlices.size() > 0) {
			for (TimeSlice<OBJECT> ts : this.timeSlices) {
				if (ts.contains(o)) {
					ts.remove(o);
				}
			}
		}
	}

	public void stop() {
		this.shutdownTimeSlice();

		while (!this.timeSlices.isEmpty()) {
			TimeSlice<OBJECT> slice = this.timeSlices.poll();

			if (slice != null && this.deathHandler != null) {
				slice.invoke(this.deathHandler);
			}
		}
	}

	private void shutdownTimeSlice() {
		try {
			this.timeSlicer.shutdown();
		} catch (InterruptedException e) {
			// swallow exception
		}

		this.timeSlicer = null;
	}

	public Duration getSliceDuration() {
		return sliceDuration;
	}

}
