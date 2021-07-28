package abs.ixi.server.sys.monitor;

import java.beans.ConstructorProperties;
import java.lang.reflect.Constructor;
import java.util.concurrent.ThreadPoolExecutor;

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

public class IxiJmxTaskRunnerBean implements IXIMBean {

	private ThreadPoolExecutor pool;
	
	@ConstructorProperties({"pool"})
	public IxiJmxTaskRunnerBean(ThreadPoolExecutor pool){
		this.pool = pool;
	}
	
	

	@Override
	public Object getAttribute(String attribute)
			throws AttributeNotFoundException, MBeanException, ReflectionException {

		if(attribute.equals("activeThreadCount")){
			return getActiveThreadCount();
		}else if(attribute.equals("QueueRemainingCapacity")){
			return getQueueRemainingCapacity();
		}else {
			return null;
		}
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
		
		MBeanAttributeInfo activeThreadattInfo = new MBeanAttributeInfo("activeThreadCount","int","activeThreadCount",true,false,false);
		MBeanAttributeInfo queueRemainingCapacityattInfo = new MBeanAttributeInfo("QueueRemainingCapacity","int","QueueRemainingCapacity",true,false,false);
		MBeanAttributeInfo []attributes = new MBeanAttributeInfo[2];
		attributes[0] = activeThreadattInfo;
		attributes[1] = queueRemainingCapacityattInfo;
	
		Constructor<?> cons[] = this.getClass().getConstructors();
		MBeanConstructorInfo[] dConstructors = new MBeanConstructorInfo[1];
		dConstructors[0] = new MBeanConstructorInfo(
			        "SimpleDynamic(): No-parameter constructor",  //description
			        cons[0]); 
		
		MBeanParameterInfo[] params = null;
		MBeanOperationInfo[] dOperations = new MBeanOperationInfo[1];
		dOperations[0] = new MBeanOperationInfo(
		        "reset",                     // name
		        "Resets State and NbChanges attributes to their initial values",
		                                     // description
		        params,                      // parameter types
		        "void",                      // return type
		        MBeanOperationInfo.ACTION);  // impact
		
		
		MBeanInfo info = new MBeanInfo(
								"abs.ixi.server.jmx.IxiJmxThreadPoolBean",
								"ThreadPool",
								attributes,
								dConstructors,
								dOperations,
								new MBeanNotificationInfo[0]);
		return info;
	}



	public int getActiveThreadCount() {
		
		return this.pool.getActiveCount();
	}


	public int getQueueRemainingCapacity() {
		// TODO Auto-generated method stub
		return this.pool.getQueue().remainingCapacity();
	}

}
