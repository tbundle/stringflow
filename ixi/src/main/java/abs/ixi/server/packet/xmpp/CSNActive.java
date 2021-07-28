package abs.ixi.server.packet.xmpp;

import abs.ixi.server.io.MalformedXMPPRequestException;
import abs.ixi.server.packet.XMPPNamespaces;
import abs.ixi.util.StringUtils;
import abs.ixi.xml.Element;
import abs.ixi.xml.XMLUtils;

public class CSNActive implements MessageContent {
	private static final long serialVersionUID = 2973186091824242325L;

	public static final String XML_ELM_NAME = "active".intern();

	public static final String CSN_ACTIVE_XML = "<active xmlns='http://jabber.org/protocol/chatstates'/>".intern();

	private String xmlns;

	public CSNActive(String xmlns) {
		this.setXmlns(xmlns);
	}

	public CSNActive(Element activeElm) throws MalformedXMPPRequestException {
		if (StringUtils.safeEquals(XMPPNamespaces.CHAT_STATE_NOTIFICATION_NAMESPACE,
				activeElm.getAttribute(XMLUtils.XMLNS_ATTRIBUTE))) {
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
		return MessageContentType.CSN_ACTIVE == type;
	}

	@Override
	public String xml() {
		return CSN_ACTIVE_XML;
	}

	@Override
	public StringBuilder appendXml(StringBuilder sb) {
		return sb.append(CSN_ACTIVE_XML);
	}

}
