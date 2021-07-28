package abs.ixi.server.packet.xmpp;

import abs.ixi.xml.Element;
import abs.ixi.xml.XMLUtils;

public class IQSession extends AbstractIQContent {
	private static final long serialVersionUID = -4311986706193237224L;

	public static final String XML_ELM_NAME = "session";

	public IQSession(String xmlns) {
		super(xmlns, IQContentType.SESSION);
	}

	public IQSession(Element elm) {
		this(elm.getAttribute(XMLUtils.XMLNS_ATTRIBUTE));
	}

	@Override
	public String xml() {
		return null;
	}

	@Override
	public StringBuilder appendXml(StringBuilder sb) {
		return sb;
	}

}
