package abs.ixi.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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
	 * It swallows exceptions and returns -1 in all the error cases
	 */
	public static final int parseToInt(String str) {
		try {
			if (!StringUtils.isNullOrEmpty(str)) {
				return Integer.parseInt(str);
			} else {
				return -1;
			}
		} catch (NumberFormatException e) {
			LOGGER.warn("Failed to parse string {} into an Integer", str);
			return -1;
		}
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

	/**
	 * Makes a deep copy of the object by serializing object into bytes. Make
	 * sure the object passed is serializable. The method does not check if the
	 * given object is serializable; any object which is not serializable will
	 * result in exception
	 * 
	 * @param obj
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T cloneObject(T obj) {
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			oos.flush();
			ByteArrayInputStream bin = new ByteArrayInputStream(bos.toByteArray());
			ois = new ObjectInputStream(bin);
			return (T) ois.readObject();
		} catch (Exception e) {
			LOGGER.warn("failed to clone object", e);
		} finally {
			try {
				oos.close();
				ois.close();
			} catch (IOException e) {

			}
		}

		return null;
	}

	public static Object deserialize(InputStream is) {

		try (ObjectInputStream in = new ObjectInputStream(is)) {

			return in.readObject();

		} catch (Exception e) {
			LOGGER.warn("failed to deserialize bytes " + is);

			return null;
		}
	}

}
