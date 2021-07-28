package abs.ixi.server.sys.monitor;

import java.lang.reflect.Constructor;

import javax.management.AttributeNotFoundException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.ReflectionException;

import abs.ixi.server.DiscoHandler;
import abs.ixi.server.PacketEnvelope;
import abs.ixi.server.common.ConcurrentQueue;
import abs.ixi.server.packet.Packet;

public class DiscoHandlerJmxBean extends ServerComponentMBean {

	private ConcurrentQueue<PacketEnvelope<? extends Packet>> inboundQ;
	private DiscoHandler discoHandler;

	public DiscoHandlerJmxBean(ConcurrentQueue<PacketEnvelope<? extends Packet>> inboundQ, DiscoHandler discoHandler) {
		this.inboundQ = inboundQ;
		this.discoHandler = discoHandler;
	}

	@Override
	public Object getAttribute(String attribute)
			throws AttributeNotFoundException, MBeanException, ReflectionException {
		if (attribute.equals("inboudPacketCount")) {
			return this.discoHandler.getInboundPacketCount();
		} else if (attribute.equals("outboudPacketCount")) {
			return this.discoHandler.getOutboundPacketCount();
		} else if (attribute.equals("incomingPacketQueueSize")) {
			return this.inboundQ.size();
		} else {
			return null;
		}
	}

	@Override
	public MBeanInfo getMBeanInfo() {
		MBeanInfo componentBean = super.getMBeanInfo();
		MBeanAttributeInfo serverComponents = new MBeanAttributeInfo("activeThreadCount", "int", "activeThreadCount",
				true, false, false);
		MBeanAttributeInfo[] attributes = new MBeanAttributeInfo[componentBean.getAttributes().length + 1];
		attributes[0] = serverComponents;

		int i = 1;
		for (MBeanAttributeInfo att : componentBean.getAttributes()) {
			attributes[i++] = att;
		}

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

		MBeanInfo info = new MBeanInfo("abs.ixi.server.jmx.PacketRouterJmxBean", "PacketRouter", attributes,
				dConstructors, dOperations, new MBeanNotificationInfo[0]);
		return info;
	}
}
