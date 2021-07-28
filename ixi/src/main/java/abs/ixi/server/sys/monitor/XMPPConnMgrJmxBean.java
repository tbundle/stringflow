package abs.ixi.server.sys.monitor;

import java.beans.ConstructorProperties;
import java.lang.reflect.Constructor;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;

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

import abs.ixi.server.io.net.XMPPConnectionManager;

public class XMPPConnMgrJmxBean implements IXIMBean {

    private XMPPConnectionManager connMgr;
    private BlockingQueue<SocketChannel> incomingConnQ;

    @ConstructorProperties({ "connMgr", "incomingConnQ" })
    public XMPPConnMgrJmxBean(XMPPConnectionManager connMgr, BlockingQueue<SocketChannel> incomingConnQ) {
	this.setConnMgr(connMgr);
	this.incomingConnQ = incomingConnQ;
    }

    @Override
    public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
	if (attribute.equals("inboudPacketCount")) {
	    // return this.connMgr.getInboundPacketCount();
	} else if (attribute.equals("outboudPacketCount")) {
	    // return this.connMgr.getOutboundPacketCount();
	} else if (attribute.equals("incomingConnQueueSize")) {
	    return this.incomingConnQ.size();
	} else {
	    return null;
	}
	return null;
    }

    @Override
    public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException,
	    MBeanException, ReflectionException {
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
    public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException,
	    ReflectionException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public MBeanInfo getMBeanInfo() {
	MBeanAttributeInfo inboudPacketCount = new MBeanAttributeInfo("inboudPacketCount", "int", "inboudPacketCount",
		true, false, false);
	MBeanAttributeInfo outboudPacketCount = new MBeanAttributeInfo("outboudPacketCount", "int",
		"outboudPacketCount", true, false, false);
	MBeanAttributeInfo incomingConnQueueSize = new MBeanAttributeInfo("incomingConnQueueSize", "int",
		"incomingQueueSize", true, false, false);
	MBeanAttributeInfo[] attributes = new MBeanAttributeInfo[3];
	attributes[0] = inboudPacketCount;
	attributes[1] = outboudPacketCount;
	attributes[2] = incomingConnQueueSize;

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

	MBeanInfo info = new MBeanInfo("abs.ixi.server.jmx.XMPPConnMgrJmxBean", "ThreadPool", attributes,
		dConstructors, dOperations, new MBeanNotificationInfo[0]);
	return info;
    }

    public XMPPConnectionManager getConnMgr() {
	return connMgr;
    }

    public void setConnMgr(XMPPConnectionManager connMgr) {
	this.connMgr = connMgr;
    }

}
