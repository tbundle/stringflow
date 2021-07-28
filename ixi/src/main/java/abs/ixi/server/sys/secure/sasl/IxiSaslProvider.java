package abs.ixi.server.sys.secure.sasl;

import static abs.ixi.server.etc.conf.Configurations.Bundle.PROCESS;

import java.security.Provider;

import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.Stringflow;
import abs.ixi.server.etc.conf.ProcessConfigAware;

/**
 * {@code IxiSaslProvider} is a provider implementation as specified in JCA
 * specifications. For more details please refer to the <a href=
 * "http://docs.oracle.com/javase/7/docs/technotes/guides/security/crypto/CryptoSpec.html">JCA
 * documentation</a>
 */
public class IxiSaslProvider extends Provider implements ProcessConfigAware {
	private static final long serialVersionUID = 6094071159413808181L;

	private static final Logger LOGGER = LoggerFactory.getLogger(IxiSaslProvider.class);

	// Name of this provider as mandated by JCA for providers
	public static final String PROVIDER_NAME = "ixi.sasl.provider";

	// provider version as per JCA specifications
	public static final double VERSION = 1.0D;

	// Description information to help understand provider offerings
	public static final String INFO = "Sasl provider which offers mechanism not offered by Java8";

	private static final String SERVICE_TYPE = "SaslServerFactory";

	public IxiSaslProvider() throws SaslException {
		super(PROVIDER_NAME, VERSION, INFO);

		try {
			Class<?> fClz = Class.forName(Stringflow.runtime().configurations().get(SASL_SERVER_FACTORY, PROCESS));

			SaslServerFactory fSasl = (SaslServerFactory) fClz.newInstance();

			String[] mechanisms = fSasl.getMechanismNames(null);

			for (String name : mechanisms) {
				LOGGER.info("service with SASL mechanism '" + name + "' and factory " + fClz.getName());
				putService(new Provider.Service(this, SERVICE_TYPE, name, fClz.getName(), null, null));
			}

		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			LOGGER.error("Failed to instantiate IXISaslProvider", e);
			throw new SaslException("Failed to instantiate IXISaslProvider", e);
		}

	}

}
