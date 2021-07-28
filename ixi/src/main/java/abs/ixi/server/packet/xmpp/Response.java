package abs.ixi.server.packet.xmpp;

import java.util.Map;

import abs.ixi.server.app.HeaderParser;
import abs.ixi.server.io.MalformedXMPPRequestException;
import abs.ixi.xml.Element;

/**
 * Represents response element within a Message or IQ stanza. This element is
 * used for server response for a request
 */
public class Response extends AbstractIQContent {
    private static final long serialVersionUID = -4840548230828653713L;

    public static final String XML_ELM_NAME = "response";
    public static final String NAMESPACE = "stringflow:response";

    private Map<String, Object> headers;
    private boolean hasPiggyback;

    public Response(Element elm) throws MalformedXMPPRequestException {
	this(elm.val());
    }

    public Response(String data) throws MalformedXMPPRequestException {
	super(XML_ELM_NAME, IQContentType.RESPOSNE);

	this.headers = HeaderParser.parseHeader(data);
	this.hasPiggyback = isMIMERequest();
    }

    public Response(Map<String, Object> headers) {
	super(XML_ELM_NAME, IQContentType.RESPOSNE);
	this.headers = headers;

	// TODO populate piggyback here
    }

    public Response(Map<String, Object> headers, boolean hasPiggyback) {
	super(XML_ELM_NAME, IQContentType.RESPOSNE);

	this.headers = headers;
	this.hasPiggyback = hasPiggyback;
    }

    public boolean hasPiggyback() {
	return this.hasPiggyback;
    }

    public boolean isMIMERequest() {
	if (this.headers.containsKey(HeaderParser.BOUNDARY)) {
	    return true;
	}
	return false;
    }

    public Map<String, Object> getHeaders() {
	return headers;
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
