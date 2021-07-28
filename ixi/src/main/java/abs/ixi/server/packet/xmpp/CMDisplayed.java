package abs.ixi.server.packet.xmpp;

import abs.ixi.server.io.MalformedXMPPRequestException;
import abs.ixi.server.packet.XMPPNamespaces;
import abs.ixi.util.StringUtils;
import abs.ixi.xml.Element;
import abs.ixi.xml.XMLUtils;

public class CMDisplayed implements MessageContent {
	private static final long serialVersionUID = 3739994435263090083L;
	public static final String XML_ELM_NAME = "displayed";

	private static final String ID_ATTRIBUTE = "id";
	private static final String CM_DISPLAYED_XML = "<displayed xmlns='urn:xmpp:chat-markers:0' id='%s' />".intern();

	private String xmlns;
	private String messageId;

	public CMDisplayed(Element displayedElm) throws MalformedXMPPRequestException {
		if (StringUtils.safeEquals(XMPPNamespaces.CHAT_MARKER_NAMESPACE,
				displayedElm.getAttribute(XMLUtils.XMLNS_ATTRIBUTE))) {

			this.xmlns = displayedElm.getAttribute(XMLUtils.XMLNS_ATTRIBUTE);
			this.messageId = displayedElm.getAttribute(ID_ATTRIBUTE);

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

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	@Override
	public boolean isContentType(MessageContentType type) {
		return MessageContentType.CM_DISPLAYED == type;
	}

	@Override
	public String xml() {
		return String.format(CM_DISPLAYED_XML, messageId);
	}

	@Override
	public StringBuilder appendXml(StringBuilder sb) {
		return sb.append(xml());
	}
}
