package abs.ixi.server.packet;

import abs.ixi.server.ServerException;

/**
 * {@code XMPPException} captures protocol related violations/issues. It's a
 * generic exception which can be used to capture, may be, undefined error
 * conditions.
 */
public class XMPPException extends ServerException {
	private static final long serialVersionUID = 1L;

	public XMPPException(String msg) {
		super(msg);
	}

	public XMPPException(Exception ex) {
		super(ex);
	}

}
