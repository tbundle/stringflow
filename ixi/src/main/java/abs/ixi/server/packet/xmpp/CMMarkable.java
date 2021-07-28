package abs.ixi.server.packet.xmpp;

import abs.ixi.server.io.MalformedXMPPRequestException;
import abs.ixi.server.packet.XMPPNamespaces;
import abs.ixi.util.StringUtils;
import abs.ixi.xml.Element;
import abs.ixi.xml.XMLUtils;

public class CMMarkable implements MessageContent {
	private static final long serialVersionUID = 1L;

	public static final String XML_ELM_NAME = "markable".intern();

	private static final String CM_MARKABLE_XML = "<markable xmlns='urn:xmpp:chat-markers:0'/>".intern();

	private String xmlns;

	public CMMarkable(String xmlns) {
		this.setXmlns(xmlns);
	}

	public CMMarkable(Element markableElm) throws MalformedXMPPRequestException {
		if (StringUtils.safeEquals(XMPPNamespaces.CHAT_MARKER_NAMESPACE,
				markableElm.getAttribute(XMLUtils.XMLNS_ATTRIBUTE))) {
			this.xmlns = markableElm.getAttribute(XMLUtils.XMLNS_ATTRIBUTE);

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
		return MessageContentType.CM_MARKABLE == type;
	}

	@Override
	public String xml() {
		return CM_MARKABLE_XML;
	}

	@Override
	public StringBuilder appendXml(StringBuilder sb) {
		return sb.append(CM_MARKABLE_XML);
	}

}
