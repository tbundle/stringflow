package abs.ixi.server.packet.xmpp;

import abs.ixi.server.io.MalformedXMPPRequestException;
import abs.ixi.server.packet.XMPPNamespaces;
import abs.ixi.util.StringUtils;
import abs.ixi.xml.Element;
import abs.ixi.xml.XMLUtils;

public class CSNPaused implements MessageContent {
	private static final long serialVersionUID = -1935875475210909115L;

	public static final String XML_ELM_NAME = "paused".intern();

	public static final String CSN_PAUSED_XML = "<paused xmlns='http://jabber.org/protocol/chatstates'/>".intern();

	private String xmlns;

	public CSNPaused(String xmlns) {
		this.setXmlns(xmlns);
	}

	public CSNPaused(Element pausedElm) throws MalformedXMPPRequestException {
		if (StringUtils.safeEquals(XMPPNamespaces.CHAT_STATE_NOTIFICATION_NAMESPACE,
				pausedElm.getAttribute(XMLUtils.XMLNS_ATTRIBUTE))) {
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
		return MessageContentType.CSN_PAUSED == type;
	}

	@Override
	public String xml() {
		return CSN_PAUSED_XML;
	}

	@Override
	public StringBuilder appendXml(StringBuilder sb) {
		return sb.append(CSN_PAUSED_XML);
	}
}
