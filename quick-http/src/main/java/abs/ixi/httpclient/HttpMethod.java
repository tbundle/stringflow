package abs.ixi.httpclient;

public enum HttpMethod {
	POST("POST"),

	GET("GET"),

	PUT("PUT"),

	DELETE("DELETE"),

	HEAD("HEAD");

	private String val;

	private HttpMethod(String val) {
		this.val = val;
	}

	public String val() {
		return val;
	}

	public static HttpMethod valueFrom(String val) {
		for (HttpMethod method : values()) {
			if (method.val().equalsIgnoreCase(val)) {
				return method;
			}
		}
		throw new IllegalArgumentException("No HttpMethod for value [" + val + "]");
	}
}
