package abs.ixi.server.packet;

import abs.ixi.server.io.MalformedXMPPRequestException;
import abs.ixi.server.packet.xmpp.AckPacket;
import abs.ixi.server.packet.xmpp.AckRequestPacket;
import abs.ixi.server.packet.xmpp.BOSHBody;
import abs.ixi.server.packet.xmpp.IQ;
import abs.ixi.server.packet.xmpp.Message;
import abs.ixi.server.packet.xmpp.Presence;
import abs.ixi.server.packet.xmpp.SASLAuthPacket;
import abs.ixi.server.packet.xmpp.SMEnablePacket;
import abs.ixi.server.packet.xmpp.SMResumePacket;
import abs.ixi.server.packet.xmpp.StartTlsPacket;
import abs.ixi.server.packet.xmpp.StreamHeader;
import abs.ixi.server.packet.xmpp.XMPPPacket;
import abs.ixi.util.StringUtils;
import abs.ixi.xml.Element;

/**
 * Factory implementation to instantiate a {@link Packet} for a XML
 * {@link Element}. Based on the root element string, factory instantiate a
 * matching {@link Packet}
 * 
 * @author Yogi
 *
 */
public class XmppPacketFactory {
    private static final String ElEMENT_NAME_BODY = "body".intern();

    public static XMPPPacket createPacket(Element element) throws XMPPException, MalformedXMPPRequestException {
	if (StringUtils.safeEquals(element.getName(), Message.XML_ELM_NAME.elementNameString())) {
	    return new Message(element);

	} else if (StringUtils.safeEquals(element.getName(), Presence.XML_ELM_NAME.elementNameString())) {
	    return new Presence(element);

	} else if (StringUtils.safeEquals(element.getName(), IQ.XML_ELM_NAME.elementNameString())) {
	    return new IQ(element);

	} else if (StringUtils.safeEquals(element.getName(), StartTlsPacket.XML_ELM_NAME.elementNameString())) {
	    return new StartTlsPacket(element);

	} else if (StringUtils.safeEquals(element.getName(), SASLAuthPacket.XML_ELM_NAME.elementNameString())) {
	    return new SASLAuthPacket(element);

	} else if (StringUtils.safeEquals(element.getName(), StreamHeader.XML_ELM_NAME.elementNameString())) {
	    return new StreamHeader(element);

	} else if (StringUtils.safeEquals(element.getName(), SMEnablePacket.XML_ELM_NAME.elementNameString())) {
	    return new SMEnablePacket(element);

	} else if (StringUtils.safeEquals(element.getName(), SMResumePacket.XML_ELM_NAME.elementNameString())) {
	    return new SMResumePacket(element);

	} else if (StringUtils.safeEquals(element.getName(), AckPacket.XML_ELM_NAME.elementNameString())) {
	    return new AckPacket(element);

	} else if (StringUtils.safeEquals(element.getName(), AckRequestPacket.XML_ELM_NAME.elementNameString())) {
	    return new AckRequestPacket(element);

	} else {

	    throw new XMPPException("Unsupported xmpp packet " + element.getName());
	}

    }

    public static BOSHBody generateBoshBodyPacket(Element element) throws Exception {

	if (StringUtils.safeEquals(element.getName(), ElEMENT_NAME_BODY)) {
	    return new BOSHBody(element);
	}

	return null;
    }

}
