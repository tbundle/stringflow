package abs.ixi.server.sys.secure.sasl;

import java.util.Enumeration;
import java.util.function.Predicate;

import javax.security.sasl.Sasl;
import javax.security.sasl.SaslServer;
import javax.security.sasl.SaslServerFactory;

/**
 * Factory implementation to find a matching {@link SaslServer} instance.
 * {@code SaslMechanismSelector} scans through all the {@link SaslServer}
 * available with JCA providers to match a given predicate.
 * 
 * <p>
 * Mere availability of a mechanism does not guarantee a match as the
 * {@code SaslMechanismSelector} filters mechanisms further for custom rules
 * imposed in ixi server; And for that, the class also keeps list of
 * allowed/not-allowed mechanism.
 * </p>
 */
public class SaslMechanismSelector {
	public String match(Predicate<String> p) {
		Enumeration<SaslServerFactory> saslServerFactories = availableSaslServerFactories();

		while (saslServerFactories.hasMoreElements()) {
			SaslServerFactory factory = saslServerFactories.nextElement();

			String[] mechanisms = factory.getMechanismNames(null);

			for (String m : mechanisms) {
				if (p.test(m)) {
					return m;
				}
			}
		}

		return null;
	}

	/**
	 * Retrieves {@link SaslServerFactory} instances registered with JCA
	 * providers. If a child implementation wish to refine the set of factories
	 * considered this method should be overridden by the implementations
	 */
	protected Enumeration<SaslServerFactory> availableSaslServerFactories() {
		return Sasl.getSaslServerFactories();
	}
}
