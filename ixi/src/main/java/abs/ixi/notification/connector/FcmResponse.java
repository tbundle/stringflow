package abs.ixi.notification.connector;

import com.fasterxml.jackson.annotation.JsonSetter;

public class FcmResponse implements Response {
    private static final long serialVersionUID = 1L;

    private int responseCode;
    private String multicastId;
    private int success;
    private int failure;
    private int canonicalId;
    private Result[] results;

    public int getResponseCode() {
	return responseCode;
    }

    public void setResponseCode(int responseCode) {
	this.responseCode = responseCode;
    }

    public String getMulticastId() {
	return multicastId;
    }

    @JsonSetter("multicast_id")
    public void setMulticastId(String multicastId) {
	this.multicastId = multicastId;
    }

    public int getSuccess() {
	return success;
    }

    @JsonSetter("success")
    public void setSuccess(int success) {
	this.success = success;
    }

    public int getFailure() {
	return failure;
    }

    @JsonSetter("failure")
    public void setFailure(int failure) {
	this.failure = failure;
    }

    public int getCanonicalId() {
	return canonicalId;
    }

    @JsonSetter("canonical_ids")
    public void setCanonicalId(int canonicalId) {
	this.canonicalId = canonicalId;
    }

    public Result[] getResults() {
	return results;
    }

    @JsonSetter("results")
    public void setResults(Result[] results) {
	this.results = results;
    }

    public static class Result {

	private String messageId;
	private String registrationId;
	private String error;

	public String getMessageId() {
	    return messageId;
	}

	@JsonSetter("message_id")
	public void setMessageId(String messageId) {
	    this.messageId = messageId;
	}

	public String getRegistrationId() {
	    return registrationId;
	}

	@JsonSetter("registration_id")
	public void setRegistrationId(String registrationId) {
	    this.registrationId = registrationId;
	}

	public String getError() {
	    return error;
	}

	@JsonSetter("error")
	public void setError(String error) {
	    this.error = error;
	}
    }

}
