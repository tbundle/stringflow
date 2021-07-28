package abs.ixi.httpclient.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for common operations on various objects
 */
public class ObjectUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(ObjectUtils.class);

	/**
	 * Parses string into Integer. If string is null or empty, or if the string
	 * supplied is not parsable into {@link String}, the method would return 0
	 * 
	 * It swallows exceptions and returns 0 in all the error cases
	 */
	public static final int parseToInt(String str) {
		try {
			if (!StringUtils.isNullOrEmpty(str)) {
				return Integer.parseInt(str);
			}
		} catch (NumberFormatException e) {
			LOGGER.warn("Failed to parse string {} into an Integer", str);
		}

		return 0;
	}

	/**
	 * Parses string into {@link Long}. This method swallows
	 * {@link NumberFormatException} and returns 0 if the string can not be
	 * parsed.
	 */
	public static final long parseToLong(String str) {
		try {
			if (!StringUtils.isNullOrEmpty(str)) {
				return Long.parseLong(str);
			}
		} catch (NumberFormatException e) {
			LOGGER.warn("Failed to parse string {} into an Integer", str);
		}

		return 0;
	}

	/**
	 * Parses string into {@link Long}. This method swallows
	 * {@link NumberFormatException} and returns defaultValue if the string can
	 * not be parsed.
	 * 
	 * @param str
	 * @param defaultValue
	 * @return
	 */
	public static final long parseToLong(String str, long defaultValue) {
		long val = parseToLong(str);

		return val == 0 ? defaultValue : val;
	}

}
