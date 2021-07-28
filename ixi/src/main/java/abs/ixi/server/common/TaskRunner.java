package abs.ixi.server.common;

import static abs.ixi.server.etc.conf.Configurations.Bundle.PROCESS;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.Stringflow;
import abs.ixi.server.etc.conf.Configurations;
import abs.ixi.server.etc.conf.ProcessConfigAware;
import abs.ixi.server.sys.monitor.IxiJmxTaskRunnerBean;
import abs.ixi.server.sys.monitor.JmxRegistrar;

/**
 * Common thread-pool to run all the server tasks.
 */
public final class TaskRunner implements ProcessConfigAware {
	private static final Logger LOGGER = LoggerFactory.getLogger(TaskRunner.class);

	/**
	 * Default core pool size of executor service
	 */
	private static final int CORE_POOL_SIZE = 1;

	/**
	 * Default max pool size of executor service
	 */
	private static final int MAX_POOL_SIZE = 2;

	/**
	 * Keep alive time in minutes
	 */
	private static final int KEEP_ALLIVE_TIME = 2;

	/**
	 * General purpose executor service
	 */
	private ExecutorService execService;

	/**
	 * Executor service for Scheduling
	 */
	private ScheduledExecutorService scheduler;

	/**
	 * Singleton instance
	 */
	private static TaskRunner instance;

	private TaskRunner() {
		Configurations conf = Stringflow.runtime().configurations();
		instantiateExecutor(conf);
		instantiateScheduledExecutor(conf);
	}

	private void instantiateExecutor(Configurations conf) {
		int cps = conf.getOrDefaultInteger(_TASK_RUNNER_THREADPOOL_CORE_SIZE, CORE_POOL_SIZE, PROCESS);
		if (cps < 0) {
			LOGGER.warn("Invalid core pool size;defaulting to {}", CORE_POOL_SIZE);
			cps = CORE_POOL_SIZE;
		}

		int mps = conf.getOrDefaultInteger(_TASK_RUNNER_THREADPOOL_MAX_SIZE, MAX_POOL_SIZE, PROCESS);
		if (mps < 0) {
			LOGGER.warn("Invalid max pool size;defaulting to {}", MAX_POOL_SIZE);
			mps = MAX_POOL_SIZE;
		}

		int keepAlive = conf.getOrDefaultInteger(_TASK_RUNNER_THREADPOOl_KEEPALIVE, KEEP_ALLIVE_TIME, PROCESS);
		if (keepAlive < 0) {
			LOGGER.warn("Invalid keep alive time;defaulting to {} minutes", KEEP_ALLIVE_TIME);
			keepAlive = KEEP_ALLIVE_TIME;
		}

		this.execService = new ThreadPoolExecutor(cps, mps, keepAlive, TimeUnit.MINUTES, new LinkedBlockingQueue<>());

		try {
			JmxRegistrar.registerBean("abs.ixi.server.jmx:type=TaskRunner",
					new IxiJmxTaskRunnerBean((ThreadPoolExecutor) this.execService));
		} catch (MalformedObjectNameException | InstanceAlreadyExistsException | MBeanRegistrationException
				| NotCompliantMBeanException e) {
			e.printStackTrace();
		}

		LOGGER.info("TaskRunner is initilized with core pool size : {} max poll size : {} and keep alive time : {}",
				cps, mps, keepAlive);

	}

	private void instantiateScheduledExecutor(Configurations conf) {
		int cps = conf.getOrDefaultInteger(_TASK_RUNNER_SCHEDULE_THREADPOOL_CORE_SIZE, CORE_POOL_SIZE, PROCESS);
		if (cps < 0) {
			LOGGER.warn("Invalid value of scheduler thredapool core pool size;defaulting to {}", CORE_POOL_SIZE);
			cps = CORE_POOL_SIZE;
		}

		int mps = conf.getOrDefaultInteger(_TASK_RUNNER_SCHEDULE_THREADPOOL_MAX_SIZE, MAX_POOL_SIZE, PROCESS);
		if (mps < 0) {
			LOGGER.warn("Invalid value of scheduler thredapool max pool size; defaulting to {}", MAX_POOL_SIZE);
			mps = MAX_POOL_SIZE;
		}

		int keepAlive = conf.getOrDefaultInteger(_TASK_RUNNER_SCHEDULE_THREADPOOL_KEEPALIVE, KEEP_ALLIVE_TIME, PROCESS);
		if (keepAlive < 0) {
			LOGGER.info("Invalid value of scheduler thredapool keep alive;defaulting to {} minutes", KEEP_ALLIVE_TIME);
			keepAlive = KEEP_ALLIVE_TIME;
		}

		// TODO Make use of other properties such as keep alive time, max pool
		// size for scheduled pool
		this.scheduler = new ScheduledThreadPoolExecutor(cps);

		LOGGER.info(
				"TaskRunner's Schedular is initilized with core pool size : {} max poll size : {} and keep alive time : {}",
				cps, mps, keepAlive);

	}

	/**
	 * Returns singleton instance of {@link TaskRunner}
	 */
	public static TaskRunner getInstance() {
		if (instance == null) {
			LOGGER.debug("TaskRunner initialised");
			instance = new TaskRunner();
		}

		return instance;
	}

	/**
	 * Submit a task executed by {@link TaskRunner}
	 * 
	 * @param task
	 * @return
	 */
	public <T> Future<T> submit(Callable<T> task) {
		return execService.submit(task);
	}

	public void execute(Runnable task) {
		execService.execute(task);
	}

	/**
	 * Schedule a task run by {@link TaskRunner}
	 * 
	 * @param task
	 * @param time
	 * @param unit
	 * @return
	 */
	public <T> Future<T> schedule(Callable<T> task, long time, TimeUnit unit) {
		return scheduler.schedule(task, time, unit);
	}

	/**
	 * Schedule a task to be run by {@link TaskRunner}
	 * 
	 * @param task
	 * @param time
	 * @param unit
	 */
	public void schedule(Runnable task, long time, TimeUnit unit) {
		scheduler.schedule(task, time, unit);
	}

}
