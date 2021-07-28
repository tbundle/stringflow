package abs.ixi.server.sys.monitor;

import java.lang.reflect.Constructor;
import java.util.Map;

import javax.management.AttributeNotFoundException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.ReflectionException;

import abs.ixi.server.PacketConsumer;
import abs.ixi.server.PacketEnvelope;
import abs.ixi.server.common.ConcurrentQueue;
import abs.ixi.server.packet.Packet;
import abs.ixi.server.router.PacketRouter;

public class PacketRouterJmxBean extends ServerComponentMBean {
	// private ConcurrentQueue<PacketEnvelope<? extends Packet>> inboundQ;
	// private Map<String, PacketConsumer> components;
	// private PacketRouter router;

	public PacketRouterJmxBean(ConcurrentQueue<PacketEnvelope<? extends Packet>> inboundQ,
			Map<String, PacketConsumer> components, PacketRouter router) {
		// this.inboundQ = inboundQ;
		// this.components = components;
		// this.router = router;
	}

	@Override
	public Object getAttribute(String attribute)
			throws AttributeNotFoundException, MBeanException, ReflectionException {
		return null;
	}

	@Override
	public MBeanInfo getMBeanInfo() {
		MBeanInfo componentBean = super.getMBeanInfo();
		MBeanAttributeInfo serverComponents = new MBeanAttributeInfo("serverComponents", "map", "serverComponents",
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
