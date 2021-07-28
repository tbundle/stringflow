package abs.ixi.notification.payload;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApnsMessage implements NotificationMessage {
    private String message;
    private String alertTitle;
    private boolean silent;
    private Map<String, String> customFields;

    public ApnsMessage() {
	this.customFields = new HashMap<>();
    }

    public String getMessage() {
	return message;
    }

    public void setMessage(String message) {
	this.message = message;
    }

    public Map<String, String> getCustomFields() {
	return customFields;
    }

    public void setCustomFields(Map<String, String> customFields) {
	this.customFields = customFields;
    }

    public void addCustomField(String key, String value) {
	this.customFields.put(key, value);
    }

    public String getAlertTitle() {
	return alertTitle;
    }

    public void setAlertTitle(String alertTitle) {
	this.alertTitle = alertTitle;
    }

    public boolean isSilent() {
	return silent;
    }

    public void setSilent(boolean silent) {
	this.silent = silent;
    }

    @Override
    public String stringify() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public String xmppPayload() {
	// TODO Auto-generated method stub
	return null;
    }

    public static class ApnsMessageBuilder implements MessageBuilder<ApnsMessage> {
	private ApnsMessage message;
	private PushNotification<ApnsMessage> notification;

	public ApnsMessageBuilder() {
	    this.message = new ApnsMessage();
	    this.notification = new PushNotification<ApnsMessage>(new ArrayList<>(), message);
	}

	public ApnsMessageBuilder withTarget(List<String> targets) {
	    this.notification.addTargets(targets);
	    return this;
	}

	public ApnsMessageBuilder withTarget(String target) {
	    this.notification.addTarget(target);
	    return this;
	}

	public ApnsMessageBuilder withMessage(String message) {
	    this.message.setMessage(message);
	    return this;
	}

	public ApnsMessageBuilder withCustomFields(Map<String, String> customFields) {
	    this.message.setCustomFields(customFields);
	    return this;
	}

	public ApnsMessageBuilder withCustomField(String key, String value) {
	    this.message.addCustomField(key, value);
	    return this;
	}

	public ApnsMessageBuilder withAlertTitle(String title) {
	    this.message.setAlertTitle(title);
	    return this;
	}

	public ApnsMessageBuilder withSilent(boolean silent) {
	    this.message.setSilent(silent);
	    return this;
	}

	@Override
	public PushNotification<ApnsMessage> build() {
	    return this.notification;
	}

    }

}
