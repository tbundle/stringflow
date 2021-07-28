package abs.ixi.server.io.multipart;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.channels.SocketChannel;

import abs.ixi.server.common.ChannelStream;

/**
 * A {@link ContentSource} with Byte array as source of the content bytes.
 */
public class ByteArrayContentSource extends AbstractContentSource {
    private byte[] source;
    private int position = 0; // first index at which content can be written

    public ByteArrayContentSource(int length) {
	this.source = new byte[length];
	this.position = 0;
    }

    public ByteArrayContentSource(byte[] content) {
	this.source = content;
	this.position = content.length;
    }

    public byte[] getSource() {
	return this.source;
    }

    @Override
    public long getLength() {
	return this.source == null ? -1 : this.position;
    }

    @Override
    public long writeTo(Socket socket) throws IOException {
	if (source != null && position > 0) {
	    this.write(source, 0, position, socket);
	    return this.source.length;
	}

	return 0;
    }

    @Override
    public long writeTo(SocketChannel channel) throws IOException {
	if (source != null && source.length > 0) {
	    return this.write(source, channel);
	}

	return 0;
    }

    @Override
    public long writeTo(OutputStream stream) throws IOException {
	if (source != null && source.length > 0) {
	    this.write(source, stream);

	    return this.source.length;
	}

	return 0;
    }

    @Override
    public long writeTo(ChannelStream cs) throws IOException {
	if (source != null && source.length > 0) {
	    this.write(source, cs);

	    return this.source.length;
	}

	return 0;
    }

    /**
     * Add content to this {@link ByteArrayContentSource}
     * 
     * @param arr
     *            array oof content
     * @param offset
     *            starting index
     * @param length
     *            number of bytes to be added
     * @return true if the content was added to this {@link ContentSource};
     *         false if the internal array didnt have enough space
     */
    public boolean addContent(byte[] arr, int offset, int length) {
	if ((this.source.length - position) >= length) {
	    System.arraycopy(arr, offset, this.source, this.position, length);

	    this.position += length;

	    return true;

	} else {
	    return false;
	}
    }

}
