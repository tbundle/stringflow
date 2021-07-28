package abs.ixi.server.io.multipart;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.channels.SocketChannel;

import abs.ixi.server.common.ChannelStream;

/**
 * A {@link ContentSource} implementation with {@link InputStream} as underlying
 * byte source.
 */
public class InputStreamContentSource extends AbstractContentSource {
	private InputStream is;

	public InputStreamContentSource(InputStream is) {
		this.is = is;
	}

	@Override
	public long getLength() {
		// TODO : calculate length
		return is != null ? 0 : -1;
	}

	@Override
	public long writeTo(Socket socket) throws IOException {
		if (is == null) {
			return 0;
		}

		return this.write(is, socket);
	}

	@Override
	public long writeTo(SocketChannel socketChannel) throws IOException {
		if (is == null) {
			return 0;
		}

		return this.write(is, socketChannel);
	}

	@Override
	public long writeTo(OutputStream os) throws IOException {
		if (is == null) {
			return 0;
		}

		return this.write(is, os);
	}

	@Override
	public long writeTo(ChannelStream cs) throws IOException {
		if (is == null) {
			return 0;
		}

		return this.write(is, cs);
	}

}
