package abs.ixi.server.common;

/**
 * {@code ByteSource} is a wrapper around byte array. It maintains vital fields
 * such as position, limit and capacity to facilitate better reading process.
 * <p>
 * {@code ByteSource} is unidirectional (can be read only in one direction);
 * however it offers random access using index.
 * </p>
 * <p>
 * position is is the index of first byte which will be read. limit is the index
 * of first byte which will not be read.
 * </p>
 */
public class ByteArray implements Resetable {
	private byte[] bytes;
	private int position;
	private int limit;

	public ByteArray(byte[] bytes) {
		this(bytes, 0, bytes.length);
	}

	public ByteArray(byte[] bytes, int position, int limit) {
		this.bytes = bytes;
		this.position = position;
		this.limit = limit;
	}

	public byte[] bytes() {
		return bytes;
	}

	public int position() {
		return position;
	}

	/**
	 * Returns limit of this byte source. Important to note that limit is an
	 * index; not an absolute number.
	 */
	public int limit() {
		return limit;
	}

	public int capacity() {
		return bytes == null ? 0 : bytes.length;
	}

	public int getPosition() {
		return position;
	}

	/**
	 * Reads next byte from data source; after reading the byte, position moves
	 * to next byte.
	 */
	public byte next() {
		return this.bytes[position++];
	}

	/**
	 * Returns true if there is another byte which can be read from this
	 * {@code ByteSource}
	 */
	public final boolean hasNext() {
		return this.position < this.limit;
	}

	/**
	 * Returns the byte at the index. The method does not change any marker such
	 * as limit, position etc.
	 * 
	 * @param index index of the byte
	 */
	public byte get(int index) {
		return this.bytes[index];
	}

	/**
	 * Copies remaining byte data of this {@link ByteArray} into the src byte
	 * array. If the length of src array is smaller than the remaining content,
	 * the method will throw {@link ArrayIndexOutOfBoundsException}
	 * 
	 * @param src
	 */
	public void get(byte[] src) {
		System.arraycopy(src, 0, this.bytes, position, (this.limit - position));
		this.position = bytes.length;
	}

	/**
	 * Copies remaining byte data of this {@link ByteArray} into the src byte
	 * starting from the srcPos. If the length of src array is smaller than the
	 * remaining content, the method will throw
	 * {@link ArrayIndexOutOfBoundsException}
	 * 
	 * @param src
	 * @param srcPos
	 */
	public void get(byte[] src, int srcPos) {
		System.arraycopy(src, srcPos, this.bytes, position, (this.limit - position));
		this.position = this.bytes.length;
	}

	/**
	 * Copies bytes data of this {@link ByteArray} into the src byte starting
	 * from the srcPos. If the length of src array is smaller than the remaining
	 * content, the method will throw {@link ArrayIndexOutOfBoundsException}
	 * 
	 * @param src
	 * @param srcPos
	 * @param length
	 */
	public void get(byte[] src, int srcPos, int length) {
		System.arraycopy(src, srcPos, this.bytes, position, length);
		this.position += length;
	}

	@Override
	public void reset() {
		this.bytes = null;
		this.position = 0;
		this.limit = 0;
	}

	/**
	 * Reloads this {@code ByteSource} with given bytes. The position of the
	 * byte source will be set to zero and limit will be set to the length of
	 * the byte array.
	 * 
	 * @param bytes bytes array (data)
	 */
	public void reload(byte[] bytes) {
		this.reload(bytes, 0, bytes.length);
	}

	/**
	 * Reloads this {@code ByteSource} with given bytes. The position of the
	 * byte source will be set to the position supplied and limit will be set to
	 * the length of the byte array.
	 * 
	 * @param bytes bytes array (data)
	 * @param position position from which reading will start
	 */
	public void reload(byte[] bytes, int position) {
		this.reload(bytes, position, bytes.length);
	}

	/**
	 * Reloads this {@code ByteSource} with given bytes
	 * 
	 * @param bytes bytes array (data)
	 * @param position position from which reading will start
	 * @param limit boundary index. The reading cursor will always be behind the
	 *            boundary
	 */
	public void reload(byte[] bytes, int position, int limit) {
		this.bytes = bytes;
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
	 * Number of chars remainig in this {@link ByteArray}. The number is
	 * calculated based on the value of {@link ByteSourcee#limit} and
	 * {@link ByteArray#position} variables; although at any point underlying
	 * array has all the elements.
	 */
	public int remaining() {
		return this.limit - this.position;
	}

	/**
	 * Range captures a byte range with a start index and an end index. Start
	 * index is the index at which byte can be read from the byte source. End
	 * index is the first byte which can NOT be read.
	 */
	public static class ByteRange {
		private int startIndex;
		private int endIndex;

		public ByteRange(int startIndex) {
			this(startIndex, 0);
		}

		public ByteRange(int startIndex, int endIndex) {
			this.startIndex = startIndex;
			this.endIndex = endIndex;
		}

		/**
		 * Returns start index of the {@code ByteRange}
		 */
		public int start() {
			return startIndex;
		}

		/**
		 * Returns end index of this {@code ByteRange}
		 */
		public int end() {
			return endIndex;
		}

		/**
		 * Sets the end index to the given value
		 */
		public void end(int endIndex) {
			this.endIndex = endIndex;
		}

		/**
		 * Returns length of this {@code ByteRange}
		 */
		public int length() {
			return this.endIndex - this.startIndex;
		}

		/**
		 * Checks if the start index and end index are same. In this case range
		 * represents nothing.
		 */
		public boolean isNull() {
			return this.startIndex == this.endIndex;
		}

	}

}
