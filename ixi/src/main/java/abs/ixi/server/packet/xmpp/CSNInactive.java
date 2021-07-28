package abs.ixi.server.packet.xmpp;

import abs.ixi.server.io.MalformedXMPPRequestException;
import abs.ixi.server.packet.XMPPNamespaces;
import abs.ixi.util.StringUtils;
import abs.ixi.xml.Element;
import abs.ixi.xml.XMLUtils;

public class CSNInactive implements MessageContent {
	private static final long serialVersionUID = 2806045356476081840L;

	public static final String XML_ELM_NAME = "inactive".intern();

	public static final String CSN_INACTIVE_XML = "<inactive xmlns='http://jabber.org/protocol/chatstates'/>".intern();

	private String xmlns;

	public CSNInactive(String xmlns) {
		this.setXmlns(xmlns);
	}

	public CSNInactive(Element inactiveElm) throws MalformedXMPPRequestException {
		if (StringUtils.safeEquals(XMPPNamespaces.CHAT_STATE_NOTIFICATION_NAMESPACE,
				inactiveElm.getAttribute(XMLUtils.XMLNS_ATTRIBUTE))) {
			this.xmlns = XMPPNamespaces.CHAT_STATE_NOTIFICATION_NAMESPACE;

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
		return MessageContentType.CSN_INACTIVE == type;
	}

	@Override
	public String xml() {
		return CSN_INACTIVE_XML;
	}

	@Override
	public StringBuilder appendXml(StringBuilder sb) {
		return sb.append(CSN_INACTIVE_XML);
	}
}
