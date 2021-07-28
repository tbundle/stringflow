package abs.ixi.server.packet.xmpp;

public class TLSProceed extends XMPPPacket {
    private static final long serialVersionUID = 2457705187192157940L;

    public static final PacketXmlElement TLS_PROCEED = PacketXmlElement.TLS_PROCEED;

    private static final String TLS_PROCEED_XML = "<proceed xmlns='urn:ietf:params:xml:ns:xmpp-tls'/>".intern();

    private String sourceId;

    public void setSourceId(String sourceId) {
	this.sourceId = sourceId;
    }

    @Override
    public String getSourceId() {
	return this.sourceId;
    }

    @Override
    public String xml() {
	return TLS_PROCEED_XML;
    }

    @Override
    public PacketXmlElement getXmlElementName() {
	return TLS_PROCEED;
    }
}
