package abs.ixi.server.packet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Roster implements Serializable {
	private static final long serialVersionUID = -4653453813128573150L;

	private int version;
	private List<RosterItem> items;

	public Roster() {
		this.items = new ArrayList<>();
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public List<RosterItem> getItems() {
		return items;
	}

	public void setItems(List<RosterItem> items) {
		this.items = items;
	}

	public void addItem(RosterItem item) {
		this.items.add(item);
	}

	public void removeItem(RosterItem item) {
		this.items.remove(item);
	}

	public class RosterItem implements Serializable {
		private static final long serialVersionUID = 8588187011618610689L;

		private JID jid;
		private String name;
		private PresenceSubscription subscription;
		private int itemVersion;

		public RosterItem(JID jid) {
			this(jid, null, PresenceSubscription.NONE);
		}

		public RosterItem(JID jid, String name) {
			this(jid, name, PresenceSubscription.NONE);
		}

		public RosterItem(JID jid, PresenceSubscription subscription) {
			this(jid, null, subscription);
		}

		public RosterItem(JID jid, String name, PresenceSubscription subscription) {
			this.jid = jid;
			this.name = name;
			this.subscription = subscription;
		}

		public JID getJid() {
			return jid;
		}

		public void setJid(JID jid) {
			this.jid = jid;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public PresenceSubscription getSubscription() {
			return subscription;
		}

		public void setSubscription(PresenceSubscription subscription) {
			this.subscription = subscription;
		}

		public int getItemVersion() {
			return itemVersion;
		}

		public void setItemVersion(int itemVersion) {
			this.itemVersion = itemVersion;
		}

		@Override
		public boolean equals(Object o) {
			if (o == null) {
				return false;
			}

			if (this == o) {
				return true;
			}

			if (o instanceof RosterItem) {
				RosterItem item = (RosterItem) o;
				return this.getJid().equals(item.getJid());

			} else {
				return false;
			}
		}

		@Override
		public int hashCode() {
			return this.jid.hashCode();
		}

	}

}
