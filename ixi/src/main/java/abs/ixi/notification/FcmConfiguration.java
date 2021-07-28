package abs.ixi.notification;

/**
 * Represents configurations required to build {@link FcmService} instance
 * within server; Not all the configurations are mandatory though
 */
public class FcmConfiguration implements ServiceConfiguration {
    private String serverKey;
    private String senderId;

    public String getServerKey() {
	return serverKey;
    }

    public void setServerKey(String serverKey) {
	this.serverKey = serverKey;
    }

    public String getSenderId() {
	return senderId;
    }

    public void setSenderId(String senderId) {
	this.senderId = senderId;
    }

    @Override
    public String toString() {
	return "FCMConfig-serverkey[" + this.serverKey + "]-senderID[" + this.senderId + "]";
    }
}
