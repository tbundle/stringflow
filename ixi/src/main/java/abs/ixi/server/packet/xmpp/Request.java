package abs.ixi.server.packet.xmpp;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import abs.ixi.server.app.HeaderParser;
import abs.ixi.server.io.MalformedXMPPRequestException;
import abs.ixi.util.StringUtils;
import abs.ixi.xml.Element;

/**
 * Represents application request. Currently server supports request element
 * within {@link IQ} packets only. The behavior resembles http request
 */
public class Request extends AbstractIQContent {
    private static final long serialVersionUID = 5556905173540524500L;

    private static final String MIME_BOUNDARY = "boundary";
    public static final String XML_ELM_NAME = "request";
    public static final String NAMESPACE = "stringflow:request";

    private Map<String, Object> headers;
    private boolean piggyback;

    public Request(Element requestElm) throws MalformedXMPPRequestException {
	this(requestElm.val());
    }

    public Request(String data) throws MalformedXMPPRequestException {
	super(XML_ELM_NAME, IQContentType.REQUEST);

	this.headers = HeaderParser.parseHeader(data);
	this.piggyback = this.headers.containsKey(MIME_BOUNDARY);
    }

    public Request(Map<String, Object> headers) {
	super(XML_ELM_NAME, IQContentType.REQUEST);
	this.headers = headers;
    }

    public Map<String, Object> getHeaders() {
	return headers;
    }

    public void addHeader(String key, String value) {
	headers.put(key, value);
    }

    public boolean hasPiggyback() {
	return piggyback;
    }

    public byte[] getMimeBoundary() {
	String boundary = (String) this.headers.get(MIME_BOUNDARY);

	if (StringUtils.isNullOrEmpty(boundary))
	    return null;

	return boundary.getBytes(StandardCharsets.US_ASCII);
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
