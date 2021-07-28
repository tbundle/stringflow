package abs.ixi.server.sys.secure.sasl;

import javax.security.auth.callback.CallbackHandler;

/**
 * Factory implementation to identify and instantiate {@link CallbackHandler}
 * instances for Sasl
 */
public class CallbackHandlerFactory {
    /**
     * factory method which returns callback handler instance for a mechanism
     * 
     * @param mechanism
     * @return
     */
    public static CallbackHandler create(String mechanism) {
	switch (mechanism) {

	case PlainMechanism.NAME:
	    return new PlainCallbackHandler();

	default:
	    return new DefaultCallbackHandler();
	}

    }
}
