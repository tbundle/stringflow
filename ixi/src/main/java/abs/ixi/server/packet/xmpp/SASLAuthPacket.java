package abs.ixi.server.packet.xmpp;

import abs.ixi.server.sys.secure.sasl.SASLMechanismName;
import abs.ixi.util.StringUtils;
import abs.ixi.xml.Element;

public class SASLAuthPacket extends XMPPPacket {
    private static final long serialVersionUID = 7928203548865798484L;

    public static final PacketXmlElement XML_ELM_NAME = PacketXmlElement.SASL_AUTH;

    private static final String MECHANISM = "mechanism".intern();

    private SASLMechanismName mechanism;
    private String authResponse;
    private String sourceId;

    public SASLAuthPacket(Element element) {
	this.element = element;

	this.authResponse = element.val();

	if (!StringUtils.isNullOrEmpty(element.getAttribute(MECHANISM))) {
	    this.mechanism = SASLMechanismName.valueFrom(element.getAttribute(MECHANISM));
	}
    }

    public SASLMechanismName getMechanism() {
	return mechanism;
    }

    public void setMechanism(SASLMechanismName mechanism) {
	this.mechanism = mechanism;
    }

    public String getAuthResponse() {
	return authResponse;
    }

    public void setAuthResponse(String authResponse) {
	this.authResponse = authResponse;
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
