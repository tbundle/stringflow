package abs.ixi.server.common;

/**
 * {@code CharSource} is a wrapper around char array. It maintains vital fields
 * such as position, limit and capacity to facilitate better reading process.
 */
public class CharArray implements Resetable {
	private char[] chars;
	private int position;
	private int limit;

	public CharArray(char[] chars) {
		this(chars, 0, chars.length);
	}

	public CharArray(char[] chars, int position, int limit) {
		this.chars = chars;
		this.position = position;
		this.limit = limit;
	}

	public char[] getChars() {
		return chars;
	}

	public int position() {
		return position;
	}

	public int limit() {
		return limit;
	}

	public int getCapacity() {
		return chars == null ? 0 : chars.length;
	}

	/**
	 * Returns true if there is another byte which can be read from this
	 * {@code ByteSource}
	 */
	public final boolean hasNext() {
		return this.position < this.limit;
	}

	/**
	 * Reads next char from data source
	 */
	public char next() {
		return this.chars[position++];
	}

	/**
	 * Returns the char at the index. The method does not change any marker such
	 * as limit, position etc.
	 * 
	 * @param index index of the byte
	 */
	public char get(int index) {
		return this.chars[index];
	}

	/**
	 * Copies remaining char data of this {@link CharArray} into the src char
	 * array. If the length of src array is smaller than the remaining content,
	 * the method will throw {@link ArrayIndexOutOfBoundsException}
	 * 
	 * @param src
	 */
	public void get(char[] src) {
		System.arraycopy(src, 0, this.chars, position, (chars.length - position));
		this.position = chars.length;
	}

	/**
	 * Copies remaining char data of this {@link CharArray} into the src char
	 * starting from the srcPos. If the length of src array is smaller than the
	 * remaining content, the method will throw
	 * {@link ArrayIndexOutOfBoundsException}
	 * 
	 * @param src
	 * @param srcPos
	 */
	public void get(char[] src, int srcPos) {
		System.arraycopy(src, srcPos, this.chars, position, (chars.length - position));
		this.position = this.chars.length;
	}

	/**
	 * Copies chars data of this {@link CharArray} into the src char starting
	 * from the srcPos. If the length of src array is smaller than the remaining
	 * content, the method will throw {@link ArrayIndexOutOfBoundsException}
	 * 
	 * @param src
	 * @param srcPos
	 * @param length
	 */
	public void get(char[] src, int srcPos, int length) {
		System.arraycopy(src, srcPos, this.chars, position, length);
		this.position += length;
	}

	@Override
	public void reset() {
		this.chars = null;
		this.position = 0;
		this.limit = 0;
	}

	/**
	 * Reloads this {@code CharSource} with given chars. The position of the
	 * char source will be set to zero and limit will be set to the length of
	 * the char array.
	 * 
	 * @param chars chars array (data)
	 */
	public void reload(char[] chars) {
		this.reload(chars, 0, chars.length);
	}

	/**
	 * Reloads this {@code CharSource} with given chars. The position of the
	 * char source will be set to the position supplied and limit will be set to
	 * the length of the char array.
	 * 
	 * @param chars chars array (data)
	 * @param position position from which reading will start
	 */
	public void reload(char[] chars, int position) {
		this.reload(chars, position, chars.length);
	}

	/**
	 * Reloads this {@code CharSource} with given chars
	 * 
	 * @param chars chars array (data)
	 * @param position position from which reading will start
	 * @param limit boundary index. The reading cursor will always be behind the
	 *            boundary
	 */
	public void reload(char[] chars, int position, int limit) {
		this.chars = chars;
		this.position = position;
		this.limit = limit;
	}

	/**
	 * Sets position index to the given value
	 * 
	 * @param position
	 */
	public void setPosition(int position) {
		this.position = position;
	}

	/**
	 * Number of chars remaining in this {@link CharArray}. The number is
	 * calculated based on the value of {@link CharArray#limit} and
	 * {@link CharArray#position} variables; although at any point underlying
	 * array has all the elements.
	 */
	public int remaining() {
		return this.limit - this.position;
	}
}
