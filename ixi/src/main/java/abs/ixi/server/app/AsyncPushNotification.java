package abs.ixi.server.app;

import java.util.Map;

import abs.ixi.server.io.multipart.MultipartMessage;
import abs.ixi.util.UUIDGenerator;

public class AsyncPushNotification extends ApplicationMessage implements ApplicationRequest {
	private static final long serialVersionUID = 1L;

	public AsyncPushNotification() {
		this(UUIDGenerator.uuid(), null);
	}

	public AsyncPushNotification(Map<String, Object> headers) {
		this(UUIDGenerator.uuid(), headers);
	}
	
	public AsyncPushNotification(Map<String, Object> headers, MultipartMessage multipartMessage) {
		this(UUIDGenerator.uuid(), headers, multipartMessage);
	}

	public AsyncPushNotification(String id, Map<String, Object> headers) {
		this(id, headers, null);
	}

	public AsyncPushNotification(String id, Map<String, Object> headers, MultipartMessage multipartMessage) {
		super(id, headers, multipartMessage);
	}

	@Override
	public String toString() {
		return "xmpp-request-" + getId();
	}

	@Override
	public String getEndpoint() {
		// TODO Auto-generated method stub
		return null;
	}  

}
