package abs.ixi.server.io.multipart;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import abs.ixi.server.Writable;
import abs.ixi.server.common.ChannelStream;
import abs.ixi.util.CollectionUtils;
import abs.ixi.util.UUIDGenerator;

/**
 * Represents a multipart message. A multipart message is a collection of its
 * body parts. A body part contains header and body content (represented by
 * {@link Multipart}). The class maintains order of multipart bodies as they
 * appear in multipart byte stream.
 */
public class MultipartMessage implements Writable {
	protected static final byte DASH = 45;
	protected static final byte LF = 0xA;
	protected static final byte CR = 0xD;

	protected static final byte[] LINE_FEED = new byte[] { CR, LF };
	protected static final ByteBuffer LINE_FEED_BUFFER = ByteBuffer.wrap(new byte[] { CR, LF });

	private static final String BOUNDARY = "boundary";
	private static final char HYPHAN_CHAR = '-';

	private Map<String, String> headers;
	private List<Multipart> parts;
	private byte[] boundary;

	public MultipartMessage() {
		this.headers = new HashMap<>();
		this.parts = new ArrayList<>();
		this.addHeader(BOUNDARY, generateBoundary());
	}

	public MultipartMessage(byte[] boundary) {
		this.headers = new HashMap<>();
		this.parts = new ArrayList<>();
		this.boundary = boundary;
		this.addHeader(BOUNDARY, new String(boundary, StandardCharsets.US_ASCII));
	}

	public MultipartMessage(String boundary) {
		this.headers = new HashMap<>();
		this.parts = new ArrayList<>();
		this.boundary = boundary.getBytes(StandardCharsets.US_ASCII);
		this.addHeader(BOUNDARY, boundary);
	}

	private void addHeader(String key, String value) {
		this.headers.put(key, value);
	}

	private static String generateBoundary() {
		StringBuilder sb = new StringBuilder().append(UUIDGenerator.secureId(16)).append(HYPHAN_CHAR)
				.append(UUIDGenerator.secureId(16));

		return sb.toString();
	}

	public List<Multipart> getParts() {
		return parts;
	}

	public void addMultiPart(Multipart part) {
		this.parts.add(part);
	}

	public byte[] getBoundary() {
		return boundary;
	}

	public int getBoundayLength() {
		return this.getBoundary().length;
	}

	/**
	 * Returns a new multipart. Returned multipart is added to multipart list
	 * before returning.
	 */
	public Multipart newMultipart() {
		Multipart m = new Multipart();
		parts.add(m);
		return m;
	}

	/**
	 * @return number of body parts held by this message
	 */
	public int partCount() {
		return this.parts.size();
	}

	@Override
	public long writeTo(OutputStream os) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long writeTo(Socket socket) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long writeTo(ChannelStream cs) throws IOException {
		long byteCount = 0;

		if (CollectionUtils.isNullOrEmpty(parts)) {
			return byteCount;
		}

		byte[] beginBoundryBytes = generateBoundaryBegin();

		for (Multipart part : parts) {
			cs.enqueue(beginBoundryBytes);
			byteCount += beginBoundryBytes.length;
			byteCount += part.writeTo(cs);
		}

		byte[] endBoundryBytes = generateBoundaryEnd();
		cs.enqueue(generateBoundaryEnd());
		byteCount += endBoundryBytes.length;

		cs.enqueue(LINE_FEED);
		byteCount += LINE_FEED.length;

		return byteCount;
	}

	@Override
	public long writeTo(SocketChannel socketChannel) throws IOException {
		long byteCount = 0;

		if (CollectionUtils.isNullOrEmpty(parts)) {
			return byteCount;
		}

		ByteBuffer boundaryBuffer = ByteBuffer.wrap(generateBoundaryBegin());

		for (Multipart part : parts) {
			byteCount += socketChannel.write(boundaryBuffer);
			boundaryBuffer.flip();

			part.writeTo(socketChannel);
		}

		byteCount += socketChannel.write(ByteBuffer.wrap(generateBoundaryEnd()));
		byteCount += writeLineFeed(socketChannel);

		return byteCount;
	}

	private int writeLineFeed(SocketChannel socketChannel) throws IOException {
		int count = socketChannel.write(LINE_FEED_BUFFER);
		LINE_FEED_BUFFER.flip();
		return count;
	}

	private byte[] generateBoundaryBegin() {
		byte[] boundaryBegin = new byte[this.boundary.length + 4];
		int offset = 0;

		boundaryBegin[offset++] = DASH;
		boundaryBegin[offset++] = DASH;

		System.arraycopy(this.getBoundary(), 0, boundaryBegin, offset, this.getBoundayLength());

		offset += this.getBoundayLength();

		boundaryBegin[offset++] = CR;
		boundaryBegin[offset++] = LF;

		return boundaryBegin;
	}

	private long getBoundaryBeginLength() {
		return this.boundary.length + 4;
	}

	private byte[] generateBoundaryEnd() {
		byte[] boundaryEnd = new byte[this.boundary.length + 6];
		int offset = 0;

		boundaryEnd[offset++] = DASH;
		boundaryEnd[offset++] = DASH;

		System.arraycopy(this.getBoundary(), 0, boundaryEnd, offset, this.getBoundayLength());

		offset += this.getBoundayLength();

		boundaryEnd[offset++] = DASH;
		boundaryEnd[offset++] = DASH;
		boundaryEnd[offset++] = CR;
		boundaryEnd[offset++] = LF;

		return boundaryEnd;
	}

	private long getBoundaryEndLength() {
		return this.boundary.length + 6;
	}

	public long getMimeLength() {
		long byteCount = 0;

		if (CollectionUtils.isNullOrEmpty(parts))
			return byteCount;

		for (Multipart part : parts) {
			byteCount += getBoundaryBeginLength();

			byteCount += part.getMultipartLength();
		}

		byteCount += getBoundaryEndLength();
		byteCount += LINE_FEED_BUFFER.limit();

		return byteCount;
	}

}
