package abs.ixi.server.app;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import abs.ixi.server.common.Identifiable;
import abs.ixi.server.io.multipart.MultipartMessage;
import abs.ixi.util.ObjectUtils;
import abs.ixi.util.StringUtils;

/**
 * Base class for all the {@link ApplicationRequest} and
 * {@link ApplicationResponse} implementations in server. This is used only in
 * the context of an application.
 */
public abstract class ApplicationMessage implements Identifiable<String>, Serializable {
    private static final long serialVersionUID = 1L;

    protected static final String MIME_BOUNDARY = "boundary";
    protected static final String CONTENT_TYPE = "Content-Type";
    protected static final String CONTENT_LENGTH = "Content-Length";
    protected static final String MULTIPART = "multipart";

    protected static final byte DASH = 45;
    protected static final byte LF = 0xA;
    protected static final byte CR = 0xD;
    protected static final char EQUAL = '=';
    protected static final String LINE_FEED = "\r\n";

    protected String id;

    protected Map<String, Object> headers;
    protected MultipartMessage multipartMessage;

    public ApplicationMessage() {
	this(null, new HashMap<>(), null);
    }

    public ApplicationMessage(String id) {
	this(id, new HashMap<>(), null);
    }

    public ApplicationMessage(String id, Map<String, Object> headers) {
	this(id, headers, null);
    }

    public ApplicationMessage(String id, Map<String, Object> headers, MultipartMessage multipartMessage) {
	this.id = id;
	this.headers = headers;
	this.multipartMessage = multipartMessage;
    }

    @Override
    public String getId() {
	return this.id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public Map<String, Object> getHeaders() {
	return headers;
    }

    public void setHeaders(Map<String, Object> headers) {
	this.headers = headers;
    }

    public MultipartMessage getMultipartMessage() {
	return multipartMessage;
    }

    public void setMultipartMessage(MultipartMessage multipartMessage) {
	this.multipartMessage = multipartMessage;
    }

    public Object getHeader(String key) {
	return this.headers.get(key);
    }

    public void addHeader(String key, Object value) {
	this.headers.put(key, value);
    }

    public MultipartMessage newMultipartMessage() {
	this.multipartMessage = new MultipartMessage();
	return this.multipartMessage;
    }

    public int getContentLength() {
	Object obj = this.getHeader(CONTENT_LENGTH);
	return obj == null ? 0 : ObjectUtils.parseToInt(obj.toString());
    }

    public byte[] getMimeBoundary() {
	String boundary = (String) this.getHeaders().get(MIME_BOUNDARY);

	if (StringUtils.isNullOrEmpty(boundary))
	    return null;

	return boundary.getBytes(StandardCharsets.US_ASCII);
    }

    protected long getMimeLength() {
	if (multipartMessage != null)
	    return multipartMessage.getMimeLength();

	return 0;
    }

    protected String getHeaderDataString() {
	StringBuilder sb = new StringBuilder();

	if (this.getHeaders() != null) {
	    for (Entry<String, Object> header : this.getHeaders().entrySet()) {
		sb.append(header.getKey());
		sb.append(EQUAL);
		sb.append(header.getValue());
		sb.append(LINE_FEED);
	    }
	}

	return sb.toString();
    }

}
