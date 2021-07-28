package abs.ixi.server.io.multipart;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.channels.SocketChannel;

import abs.ixi.server.common.ChannelStream;

/**
 * {@link ContentSource} with {@link File} as underlying byte source.
 */
public class FileContentSource extends AbstractContentSource {
	private File file;

	public FileContentSource(String path) {
		this(new File(path));
	}

	public FileContentSource(File file) {
		this.file = file;
	}

	@Override
	public long getLength() {
		if (file != null) {
			return file.length();
		}

		return -1;
	}

	@Override
	public long writeTo(Socket socket) throws IOException {
		try (InputStream is = new FileInputStream(file)) {
			return this.write(is, socket);
		}

	}

	@Override
	public long writeTo(SocketChannel channel) throws IOException {
		try (InputStream is = new FileInputStream(file)) {
			return this.write(is, channel);
		}
	}

	@Override
	public long writeTo(OutputStream outStream) throws IOException {
		try (InputStream is = new FileInputStream(file)) {
			return this.write(is, outStream);
		}
	}

	@Override
	public long writeTo(ChannelStream cs) throws IOException {
		try (InputStream is = new FileInputStream(file)) {
			return this.write(is, cs);
		}
	}

}
