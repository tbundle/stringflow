package abs.ixi.server.common;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A thread which continuously pushes {@link TimeSlice} on the queue at regular
 * intervals.
 * 
 * @author Yogendra Sharma
 *
 */
public class TimeSliceGenerator extends Thread {
	private static final Logger LOGGER = LoggerFactory.getLogger(TimeSliceGenerator.class);

	/**
	 * Slice generator thread name
	 */
	private static final String THREAD_NAME = "TimeSliceGenerator";

	/**
	 * {@link AgeQueue} instance which this thread is associated to
	 */
	private AgeQueue<?> ageQ;

	/**
	 * A flag to indicate if this thread is running currently
	 */
	private volatile boolean running = false;

	/**
	 * Flag to indicate if the thread has been stopped
	 */
	private volatile boolean stopped = false;

	public TimeSliceGenerator(AgeQueue<?> ageQ) {
		super(THREAD_NAME);
		this.ageQ = ageQ;
	}

	@Override
	public void run() {
		LOGGER.info("Running AgeQueue TimeSlicer Thread");

		this.running = true;
		this.stopped = false;

		while (!Thread.interrupted() && this.running) {
			this.ageQ.pushTimeSlice();

			try {
				TimeUnit.SECONDS.sleep(this.ageQ.getSliceDuration().getSeconds());
			} catch (InterruptedException e) {
				// Ignore
			}
		}

		this.running = false;
		this.stopped = true;

		LOGGER.info("Exiting AgeQueue TimeSlicer Thread");
	}

	/**
	 * Turn off the {@link TimeSliceGenerator#running} flag which will result in
	 * {@code TimeSlicerThread} to quit running in next interactions.
	 * 
	 * @throws InterruptedException
	 */
	public void shutdown() throws InterruptedException {
		if (running) {
			this.running = false;

			while (!stopped) {
				LOGGER.info("TimeSlicer is currently running. Waiting to shutdown...");
				TimeUnit.SECONDS.sleep(2);
			}
		}
	}

}
