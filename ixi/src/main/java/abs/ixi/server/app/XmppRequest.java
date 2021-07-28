package abs.ixi.server.app;

import java.util.Map;

import abs.ixi.server.io.multipart.MultipartMessage;
import abs.ixi.util.UUIDGenerator;

/**
 * Represents a user request to an application deployed within server. By
 * design, xmpp requests are very similar to http requests; In fact, xmpp
 * requests are nothing but a http request wrapped inside a
 * {@link abs.ixi.server.packet.xmpp.Message} packet.
 */
public class XmppRequest extends ApplicationMessage implements ApplicationRequest {
	private static final long serialVersionUID = 1L;

	private static final String URI = "uri";

	private String endpoint;

	public XmppRequest() {
		this(UUIDGenerator.uuid(), null);
	}

	public XmppRequest(Map<String, Object> headers) {
		this(UUIDGenerator.uuid(), headers);
	}

	public XmppRequest(String id, Map<String, Object> headers) {
		this(id, headers, null);
	}

	public XmppRequest(Map<String, Object> headers, MultipartMessage multipartMessage) {
		this(UUIDGenerator.uuid(), headers, multipartMessage);
	}

	public XmppRequest(String id, Map<String, Object> headers, MultipartMessage multipartMessage) {
		super(id, headers, multipartMessage);

		if (this.headers != null) {
			this.endpoint = (String) headers.get(URI);
		}
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	@Override
	public String toString() {
		return "xmpp-request-" + getId();
	}

}
