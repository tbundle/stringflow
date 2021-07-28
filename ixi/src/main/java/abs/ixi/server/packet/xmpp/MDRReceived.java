package abs.ixi.server.packet.xmpp;

import abs.ixi.server.io.MalformedXMPPRequestException;
import abs.ixi.server.packet.XMPPNamespaces;
import abs.ixi.util.StringUtils;
import abs.ixi.xml.Element;
import abs.ixi.xml.XMLUtils;

public class MDRReceived implements MessageContent {
	private static final long serialVersionUID = 1L;

	public static final String XML_ELM_NAME = "received";
	private static final String ID_ATTRIBUTE = "id";
	private static final String MDR_RECEIVED_XML = "<received xmlns='urn:xmpp:receipts' id='%s' />";

	private String xmlns;
	private String messageId;

	public MDRReceived(Element receivedElm) throws MalformedXMPPRequestException {
		if (StringUtils.safeEquals(XMPPNamespaces.MESSAGE_DELIVERY_RECEIPT_NAMESPACE,
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
		return MessageContentType.MDR_RECEIVED == type;
	}

	@Override
	public String xml() {
		return String.format(MDR_RECEIVED_XML, messageId);
	}

	@Override
	public StringBuilder appendXml(StringBuilder sb) {
		return sb.append(xml());
	}

}
