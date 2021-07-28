package abs.ixi.httpclient;

import java.util.HashMap;
import java.util.Map;

import abs.ixi.httpclient.util.StringUtils;

public class HttpRequestBuilder {
	private HttpUrl url;
	private HttpMethod method;
	private String contentType;
	private String contentEncoding;
	private HttpContent content;
	private Map<String, String> headers;
	private HttpTransport transport;
	private HttpUrlBuilder urlBuilder;

	// Default access to constructor
	HttpRequestBuilder(HttpTransport transport) {
		this.transport = transport;
		this.headers = new HashMap<String, String>();
		this.urlBuilder = new HttpUrlBuilder();
	}

	public HttpRequestBuilder withMethod(HttpMethod method) {
		this.method = method;
		return this;
	}

	public HttpRequestBuilder withHeader(String name, String value) {
		headers.put(name, value);
		return this;
	}

	public HttpRequestBuilder withHeaders(Map<String, String> headers) {
		headers.putAll(headers);
		return this;
	}

	public HttpRequestBuilder withContentType(String contentType) {
		this.contentType = contentType;
		return this;
	}

	public HttpRequestBuilder withContentEncoding(String contentEncoding) {
		this.contentEncoding = contentEncoding;
		return this;
	}

	public HttpRequestBuilder withContent(HttpContent content) {
		this.content = content;
		return this;
	}

	public HttpUrlBuilder getUrlBuilder() {
		return urlBuilder;
	}

	public HttpRequestBuilder withUrl(HttpUrl url) {
		this.url = url;
		return this;
	}

	public HttpRequest build() {
		HttpRequest request = new HttpRequest();
		request.setHeaders(headers);
		request.setMethod(method);

		if (!StringUtils.isNullOrEmpty(contentEncoding))
			request.setContentEncoding(contentEncoding);

		if (!StringUtils.isNullOrEmpty(contentType))
			request.setContentType(contentType);

		request.addHeader(HttpHeader.USER_AGENT.val(), HttpRequest.IXI_USER_AGENT);

		request.setContent(content);
		request.setHttpUrl(url);
		request.setTransport(transport);
		return request;
	}
}
