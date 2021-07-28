package abs.ixi.server.packet;

public enum PresenceSubscription {
	NONE("none"),

	TO("to"),

	FROM("from"),

	BOTH("both"),

	REMOVE("remove");

	private String val;

	private PresenceSubscription(String val) {
		this.val = val;
	}

	public String val() {
		return val;
	}

	public static PresenceSubscription valueFrom(String val) throws IllegalArgumentException {
		for (PresenceSubscription type : values()) {
			if (type.val().equalsIgnoreCase(val)) {
				return type;
			}
		}

		throw new IllegalArgumentException("No PresenceSubscription for value [" + val + "]");
	}

}
