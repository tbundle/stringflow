package abs.ixi.server.io.multipart;

/**
 * {@code BinaryContent} is to represent binary content.
 */
public class BinaryContent extends AbstractContent {
	public BinaryContent(ContentSource source) {
		super(source);
	}

	public BinaryContent(ContentSource source, ContentType contentType) {
		super(source);

		if (contentType != null) {
			this.contentType = contentType.getMimeType();
		}
	}

	public BinaryContent(byte[] content, ContentType contentType) {
		super(new ByteArrayContentSource(content));
	}

	public BinaryContent(int contentLength) {
		super(new ByteArrayContentSource(contentLength));
	}

	@Override
	public long getContentLength() {
		return this.source.getLength();
	}

}
