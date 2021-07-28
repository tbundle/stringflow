package abs.ixi.server.sys.secure.sasl;

import javax.security.auth.callback.Callback;

/**
 * Auth callback implementation to verify user password. Callbacks are invoked
 * by {@link SASLMechanism}.
 */
public class PasswordVerificationCallback implements Callback {
    private String passwd;
    private boolean result;

    public PasswordVerificationCallback(final String passwd) {
	this.passwd = passwd;
    }

    public boolean isSuccessful() {
	return result;
    }

    public void setResult(boolean result) {
	this.result = result;
    }

    public String getPasswd() {
	return passwd;
    }

}
