package abs.ixi.server.common;

import java.nio.ByteBuffer;

/**
 * {@link BufferFactory} maintains a pool of byte buffers. These buffers can be
 * borrowed by an entity and after finished using it, the entity is expected to
 * return the buffer to {@link BufferFactory}
 */
public interface BufferFactory {
	/**
	 * Borrow a byte buffer of default size from factory. Returns null if the
	 * factory has exhausted its pool of buffers
	 */
	public ByteBuffer borrowBuffer();

	/**
	 * Return the buffer back to the factory pool
	 */
	public void returnBuffer(ByteBuffer buf);

	/**
	 * Get size of each buffer that this factory generates
	 */
	public int bufferSize();

	/**
	 * @return number of buffers that have been allocated and mananged by this
	 *         factory until now.
	 */
	public int size();

	/**
	 * Number of buffers that have been loaned at this point. This number is
	 * dynmaic.
	 */
	public int loanedBufferCount();

	/**
	 * Enumeration to indicate type of buffer: DIRECT and DEFAULT.
	 * 
	 * @author Yogi
	 *
	 */
	public enum BufferType {
		DIRECT("direct"),

		DEFAULT("default");

		private String val;

		private BufferType(String val) {
			this.val = val;
		}

		public String val() {
			return val;
		}

		public static BufferType from(String val) {
			for (BufferType bt : values()) {
				if (bt.val().equalsIgnoreCase(val)) {
					return bt;
				}
			}

			throw new IllegalArgumentException("Invalid buffer type value " + val);
		}
	}

}
