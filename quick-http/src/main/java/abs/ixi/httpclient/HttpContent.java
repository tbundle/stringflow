package abs.ixi.httpclient;

public class HttpContent {
	private String body;

	public HttpContent() {
		// Do nothing constructor
	}

	public HttpContent(String body) {
		this.body = body;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
}
