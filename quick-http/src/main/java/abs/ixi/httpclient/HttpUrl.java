package abs.ixi.httpclient;

import java.net.URL;

/**
 * A wrapper around {@link URL}
 */
public class HttpUrl {
	private URL url;

	public HttpUrl(URL url) {
		this.url = url;
	}

	public URL getUrl() {
		return url;
	}

}
