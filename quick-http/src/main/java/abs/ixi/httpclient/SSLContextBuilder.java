package abs.ixi.httpclient;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class SSLContextBuilder {
	private KeyManagerFactory keyManagerFactory;
	private TrustManager[] trustManagers;
	private String algo = "sunx509";

	public SSLContextBuilder withAlgo(String algo) {
		this.algo = algo;
		return this;
	}

	public SSLContextBuilder withDefaultTrustKeyStore() throws KeyStoreException, NoSuchAlgorithmException {

		TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(algo);
		trustManagerFactory.init((KeyStore) null);
		trustManagers = trustManagerFactory.getTrustManagers();
		return this;

	}

	public SSLContextBuilder withTrustKeyStore(InputStream keyStoreStream, String keyStorePassword, String keyStoreType)
			throws NoSuchAlgorithmException, CertificateException, IOException, KeyStoreException {
		final KeyStore ks = KeyStore.getInstance(keyStoreType);
		ks.load(keyStoreStream, keyStorePassword.toCharArray());
		return withTrustKeyStore(ks, keyStorePassword);

	}

	public SSLContextBuilder withTrustKeyStore(KeyStore keyStore, String keyStorePassword)
			throws NoSuchAlgorithmException {
		TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(algo);
		trustManagers = trustManagerFactory.getTrustManagers();
		return this;
	}

	public SSLContext build() throws NoSuchAlgorithmException, KeyManagementException {
		if (keyManagerFactory == null) {
			return null;

		}

		if (trustManagers == null) {
			return null;
		}

		final SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(keyManagerFactory.getKeyManagers(), trustManagers, null);
		return sslContext;

	}

}
