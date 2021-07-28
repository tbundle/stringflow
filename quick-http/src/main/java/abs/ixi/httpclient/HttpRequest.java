package abs.ixi.httpclient;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Http request implementation. The implementation is not thread-safe
 */
public class HttpRequest {
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequest.class);

	public static final String IXI_USER_AGENT = "ixi-http-client-ver-1.0.0";

	private Map<String, String> headers;
	private HttpMethod method;
	private HttpContent content;
	private HttpTransport transport;
	private HttpUrl url;

	public HttpTransport getTransport() {
		return transport;
	}

	public void setTransport(HttpTransport transport) {
		this.transport = transport;
	}

	public HttpUrl getHttpUrl() {
		return url;
	}

	public void setHttpUrl(HttpUrl url) {
		this.url = url;
	}

	public HttpContent getContent() {
		return content;
	}

	public void setContent(HttpContent content) {
		this.content = content;
	}

	public HttpMethod getMethod() {
		return method;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void addHeader(String name, String value) {
		headers.put(name, value);
	}

	public void addHeaders(Map<String, String> headers) {
		headers.putAll(headers);
	}

	public void setMethod(HttpMethod method) {
		this.method = method;
	}

	public void setContentType(String contentType) {
		headers.put(HttpHeader.CONTENT_TYPE.val(), contentType);
	}

	public void setContentEncoding(String contentEncoding) {
		headers.put(HttpHeader.CONTENT_ENCODING.val(), contentEncoding);
	}

	public static String getUserAgent() {
		return IXI_USER_AGENT;
	}

	public String getContentType() {
		return headers.get(HttpHeader.CONTENT_TYPE.val());
	}

	public String getContentEncoding() {
		return headers.get(HttpHeader.CONTENT_ENCODING.val());
	}

	public HttpResponse execute() throws IOException {
		LOGGER.debug("executing request {}", this);

		return this.transport.sendRequest(this);
	}

	public void executeAsync(OnResponseCallback callback) throws IOException {
		LOGGER.debug("Executing request in async mode {}", this);
		callback.onResponse(execute());
	}

}
