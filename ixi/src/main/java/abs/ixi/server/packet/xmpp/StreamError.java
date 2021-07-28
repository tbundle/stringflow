package abs.ixi.server.packet.xmpp;

public class StreamError extends XMPPPacket {
    private static final long serialVersionUID = -8385619918759296019L;

    public static final PacketXmlElement XML_ELM_NAME = PacketXmlElement.STREAM_ERROR;

    private String streamErrorXML;
    private String sourceId;

    public StreamError(String streamErrorXML) {
	this.streamErrorXML = streamErrorXML;
    }

    public String getStreamErrorXML() {
	return streamErrorXML;
    }

    public void setStreamErrorXML(String streamErrorXML) {
	this.streamErrorXML = streamErrorXML;
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
	return this.streamErrorXML;
    }

    @Override
    public PacketXmlElement getXmlElementName() {
	return XML_ELM_NAME;
    }

}
