package abs.ixi.server.sys.secure.sasl;

import java.util.Arrays;
import java.util.Map;

import javax.security.auth.callback.CallbackHandler;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;
import javax.security.sasl.SaslServerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory implementation to instantiate {@link SASLMechanism} instances. The
 * factory needs to determine the characteristics of the mechanism that it
 * supports (as described by the Sasl.POLICY_* properties) so that it can return
 * an instance of the mechanism when the API user requests it using compatible
 * policy properties. The factory may also check for validity of the parameters
 * before creating the mechanism.
 * 
 * <p>
 * As specified in Java documentation for {@link SaslServerFactory}, this class
 * MUST be thread-safe and MUST have a public constructor that accepts no
 * argument.
 * </p>
 */
public class SaslMechanismFactory implements SaslServerFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(SaslMechanismFactory.class);

    private static String[] mechanisms;

    static {
	mechanisms = new String[] { PlainMechanism.NAME };
	LOGGER.info("Added supported mechanisms " + Arrays.toString(mechanisms));
    }

    @Override
    public SaslServer createSaslServer(String mechanism, String protocol, String serverName, Map<String, ?> props,
	    CallbackHandler cbh) throws SaslException {
	switch (mechanism) {
	case PlainMechanism.NAME:
	    return new PlainMechanism(props, cbh);
	case "ANONYMOUS":
	    return new AnonymousMechanism(props, cbh);
	case "EXTERNAL":
	    return new ExternalMechanism(props, cbh);
	default:
	    throw new SaslException("Mechanism not supported yet.");
	}
    }

    @Override
    public String[] getMechanismNames(Map<String, ?> props) {
	return mechanisms;
    }

}
