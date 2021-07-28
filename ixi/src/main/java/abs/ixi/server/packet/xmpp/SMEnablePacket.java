package abs.ixi.server.packet.xmpp;

import abs.ixi.util.StringUtils;
import abs.ixi.xml.Element;
import abs.ixi.xml.XMLUtils;

public class SMEnablePacket extends XMPPPacket {
	private static final long serialVersionUID = 929426759594533892L;

	public static final PacketXmlElement XML_ELM_NAME = PacketXmlElement.SM_ENABLE;

	private static final String RESUME_ATTRIBUTE = "resume";
	private static final String MAX_ATTRIBUTE = "max";

	private static final String TRUE = "true";

	private String xmlns;
	private boolean resumable;
	private long maxResumptionTime;

	public SMEnablePacket(Element element) {
		this.xmlns = element.getAttribute(XMLUtils.XMLNS_ATTRIBUTE);

		String resumableFlag = element.getAttribute(RESUME_ATTRIBUTE);
		this.resumable = StringUtils.isNullOrEmpty(resumableFlag) ? false
				: StringUtils.safeEquals(resumableFlag, TRUE, false) || Integer.parseInt(resumableFlag) == 1;

		String maxTime = element.getAttribute(MAX_ATTRIBUTE);
		this.maxResumptionTime = StringUtils.isNullOrEmpty(maxTime) ? 0 : Long.parseLong(maxTime);
	}

	public String getXmlns() {
		return xmlns;
	}

	public void setXmlns(String xmlns) {
		this.xmlns = xmlns;
	}

	public boolean isResumable() {
		return resumable;
	}

	public long getMaxResumptionTime() {
		return maxResumptionTime;
	}

	@Override
	public PacketXmlElement getXmlElementName() {
		return PacketXmlElement.SM_ENABLE;
	}

	@Override
	public String getSourceId() {
		return null;
	}

}
