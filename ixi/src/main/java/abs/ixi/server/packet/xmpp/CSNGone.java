package abs.ixi.server.packet.xmpp;

import abs.ixi.server.io.MalformedXMPPRequestException;
import abs.ixi.server.packet.XMPPNamespaces;
import abs.ixi.util.StringUtils;
import abs.ixi.xml.Element;
import abs.ixi.xml.XMLUtils;

public class CSNGone implements MessageContent {
	private static final long serialVersionUID = -4752043806235794962L;

	public static final String XML_ELM_NAME = "gone".intern();

	public static final String CSN_GONE_XML = "<gone xmlns='http://jabber.org/protocol/chatstates'/>".intern();

	private String xmlns;

	public CSNGone(String xmlns) {
		this.setXmlns(xmlns);
	}

	public CSNGone(Element goneElm) throws MalformedXMPPRequestException {
		if (StringUtils.safeEquals(XMPPNamespaces.CHAT_STATE_NOTIFICATION_NAMESPACE,
				goneElm.getAttribute(XMLUtils.XMLNS_ATTRIBUTE))) {
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
		return MessageContentType.CSN_GONE == type;
	}

	@Override
	public String xml() {
		return CSN_GONE_XML;
	}

	@Override
	public StringBuilder appendXml(StringBuilder sb) {
		return sb.append(CSN_GONE_XML);
	}
}
