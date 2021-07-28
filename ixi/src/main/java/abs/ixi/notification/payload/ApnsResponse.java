package abs.ixi.notification.payload;

import abs.ixi.notification.connector.Response;

public class ApnsResponse implements Response {
    private static final long serialVersionUID = 1L;

    private final int identifier;
    private final int expiry;
    private final byte[] deviceToken;
    private final byte[] payload;

    public ApnsResponse() {
	this.identifier = 0;
	this.expiry = 0;
	this.deviceToken = new byte[0];
	this.payload = new byte[0];
    }

    public ApnsResponse(int identifier, int expiry, byte[] deviceToken, byte[] payload) {
	this.identifier = identifier;
	this.expiry = expiry;
	this.deviceToken = deviceToken;
	this.payload = payload;
    }

    public byte[] marshal() {
	return null;
    }

    public void setResponseCode(int respCode) {

    }

}
