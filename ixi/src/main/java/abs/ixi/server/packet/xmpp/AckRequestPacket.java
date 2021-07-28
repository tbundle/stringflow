package abs.ixi.server.packet.xmpp;

import abs.ixi.xml.Element;
import abs.ixi.xml.XMLUtils;

public class AckRequestPacket extends XMPPPacket {
    private static final long serialVersionUID = -5614532317211621680L;

    public static final PacketXmlElement XML_ELM_NAME = PacketXmlElement.ACK_REQUEST;

    private static final String REQUEST_XML = "<r xmlns='urn:xmpp:sm:3'/>".intern();

    private String xmlns;
    private String sourceId;

    public AckRequestPacket(Element element) {
	this.setXmlns(element.getAttribute(XMLUtils.XMLNS_ATTRIBUTE));
    }

    public String getXmlns() {
	return xmlns;
    }

    public void setXmlns(String xmlns) {
	this.xmlns = xmlns;
    }

    public void setSourceId(String sourceId) {
	this.sourceId = sourceId;
    }

    @Override
    public String getSourceId() {
	return sourceId;
    }

    @Override
    public String xml() {
	return REQUEST_XML;
    }

    @Override
    public PacketXmlElement getXmlElementName() {
	return XML_ELM_NAME;
    }
}
