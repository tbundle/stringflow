package abs.ixi.httpclient;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultConnectionFactory implements ConnectionFactory {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultConnectionFactory.class);

	private static final String KEYSTORE_TYPE = "PKCS12";
	private static final String KEY_ALGORITHM = ((java.security.Security
			.getProperty("ssl.KeyManagerFactory.algorithm") == null) ? "sunx509"
					: java.security.Security.getProperty("ssl.KeyManagerFactory.algorithm"));

	@Override
	public HttpURLConnection openHttpConnection(URL url) throws IOException {
		try {
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			return conn;
		} catch (IOException e) {
			LOGGER.error("Failed to open http connection for url {}", url);
			throw e;
		}
	}

	@Override
	public HttpsURLConnection openHttpsConnection(URL url, String certPath, String password) throws IOException,
			KeyManagementException, NoSuchAlgorithmException, KeyStoreException, CertificateException {

		SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
		SSLContext sslContext = sslContextBuilder.withAlgo(KEY_ALGORITHM)
				.withTrustKeyStore(new FileInputStream(certPath), password, KEYSTORE_TYPE).withDefaultTrustKeyStore()
				.build();

		SSLSocketFactory sslFactory = sslContext.getSocketFactory();
		HttpsURLConnection.setDefaultSSLSocketFactory(sslFactory);

		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setDoInput(true);
		return conn;
	}

}
