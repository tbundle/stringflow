package abs.ixi.server.sys.secure.sasl;

import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.util.Base64;

import javax.security.auth.callback.CallbackHandler;
import javax.security.sasl.Sasl;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.Stringflow;
import abs.ixi.server.packet.JID;
import abs.ixi.server.packet.Packet;
import abs.ixi.server.packet.XMPPNamespaces;
import abs.ixi.server.packet.XMPPUtil;
import abs.ixi.server.packet.xmpp.FailurePacket;
import abs.ixi.server.packet.xmpp.SASLAuthPacket;
import abs.ixi.server.packet.xmpp.SASLChallenge;
import abs.ixi.server.packet.xmpp.SASLSuccess;
import abs.ixi.util.StringUtils;

/**
 * {@code SaslEngine} is NOT the engine mentioned in JCA specifications. For the
 * lack of name, we have resorted to this name. The engine is the entity which
 * is the governing body of Sasl feature in server.
 */
public class SaslEngine {
	private static final Logger LOGGER = LoggerFactory.getLogger(SaslEngine.class);

	private static final String XML_TEMPLET = "<%s />".intern();

	private static final String PROTOCOL_LITERAL = "XMPP";

	private SaslMechanismSelector selector;
	private String server;

	public SaslEngine() throws SaslException {
		init();
	}

	private void init() throws SaslException {
		LOGGER.info("Initializing SaslEngine");

		if (Security.getProvider(IxiSaslProvider.PROVIDER_NAME) != null) {
			LOGGER.debug("Found exisitng registration of ixi provider");
			Security.removeProvider(IxiSaslProvider.PROVIDER_NAME);
		}

		// Making ixi provider as preferred provider
		Security.insertProviderAt(new IxiSaslProvider(), 1);

		this.selector = new SaslMechanismSelector();
		this.server = Stringflow.runtime().node();
	}

	public Packet processPacket(SASLAuthPacket authPacket) {
		SASLAuthPacket auth = (SASLAuthPacket) authPacket;

		String name = auth.getMechanism().name();
		String mechanism = this.selector.match((v) -> StringUtils.safeEquals(name, v));

		if (StringUtils.isNullOrEmpty(mechanism)) {
			FailurePacket failurePacket = new FailurePacket(XMPPNamespaces.SASL_NAMESPACE);
			failurePacket.setReasionXml(XMPPUtil.INVALID_MECHANISM_XML);
			failurePacket.setSourceId(authPacket.getSourceId());
			return failurePacket;
		}

		byte[] response = getClientResponse(auth.getAuthResponse());

		try {
			CallbackHandler cbHandler = CallbackHandlerFactory.create(mechanism);
			SaslServer saslMechanism = Sasl.createSaslServer(mechanism, PROTOCOL_LITERAL, server, null, cbHandler);

			byte[] challenge = saslMechanism.evaluateResponse(response);

			if (saslMechanism.isComplete()) {
				if (saslMechanism.getAuthorizationID() != null) {
					String authzid = saslMechanism.getAuthorizationID();

					// TODO: Handling for anonymous users
					JID userJID = new JID(authzid, Stringflow.runtime().domain());
					SASLSuccess saslSuccess = new SASLSuccess(userJID);
					saslSuccess.setSourceId(auth.getSourceId());

					return saslSuccess;

				} else {
					FailurePacket failurePacket = new FailurePacket(XMPPNamespaces.SASL_NAMESPACE);
					failurePacket.setReasionXml(XMPPUtil.INVALID_AUTH_JID_XML);
					failurePacket.setSourceId(authPacket.getSourceId());
					return failurePacket;
				}

			} else {
				byte[] encodedBytes = Base64.getEncoder().encode(challenge);
				String encodedChallenge = new String(encodedBytes, StandardCharsets.UTF_8);

				SASLChallenge saslChallenge = new SASLChallenge(encodedChallenge);
				saslChallenge.setSourceId(authPacket.getSourceId());

				return saslChallenge;
			}

		} catch (ServerSaslException e) {
			LOGGER.error("Failed to process auth packet due to Sasl error {}", e.getError().val(), e);
			FailurePacket failurePacket = new FailurePacket(XMPPNamespaces.SASL_NAMESPACE);
			failurePacket.setReasionXml(String.format(XML_TEMPLET, e.getError().val()));
			failurePacket.setSourceId(authPacket.getSourceId());
			return failurePacket;

		} catch (Exception e) {
			LOGGER.error("Failed to process auth packet", e);
			FailurePacket failurePacket = new FailurePacket(XMPPNamespaces.SASL_NAMESPACE);
			failurePacket.setReasionXml(String.format(XML_TEMPLET, SaslError.not_authorized.val()));
			failurePacket.setSourceId(authPacket.getSourceId());
			return failurePacket;

		}
	}

	/**
	 * Returns Base64 decoded user response for server challenge
	 * 
	 * @param response
	 * @return
	 */
	private byte[] getClientResponse(String response) {
		LOGGER.debug("encoded data : " + response);
		return (StringUtils.isNullOrEmpty(response) || StringUtils.safeEquals("=", response)) ? new byte[] {}
				: Base64.getDecoder().decode(response);
	}

}
