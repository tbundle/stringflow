package abs.ixi.server.io.multipart;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import abs.ixi.server.common.ChannelStream;

/**
 * Abstract implementation of {@link ContentSource} interface. This class has
 * implementation of all the write operations defined in contract
 * {@link ContentSource}
 */
public abstract class AbstractContentSource implements ContentSource {
	public static final int BUFFER_SIZE = 2048;

	/**
	 * Write data onto a {@link SocketChannel} from an {@link InputStream}
	 * 
	 * @param is {@link InputStream} instance from which data will be read
	 * @param channel {@link SocketChannel} on which data will be written to
	 * @return number of bytes written to {@link SocketChannel}
	 * @throws IOException if any IO error occurs during writing
	 */
	protected long write(InputStream is, SocketChannel channel) throws IOException {
		byte[] bytes = new byte[BUFFER_SIZE];

		long bytesWritten = 0;
		int count = 0;

		while ((count = is.read(bytes)) > 0) {
			bytesWritten += write(bytes, 0, count, channel);
		}

		return bytesWritten;
	}

	/**
	 * Write data onto a {@link ChannelStream} from an {@link InputStream}
	 * 
	 * @param is {@link InputStream} instance from which data will be read
	 * @param channel {@link ChannelStream} on which data will be written to
	 * @return number of bytes written to {@link ChannelStream}
	 * @throws IOException if any IO error occurs during writing
	 */
	protected long write(InputStream is, ChannelStream cs) throws IOException {
		byte[] bytes = new byte[BUFFER_SIZE];

		long bytesWritten = 0;
		int count = 0;

		while ((count = is.read(bytes)) > 0) {
			bytesWritten += write(bytes, 0, count, cs);
		}

		return bytesWritten;
	}

	/**
	 * Drain content from byte array to {@link SocketChannel}
	 * 
	 * @param bytes content to be drained onto {@link SocketChannel}
	 * @param channel {@link SocketChannel} on which content will be written
	 * @return number of bytes written
	 * @throws IOException if IO error occurs during write
	 */
	protected int write(byte[] bytes, SocketChannel channel) throws IOException {
		return write(bytes, 0, bytes.length, channel);
	}

	/**
	 * Drain content from byte array to {@link SocketChannel}
	 * 
	 * @param bytes byte array with content
	 * @param offset index of first byte to be read
	 * @param length number of bytes to be read starting from offset index
	 * @param channel {@link SocketChannel} on which content to be written
	 * @return number of bytes written
	 * @throws IOException if IO error occurs during write
	 */
	protected int write(byte[] bytes, int offset, int length, SocketChannel channel) throws IOException {
		ByteBuffer buf = ByteBuffer.wrap(bytes, 0, length);

		return channel.write(buf);
	}

	/**
	 * Drain content from byte array to {@link ChannelStream}
	 * 
	 * @param bytes content to be drained onto {@link ChannelStream}
	 * @param channel {@link ChannelStream} on which content will be written
	 * @return number of bytes written
	 * @throws IOException if IO error occurs during write
	 */

	protected int write(byte[] bytes, ChannelStream cs) {
		return write(bytes, 0, bytes.length, cs);

	}

	/**
	 * Drain content from byte array to {@link ChannelStream}
	 * 
	 * @param bytes byte array with content
	 * @param offset index of first byte to be read
	 * @param length number of bytes to be read starting from offset index
	 * @param channel {@link ChannelStream} on which content to be written
	 * @return number of bytes written
	 * @throws IOException if IO error occurs during write
	 */
	protected int write(byte[] bytes, int offset, int length, ChannelStream cs) {
		cs.enqueue(bytes, offset, length);
		return length;
	}

	/**
	 * Write data onto a {@link Socket} from an {@link InputStream}
	 * 
	 * @param is {@link InputStream} instance from which data will be read
	 * @param socket {@link Socket} on which data will be written to
	 * @return number of bytes written to {@link SocketChannel}
	 * @throws IOException if any IO error occurs duriing writting
	 */
	protected long write(InputStream is, Socket socket) throws IOException {
		return this.write(is, socket.getOutputStream());
	}

	/**
	 * Read content from given inputstream and write it onto the
	 * {@link OutputStream}; essentially this method drains the
	 * {@link InputStream} content onto {@link OutputStream}
	 * 
	 * @param is inputstream from which content will be read
	 * @param os outputstream on which content will be written
	 * @return number of bytes written on to the outputstream
	 * @throws IOException if IO error during writting or reading
	 */
	protected long write(InputStream is, OutputStream os) throws IOException {
		byte[] bytes = new byte[BUFFER_SIZE];

		long bytesWritten = 0;
		int count = 0;

		while ((count = is.read(bytes)) > 0) {
			bytesWritten += count;
			write(bytes, 0, count, os);
		}

		os.flush();

		return bytesWritten;
	}

	/**
	 * Drain byte array content to {@link OutputStream} instance.
	 * 
	 * @param bytes byte array with content
	 * @param socket {@link Socket} on which content will be written
	 * @throws IOException if IO error occurs
	 */
	protected void write(byte[] bytes, Socket socket) throws IOException {
		write(bytes, socket.getOutputStream());
	}

	/**
	 * Drain byte array content to {@link OutputStream} instance.
	 * 
	 * @param bytes byte array with content
	 * @param offset
	 * @param length
	 * @param socket {@link Socket} on which content will be written
	 * @throws IOException if IO error occurs
	 */
	protected void write(byte[] bytes, int offset, int length, Socket socket) throws IOException {
		write(bytes, socket.getOutputStream());
	}

	/**
	 * Drain byte array content to {@link OutputStream} instance.
	 * 
	 * @param bytes byte array with content
	 * @param os {@link OutputStream} on which content will be written
	 * @throws IOException if IO error occurs
	 */
	protected void write(byte[] bytes, OutputStream os) throws IOException {
		write(bytes, 0, bytes.length, os);
	}

	/**
	 * Drain byte array content to {@link OutputStream} instance. After draining
	 * the content {@link OutputStream#flush()} is invoked.
	 * 
	 * @param bytes byte array with content
	 * @param offset starting index of the array from which content will be read
	 * @param length number of bytes to be read
	 * @param os {@link OutputStream} on which content will be written
	 * @throws IOException if IO error occurs
	 */
	protected void write(byte[] bytes, int offset, int length, OutputStream os) throws IOException {
		os.write(bytes, offset, length);
		os.flush();
	}

}
