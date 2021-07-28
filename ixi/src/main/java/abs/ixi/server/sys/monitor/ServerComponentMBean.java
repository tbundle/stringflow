package abs.ixi.server.sys.monitor;

import java.lang.reflect.Constructor;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.ReflectionException;

/**
 * An abstract class to offer common attributes, constructor and operation info
 * for server component MBeans. These MBean are used for monitoring thorugh JMX.
 */
public abstract class ServerComponentMBean implements IXIMBean {

	@Override
	public Object getAttribute(String attribute)
			throws AttributeNotFoundException, MBeanException, ReflectionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAttribute(Attribute attribute)
			throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
		// TODO Auto-generated method stub

	}

	@Override
	public AttributeList getAttributes(String[] attributes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AttributeList setAttributes(AttributeList attributes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object invoke(String actionName, Object[] params, String[] signature)
			throws MBeanException, ReflectionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MBeanInfo getMBeanInfo() {
		MBeanAttributeInfo inboudPacketCount = new MBeanAttributeInfo("inboudPacketCount", "int", "inboudPacketCount",
				true, false, false);
		MBeanAttributeInfo outboudPacketCount = new MBeanAttributeInfo("outboudPacketCount", "int",
				"outboudPacketCount", true, false, false);
		MBeanAttributeInfo incomingPacketQueueSize = new MBeanAttributeInfo("incomingPacketQueueSize", "int",
				"incomingPacketQueueSize", true, false, false);
		MBeanAttributeInfo[] attributes = new MBeanAttributeInfo[3];
		attributes[0] = inboudPacketCount;
		attributes[1] = outboudPacketCount;
		attributes[2] = incomingPacketQueueSize;

		Constructor<?> cons[] = this.getClass().getConstructors();
		MBeanConstructorInfo[] dConstructors = new MBeanConstructorInfo[1];
		dConstructors[0] = new MBeanConstructorInfo("SimpleDynamic(): No-parameter constructor", // description
				cons[0]);

		MBeanParameterInfo[] params = null;
		MBeanOperationInfo[] dOperations = new MBeanOperationInfo[1];
		dOperations[0] = new MBeanOperationInfo("reset", // name
				"Resets State and NbChanges attributes to their initial values",
				// description
				params, // parameter types
				"void", // return type
				MBeanOperationInfo.ACTION); // impact

		MBeanInfo info = new MBeanInfo("abs.ixi.server.jmx.ServerIOJmxBean", "ThreadPool", attributes, dConstructors,
				dOperations, new MBeanNotificationInfo[0]);
		return info;
	}

}
