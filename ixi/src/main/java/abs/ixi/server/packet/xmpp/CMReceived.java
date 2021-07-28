package abs.ixi.server.packet.xmpp;

import abs.ixi.server.io.MalformedXMPPRequestException;
import abs.ixi.server.packet.XMPPNamespaces;
import abs.ixi.util.StringUtils;
import abs.ixi.xml.Element;
import abs.ixi.xml.XMLUtils;

public class CMReceived implements MessageContent {
	private static final long serialVersionUID = -1970159850519552430L;

	public static final String XML_ELM_NAME = "received";
	private static final String ID_ATTRIBUTE = "id";
	private static final String CM_RECEIVED_XML = "<received xmlns='urn:xmpp:chat-markers:0' id='%s' />";

	private String xmlns;
	private String messageId;

	public CMReceived(Element receivedElm) throws MalformedXMPPRequestException {
		if (StringUtils.safeEquals(XMPPNamespaces.CHAT_MARKER_NAMESPACE,
				receivedElm.getAttribute(XMLUtils.XMLNS_ATTRIBUTE))) {

			this.xmlns = receivedElm.getAttribute(XMLUtils.XMLNS_ATTRIBUTE);
			this.messageId = receivedElm.getAttribute(ID_ATTRIBUTE);

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
		return MessageContentType.CM_RECEIVED == type;
	}

	@Override
	public String xml() {
		return String.format(CM_RECEIVED_XML, messageId);
	}

	@Override
	public StringBuilder appendXml(StringBuilder sb) {
		return sb.append(xml());
	}
}
