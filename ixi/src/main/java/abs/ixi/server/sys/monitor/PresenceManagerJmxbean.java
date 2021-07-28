package abs.ixi.server.sys.monitor;

import java.lang.reflect.Constructor;

import javax.management.AttributeNotFoundException;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.ReflectionException;

import abs.ixi.server.PacketEnvelope;
import abs.ixi.server.common.ConcurrentQueue;
import abs.ixi.server.packet.Packet;
import abs.ixi.server.session.PresenceManager;

public class PresenceManagerJmxbean extends ServerComponentMBean {

	private ConcurrentQueue<PacketEnvelope<? extends Packet>> inboundQ;
	private PresenceManager presenceMgr;

	public PresenceManagerJmxbean(ConcurrentQueue<PacketEnvelope<? extends Packet>> inboundQ,
			PresenceManager presenceMgr) {
		this.presenceMgr = presenceMgr;
		this.inboundQ = inboundQ;
	}

	@Override
	public Object getAttribute(String attribute)
			throws AttributeNotFoundException, MBeanException, ReflectionException {
		if (attribute.equals("inboudPacketCount")) {
			return this.presenceMgr.getInboundPacketCount();
		} else if (attribute.equals("outboudPacketCount")) {
			return this.presenceMgr.getOutboundPacketCount();
		} else if (attribute.equals("incomingPacketQueueSize")) {
			return this.inboundQ.size();
		} else {
			return null;
		}
	}

	@Override
	public MBeanInfo getMBeanInfo() {

		MBeanInfo componentBean = super.getMBeanInfo();
		Constructor<?> cons[] = this.getClass().getConstructors();
		MBeanConstructorInfo[] dConstructors = new MBeanConstructorInfo[1];
		dConstructors[0] = new MBeanConstructorInfo("SimpleDynamic(): No-parameter constructor", // description
				cons[0]);

		MBeanInfo info = new MBeanInfo("abs.ixi.server.jmx.PresenceManagerJmxBean", "PresenceManager",
				componentBean.getAttributes(), dConstructors, componentBean.getOperations(),
				new MBeanNotificationInfo[0]);

		return info;
	}
}
