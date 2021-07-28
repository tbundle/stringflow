package abs.ixi.server.sys.secure.sasl;

public enum SASLMechanismName {
    PLAIN("plain");

    String val;

    private SASLMechanismName(String val) {
	this.val = val;
    }

    public String val() {
	return val;
    }

    public static SASLMechanismName valueFrom(String val) throws IllegalArgumentException {
	for (SASLMechanismName type : values()) {
	    if (type.val().equalsIgnoreCase(val)) {
		return type;
	    }
	}

	throw new IllegalArgumentException("No machanism for value [" + val + "]");
    }

}
