package abs.ixi.notification;

/**
 * {@code ApnsConfiguration} is a holder object for Apns Configurations which
 * are required to send notification through APNS service.
 */
public class ApnsConfiguration implements ServiceConfiguration {
    private String passPhrase;
    private Environment env;
    private String certFilePath;

    public String getPassPhrase() {
	return passPhrase;
    }

    public void setPassPhrase(String passPhrase) {
	this.passPhrase = passPhrase;
    }

    public Environment getEnv() {
	return env;
    }

    public void setEnv(Environment env) {
	this.env = env;
    }

    public boolean isProd() {
	return Environment.PROD == this.env;
    }

    public String getCertFilePath() {
	return certFilePath;
    }

    public void setCertFilePath(String certFilePath) {
	this.certFilePath = certFilePath;
    }

    public enum Environment {
	PROD, DEV
    }

}
