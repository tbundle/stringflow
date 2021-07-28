package abs.ixi.server.sys.secure.sasl;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

import abs.ixi.server.session.SessionManager;

/**
 * {@link CallbackHandler} to handle {@link PlainMechanism} callbacks
 */
public class PlainCallbackHandler implements CallbackHandler {

    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
	if (callbacks != null) {
	    PasswordVerificationCallback passCallback = null;
	    String name = null;
	    String passwd = null;

	    for (Callback callback : callbacks) {

		if (NameCallback.class.isAssignableFrom(callback.getClass())) {
		    NameCallback nameCallBack = (NameCallback) callback;
		    name = nameCallBack.getDefaultName();

		} else if (PasswordVerificationCallback.class.isAssignableFrom(callback.getClass())) {
		    passCallback = (PasswordVerificationCallback) callback;
		    passwd = passCallback.getPasswd();

		} else {

		    throw new UnsupportedCallbackException(callback);
		}

	    }

	    if (passCallback != null && name != null && passwd != null) {
		boolean result = SessionManager.getInstance().authenticate(name, passwd);
		passCallback.setResult(result);
	    }

	}
    }

}
