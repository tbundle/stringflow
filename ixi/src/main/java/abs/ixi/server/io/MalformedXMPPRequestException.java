package abs.ixi.server.io;

public class MalformedXMPPRequestException extends Exception {
	private static final long serialVersionUID = 1L;

	public MalformedXMPPRequestException() {
		super();
	}
	
	public MalformedXMPPRequestException(Exception e) {
		super(e);
	}

	public MalformedXMPPRequestException(String msg) {
		super(msg);
	}
}
