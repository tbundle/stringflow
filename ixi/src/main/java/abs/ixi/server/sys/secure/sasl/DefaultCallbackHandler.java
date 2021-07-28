package abs.ixi.server.sys.secure.sasl;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.SaslServer;

/**
 * Default callback handler is used when {@link CallbackHandlerFactory} is
 * unable to find a suitable {@link CallbackHandler} for the given
 * {@link SaslServer}
 */
public class DefaultCallbackHandler implements CallbackHandler {

    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
	// do nothing
    }

}
