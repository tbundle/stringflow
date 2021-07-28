package abs.ixi.server.io.multipart;

public class TextHandler extends AbstractContentHandler<String> {
    public TextHandler(Multipart content) {
	super(content);
    }

    @Override
    public String getContent() {
	return null;
    }
}
