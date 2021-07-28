package abs.ixi.server.sys.secure.sasl;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.sasl.SaslException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of PLAIN SASL mechanism. PLAIN mechanism does not mandate data
 * integrity, data confidentiality and other security features; therefore server
 * must take care of the data security on the wire using SSL/TSL.
 * 
 * For more information, please see <a href="">SASL PLAIN Documentation</a>
 */
public class PlainMechanism extends AbstractMechanism {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlainMechanism.class);

    public static final String NAME = "PLAIN";

    private static final int MAX_LENGTH = 255;

    protected PlainMechanism(Map<? super String, ?> props, CallbackHandler callbackHandler) {
	super(props, callbackHandler);
    }

    @Override
    public String getMechanismName() {
	return NAME;
    }

    // TODO: Refector this method
    @Override
    public byte[] evaluateResponse(byte[] response) throws SaslException {
	String res = new String(response, StandardCharsets.UTF_8);

	String[] parts = res.split("\u0000");

	if (parts == null || parts.length != 3) {
	    throw new ServerSaslException(SaslError.malformed_request);
	}

	final String authzid = parts[0];
	final String authcid = parts[1];
	final String passwd = parts[2];

	// Currently we do not process authzid
	// Validate all three fields for byte length as mentioned in RFC

	if (authzid.getBytes(StandardCharsets.UTF_8).length > MAX_LENGTH) {
	    LOGGER.info("Length of autherization Id is greter then {} ", MAX_LENGTH);
	    throw new ServerSaslException(SaslError.malformed_request);
	}

	if (authcid.getBytes(StandardCharsets.UTF_8).length > MAX_LENGTH) {
	    LOGGER.info("Length of authentication Id is greter then {} ", MAX_LENGTH);
	    throw new ServerSaslException(SaslError.malformed_request);
	}

	if (passwd.getBytes(StandardCharsets.UTF_8).length > MAX_LENGTH) {
	    LOGGER.info("Length of password Id is greter then {} ", MAX_LENGTH);
	    throw new ServerSaslException(SaslError.malformed_request);
	}

	final NameCallback cbName = new NameCallback("Auth identity", authcid);
	final PasswordVerificationCallback cbPwd = new PasswordVerificationCallback(passwd);

	handleCallbacks(new Callback[] { cbName, cbPwd });

	if (!cbPwd.isSuccessful()) {
	    throw new ServerSaslException(SaslError.not_authorized);
	}

	this.authzid = authcid;
	this.complete = true;

	return null;
    }

    // //TODO Refactor it later
    // private String[] splitResponse(String res) {
    // char[] chars = res.toCharArray();
    //
    // String[] arr = new String[3];
    // for(int i = 0; i < chars.length; i++){
    // if(chars[i] == '\u0000'){
    //
    // }
    // }
    //
    // }

}
