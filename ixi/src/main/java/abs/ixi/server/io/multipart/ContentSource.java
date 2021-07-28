package abs.ixi.server.io.multipart;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.channels.SocketChannel;

import abs.ixi.server.common.ChannelStream;

/**
 * An interface which unifies content handling through out SDK and server. Each
 * implementation of this interface will have its own way of storing data,
 * writing it to a stream or network socket.
 */
public interface ContentSource {
    /**
     * @return number of bytes required to capture the content in in-memory.
     *         Returns -1 if the length can not be determined.
     */
    public long getLength();

    /**
     * Write this content on to a network socket
     * 
     * @param socket
     * @throws IOException
     */

    public long writeTo(Socket socket) throws IOException;

    /**
     * Write this content on to a {@link SocketChannel}
     * 
     * @param channel
     * @throws IOException
     */
    public long writeTo(SocketChannel channel) throws IOException;

    /**
     * Write this content to a {@link OutputStream} instance.
     * 
     * @param stream
     * @throws IOException
     */
    public long writeTo(OutputStream stream) throws IOException;

    /**
     * Write this content to a {@link ChannelStream} instance.
     * 
     * @param stream
     * @throws IOException
     */
    public long writeTo(ChannelStream cs) throws IOException;

}
