package abs.ixi.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class for String operations
 */
public final class StringUtils {
	public static final String EMPTY = "";

	/**
	 * Change the case to upper.
	 * 
	 * @param s String value which will be changed to uppercase
	 * @return String in uppercase; null if the value was null
	 */
	public static final String toUpper(String s) {
		return s != null ? s.toUpperCase() : s;
	}

	/**
	 * Remoove last character from given String.
	 * 
	 * @param s String from which last char will be removed
	 * @return String after removing the last char
	 */
	public static final String removeLastChar(String s) {
		return s != null ? s.substring(0, s.length() - 1) : s;
	}

	/**
	 * Remove last character from a StringBuilder instance
	 * 
	 * @param sb
	 * @return
	 */
	public static final StringBuilder removeLastChar(StringBuilder sb) {
		return sb != null ? new StringBuilder(sb.substring(0, sb.length() - 1)) : sb;
	}

	/**
	 * Change a string to lower case
	 * 
	 * @param s
	 * @return
	 */
	public static final String toLower(String s) {
		return s != null ? s.toLowerCase() : s;
	}

	/**
	 * Check if a String is Null or blank. A blank string is a an empty string.
	 * 
	 * @param s
	 * @return
	 */
	public static final boolean isNullOrEmpty(String s) {
		return s == null || EMPTY.equals(s) ? true : false;
	}

	public static final boolean safeEquals(String s1, String s2) {
		return safeEquals(s1, s2, true);
	}

	public static final boolean safeEquals(String s1, String s2, boolean caseSensitive) {
		if (s1 == s2) {
			return true;
		}

		if (s1 == null || s2 == null) {
			return false;
		}

		if (caseSensitive) {
			return s1.equals(s2);

		} else {
			return s1.equalsIgnoreCase(s2);
		}
	}

	public static final String safeToString(Object o) {
		return o != null ? o.toString() : null;
	}

	/**
	 * removes spaces from the supplied string. It does not remove tab, new line
	 * and carriage returns.
	 */
	public static final String removeSpaces(String s) {
		return isNullOrEmpty(s) ? s : s.replaceAll(" ", "");
	}

	/**
	 * removes last occurrence of String subStr in String s
	 * 
	 * @param s original string
	 * @param subStr string to be removed
	 * @return New string after removing the last occurrence of subStr. If
	 *         subStr is not found, String s would be returned unchanged
	 */
	public static final String removeLast(String s, String subStr) {
		return s.substring(0, s.lastIndexOf(subStr));
	}

	// TODO enforce static type safety
	public static final List<String> safeSplit(String s, String separator) {
		return s == null ? new ArrayList<String>() : Arrays.asList(s.split(separator));
	}

	/**
	 * Changes the first char of string capital and others to lower
	 */

	public static final String firstCharCap(String s) {
		if (!isNullOrEmpty(s)) {
			String lower = toLower(s);
			char c = lower.charAt(0);
			char upper = Character.toUpperCase(c);

			return upper + s.substring(1);
		}

		return s;
	}

	public static String safeTrim(String s) {
		return s == null ? s : s.trim().equals(EMPTY) ? null : s.trim();
	}

	/**
	 * Convinience method to convert integer to String.
	 * 
	 * @param i
	 * @return
	 */
	public static String toString(Integer i) {
		return i.toString();
	}

	/**
	 * Convinience method to convert long to String.
	 * 
	 * @param l
	 * @return
	 */
	public static String toString(Long l) {
		return l.toString();
	}
}
