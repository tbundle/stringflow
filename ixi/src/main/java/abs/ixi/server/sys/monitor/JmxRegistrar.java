package abs.ixi.server.sys.monitor;

import java.lang.management.ManagementFactory;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class to offer utility methods to register a MBean with JMX agent.
 */
public class JmxRegistrar {
	private static final Logger LOGGER = LoggerFactory.getLogger(JmxRegistrar.class);

	/**
	 * Register a MBean with JMX agent. The methods starts a JMX agent if it is
	 * not already running.
	 * 
	 * @param beanName
	 * @param bean
	 * @throws MalformedObjectNameException
	 * @throws InstanceAlreadyExistsException
	 * @throws MBeanRegistrationException
	 * @throws NotCompliantMBeanException
	 */
	public static void registerBean(String beanName, IXIMBean bean) throws MalformedObjectNameException,
			InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {

		MBeanServer beanServer = ManagementFactory.getPlatformMBeanServer();
		ObjectName mBeanName = new ObjectName(beanName);
		beanServer.registerMBean(bean, mBeanName);
	}

	/**
	 * Register a MBean with JMX agent; the method swallows all the exceptions
	 * after logging them at error level.
	 * <p>
	 * The methods starts a JMX agent if it is not already running.
	 * </p>
	 * 
	 * @param beanName
	 * @param bean
	 */
	public static void registerBeanSilently(String beanName, IXIMBean bean) {
		try {
			registerBean(beanName, bean);
		} catch (MalformedObjectNameException | InstanceAlreadyExistsException | MBeanRegistrationException
				| NotCompliantMBeanException e) {
			LOGGER.error("Failed to register jmx bean {}", beanName, e);
		}
	}

}
