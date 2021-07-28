package abs.ixi.util;

/**
 * Holds convenience methods around Java Reflection
 */
public class ReflectionUtils {
    /**
     * Casts string value to the primitive type objects
     * 
     * @param value
     *            string value
     * @param type
     *            java primitive type
     * @return object instance after the has been casted
     */
    public static <T> Object cast(String value, Class<T> type) {
	if (Integer.TYPE.equals(type) || Integer.class.equals(type)) {
	    return Integer.valueOf(value);
	}

	if (Long.TYPE.equals(type) || Long.class.equals(type)) {
	    return Long.valueOf(value);
	}

	if (Double.TYPE.equals(type) || Double.class.equals(type)) {
	    return Double.valueOf(value);
	}

	if (Boolean.TYPE.equals(type) || Boolean.class.equals(type)) {
	    return Boolean.valueOf(value);
	}

	if (Byte.TYPE.equals(type) || Byte.class.equals(type)) {
	    return Byte.valueOf(value);
	}

	if (Short.TYPE.equals(type) || Short.class.equals(type)) {
	    return Short.valueOf(value);
	}

	if (Float.TYPE.equals(type) || Float.class.equals(type)) {
	    return Float.valueOf(value);
	}

	if (Character.TYPE.equals(type) || Character.class.equals(type)) {
	    return value.charAt(0);
	}

	return value;
    }
}
