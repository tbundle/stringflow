package abs.ixi.server.packet.xmpp;

import abs.ixi.server.packet.JID;

public class SASLSuccess extends XMPPPacket {
    private static final long serialVersionUID = 6544366089592713172L;

    public static final PacketXmlElement XML_ELM_NAME = PacketXmlElement.SASL_SUCCESS;

    private static final String SASL_SUCCESS_XML = "<success xmlns='urn:ietf:params:xml:ns:xmpp-sasl'/>".intern();

    private JID userJID;
    private String sourceId;

    public SASLSuccess(JID userJID) {
	this.userJID = userJID;
    }

    public JID getUserJID() {
	return userJID;
    }

    public void setUserJID(JID userJID) {
	this.userJID = userJID;
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
	return SASL_SUCCESS_XML;
    }

    @Override
    public PacketXmlElement getXmlElementName() {
	return XML_ELM_NAME;
    }
}
