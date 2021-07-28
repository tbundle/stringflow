package abs.ixi.server.common;

import abs.ixi.server.common.ByteArray.ByteRange;

/**
 * Root interface to read a line from a byte source. A line could be separated
 * by LF or CRLF. {@code LineReader} implementations must be state less
 * therefore thread-safe.
 */
public interface LineReader<T> {
	public static final byte LF = 0xA;
	public static final byte CR = 0xD;

	/**
	 * Reads a line from the {@link ByteArray}
	 * 
	 * @param src
	 * @return
	 */
	public T readLine(ByteArray src);

	/**
	 * Reads a line from the {@link ByteArray}
	 * 
	 * @param src byte source with byte stream
	 * @param skipBlankLines when true, the reader skips blank lines (LF/CRLF)
	 * @return returns a {@link ByteRange}
	 */
	public T readLine(ByteArray src, boolean skipBlankLines);

	/**
	 * Reads a line from the {@link CharArray}
	 * 
	 * @param src
	 * @return
	 */
	public T readLine(CharArray src);

	/**
	 * Reads a line from the {@link CharArray}
	 * 
	 * @param src byte source with byte stream
	 * @param skipBlankLines when true, the reader skips blank lines (LF/CRLF)
	 * @return returns a {@link ByteRange}
	 */
	public T readLine(CharArray src, boolean skipBlankLines);

	/**
	 * {@code Line} class captures information to represent a line within a
	 * {@link ByteArray}
	 */
	public static class Line {
		private int startIndex;
		private int endIndex;
		private LineBreak lineBreak;

		public Line(int startIndex) {
			this(startIndex, null);
		}

		public Line(int startIndex, int endIndex) {
			this(startIndex, endIndex, null);
		}

		public Line(int startIndex, LineBreak lineBreak) {
			this(startIndex, 0, lineBreak);
		}

		public Line(int startIndex, int endIndex, LineBreak lineBreak) {
			this.startIndex = startIndex;
			this.endIndex = endIndex;
			this.lineBreak = lineBreak;
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

		/**
		 * Checks if the start index and end index are same. In this case range
		 * represents end of message.
		 */
		public boolean isMessageEnd() {
			return this.startIndex > this.endIndex;
		}

		public LineBreak getLineBreak() {
			return lineBreak;
		}

		public boolean isBlankLine() {
			return isNull();
		}

		public boolean isPartialLine() {
			return this.lineBreak == null;
		}
	}

	public enum LineBreak {
		LF, CRLF;

		public LineBreak valueFrom(byte b) {
			return b == LineReader.CR ? CRLF : LF;
		}

		public byte[] getBytes() {
			if (this == CRLF) {
				return new byte[] { LineReader.CR, LineReader.LF };

			} else {
				return new byte[] { LineReader.LF };
			}
		}

	}

}
