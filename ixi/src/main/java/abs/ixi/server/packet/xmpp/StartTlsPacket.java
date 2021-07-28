package abs.ixi.server.packet.xmpp;

import abs.ixi.xml.Element;

public class StartTlsPacket extends XMPPPacket {
    private static final long serialVersionUID = 8295211319586725278L;

    public static final PacketXmlElement XML_ELM_NAME = PacketXmlElement.START_TLS;

    private String START_TLS_XML = "<starttls xmlns='urn:ietf:params:xml:ns:xmpp-tls'/>";

    private String xmlns;
    private String sourceId;

    public StartTlsPacket(Element element) {
	this.xmlns = element.getAttribute("xmlns");
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
	return this.sourceId;
    }

    @Override
    public String xml() {
	return START_TLS_XML;
    }

    @Override
    public PacketXmlElement getXmlElementName() {
	return XML_ELM_NAME;
    }

}
