package abs.ixi.server.packet.xmpp;

import abs.ixi.util.StringUtils;

public class SASLChallenge extends XMPPPacket {
    private static final long serialVersionUID = 6454952949377018022L;

    public static final PacketXmlElement XML_ELM_NAME = PacketXmlElement.SASL_CHALLANGE;

    private static final String CHALANGE_OPEN_TAG = "<challenge xmlns='urn:ietf:params:xml:ns:xmpp-sasl'>".intern();
    private static final String CHALLANGE_CLOSE_TAG = "</challenge>".intern();

    private String challange;
    private String sourceId;

    public SASLChallenge(String response) {
	this.challange = response;
    }

    public String getChallange() {
	return challange;
    }

    public void setChallange(String challange) {
	this.challange = challange;
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
	StringBuilder sb = new StringBuilder();
	sb.append(CHALANGE_OPEN_TAG);

	if (!StringUtils.isNullOrEmpty(challange)) {
	    sb.append(challange);
	}

	sb.append(CHALLANGE_CLOSE_TAG);
	return sb.toString();
    }

    @Override
    public PacketXmlElement getXmlElementName() {
	return XML_ELM_NAME;
    }

}
