package abs.ixi.util;

import java.util.Arrays;

/**
 * {@code ArrayUtils} has convenience methods for java arrays.
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

    /**
     * Convenience method to compare two array ranges. This a extension of
     * equals method exposed in {@link Arrays} class. Arrays class do not offer
     * range comparison.
     * 
     * @param a
     *            first array
     * @param aStart
     *            first array start index
     * @param aEnd
     *            first array end index
     * @param b
     *            second array
     * @param bStart
     *            second array start index
     * @param bEnd
     *            second array end index
     * @return true if the array ranges were equal otherwise false
     */
    public static boolean areEqual(byte[] a, int aStart, int aEnd, byte[] b, int bStart, int bEnd) {
	if (a == b) {
	    return true;
	}

	if (a == null || b == null) {
	    return false;
	}

	if ((aEnd - aStart) != (bEnd - bStart)) {
	    return false;
	}

	int length = aEnd - aStart;
	for (int i = 0; i < length; i++) {
	    if (a[aStart + i] != b[bStart + i]) {
		return false;
	    }
	}

	return true;
    }

}
