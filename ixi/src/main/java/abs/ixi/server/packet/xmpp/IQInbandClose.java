package abs.ixi.server.packet.xmpp;

import abs.ixi.xml.Element;
import abs.ixi.xml.XMLUtils;

public class IQInbandClose extends AbstractIQContent {
	private static final long serialVersionUID = -5775435321741092191L;

	public static final String XML_ELM_NAME = "close";

	private String sid;

	public IQInbandClose(String xmlns) {
		super(xmlns, IQContentType.CLOSE);
	}

	public IQInbandClose(Element elm) {
		this(elm.getAttribute(XMLUtils.XMLNS_ATTRIBUTE));
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	@Override
	public String xml() {
		throw new UnsupportedOperationException();
	}

	@Override
	public StringBuilder appendXml(StringBuilder sb) {
		throw new UnsupportedOperationException();
	}

}
