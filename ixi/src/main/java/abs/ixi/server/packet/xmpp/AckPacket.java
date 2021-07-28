package abs.ixi.server.packet.xmpp;

import abs.ixi.server.packet.XMPPNamespaces;
import abs.ixi.xml.Element;
import abs.ixi.xml.XMLUtils;

public class AckPacket extends XMPPPacket {
	private static final long serialVersionUID = 7389374175145706551L;

	public static final PacketXmlElement XML_ELM_NAME = PacketXmlElement.ACK;

	private static final String H_ATTRIBUTE = "h".intern();

	private String xmlns;
	private long handledPacketCount;
	private String sourceId;

	public AckPacket(long handledPacketCount) {
		this.handledPacketCount = handledPacketCount;
	}

	public AckPacket(Element element) {
		this.setXmlns(element.getAttribute(XMLUtils.XMLNS_ATTRIBUTE));

		if (element.getAttribute(H_ATTRIBUTE) != null) {
			this.handledPacketCount = Integer.parseInt(element.getAttribute(H_ATTRIBUTE));
		}
	}

	public String getXmlns() {
		return xmlns;
	}

	public void setXmlns(String xmlns) {
		this.xmlns = xmlns;
	}

	public long getHandledPacketCount() {
		return handledPacketCount;
	}

	public void setHandledPacketCount(int handledPacketCount) {
		this.handledPacketCount = handledPacketCount;
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

	@Override
	public String xml() {
		StringBuilder sb = new StringBuilder();

		sb.append(XMLUtils.OPEN_BRACKET).append(XML_ELM_NAME.elementNameString()).append(XMLUtils.SPACE)
				.append(XMLUtils.XMLNS_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE)
				.append(XMPPNamespaces.STREAM_MANAGEMENT_NAMESPACE).append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE)
				.append(H_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE)
				.append(this.handledPacketCount).append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE)
				.append(XMLUtils.SLASH).append(XMLUtils.CLOSE_BRACKET);

		return sb.toString();
	}
}
