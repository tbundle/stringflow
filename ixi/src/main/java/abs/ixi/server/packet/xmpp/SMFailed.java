package abs.ixi.server.packet.xmpp;

public class SMFailed extends XMPPPacket {
    private static final long serialVersionUID = -6504901618244621388L;

    public static final PacketXmlElement XML_ELM_NAME = PacketXmlElement.SM_FAILED;
    
    private static final String SM_FAILED_XML = "<failed xmlns='urn:xmpp:sm:3'>"
	    + "<item-not-found xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>" + "</failed>";

    private String sourceId;

    public void setSourceId(String sourceId) {
	this.sourceId = sourceId;
    }

    @Override
    public String getSourceId() {
	return sourceId;
    }

    @Override
    public String xml() {
	return SM_FAILED_XML;
    }

    @Override
    public PacketXmlElement getXmlElementName() {
	return XML_ELM_NAME;
    }
}
