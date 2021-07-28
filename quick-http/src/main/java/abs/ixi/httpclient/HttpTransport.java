package abs.ixi.httpclient;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpTransport {
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpTransport.class);
	private ConnectionFactory connFactory;

	public HttpTransport() {
		this.connFactory = new DefaultConnectionFactory();
	}

	public HttpResponse sendRequest(HttpRequest request) throws IOException {
		LOGGER.debug("opening connection with destination {}", request.getHttpUrl());

		HttpResponse response = null;
		HttpURLConnection connection = null;

		try {

			connection = this.connFactory.openHttpConnection(request.getHttpUrl().getUrl());
			connection.setRequestMethod(request.getMethod().val());

			for (Map.Entry<String, String> header : request.getHeaders().entrySet()) {
				connection.setRequestProperty(header.getKey(), header.getValue());
			}

			if (HttpMethod.POST == request.getMethod()) {
				connection.setDoOutput(true);
				connection.getOutputStream().write(request.getContent().getBody().getBytes("UTF-8"));
			}

			LOGGER.debug("Sending request {}", request);
			connection.connect();

			response = new HttpResponse(connection);

		} catch (IOException e) {
			LOGGER.error("Exception caught while processing the request", e);
			throw e;

		}

		return response;
	}

	public HttpResponse sendRequest(HttpRequest request, String certPath, String password) throws IOException,
			KeyManagementException, NoSuchAlgorithmException, KeyStoreException, CertificateException {
		LOGGER.debug("opening connection with destination {}", request.getHttpUrl());

		HttpResponse response = null;
		HttpURLConnection connection = null;

		try {

			connection = connFactory.openHttpsConnection(request.getHttpUrl().getUrl(), certPath, password);
			connection.setRequestMethod(request.getMethod().val());

			for (Map.Entry<String, String> header : request.getHeaders().entrySet()) {
				connection.setRequestProperty(header.getKey(), header.getValue());
			}

			LOGGER.debug("Sending request {}", request);
			connection.connect();

			response = new HttpResponse(connection);

		} catch (IOException e) {
			LOGGER.error("Exception caught while processing the request", e);
			throw e;

		} finally {

			try {
				connection.disconnect();
			} catch (Exception e) {
				LOGGER.warn("failed to disconnect http connection. Ignoring...");
			}
		}

		return response;
	}

	public HttpRequestBuilder requestBuilder() {
		return new HttpRequestBuilder(this);
	}

}
