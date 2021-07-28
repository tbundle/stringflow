package abs.ixi.httpclient;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.HttpsURLConnection;

public interface ConnectionFactory {
	public HttpURLConnection openHttpConnection(URL url) throws IOException;

	public HttpsURLConnection openHttpsConnection(URL url, String certPath, String password) throws IOException,
			KeyManagementException, NoSuchAlgorithmException, KeyStoreException, CertificateException;
}
