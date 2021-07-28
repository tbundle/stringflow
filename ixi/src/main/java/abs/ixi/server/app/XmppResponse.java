package abs.ixi.server.app;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import abs.ixi.server.Writable;
import abs.ixi.server.common.ChannelStream;
import abs.ixi.server.io.multipart.BinaryContent;
import abs.ixi.server.io.multipart.ContentType;
import abs.ixi.server.io.multipart.FileContentSource;
import abs.ixi.server.io.multipart.Multipart;
import abs.ixi.server.io.multipart.MultipartMessage;
import abs.ixi.server.io.multipart.TextContent;
import abs.ixi.server.packet.JID;
import abs.ixi.util.StringUtils;
import abs.ixi.util.UUIDGenerator;

public class XmppResponse extends ApplicationMessage implements ApplicationResponse, Writable {
    private static final long serialVersionUID = 1L;

    private JID fromJID;
    private ResponseStatus status;

    public XmppResponse() {
	this(UUIDGenerator.uuid(), new HashMap<>());
    }

    public XmppResponse(Map<String, Object> headers) {
	this(UUIDGenerator.uuid(), headers);
    }

    public XmppResponse(String id, Map<String, Object> headers) {
	this(id, headers, null);
    }

    public XmppResponse(Map<String, Object> headers, MultipartMessage multipartMessage) {
	this(UUIDGenerator.uuid(), headers, multipartMessage);
    }

    public XmppResponse(String id, Map<String, Object> headers, MultipartMessage multipartMessage) {
	super(id, headers, multipartMessage);
    }

    public ResponseStatus getStatus() {
	return status;
    }

    public void setStatus(ResponseStatus status) {
	this.status = status;
	this.headers.put("status", status.name());
    }

    public void setFromJID(JID fromJID) {
	this.fromJID = fromJID;
    }

    public JID getFromJID() {
	return fromJID;
    }

    /**
     * Returns {@link ResponseBuilder} instance with {@link ResponseStatus} set
     * to {@link ResponseStatus#OK}. The builder returned can be further
     * operated for its chained operations and finally invoking
     * {@link ResponseBuilder#build()} will generate the {@link XmppResponse}
     * instance
     * 
     * @return {@link ResponseBuilder} instance
     */
    public static ResponseBuilder ok() {
	ResponseBuilder builder = new ResponseBuilder();
	builder.withStatus(ResponseStatus.OK);

	return builder;
    }

    @Override
    public String toString() {
	return "xmpp-request-" + getId();
    }

    private String getResponseIQ() {
	StringBuilder sb = new StringBuilder();
	sb.append("<iq type='result' id='").append(this.getId()).append("'");

	if (this.fromJID != null)
	    sb.append(" from='").append(this.fromJID).append("'");

	sb.append(">").append("<response xmlns='stringflow:response'>").append("<![CDATA[").append(LINE_FEED);

	if (this.getMultipartMessage() != null) {
	    sb.append(MIME_BOUNDARY).append(EQUAL)
		    .append(new String(this.getMultipartMessage().getBoundary(), StandardCharsets.US_ASCII))
		    .append(LINE_FEED);

	    sb.append(CONTENT_LENGTH).append(EQUAL).append(this.getMimeLength()).append(LINE_FEED);
	    sb.append(CONTENT_TYPE).append(EQUAL).append(MULTIPART).append(LINE_FEED);
	}

	String headerData = getHeaderDataString();

	if (!StringUtils.isNullOrEmpty(headerData)) {
	    sb.append(headerData);
	}

	sb.append("]]>").append("</response>").append("</iq>").append(LINE_FEED);

	return sb.toString();
    }

    @Override
    public long writeTo(OutputStream os) throws IOException {
	return 0;
    }

    @Override
    public long writeTo(Socket socket) throws IOException {
	return 0;
    }

    @Override
    public long writeTo(SocketChannel socketChannel) throws IOException {
	byte[] requestIQbytes = getResponseIQ().getBytes(StandardCharsets.US_ASCII);

	long count = socketChannel.write(ByteBuffer.wrap(requestIQbytes));

	if (this.getMultipartMessage() != null) {
	    count += this.getMultipartMessage().writeTo(socketChannel);
	}

	return count;
    }

    @Override
    public long writeTo(ChannelStream cs) throws IOException {
	byte[] requestIQbytes = getResponseIQ().getBytes(StandardCharsets.US_ASCII);

	cs.enqueue(requestIQbytes);

	long count = requestIQbytes.length;

	if (this.getMultipartMessage() != null) {
	    count += this.getMultipartMessage().writeTo(cs);
	}

	return count;
    }

    /**
     * A builder pattern implementation to build {@link XmppResponse} objects.
     */
    public static class ResponseBuilder {
	private XmppResponse response;

	public ResponseBuilder() {
	    this.response = new XmppResponse();
	}

	public ResponseBuilder withId(String id) {
	    this.response.setId(id);
	    return this;
	}

	public ResponseBuilder withFromJID(JID fromJID) {
	    this.response.setFromJID(fromJID);
	    return this;
	}

	public ResponseBuilder withStatus(ResponseStatus status) {
	    this.response.setStatus(status);

	    return this;
	}

	public ResponseBuilder withHeaders(Map<String, Object> headers) {
	    this.response.setHeaders(headers);

	    return this;
	}

	public ResponseBuilder withHeader(String key, Object value) {
	    response.addHeader(key, value);
	    return this;
	}

	public ResponseBuilder withMultipart(Multipart multipart) {
	    MultipartMessage message = this.response.getMultipartMessage() != null ? this.response.getMultipartMessage()
		    : this.response.newMultipartMessage();

	    message.addMultiPart(multipart);

	    return this;
	}

	/**
	 * 
	 * @param contentName
	 *            should not null
	 * @param text
	 * @return {@link XmppRequest}
	 */
	public ResponseBuilder withTextContent(String contentName, String text) {
	    return this.withTextContent(contentName, text, null);
	}

	/**
	 * 
	 * @param contentName
	 *            should not null
	 * @param text
	 * @return {@link XmppRequest}
	 */
	public ResponseBuilder withTextContent(String contentName, String text, ContentType type) {
	    Multipart multipart = new Multipart(contentName, new TextContent(text, type));

	    return this.withMultipart(multipart);
	}

	public ResponseBuilder withBinaryContent(String name, String filePath, ContentType type) {
	    Multipart multipart = new Multipart(name, new BinaryContent(new FileContentSource(filePath), type));
	    return this.withMultipart(multipart);
	}

	public XmppResponse build() {
	    return this.response;
	}
    }

    public enum ResponseStatus {
	OK,

	SERVER_ERROR,

	FAILED_TO_PROCESS,

	BAD_REQUEST,

	RESOURCE_NOT_FOUND;
    }

}