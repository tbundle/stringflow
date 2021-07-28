package abs.ixi.httpclient;

public enum HttpHeader {
	USER_AGENT("User-Agent"),

	CONTENT_TYPE("Content-Type"),

	CONTENT_LENGTH("Content-Length"),

	CONTENT_ENCODING("Content-Encoding"),

	AUTHORIZATION("Authorization");

	private String val;

	private HttpHeader(String val) {
		this.val = val;
	}

	public String val() {
		return this.val;
	}
}
