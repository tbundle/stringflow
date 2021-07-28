package abs.ixi.server.etc;

public enum NotifiactionCode {
	RE_CONNECT(1000),

	TEXT_MESSAGE(1001),

	MEDIA_MESSAGE(1002);

	private int val;

	private NotifiactionCode(int val) {
		this.val = val;
	}

	public int val() {
		return val;
	}

	public static NotifiactionCode valueFrom(int val) throws IllegalArgumentException {
		for (NotifiactionCode type : values()) {
			if (type.val() == val) {
				return type;
			}
		}

		throw new IllegalArgumentException("No NotifiactionCode for value [" + val + "]");
	}

	public static final String SF_NOTIFICATION_CODE = "SF_NOTIFICATION_CODE";

}
