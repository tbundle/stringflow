package abs.ixi.server.sys.secure.sasl;

import javax.security.sasl.SaslException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SaslEngineFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(SaslEngineFactory.class);

    private static SaslEngine saslEngine;

    public static SaslEngine getSaslEngine() {
	if (saslEngine == null) {

	    synchronized (SaslEngineFactory.class) {
		if (saslEngine == null) {
		    try {
			saslEngine = new SaslEngine();
			return saslEngine;

		    } catch (SaslException e) {
			LOGGER.error("Failes to instansiate SaslEngine", e);
		    }
		}
	    }

	}

	return saslEngine;
    }

}
