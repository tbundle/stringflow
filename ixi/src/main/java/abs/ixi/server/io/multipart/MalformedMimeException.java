package abs.ixi.server.io.multipart;

public class MalformedMimeException extends Exception {
    private static final long serialVersionUID = 1L;

    public MalformedMimeException() {
	super();
    }

    public MalformedMimeException(String msg) {
	super(msg);
    }

    public MalformedMimeException(Exception e) {
	super(e);
    }
}
