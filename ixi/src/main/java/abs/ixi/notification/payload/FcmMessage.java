package abs.ixi.notification.payload;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import abs.ixi.httpclient.util.StringUtils;

public class FcmMessage implements NotificationMessage {
    private Notification notification;
    private Data data;
    private String collapseKey;
    private Priority priority;
    private int ttl;

    private FcmMessage() {
    }

    public FcmMessage(Notification notification, List<String> targets) {
	this(notification, null, targets);
    }

    public FcmMessage(Data data, List<String> targets) {
	this(null, data, targets);
    }

    public FcmMessage(Notification notification, Data data, List<String> targets) {
	this.notification = notification;
	this.data = data;
    }

    public Notification getNotification() {
	return notification;
    }

    public void setNotification(Notification notification) {
	this.notification = notification;
    }

    public Data getData() {
	return data;
    }

    public void setData(Data data) {
	this.data = data;
    }

    public String getCollapseKey() {
	return collapseKey;
    }

    public void setCollapseKey(String collapseKey) {
	this.collapseKey = collapseKey;
    }

    public Priority getPriority() {
	return priority;
    }

    public void setPriority(Priority priority) {
	this.priority = priority;
    }

    public int getTtl() {
	return ttl;
    }

    public void setTtl(int ttl) {
	this.ttl = ttl;
    }

    public void setNotificationTitle(String title) {
	if (this.notification == null) {
	    this.notification = new Notification();
	}

	this.notification.setTitle(title);
    }

    public void setNotificationBody(String body) {
	if (this.notification == null) {
	    this.notification = new Notification();
	}

	this.notification.setBody(body);
    }

    public void setNotificationIcon(String icon) {
	if (this.notification == null) {
	    this.notification = new Notification();
	}

	this.notification.setIcon(icon);
    }

    public void addData(String key, String value) {
	if (this.data == null) {
	    this.data = new Data();
	}

	this.data.addData(key, value);
    }

    public void addData(Map<String, String> data) {
	if (this.data == null) {
	    this.data = new Data(data);

	} else {
	    data.forEach((k, v) -> this.data.addData(k, v));
	}
    }

    @Override
    public String stringify() {
	StringBuilder sb = new StringBuilder();

	if (this.notification != null && !StringUtils.isNullOrEmpty(this.notification.stringify())) {
	    sb.append(this.notification.stringify());
	    sb.append(",");
	}

	if (this.data != null && !StringUtils.isNullOrEmpty(this.data.stringify())) {
	    sb.append(this.data.stringify());
	    sb.append(",");
	}

	if (this.priority != null) {
	    sb.append("\"priority\" : ");
	    sb.append("\"");
	    sb.append(this.priority.value());
	    sb.append("\"");
	    sb.append(",");
	}

	if (this.ttl != 0) {
	    sb.append("\"time_to_live\" : ");
	    sb.append(this.ttl);
	    sb.append(",");
	}

	if (!StringUtils.isNullOrEmpty(this.collapseKey)) {
	    sb.append("\"collapse_key\" : ");
	    sb.append("\"");
	    sb.append(this.collapseKey);
	    sb.append("\"");
	    sb.append(",");
	}

	return sb.substring(0, sb.lastIndexOf(","));
    }

    @Override
    public String xmppPayload() {
	// TODO implement it here
	return null;
    }

    public enum Priority {
	HIGH("high"), NORMAL("normal");

	private String val;

	private Priority(String val) {
	    this.val = val;
	}

	public String value() {
	    return val;
	}
    }

    public static class Notification {
	private String body;
	private String title;
	private String icon;

	private Notification() {

	}

	public Notification(String body, String title, String icon) {
	    this.body = body;
	    this.title = title;
	    this.icon = icon;
	}

	public String getBody() {
	    return body;
	}

	public void setBody(String body) {
	    this.body = body;
	}

	public String getTitle() {
	    return title;
	}

	public void setTitle(String title) {
	    this.title = title;
	}

	public String getIcon() {
	    return icon;
	}

	public void setIcon(String icon) {
	    this.icon = icon;
	}

	public String stringify() {
	    StringBuilder sb = new StringBuilder("\"notification\" : { ");

	    if (!StringUtils.isNullOrEmpty(title)) {
		sb.append("\"title\" : ");
		sb.append("\"");
		sb.append(title);
		sb.append("\"");
		sb.append(",");
	    }

	    if (!StringUtils.isNullOrEmpty(body)) {
		sb.append("\"body\" : ");
		sb.append("\"");
		sb.append(body);
		sb.append("\"");
		sb.append(",");
	    }

	    if (!StringUtils.isNullOrEmpty(icon)) {
		sb.append("\"icon\" : ");
		sb.append("\"");
		sb.append(icon);
		sb.append("\"");
		sb.append(",");
	    }

	    return sb.substring(0, sb.lastIndexOf(",")) + "}";
	}

    }

    public static class Data {
	private Map<String, String> data;

	private Data() {
	    this.data = new HashMap<>();
	}

	public Data(Map<String, String> data) {
	    this.data = data;
	}

	public void addData(String key, String value) {
	    this.data.put(key, value);
	}

	public Map<String, String> getDataMap() {
	    return this.data;
	}

	public String stringify() {
	    if (this.data != null) {
		StringBuilder sb = new StringBuilder("\"data\" : { ");

		for (Entry<String, String> map : this.data.entrySet()) {
		    sb.append("\"");
		    sb.append(map.getKey());
		    sb.append("\"");
		    sb.append(" : ");
		    sb.append("\"");
		    sb.append(map.getValue());
		    sb.append("\"");
		    sb.append(",");
		}

		return sb.substring(0, sb.lastIndexOf(",")) + "}";

	    }

	    return StringUtils.EMPTY;
	}
    }

    public static class FcmMessageBuilder implements MessageBuilder<FcmMessage> {
	private FcmMessage fcmMessage;
	private PushNotification<FcmMessage> notification;

	public FcmMessageBuilder() {
	    this.fcmMessage = new FcmMessage();
	    this.notification = new PushNotification<FcmMessage>(new ArrayList<>(), fcmMessage);
	}

	public FcmMessageBuilder withNotification(Notification notification) {
	    this.fcmMessage.setNotification(notification);
	    return this;
	}

	public FcmMessageBuilder withNotificationTitle(String title) {
	    this.fcmMessage.setNotificationTitle(title);
	    return this;
	}

	public FcmMessageBuilder withNotificationBody(String body) {
	    this.fcmMessage.setNotificationBody(body);
	    return this;
	}

	public FcmMessageBuilder withNotificationIcon(String icon) {
	    this.fcmMessage.setNotificationIcon(icon);
	    return this;
	}

	public FcmMessageBuilder withData(Data data) {
	    this.fcmMessage.setData(data);
	    return this;
	}

	public FcmMessageBuilder withData(Map<String, String> data) {
	    this.fcmMessage.addData(data);
	    return this;
	}

	public FcmMessageBuilder withData(String key, String value) {
	    this.fcmMessage.addData(key, value);
	    return this;
	}

	public FcmMessageBuilder withTarget(List<String> targets) {
	    this.notification.addTargets(targets);
	    return this;
	}

	public FcmMessageBuilder withTarget(String target) {
	    this.notification.addTarget(target);
	    return this;
	}

	public FcmMessageBuilder withTTL(int ttl) {
	    this.fcmMessage.setTtl(ttl);
	    return this;
	}

	public FcmMessageBuilder withCollapseKey(String collapseKey) {
	    this.fcmMessage.setCollapseKey(collapseKey);
	    return this;
	}

	public FcmMessageBuilder withPriority(Priority priority) {
	    this.fcmMessage.setPriority(priority);
	    return this;
	}

	@Override
	public PushNotification<FcmMessage> build() {
	    return this.notification;
	}

    }
}