package abs.ixi.server.packet.xmpp;

import abs.ixi.xml.Element;
import abs.ixi.xml.XMLUtils;

public class IQInbandOpen extends AbstractIQContent {
    private static final long serialVersionUID = -4453592001212398361L;

    public static final String XML_ELM_NAME = "open";

    private String sid;
    private int blockSize;
    private String stanza;

    public IQInbandOpen(String xmlns) {
	super(xmlns, IQContentType.OPEN);
    }

    public IQInbandOpen(Element elm) {
	this(elm.getAttribute(XMLUtils.XMLNS_ATTRIBUTE));
    }

    public String getSid() {
	return sid;
    }

    public void setSid(String sid) {
	this.sid = sid;
    }

    public int getBlockSize() {
	return blockSize;
    }

    public void setBlockSize(int blockSize) {
	this.blockSize = blockSize;
    }

    public String getStanza() {
	return stanza;
    }

    public void setStanza(String stanza) {
	this.stanza = stanza;
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
