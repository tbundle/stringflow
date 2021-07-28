package abs.ixi.server.sys.monitor;

import java.beans.ConstructorProperties;
import java.lang.reflect.Constructor;

import javax.management.AttributeNotFoundException;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.ReflectionException;

import abs.ixi.server.io.net.NetworkTransport;

public class NetworkTransportJmxBean extends ServerComponentMBean {

	private NetworkTransport netTransport;

	@ConstructorProperties({ "networkTransport" })
	public NetworkTransportJmxBean(NetworkTransport transport) {
		this.setNetworkTransport(transport);
	}

	@Override
	public Object getAttribute(String attribute)
			throws AttributeNotFoundException, MBeanException, ReflectionException {
		if (attribute.equals("inboudPacketCount")) {
			// return this.serverIO.getInboundPacketCount();
		} else if (attribute.equals("outboudPacketCount")) {
			// return this.serverIO.getOutboundPacketCount();
			// } else if (attribute.equals("incomingPacketQueueSize")) {
			// return this.inboundQ.size();
		} else {
			return null;
		}

		return null;
	}

	@Override
	public MBeanInfo getMBeanInfo() {
		MBeanInfo componentBean = super.getMBeanInfo();
		Constructor<?> cons[] = this.getClass().getConstructors();
		MBeanConstructorInfo[] dConstructors = new MBeanConstructorInfo[1];
		dConstructors[0] = new MBeanConstructorInfo("SimpleDynamic(): No-parameter constructor", // description
				cons[0]);

		MBeanInfo info = new MBeanInfo("abs.ixi.server.jmx.ServerIOJmxBean", "ServerIOJmxBean",
				componentBean.getAttributes(), dConstructors, componentBean.getOperations(),
				new MBeanNotificationInfo[0]);

		return info;
	}

	public NetworkTransport getNetworkTransport() {
		return netTransport;
	}

	public void setNetworkTransport(NetworkTransport transport) {
		this.netTransport = transport;
	}

}
