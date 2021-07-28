package abs.ixi.server.packet.xmpp;

import abs.ixi.server.packet.XMPPNamespaces;
import abs.ixi.util.StringUtils;
import abs.ixi.xml.XMLUtils;

public class SMEnabledPacket extends XMPPPacket {
	private static final long serialVersionUID = 8498546314911653923L;

	public static final PacketXmlElement XML_ELM_NAME = PacketXmlElement.SM_ENABLED;

	private static final String ID_ATTRIBUTE = "id".intern();
	private static final String RESUME_ATTRIBUTE = "resume".intern();
	private static final String MAX_ATTRIBUTE = "max".intern();
	private static final String TRUE = "true".intern();
	private static final String FALSE = "false".intern();

	private String smId;
	private boolean resume;
	private long maxResumptionTime;

	public String getSmId() {
		return smId;
	}

	public void setSmId(String smId) {
		this.smId = smId;
	}

	public boolean isResume() {
		return resume;
	}

	public void setResume(boolean resume) {
		this.resume = resume;
	}

	public long getMaxResumptionTime() {
		return maxResumptionTime;
	}

	public void setMaxResumptionTime(long maxResumptionTime) {
		this.maxResumptionTime = maxResumptionTime;
	}

	@Override
	public String getSourceId() {
		return null;
	}

	@Override
	public String xml() {
		StringBuilder sb = new StringBuilder();
		sb.append(XMLUtils.OPEN_BRACKET).append(XML_ELM_NAME.elementNameString()).append(XMLUtils.SPACE)
				.append(XMLUtils.XMLNS_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE)
				.append(XMPPNamespaces.STREAM_MANAGEMENT_NAMESPACE).append(XMLUtils.SINGLE_QUOTE)
				.append(XMLUtils.SPACE);

		if (!StringUtils.isNullOrEmpty(this.smId)) {
			sb.append(ID_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE).append(this.smId)
					.append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);
		}

		if (resume) {
			sb.append(RESUME_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE).append(TRUE)
					.append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);

		} else {
			sb.append(RESUME_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE).append(FALSE)
					.append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);
		}

		if (maxResumptionTime != 0) {
			sb.append(MAX_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE).append(maxResumptionTime)
					.append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);
		}

		sb.append(XMLUtils.SLASH).append(XMLUtils.CLOSE_BRACKET);

		return sb.toString();
	}

	@Override
	public PacketXmlElement getXmlElementName() {
		return PacketXmlElement.SM_ENABLED;
	}

}
