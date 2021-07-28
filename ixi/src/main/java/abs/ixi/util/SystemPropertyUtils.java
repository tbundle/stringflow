package abs.ixi.util;

import static abs.ixi.util.StringUtils.isNullOrEmpty;

import java.security.AccessController;
import java.security.PrivilegedAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to read system properties
 * 
 * @author Yogi
 *
 */
public class SystemPropertyUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(SystemPropertyUtils.class);

	/**
	 * Get value for given key from Java environment properties
	 * 
	 * @param key
	 * @return
	 */
	public static String get(String key) {
		return get(key, null);
	}

	/**
	 * Retrieve value for a key from Java environment properties. If there is no
	 * value found, default value supplied will be returned.
	 * 
	 * @param key a String key for which value needs to be retrieved
	 * @param defaultvalue default value to be returned if no value found in
	 *            environment properties
	 * @return value for the given key
	 */
	public static String get(String key, String defaultvalue) {
		if (isNullOrEmpty(key)) {
			throw new NullPointerException("key can not be null or empty");
		}

		String value = null;
		try {
			if (System.getSecurityManager() == null) {
				value = System.getProperty(key);
			} else {
				value = AccessController.doPrivileged(new PrivilegedAction<String>() {
					@Override
					public String run() {
						return System.getProperty(key);
					}
				});
			}
		} catch (SecurityException e) {
			LOGGER.warn("Unable to retrieve value for system property '{}'; returning default value", key, e);
		}

		return value != null ? value : defaultvalue;
	}

	/**
	 * Set a key-value pair into Java system properties.
	 * 
	 * @param key
	 * @param val
	 */
	public static void set(final String key, final String val) {
		if (key == null || val == null) {
			throw new NullPointerException("either key or value is null");
		}

		try {
			if (System.getSecurityManager() == null) {
				System.setProperty(key, val);
			} else {
				AccessController.doPrivileged(new PrivilegedAction<String>() {
					@Override
					public String run() {
						System.setProperty(key, val);
						return null;
					}

				});
			}
		} catch (SecurityException e) {
			LOGGER.warn("Unable to store key-value pair {}, {} in system properties", key, val, e);
		}

	}

}
