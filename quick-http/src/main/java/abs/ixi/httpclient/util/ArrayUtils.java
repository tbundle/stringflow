package abs.ixi.httpclient.util;

import java.util.Arrays;

/**
 * Utilities which holds convenient methods for Arrays
 */
public class ArrayUtils {
	public static StringBuilder[] resizeArray(StringBuilder[] src, int size) {
		StringBuilder[] array = new StringBuilder[size];
		System.arraycopy(src, 0, array, 0, src.length);
		Arrays.fill(array, src.length, array.length, null);

		return array;
	}

	public static StringBuilder[] initArray(int size) {
		StringBuilder[] array = new StringBuilder[size];
		Arrays.fill(array, null);

		return array;
	}
}
