package abs.ixi.server.packet.xmpp;

import abs.ixi.xml.Element;
import abs.ixi.xml.XMLUtils;

public class IQPing extends AbstractIQContent {
	private static final long serialVersionUID = 7976897721181693075L;

	public static final String XML_ELM_NAME = "ping";

	private static final String PING_XML = "<ping xmlns='urn:xmpp:ping'/>";

	public IQPing() {
		super(null, IQContentType.PING);
	}

	public IQPing(String xmlns) {
		super(xmlns, IQContentType.PING);
	}

	public IQPing(Element elm) {
		this(elm.getAttribute(XMLUtils.XMLNS_ATTRIBUTE));
	}

	@Override
	public String xml() {
		return PING_XML;
	}

	@Override
	public StringBuilder appendXml(StringBuilder sb) {
		return sb.append(PING_XML);
	}

}
