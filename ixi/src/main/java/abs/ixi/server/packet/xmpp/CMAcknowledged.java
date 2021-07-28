package abs.ixi.server.packet.xmpp;

import abs.ixi.server.io.MalformedXMPPRequestException;
import abs.ixi.server.packet.XMPPNamespaces;
import abs.ixi.util.StringUtils;
import abs.ixi.xml.Element;
import abs.ixi.xml.XMLUtils;

public class CMAcknowledged implements MessageContent {

	private static final long serialVersionUID = 1804649004190241519L;

	public static final String XML_ELM_NAME = "acknowledged";
	private static final String ID_ATTRIBUTE = "id";
	private static final String CM_ACKNOWLEDGED_XML = "<acknowledged xmlns='urn:xmpp:chat-markers:0' id='%s' />";

	private String xmlns;
	private String messageId;

	public CMAcknowledged(Element elm) throws MalformedXMPPRequestException {
		if (StringUtils.safeEquals(XMPPNamespaces.CHAT_MARKER_NAMESPACE, elm.getAttribute(XMLUtils.XMLNS_ATTRIBUTE))) {
			this.xmlns = elm.getAttribute(XMLUtils.XMLNS_ATTRIBUTE);
			this.messageId = elm.getAttribute(ID_ATTRIBUTE);

		} else {
			throw new MalformedXMPPRequestException();
		}
	}

	public String getXmlns() {
		return xmlns;
	}

	public void setXmlns(String xmlns) {
		this.xmlns = xmlns;
	}

	@Override
	public boolean isContentType(MessageContentType type) {
		return MessageContentType.CM_ACKNOWLEDGED == type;
	}

	@Override
	public String xml() {
		return String.format(CM_ACKNOWLEDGED_XML, messageId);
	}

	@Override
	public StringBuilder appendXml(StringBuilder sb) {
		return sb.append(xml());
	}
}
