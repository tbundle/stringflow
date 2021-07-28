package abs.ixi.server.rest;

import java.time.Duration;
import java.util.Map;

import org.apache.commons.httpclient.HttpMethod;

/**
 * Class to represent a REST Endpoint configurations
 * 
 * @author Yogi
 */
public class RestEndpointImpl implements RestEndpoint {
	/**
	 * Default value of response timeout a rest endpoint
	 */
	private static final Duration RESPONSE_TIMEOUT = Duration.ofSeconds(10);

	/**
	 * Endpoint url which may have path variables
	 */
	private String url;

	/**
	 * Params for this endpoint
	 */
	private Map<String, RequestParam<?>> params;

	/**
	 * Http Method used while hiting this endpoint
	 */
	private HttpMethod httpMethod;

	/**
	 * It is expeccted that response will be received within the timeout
	 * duration from remote server
	 */
	private Duration timeout;

	public RestEndpointImpl(String url, HttpMethod httpMethod) {
		this(url, null, httpMethod, RESPONSE_TIMEOUT);
	}

	public RestEndpointImpl(String url, Map<String, RequestParam<?>> params, HttpMethod httpMethod) {
		this(url, params, httpMethod, RESPONSE_TIMEOUT);
	}

	public RestEndpointImpl(String url, Map<String, RequestParam<?>> params, HttpMethod httpMethod, Duration timeout) {
		this.url = url;
		this.params = params;
		this.httpMethod = httpMethod;
		this.timeout = timeout;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Map<String, RequestParam<?>> getParams() {
		return params;
	}

	/**
	 * May return a null value if there is no param with specified key
	 * 
	 * @param key
	 * @return
	 */
	public RequestParam<?> getRequestParam(String key) {
		return this.params != null ? this.params.get(key) : null;
	}

	public void setParams(Map<String, RequestParam<?>> params) {
		this.params = params;
	}

	public HttpMethod getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(HttpMethod httpMethod) {
		this.httpMethod = httpMethod;
	}

	public Duration getTimeout() {
		return timeout;
	}

	public void setTimeout(Duration timeout) {
		this.timeout = timeout;
	}

}
