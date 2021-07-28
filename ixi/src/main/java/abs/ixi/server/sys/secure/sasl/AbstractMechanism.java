package abs.ixi.server.sys.secure.sasl;

import java.io.IOException;
import java.util.Map;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.SaslException;

/**
 * Abstract implementation of a {@link SASLMechanism}.
 */
public abstract class AbstractMechanism implements SASLMechanism {
    private Map<? super String, ?> props;
    protected String authzid = null;
    protected boolean complete = false;
    protected CallbackHandler callbackHandler;

    protected AbstractMechanism(Map<? super String, ?> props, CallbackHandler callbackHandler) {
	this.props = props;
	this.callbackHandler = callbackHandler;
    }

    protected void handleCallbacks(Callback[] callbacks) throws SaslException {
	try {

	    this.callbackHandler.handle(callbacks);

	} catch (IOException e) {
	    throw new SaslException("Failed to handle callback in " + getMechanismName(), e);

	} catch (UnsupportedCallbackException e) {
	    throw new SaslException("Handler does not support callback", e);

	}
    }

    @Override
    public String getAuthorizationID() {
	return this.authzid;
    }

    @Override
    public boolean isComplete() {
	return this.complete;
    }

    @Override
    public byte[] unwrap(byte[] incoming, int offset, int len) throws SaslException {
	return null;
    }

    @Override
    public byte[] wrap(byte[] outgoing, int offset, int len) throws SaslException {
	return null;
    }

    @Override
    public Object getNegotiatedProperty(String propName) {
	return null;
    }

    @Override
    public void dispose() throws SaslException {

    }

}
