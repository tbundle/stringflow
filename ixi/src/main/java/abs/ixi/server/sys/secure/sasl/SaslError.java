package abs.ixi.server.sys.secure.sasl;

import javax.security.sasl.SaslException;

/**
 * As specified in JCA specification, {@link SaslException} is thrown if
 * anything goes wrong within JCA. {@code SaslEngineException} extends
 * {@link SaslException} to capture ixi server error while enforcing sasl.
 */
public enum SaslError {
    /**
     * The receiving entity acknowledges that the authentication handshake has
     * been aborted by the initiating entity.
     */
    aborted("aborted"),
    /**
     * The account of the initiating entity has been temporarily disabled.
     */
    account_disabled("account-disabled"),
    /**
     * The authentication failed because the initiating entity provided
     * credentials that have expired.
     */
    credentials_expired("credentials-expired"),
    /**
     * The mechanism requested by the initiating entity cannot be used unless
     * the confidentiality and integrity of the underlying stream are protected
     * (typically via TLS).
     */
    encryption_required("encryption-required"),
    /**
     * The data provided by the initiating entity could not be processed because
     * the base 64 encoding is incorrect.
     */
    incorrect_encoding("incorrect-encoding"),
    /**
     * The authzid provided by the initiating entity is invalid, either because
     * it is incorrectly formatted or because the initiating entity does not
     * have permissions to authorize that ID.
     */
    invalid_authzid("invalid-authzid"),
    /**
     * The initiating entity did not specify a mechanism, or requested a
     * mechanism that is not supported by the receiving entity.
     */
    invalid_mechanism("invalid-mechanism"),
    /**
     * The request is malformed (e.g., the {@code <auth/>} element includes
     * initial response data but the mechanism does not allow that, or the data
     * sent violates the syntax for the specified SASL mechanism).
     */
    malformed_request("malformed-request"),
    /**
     * The mechanism requested by the initiating entity is weaker than server
     * policy permits for that initiating entity.
     */
    mechanism_too_weak("mechanism-too-weak"),
    /**
     * The authentication failed because the initiating entity did not provide
     * proper credentials, or because some generic authentication failure has
     * occurred but the receiving entity does not wish to disclose specific
     * information about the cause of the failure.
     */
    not_authorized("not-authorized"),
    /**
     * The authentication failed because of a temporary error condition within
     * the receiving entity, and it is advisable for the initiating entity to
     * try again later.
     */
    temporary_auth_failure("temporary-auth-failure");

    private final String val;

    SaslError(String val) {
	this.val = val;
    }

    public String val() {
	return this.val;
    }
}
