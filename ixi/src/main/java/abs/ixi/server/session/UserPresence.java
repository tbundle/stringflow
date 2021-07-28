package abs.ixi.server.session;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import abs.ixi.server.packet.xmpp.BareJID;
import abs.ixi.server.packet.xmpp.Presence.PresenceStatus;

public class UserPresence {
	private PresenceStatus status;
	private String mood;
	private boolean online;
	private List<BareJID> subscribers;
	private Calendar presenceTime;

	public UserPresence() {
		this(new ArrayList<>());
	}

	public UserPresence(List<BareJID> subscribers) {
		this(null, null, subscribers);
	}

	public UserPresence(PresenceStatus status, String mood, List<BareJID> subscribers) {
		this.status = status;
		this.mood = mood;
		this.subscribers = subscribers;
		this.online = true;
		this.presenceTime = Calendar.getInstance();
	}

	public PresenceStatus getStatus() {
		return status;
	}

	public void setStatus(PresenceStatus status) {
		this.status = status;
	}

	public String getMood() {
		return mood;
	}

	public void setMood(String mood) {
		this.mood = mood;
	}

	public List<BareJID> getSubscribers() {
		return subscribers;
	}

	public void setSubscribers(List<BareJID> subscribers) {
		this.subscribers = subscribers;
	}

	public Calendar getPresenceTime() {
		return presenceTime;
	}

	public void setPresenceTime(Calendar presenceTime) {
		this.presenceTime = presenceTime;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
		this.presenceTime = Calendar.getInstance();
	}

	public void addPresenceSubscriber(BareJID subscriberJID) {
		if (!this.subscribers.contains(subscriberJID)) {
			this.subscribers.add(subscriberJID);
		}
	}

	public void removePresenceSubscriber(BareJID subscriberJID) {
		this.subscribers.remove(subscriberJID);
	}

}
