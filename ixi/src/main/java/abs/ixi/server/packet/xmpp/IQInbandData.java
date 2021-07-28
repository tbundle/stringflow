package abs.ixi.server.packet.xmpp;

import abs.ixi.xml.Element;
import abs.ixi.xml.XMLUtils;

public class IQInbandData extends AbstractIQContent {
	private static final long serialVersionUID = 3981210361481007388L;

	public static final String XML_ELM_NAME = "data";

	private String sid;
	private int seq;
	private String data;

	public IQInbandData(String xmlns, String data) {
		super(xmlns, IQContentType.DATA);
	}

	public IQInbandData(Element elm) {
		this(elm.getAttribute(XMLUtils.XMLNS_ATTRIBUTE), elm.getChild(XML_ELM_NAME).val());
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getData() {
		return data;
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
