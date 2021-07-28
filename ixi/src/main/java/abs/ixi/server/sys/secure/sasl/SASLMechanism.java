package abs.ixi.server.sys.secure.sasl;

import javax.security.sasl.SaslServer;

/**
 * Root interface for all the definitions of SASL mechanisms defined in server.
 * For more information on SASL mechanism, see
 * <a href="https://tools.ietf.org/html/rfc4422"> SASL Documentation</a>
 */
public interface SASLMechanism extends SaslServer {

}
