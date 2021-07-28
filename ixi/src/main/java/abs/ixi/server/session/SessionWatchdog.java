package abs.ixi.server.session;

import static abs.ixi.server.etc.conf.Configurations.Bundle.PROCESS;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.ServerStartupException;
import abs.ixi.server.Stringflow;
import abs.ixi.server.common.Initializable;
import abs.ixi.server.common.InitializationException;
import abs.ixi.server.common.TaskRunner;
import abs.ixi.server.etc.conf.ProcessConfigAware;
import abs.ixi.util.ObjectUtils;

/**
 * A dameon process which removes inactive {@link UserSession}s and
 * {@link LocalSession}s from memory.
 * 
 * @author Yogi
 *
 */
public class SessionWatchdog implements Initializable, Runnable, ProcessConfigAware {
	private static final Logger LOGGER = LoggerFactory.getLogger(SessionWatchdog.class.getName());

	/**
	 * Default inactivity time after which {@link LocalSession} will be verified
	 * if it is still active.
	 */
	public static final long DEFAULT_INACTIVITY_THRESHOLD = Duration.ofMinutes(10).toMillis();

	/**
	 * Watchdog execution frequency. Ideally it should always be less than
	 * {@link SessionWatchdog#DEFAULT_INACTIVITY_THRESHOLD}
	 */
	public static final long DEFAULT_FREQUENCY = Duration.ofMinutes(5).toMinutes();

	/**
	 * Watchdog scans sessions periodically; runFrequency is the time (in
	 * millis) interval between two consequitive executions. The frequency can
	 * be configured in server configurations.
	 */
	private long runFrequency;

	/**
	 * Time in millis for which if a session is inactive, a verification is
	 * triggerred
	 */
	private long inactivityThreshold;

	/**
	 * Flag to indicates if the watchdog is running currently
	 */
	private volatile boolean running;

	/**
	 * Flag to indicate if the watchdog is dead
	 */
	private volatile boolean dead;

	/**
	 * A flag to indicate of the {@code SessionWatchdog} has successfully
	 * executed initialization sequence
	 */
	private volatile boolean initialized;

	@Override
	public void init() throws InitializationException {
		LOGGER.info("Initializing SessionWatchDog");

		String strFreq = Stringflow.runtime().configurations().get(_SESSION_WATCHDOG_FREQUENCY, PROCESS);
		int freq = ObjectUtils.parseToInt(strFreq);

		if (freq <= 0) {
			LOGGER.error("Invalid SessionWatchDog frequency {}. Defaulting to {}", strFreq, DEFAULT_FREQUENCY);
			this.runFrequency = DEFAULT_FREQUENCY;

		} else {
			this.runFrequency = freq;
		}

		// For now its done but read it from configurations
		this.inactivityThreshold = DEFAULT_INACTIVITY_THRESHOLD;

		this.dead = false;

		this.initialized = true;

		LOGGER.info("Connection watchdog has initialized...");
	}

	/**
	 * Start SessionWatchDog
	 * 
	 * @throws ServerStartupException
	 */
	public void start() throws ServerStartupException {
		LOGGER.info("Starting SessionWacthDog...");

		if (this.initialized) {
			LOGGER.info("Scheduling Watchdog with {} minutes execution frequency", this.runFrequency);
			TaskRunner.getInstance().schedule(this, this.runFrequency, TimeUnit.MINUTES);

		} else {
			LOGGER.error("Failed to start SessionWatchDog: the watchdog has not been initialized");
			throw new ServerStartupException("Attempting to start session watchdog without initializing");
		}
	}

	@Override
	public void run() {
		if (this.dead) {
			return;
		}

		LOGGER.debug("SessionWatchDog is running");
		this.running = true;

		SessionManager.getInstance().runSessionCleanup(this.inactivityThreshold);

		this.running = false;
		LOGGER.debug("SessionWatchDog is exiting");
	}

	@Override
	public boolean isInitialized() {
		return this.initialized;
	}

	/**
	 * Check if the watchdog is running
	 */
	public boolean isRunning() {
		return this.running;
	}

	/**
	 * A blocking call to shutdown {@code DeadConnectionScanner}. If the
	 * scanning process is going on currently, the method call will block until
	 * scanning is finished.
	 * 
	 * @throws InterruptedException
	 */
	public void shutdownNow() throws InterruptedException {
		while (this.running) {
			LOGGER.info("Watchdog is scanning connections; waiting to finish...");
			TimeUnit.SECONDS.sleep(5);
		}

		this.dead = true;
	}

	/**
	 * The method simply changes {@link SessionWatchdog#dead} flag to false. If
	 * the watchdog is running currently, it will complete its execution.
	 */
	public void shutdown() {
		this.dead = true;
	}

}
