
package abs.ixi.server.io;

public class BoshStreamContext extends StreamContext {
	private static final int DEFAULT_WAIT_TIME_IN_SEC = 60;
	private static final int DEFAULT_HOLD_REQUEST_COUNT = 1;
	private static final int DEFAULT_POOLLING_TIME_IN_SEC = 60;
	private static final int DEFAULT_INACTIVITY_TIME_IN_SEC = 60;
	private static final int DEFAULT_MAXPAUSE_TIME_IN_SEC = 60;
	private static final String DEFAULT_CONTENT_TYPE = "text/xml";
	private static final String DEFAULT_CHARSET = "utf-8";

	private int hold;
	private int wait;
	private int ack;
	private int pollingTime;
	private int inactivityTime;
	private int maxpause;
	private int requests;
	private String contentType;
	private String accept;
	private String charsets;
	private String authId;

	public BoshStreamContext(String streamId) {
		super(streamId);
		this.hold = DEFAULT_HOLD_REQUEST_COUNT;
		this.wait = DEFAULT_WAIT_TIME_IN_SEC;
		this.pollingTime = DEFAULT_POOLLING_TIME_IN_SEC;
		this.inactivityTime = DEFAULT_INACTIVITY_TIME_IN_SEC;
		this.maxpause = DEFAULT_MAXPAUSE_TIME_IN_SEC;
		this.contentType = DEFAULT_CONTENT_TYPE;
		this.charsets = DEFAULT_CHARSET;
	}

	public int getHold() {
		return hold;
	}

	public void setHold(int hold) {
		this.hold = hold > DEFAULT_HOLD_REQUEST_COUNT ? hold : DEFAULT_HOLD_REQUEST_COUNT;
	}

	public int getWait() {
		return wait;
	}

	public void setWait(int wait) {
		this.wait = Math.min(this.wait, wait);
	}

	public int getAck() {
		return ack;
	}

	public void setAck(int ack) {
		this.ack = ack;
	}

	public int getPollingTime() {
		return pollingTime;
	}

	public void setPollingTime(int pollingTime) {
		this.pollingTime = pollingTime;
	}

	public int getInactivityTime() {
		return inactivityTime;
	}

	public void setInactivityTime(int inactivityTime) {
		this.inactivityTime = inactivityTime;
	}

	public int getMaxpause() {
		return maxpause;
	}

	public void setMaxpause(int maxpause) {
		this.maxpause = maxpause;
	}

	public int getRequests() {
		return requests;
	}

	public void setRequests(int requests) {
		this.requests = requests;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getAccept() {
		return accept;
	}

	public void setAccept(String accept) {
		this.accept = accept;
	}

	public String getCharsets() {
		return charsets;
	}

	public void setCharsets(String charsets) {
		this.charsets = charsets;
	}

	public String getAuthId() {
		return authId;
	}

	public void setAuthId(String authId) {
		this.authId = authId;
	}

}
