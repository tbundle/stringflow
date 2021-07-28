package abs.ixi.httpclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

public class HttpResponse {
	private HttpURLConnection connection;
	private final int responseCode;
	private final String responseMsg;

	private Map<String, String> headers;

	public HttpResponse(HttpURLConnection connection) throws IOException {
		this.headers = new HashMap<>();
		this.connection = connection;
		this.responseCode = connection.getResponseCode();
		this.responseMsg = connection.getResponseMessage();

		for (Map.Entry<String, List<String>> entry : connection.getHeaderFields().entrySet()) {
			String key = entry.getKey();
			if (key != null) {
				for (String value : entry.getValue()) {
					if (value != null) {
						// TODO Add multiple header values
						headers.put(key, value);
					}
				}
			}
		}
	}

	public Map<String, String> getHeaders() {
		return this.headers;
	}

	public int getResponseCode() {
		return this.responseCode;
	}

	public InputStream getInputStream() throws IOException {
		return this.connection.getInputStream();
	}

	public InputStream getErrorStream() {
		return this.connection.getErrorStream();
	}

	public String getResponseMessage() {
		return this.responseMsg;
	}

	public String getContentEncoding() {
		return this.connection.getContentEncoding();
	}

	public long getContentLength() {
		String strLength = headers.get(HttpHeader.CONTENT_LENGTH.val());
		return strLength == null ? -1 : Long.parseLong(strLength);
	}

	public String getContentType() {
		return this.headers.get(HttpHeader.CONTENT_TYPE.val());
	}

	/**
	 * Supports only HTTP1.0 and HTTP1.1
	 * 
	 * @return
	 */
	public String getStatusLine() {
		String result = connection.getHeaderField(0);
		return result != null && result.startsWith("HTTP/1.") ? result : null;
	}

	/**
	 * Returns response content
	 * 
	 * @throws IOException
	 */
	public String getContentTODO() {
		// TODO Lets write it properly
		long contentLength = this.getContentLength();

		if (contentLength > 0) {
			int arraySizeLimit = getVmLimitForArraySize();
			long freeMemory = Runtime.getRuntime().freeMemory();
			int byteCount = freeMemory >= Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) freeMemory;
			int arrayMaxSize = Math.min(arraySizeLimit, byteCount);

			if (contentLength <= arrayMaxSize) {
				// byte[] byteData = new byte[(int)contentLength];
			}
		}
		return null;
	}

	public String getContent() throws IOException {

		InputStream is = getInputStream();
		StringWriter writer = new StringWriter();
		IOUtils.copy(is, writer, "UTF-8");
		return writer.toString();
	}

	private int getVmLimitForArraySize() {
		for (int i = Integer.MAX_VALUE - 1; i >= 0; i--) {
			try {
				@SuppressWarnings("unused")
				char[] c = new char[i];

				return i;
			} catch (Exception e) {
				System.out.println(e);
			}
		}

		return 0;
	}

	/**
	 * Close response and release resources. It will result in closing the
	 * response stream (input stream) also. The connection is not disconnected
	 * though. Therefore the same connection can be reused provided the
	 * underlying connection has been cleanedup.
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		byte[] arr = new byte[512];

		// cleaning the stream
		while (getInputStream().read(arr) > 0) {
			// do nothing
		}

		this.getInputStream().close();
	}

	/**
	 * Disconnect underlying TCP connection to server.It will also release all
	 * the resources held by connection. This method is different than
	 * {@link HttpResponse#close()} which closes input stream only.
	 */
	public void disconnect() {
		this.connection.disconnect();
	}
}
