package abs.ixi.server.sys.secure.sasl;

import java.util.Map;

import javax.security.auth.callback.CallbackHandler;
import javax.security.sasl.SaslException;

public class ExternalMechanism extends AbstractMechanism {
    public static final String NAME = "EXTERNAL";

    public ExternalMechanism(Map<String, ?> props, CallbackHandler cbh) {
	super(props, cbh);
    }

    @Override
    public String getMechanismName() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public byte[] evaluateResponse(byte[] response) throws SaslException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public boolean isComplete() {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public String getAuthorizationID() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public byte[] unwrap(byte[] incoming, int offset, int len) throws SaslException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public byte[] wrap(byte[] outgoing, int offset, int len) throws SaslException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Object getNegotiatedProperty(String propName) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void dispose() throws SaslException {
	// TODO Auto-generated method stub

    }

}
