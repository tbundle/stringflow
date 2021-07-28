package abs.ixi.server.io.multipart;

public abstract class AbstractContentHandler<T> implements ContentHandler<T> {
    protected Multipart content;

    public AbstractContentHandler(Multipart content) {
	this.content = content;
    }

}
