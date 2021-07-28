package abs.ixi.server.packet.xmpp;

import abs.ixi.server.packet.InvalidJabberId;
import abs.ixi.xml.Element;
import abs.ixi.xml.XMLUtils;

public class SMResumePacket extends XMPPPacket {
    private static final long serialVersionUID = 323085462857711628L;

    public static final PacketXmlElement XML_ELM_NAME = PacketXmlElement.SM_RESUME;

    private static final String PREV_ID_ATTRIBUTE = "previd".intern();
    private static final String H_ATTRIBUTE = "h".intern();

    private String xmlns;
    private String prevId;
    private int prevHandledPacketCount;
    private String sourceId;

    public SMResumePacket(Element element) throws InvalidJabberId {
	super(element);

	this.xmlns = element.getAttribute(XMLUtils.XMLNS_ATTRIBUTE);
	this.prevId = element.getAttribute(PREV_ID_ATTRIBUTE);

	if (element.getAttribute(H_ATTRIBUTE) != null) {
	    this.prevHandledPacketCount = Integer.parseInt(element.getAttribute(H_ATTRIBUTE));
	}
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

    public int getPrevHandledPacketCount() {
	return prevHandledPacketCount;
    }

    public void setPrevHandledPacketCount(int prevHandledPacketCount) {
	this.prevHandledPacketCount = prevHandledPacketCount;
    }

    public void setSourceId(String sourceId) {
	this.sourceId = sourceId;
    }

    @Override
    public String getSourceId() {
	return sourceId;
    }

    @Override
    public PacketXmlElement getXmlElementName() {
	return XML_ELM_NAME;
    }

}
