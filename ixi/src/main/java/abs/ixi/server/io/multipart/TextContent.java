package abs.ixi.server.io.multipart;

import java.nio.charset.StandardCharsets;

public class TextContent extends AbstractContent {
	private String contentType;
	private long contentLength;
	private ContentSource contentSource;

	public TextContent(String text) {
		this(text, new ContentType(MimeType.PLAIN_TEXT.getMimeType()));
	}

	public TextContent(String text, ContentType type) {
		this(text, type != null ? type.getMimeType() : null);
	}

	public TextContent(String text, String contentType) {
		super(new ByteArrayContentSource(text.getBytes(StandardCharsets.US_ASCII)));

		this.contentType = contentType;
		this.contentLength = contentSource.getLength();
	}

	@Override
	public String getContentType() {
		return this.contentType;
	}

	@Override
	public ContentSource getContentSource() {
		return this.contentSource;
	}

	@Override
	public long getContentLength() {
		return this.contentLength;
	}

}
