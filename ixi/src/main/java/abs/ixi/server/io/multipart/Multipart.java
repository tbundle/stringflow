package abs.ixi.server.io.multipart;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import abs.ixi.server.Writable;
import abs.ixi.server.common.ByteArray;
import abs.ixi.server.common.ChannelStream;
import abs.ixi.util.ObjectUtils;

/**
 * A {@link MultipartMessage} can have multiple parts within it; each of these
 * parts is represented by {@link Multipart}. A {@code Multipart} contains
 * headers and body. Headers are stored in a {@link Map} and miltipart body is
 * held in a {@link Content}
 */
public class Multipart implements Writable {
	private static final char COLON = ':';
	private static final String LINE_FEED = "\r\n";
	public static final byte LF = 0xA;
	public static final byte CR = 0xD;

	public static final String NAME = "name";
	public static final String CONTENT_TYPE = "Content-Type";
	public static final String CONTENT_LENGTH = "Content-Length";

	protected static final byte[] LINE_FEED_BYTES = new byte[] { CR, LF };
	public static final ByteBuffer LINE_FEED_BUFFER = ByteBuffer.wrap(new byte[] { CR, LF });

	private Map<String, Object> headers;
	private Content content;
	private byte[] headerBytes;
	private boolean generatedHeaderBytes;

	public Multipart() {
		this.headers = new HashMap<>();
	}

	public Multipart(String name) {
		this(name, null);
	}

	public Multipart(String name, Content content) {
		this.headers = new HashMap<>();
		this.content = content;
		this.setContentName(name);

		addHeaders();
	}

	public Multipart(Map<String, Object> headers, byte[] content, ContentType type) {
		this.headers = headers;
		this.content = new BinaryContent(new ByteArrayContentSource(content), type);

		addHeaders();
	}

	private void setContentName(String name) {
		this.headers.put(NAME, name);
	}

	public String getContentName() {
		return (String) this.headers.get(NAME);
	}

	/**
	 * Add Content-Type and Content-Length header to {@link Multipart} headers
	 */
	private void addHeaders() {
		if (content != null) {
			this.headers.put(CONTENT_TYPE, content.getContentType());
			this.headers.put(CONTENT_LENGTH, content.getContentLength());
		}
	}

	public Content getContent() {
		return content;
	}

	public void setContent(Content content) {
		this.content = content;
		addHeaders();
	}

	public ContentSource getContentSource() {
		return this.content.getContentSource();
	}

	public void addHeader(String key, Object value) {
		this.headers.put(key, value);
	}

	public void addHeaders(Map<String, Object> headers) {
		this.headers.putAll(headers);
	}

	public Object getHeader(String key) {
		return this.headers.get(key);
	}

	public Map<String, Object> getHeaders() {
		return this.headers;
	}

	public int getContentLength() {
		return ObjectUtils.parseToInt(this.getHeader(CONTENT_LENGTH).toString());
	}

	public void addNewBinaryContent() {
		this.content = new BinaryContent(getContentLength());
	}

	/**
	 * Append headers to a String and generate bytes using
	 * {@link StandardCharsets#US_ASCII} encoding.
	 */
	protected byte[] generateHeaderBytes() {
		StringBuilder sb = new StringBuilder();

		for (Entry<String, Object> header : this.headers.entrySet()) {
			if (header.getValue() != null) {
				sb.append(header.getKey());
				sb.append(COLON);
				sb.append(header.getValue());
				sb.append(LINE_FEED);
			}
		}

		this.headerBytes = sb.toString().getBytes(StandardCharsets.US_ASCII);
		this.generatedHeaderBytes = true;

		return this.headerBytes;
	}

	public byte[] getHeaderBytes() {
		return this.generatedHeaderBytes ? this.headerBytes : generateHeaderBytes();
	}

	/**
	 * @return value of Content-Type header for this {@link Multipart}
	 */
	public String getContentType() {
		Object contentType = this.headers.get(CONTENT_TYPE);
		return contentType == null ? null : contentType.toString();
	}

	/**
	 * Calculate the length of this multipart object. Length is the number of
	 * bytes required to represent this Multipart in memeory. Returned length
	 * includes both header bytes along with body content bytes.
	 */
	public long getMultipartLength() {
		long byteCount = 0;

		if (content == null) {
			return byteCount;
		}

		byteCount += getHeaderBytes().length;
		byteCount += LINE_FEED_BUFFER.limit();

		if (this.getContentSource() != null) {
			byteCount += this.getContentSource().getLength();
		}

		byteCount += LINE_FEED_BUFFER.limit();

		return byteCount;
	}

	public void addMultipartContent(byte[] arr, int startIndex, int length) {
		// TODO We need to expand ContentSource and Content Interface to expose
		// methods to add content; The Multipart implementation needs to have
		// mechanism to switch ContentSource based on amount/type of data it is
		// receiving. Please note here we are directly accessing ContentSource
		// instead of Content; fix it!

		if (this.content.getContentSource() instanceof ByteArrayContentSource) {
			ByteArrayContentSource src = (ByteArrayContentSource) this.content.getContentSource();
			src.addContent(arr, startIndex, length);
		}
	}

	public void addMultipartContent(ByteArray byteSource, int startIndex, int endIndex) {
		// TODO We need to expand ContentSource and Content Interface to expose
		// methods to add content; The Multipart implementation needs to have
		// mechanism to switch ContentSource based on amount/type of data it is
		// receiving. Please note here we are directly accessing ContentSource
		// instead of Content; fix it!

		if (this.content.getContentSource() instanceof ByteArrayContentSource) {
			ByteArrayContentSource src = (ByteArrayContentSource) this.content.getContentSource();
			src.addContent(byteSource.bytes(), startIndex, endIndex - startIndex);
		}
	}

	@Override
	public long writeTo(Socket socket) throws IOException {
		return 0;
	}

	@Override
	public long writeTo(OutputStream os) throws IOException {
		return 0;
	}

	@Override
	public long writeTo(SocketChannel socketChannel) throws IOException {
		long byteCount = 0;

		if (content == null) {
			return byteCount;
		}

		byteCount += socketChannel.write(ByteBuffer.wrap(getHeaderBytes()));
		byteCount += writeLineFeed(socketChannel);

		if (this.getContentSource() != null) {
			byteCount += this.getContentSource().writeTo(socketChannel);
		}

		byteCount += writeLineFeed(socketChannel);

		return byteCount;
	}

	@Override
	public long writeTo(ChannelStream cs) throws IOException {
		long byteCount = 0;

		if (content == null) {
			return byteCount;
		}

		byte[] headerBytes = getHeaderBytes();
		cs.enqueue(headerBytes);
		byteCount += headerBytes.length;

		cs.enqueue(LINE_FEED_BYTES);
		byteCount += LINE_FEED_BYTES.length;

		if (this.getContentSource() != null) {
			byteCount += this.getContentSource().writeTo(cs);
		}

		cs.enqueue(LINE_FEED_BYTES);
		byteCount += LINE_FEED_BYTES.length;

		return byteCount;
	}

	private long writeLineFeed(SocketChannel socketChannel) throws IOException {
		int count = socketChannel.write(LINE_FEED_BUFFER);
		LINE_FEED_BUFFER.flip();

		return count;
	}

}
