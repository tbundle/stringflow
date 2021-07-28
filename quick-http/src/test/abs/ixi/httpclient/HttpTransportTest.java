package abs.ixi.httpclient;

import java.io.IOException;
import java.net.MalformedURLException;

public class HttpTransportTest {
	public static void main(String[] args) {
		try {
			HttpUrl url = new HttpUrlBuilder().withProtocol("http").withHostname("google.com").build();
			HttpRequest httpRequest = new HttpTransport().requestBuilder().withMethod(HttpMethod.GET).withUrl(url).build();

			HttpTransport transport = new HttpTransport();
			HttpResponse httpResponse = transport.sendRequest(httpRequest);
			
			String responseData = httpResponse.getContent();
			System.out.println(responseData);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
