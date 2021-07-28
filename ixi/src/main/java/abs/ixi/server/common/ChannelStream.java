package abs.ixi.server.common;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.io.net.LocalSocket;

/**
 * An implementation of {@link ConcurrentQueue} which stores Bytes data in
 * {@link ByteBuffer}s. The buffers are borrowed from a {@link BufferFactory};
 * therefore the type of buffers used depends on the {@link BufferFactory} used.
 * 
 * The implementation is thread-safe.
 */
public class ChannelStream implements SynchronizedQueue<ByteBuffer> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ChannelStream.class);

	private final BufferFactory bufFactory;
	private final LinkedList<ByteBuffer> queue;

	private ByteBuffer tail = null;

	public ChannelStream(BufferFactory bufferFactory) {
		this.bufFactory = bufferFactory;
		this.queue = new LinkedList<ByteBuffer>();
	}

	public synchronized boolean isEmpty() {
		return queue.size() == 0;
	}

	/**
	 * Fill stream from the content available on in-bound socket buffer
	 * (receiver buffer). While reading data, buffers are added to queue; These
	 * buffers are not read ready though. Before reading data from these
	 * buffers, {@link ByteBuffer#flip()} must be invoked
	 * 
	 * @return number of bytes read from the socket
	 */
	public synchronized int fillFrom(LocalSocket socket) throws IOException {
		int bytesRead = 0;
		int c = 0;

		if (!this.isEmpty() && this.tail.remaining() > 0) {
			c = socket.read(this.tail);
			bytesRead += c;

			if (this.tail.remaining() > 0) {
				return bytesRead;
			}
		}

		do {
			c = fillNewBuffer(socket);
			bytesRead += c;

		} while (c == this.bufFactory.bufferSize());

		return bytesRead;
	}

	/**
	 * Fills data into a newly borrowed buffer from {@link BufferFactory}
	 * 
	 * @param channel socket channel to read data from
	 * @return returns number of bytes read
	 * @throws IOException
	 */
	private int fillNewBuffer(LocalSocket socket) throws IOException {
		ByteBuffer buffer = this.bufFactory.borrowBuffer();
		int byteRead = socket.read(buffer);

		if (byteRead > 0) {
			this.tail = buffer;
			this.queue.add(this.tail);
		} else {
			this.bufFactory.returnBuffer(buffer);
		}

		return byteRead;
	}

	// TODO Needs exception handling for channel writing. Channel write may
	// throw exception; we should catch them and wrap them into another
	// exception (ixi defined) for clarity
	/**
	 * Writes the stream content on the {@link SocketChannel}.
	 *
	 * @param facade
	 * @return
	 * @throws IOException
	 */
	public synchronized int drainTo(LocalSocket socket) throws IOException {
		if (this.isEmpty()) {
			return 0;
		}

		int bytesWritten = 0;

		while (queue.size() != 0) {
			ByteBuffer head = queue.getFirst();
			head.flip();

			int c = socket.write(head);
			bytesWritten += c;

			if (!head.hasRemaining()) {
				bufFactory.returnBuffer(head);
				queue.removeFirst();

			} else {
				LOGGER.warn("Failed to drain data onto socket. May be selector is spinning!!!");
				head.compact();

				LOGGER.warn("Will exit for now. will try again after some time");
				break;
			}

			if (c == 0) {
				break; // This may be caused due to selector spin.
			}
		}

		if (this.isEmpty()) {
			tail = null;
		}

		return bytesWritten;
	}

	/**
	 * Adds bytes to the {@link ChannelStream}. These bytes are put into
	 * {@link ByteBuffer}s and these buffers are added to the queue
	 */
	public synchronized boolean enqueue(byte[] bytes) {
		return this.enqueue(bytes, 0, bytes.length);
	}

	/**
	 * Adds bytes to the {@link ChannelStream}. These bytes are put into
	 * {@link ByteBuffer}s and these buffers are added to the queue
	 */
	public synchronized boolean enqueue(byte[] bytes, int offset, int length) {
		if (bytes == null || length == 0) {
			return false;
		}

		if (this.tail != null && this.tail.remaining() > 0) {
			int r = this.tail.remaining();

			if (r < length) {
				this.tail.put(bytes, 0, r);
			} else {
				this.tail.put(bytes);
				return true;
			}

			offset = r;
		}

		while (offset < length) {
			this.tail = this.bufFactory.borrowBuffer();

			if ((length - offset) < this.bufFactory.bufferSize()) {
				this.tail.put(bytes, offset, (length - offset));
				this.queue.add(this.tail);

				return true;

			} else {
				this.tail.put(bytes, offset, this.bufFactory.bufferSize());
				offset += this.bufFactory.bufferSize();
				this.queue.add(this.tail);
			}
		}

		return true;
	}

	/**
	 * Adds buffer content to the {@link ChannelStream}
	 */
	@Deprecated
	public synchronized boolean enqueue(ByteBuffer byteBuffer) {
		if (byteBuffer.remaining() == 0) {
			return false;
		}

		if (queue.size() > 0) {
			if (tail.hasRemaining()) {
				topUpBuffer(tail, byteBuffer);
			}
		}

		while (byteBuffer.hasRemaining()) {
			tail = bufFactory.borrowBuffer();
			topUpBuffer(tail, byteBuffer);
			queue.add(tail);
		}

		return true;
	}

	/**
	 * Copies data into destination buffer from source buffer. The method is
	 * used by {@code ChannelStream#enqueue(ByteBuffer)} which has been
	 * deprecated
	 * 
	 * @param dest
	 * @param src
	 */
	private void topUpBuffer(ByteBuffer dest, ByteBuffer src) {
		if (src.remaining() <= dest.remaining()) {
			dest.put(src);
		} else {
			while (dest.hasRemaining()) {
				dest.put(src);
			}
		}
	}

	/**
	 * Returns buffers back to factory for reuse
	 */
	public synchronized void returnBuffers() {
		if (!queue.isEmpty()) {
			for (ByteBuffer buffer : queue) {
				bufFactory.returnBuffer(buffer);
			}
		}
	}

	/**
	 * Removes buffers from this {@link ChannelStream} and returns the array of
	 * all the removed {@link ByteBuffer}. Returns null if the stream is empty
	 */
	public synchronized ByteBuffer[] dequeueBuffers() {
		if (queue.size() != 0) {
			ByteBuffer[] buffers = new ByteBuffer[queue.size()];

			for (int i = 0; i < queue.size(); i++) {
				ByteBuffer buffer = queue.removeFirst();
				buffer.flip();

				buffers[i] = buffer;
			}

			if (this.isEmpty()) {
				this.tail = null;
			}

			return buffers;
		}

		return null;
	}

	/**
	 * Removes bytes from this {@link ChannelStream} and returns the array of
	 * all the removed bytes. Returns null if the stream is empty
	 */
	public synchronized byte[] dequeueBytes() {
		if (this.isEmpty()) {
			return null;
		}

		int totalBytesCount = (this.bufFactory.bufferSize() * (queue.size() - 1)) + this.tail.position();
		byte[] dataBytes = new byte[totalBytesCount];

		int offset = 0;

		while (!this.isEmpty()) {
			ByteBuffer head = queue.removeFirst();
			head.flip();

			int r = head.remaining();
			head.get(dataBytes, offset, r);
			offset += r;

			bufFactory.returnBuffer(head);
		}

		if (this.isEmpty()) {
			tail = null;
		}

		return dataBytes;
	}

	public synchronized byte[] dequeueBytes(byte[] partial) {
		if (partial == null) {
			return dequeueBytes();

		} else {

			if (this.isEmpty()) {
				return partial;
			}

			int totalBytesCount = (partial.length + this.bufFactory.bufferSize() * (queue.size() - 1))
					+ this.tail.position();

			byte[] dataBytes = new byte[totalBytesCount];

			int offset = 0;

			for (offset = 0; offset < partial.length; offset++) {
				dataBytes[offset] = partial[offset];
			}

			while (!this.isEmpty()) {
				ByteBuffer head = queue.getFirst();
				head.flip();

				int r = head.remaining();
				head.get(dataBytes, offset, r);
				offset += r;

				bufFactory.returnBuffer(head);
				queue.removeFirst();
			}

			if (this.isEmpty()) {
				tail = null;
			}

			return dataBytes;
		}
	}

}
