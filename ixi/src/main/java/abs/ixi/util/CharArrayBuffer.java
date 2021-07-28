package abs.ixi.util;

import java.util.Arrays;

/**
 * An in-memory buffer backed by an array The buffer maintains two pointers to
 * track start and end of the valid content.
 */
public class CharArrayBuffer {
    private char[] arr;
    private int start;
    private int end;
    private int bufferSize;

    public CharArrayBuffer(int size) {
	this.bufferSize = size;
	this.arr = new char[this.bufferSize];
    }

    public CharArrayBuffer(char[] arr) {
	this.arr = arr;
	this.start = 0;
	this.end = 0;
	this.bufferSize = arr.length;
    }

    public CharArrayBuffer(char[] arr, int start, int end) {
	this.arr = arr;
	this.start = start;
	this.end = end;
	this.bufferSize = arr.length;
    }

    public char[] content() {
	if (start >= 0 && end >= 0 && end >= start) {
	    return Arrays.copyOfRange(arr, start, end);
	} else {
	    return new char[0];
	}
    }

    public void reset() {
	this.start = -1;
	this.end = -1;
    }

    @Override
    public String toString() {
	return new String(content()).intern();
    }

    public char[] dataStore() {
	return arr;
    }
}
