package abs.ixi.server.app;

/**
 * Data holder object to be used within {@link XmppRequest}
 */
public class Payload {
	private byte[] body;

	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}

	public int contentLength() {
		return body.length;
	}
}
