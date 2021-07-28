package abs.ixi.server.packet;

import abs.ixi.server.io.MalformedXMPPRequestException;
import abs.ixi.server.packet.xmpp.MessageContent;
import abs.ixi.util.StringUtils;
import abs.ixi.xml.Element;
import abs.ixi.xml.XMLUtils;

public class MDRRequest implements MessageContent {
	private static final long serialVersionUID = 1L;

	public static final String XML_ELM_NAME = "request";
	private static final String MDR_REQUEST_XML = "<request xmlns='urn:xmpp:receipts' />";

	private String xmlns;

	public MDRRequest(Element receivedElm) throws MalformedXMPPRequestException {
		if (StringUtils.safeEquals(XMPPNamespaces.MESSAGE_DELIVERY_RECEIPT_NAMESPACE,
				receivedElm.getAttribute(XMLUtils.XMLNS_ATTRIBUTE))) {

			this.xmlns = receivedElm.getAttribute(XMLUtils.XMLNS_ATTRIBUTE);

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
		return MessageContentType.MDR_REQUEST == type;
	}

	@Override
	public String xml() {
		return MDR_REQUEST_XML;
	}

	@Override
	public StringBuilder appendXml(StringBuilder sb) {
		return sb.append(xml());
	}

}
