package abs.ixi.server.packet.xmpp;

import abs.ixi.xml.Element;
import abs.ixi.xml.XMLUtils;

public class SASLChallengeResponse extends XMPPPacket {
    private static final long serialVersionUID = 5968460068811733165L;

    public static final PacketXmlElement XML_ELM_NAME = PacketXmlElement.SASL_CHALALNGE_RESPONSE;

    private String xmlns;
    private String challangeResponse;
    private String sourceId;

    public SASLChallengeResponse(Element element) {
	this.element = element;
	this.challangeResponse = element.val();
	this.xmlns = element.getAttribute(XMLUtils.XMLNS_ATTRIBUTE);
    }

    public String getXmlns() {
	return xmlns;
    }

    public void setXmlns(String xmlns) {
	this.xmlns = xmlns;
    }

    public String getChallangeResponse() {
	return challangeResponse;
    }

    public void setChallangeResponse(String challangeResponse) {
	this.challangeResponse = challangeResponse;
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
