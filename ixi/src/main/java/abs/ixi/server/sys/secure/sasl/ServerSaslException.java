package abs.ixi.server.sys.secure.sasl;

import javax.security.sasl.SaslException;

/**
 * {@code ServerSaslException} is an extension of JCA provided
 * {@link SaslException}. It helps encapsulate the {@link SaslError} that
 * occurred.
 */
public class ServerSaslException extends SaslException {
    private static final long serialVersionUID = 3164275705155007744L;

    private SaslError error;

    public ServerSaslException() {
	super();
    }

    public ServerSaslException(SaslError error) {
	super();
	this.error = error;
    }

    public SaslError getError() {
	return error;
    }

}
