package abs.ixi.server.packet;

public class IllegalStreamStateException extends XMPPException {
	private static final long serialVersionUID = 1L;

	public IllegalStreamStateException(String msg) {
		super(msg);
	}
}
