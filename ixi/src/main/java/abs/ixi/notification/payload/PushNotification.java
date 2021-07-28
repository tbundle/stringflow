package abs.ixi.notification.payload;

import java.util.List;

public class PushNotification<T extends NotificationMessage> {
	private List<String> targets;
	private T payload;

	public PushNotification() {
		// do nothing constructor
	}

	public PushNotification(List<String> targets, T payload) {
		this.targets = targets;
		this.payload = payload;
	}

	public List<String> getTargets() {
		return targets;
	}

	public void setTargets(List<String> targets) {
		this.targets = targets;
	}

	public T getPayload() {
		return payload;
	}

	public void setPayload(T payload) {
		this.payload = payload;
	}

	public void addTarget(String target) {
		this.targets.add(target);
	}

	public void addTargets(List<String> targets) {
		this.targets.addAll(targets);
	}

}
