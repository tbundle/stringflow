package abs.ixi.server;

import abs.ixi.server.packet.XMPPError;

public class ValidationError extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private XMPPError error;

    public ValidationError(XMPPError error) {
	super(error.toString());
	this.error = error;
    }

    public XMPPError getError() {
	return error;
    }
}
