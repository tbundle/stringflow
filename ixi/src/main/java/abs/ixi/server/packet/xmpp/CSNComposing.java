package abs.ixi.server.packet.xmpp;

import abs.ixi.server.io.MalformedXMPPRequestException;
import abs.ixi.server.packet.XMPPNamespaces;
import abs.ixi.util.StringUtils;
import abs.ixi.xml.Element;
import abs.ixi.xml.XMLUtils;

public class CSNComposing implements MessageContent {
	private static final long serialVersionUID = -1220186583069620424L;

	public static final String XML_ELM_NAME = "composing";

	public static final String CSN_COMPOSING_XML = "<composing xmlns='http://jabber.org/protocol/chatstates'/>"
			.intern();

	private String xmlns;

	public CSNComposing(String xmlns) {
		this.setXmlns(xmlns);
	}

	public CSNComposing(Element composingElm) throws MalformedXMPPRequestException {
		if (StringUtils.safeEquals(XMPPNamespaces.CHAT_STATE_NOTIFICATION_NAMESPACE,
				composingElm.getAttribute(XMLUtils.XMLNS_ATTRIBUTE))) {
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
		return MessageContentType.CSN_COMPOSING == type;
	}

	@Override
	public String xml() {
		return CSN_COMPOSING_XML;
	}

	@Override
	public StringBuilder appendXml(StringBuilder sb) {
		return sb.append(CSN_COMPOSING_XML);
	}
}
