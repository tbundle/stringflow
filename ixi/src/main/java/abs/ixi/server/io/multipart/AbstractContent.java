package abs.ixi.server.io.multipart;

/**
 * Abstract implementation of {@link Content} interface. {@code AbstractContent}
 * holds {@link ContentSource} instance and delegate methods for
 * {@link ContentSource}
 */
public abstract class AbstractContent implements Content {
    protected ContentSource source;
    protected String contentType; // MIME

    public AbstractContent(ContentSource source) {
	this.source = source;
    }

    @Override
    public String getContentType() {
	return this.contentType;
    }

    @Override
    public long getContentLength() {
	return this.source.getLength();
    }

    @Override
    public ContentSource getContentSource() {
	return this.source;
    }

}
