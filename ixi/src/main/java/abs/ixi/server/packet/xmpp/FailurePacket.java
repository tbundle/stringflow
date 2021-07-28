package abs.ixi.server.packet.xmpp;

import abs.ixi.util.StringUtils;
import abs.ixi.xml.XMLUtils;

public class FailurePacket extends XMPPPacket {
	private static final long serialVersionUID = 9111589694223194944L;

	private static final PacketXmlElement XML_ELM_NAME = PacketXmlElement.FAILURE;

	private static final String FAILURE_CLOSE_TAG = "</failure>".intern();

	private String xmlns;
	private String reasionXml;
	private String sourceId;

	public FailurePacket(String xmlns) {
		this.xmlns = xmlns;
	}

	public String getXmlns() {
		return xmlns;
	}

	public void setXmlns(String xmlns) {
		this.xmlns = xmlns;
	}

	public String getReasionXml() {
		return reasionXml;
	}

	public void setReasionXml(String reasionXml) {
		this.reasionXml = reasionXml;
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
		StringBuilder sb = new StringBuilder();

		sb.append(XMLUtils.OPEN_BRACKET).append(XML_ELM_NAME.elementNameString()).append(XMLUtils.SPACE)
				.append(XMLUtils.XMLNS_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE)
				.append(this.xmlns).append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE).append(XMLUtils.CLOSE_BRACKET);

		if (!StringUtils.isNullOrEmpty(reasionXml)) {
			sb.append(reasionXml);
		}

		sb.append(FAILURE_CLOSE_TAG);

		return sb.toString();
	}

	@Override
	public PacketXmlElement getXmlElementName() {
		return XML_ELM_NAME;
	}
}
