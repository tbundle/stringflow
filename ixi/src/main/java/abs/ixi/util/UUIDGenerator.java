package abs.ixi.util;

import java.util.UUID;

/**
 * {@code UUIDGenerator} is a wrapper around the java provided UUID.
 */
public class UUIDGenerator {
    public static String uuid() {
	return UUID.randomUUID().toString();
    }

    public static String secureId() {
	return secureId(10);
    }

    public static String secureId(int n) {
	String uuid = UUID.randomUUID().toString();
	uuid = uuid.replaceAll("-", "");

	return uuid.substring(0, (n - 1));
    }

}
