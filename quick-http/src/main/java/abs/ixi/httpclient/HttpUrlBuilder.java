package abs.ixi.httpclient;

import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.httpclient.util.StringUtils;

public class HttpUrlBuilder {
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpUrlBuilder.class);

	private int port;
	private String host;
	private StringBuilder queryParams;
	private StringBuilder pathParams;
	private String protocol;

	public HttpUrlBuilder() {
		this.queryParams = new StringBuilder();
		this.pathParams = new StringBuilder();
	}

	public HttpUrlBuilder withHostname(String host) {
		this.host = host;

		return this;
	}

	public HttpUrlBuilder withPort(int port) {
		this.port = port;
		return this;
	}

	public HttpUrlBuilder withPathParameter(String pathParam) {
		pathParams.append("/" + pathParam);
		return this;
	}

	public HttpUrlBuilder withProtocol(String protocol) {
		this.protocol = protocol;
		return this;
	}

	public HttpUrlBuilder withQueryParameter(String query, String param) {
		queryParams.append(query + "=" + param + "&");
		return this;
	}

	public HttpUrl build() throws MalformedURLException {
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append(protocol).append("://").append(host);

		if (port > 0)
			urlBuilder.append(":").append(port);

		if (!StringUtils.isNullOrEmpty(pathParams.toString()))
			urlBuilder.append(pathParams.toString());

		if (!StringUtils.isNullOrEmpty(queryParams.toString()))
			urlBuilder.append("?").append(queryParams.toString().substring(0, queryParams.toString().length() - 2));

		LOGGER.debug("url :" + urlBuilder.toString());

		URL url = new URL(urlBuilder.toString());
		HttpUrl httpUrl = new HttpUrl(url);

		return httpUrl;
	}

}
