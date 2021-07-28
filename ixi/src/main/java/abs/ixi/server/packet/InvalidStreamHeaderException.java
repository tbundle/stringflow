package abs.ixi.server.packet;

public class InvalidStreamHeaderException extends XMPPException{
	private static final long serialVersionUID = 1L;

	public InvalidStreamHeaderException(String msg) {
		super(msg);
	}
	
	public InvalidStreamHeaderException(Exception ex) {
		super(ex);
	}
}
