package abs.ixi.server.packet.xmpp;

import abs.ixi.server.packet.JID;
import abs.ixi.server.packet.XMPPNamespaces;
import abs.ixi.util.StringUtils;
import abs.ixi.xml.XMLUtils;

public class SMResumedPacket extends XMPPPacket {
    private static final long serialVersionUID = 3465309326060952047L;

    public static final PacketXmlElement XML_ELM_NAME = PacketXmlElement.SM_RESUMED;

    private static final String PREV_ID_ATTRIBUTE = "previd".intern();
    private static final String H_ATTRIBUTE = "h".intern();

    private String xmlns;
    private String prevId;
    private long prevHandledPacketCount;
    private String sourceId;
    private JID from;

    public SMResumedPacket(String prevId) {
	this.prevId = prevId;
    }

    public String getXmlns() {
	return xmlns;
    }

    public void setXmlns(String xmlns) {
	this.xmlns = xmlns;
    }

    public String getPrevId() {
	return prevId;
    }

    public void setPrevId(String prevId) {
	this.prevId = prevId;
    }

    public long getPrevHandledPacketCount() {
	return prevHandledPacketCount;
    }

    public void setPrevHandledPacketCount(long prevHandledPacketCount) {
	this.prevHandledPacketCount = prevHandledPacketCount;
    }

    public void setSourceId(String sourceId) {
	this.sourceId = sourceId;
    }

    @Override
    public String getSourceId() {
	return sourceId;
    }

    public JID getFrom() {
	return from;
    }

    public void setFrom(JID from) {
	this.from = from;
    }

    @Override
    public String xml() {
	StringBuilder sb = new StringBuilder();
	sb.append(XMLUtils.OPEN_BRACKET).append(XML_ELM_NAME.elementNameString()).append(XMLUtils.SPACE)
		.append(XMLUtils.XMLNS_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE)
		.append(XMPPNamespaces.STREAM_MANAGEMENT_NAMESPACE).append(XMLUtils.SINGLE_QUOTE)
		.append(XMLUtils.SPACE);

	if (!StringUtils.isNullOrEmpty(this.prevId)) {
	    sb.append(PREV_ID_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE).append(this.prevId)
		    .append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);
	}

	if (this.prevHandledPacketCount != 0) {
	    sb.append(H_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE)
		    .append(this.prevHandledPacketCount).append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);
	}

	sb.append(XMLUtils.SLASH).append(XMLUtils.CLOSE_BRACKET);

	return sb.toString();
    }

    @Override
    public PacketXmlElement getXmlElementName() {
	return XML_ELM_NAME;
    }

}
