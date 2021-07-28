package abs.ixi.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.channels.SocketChannel;

import abs.ixi.server.common.ChannelStream;

/**
 * {@code Writable} is a contract to make an entity writable to some
 * destination; may be a network socket, output stream etc.
 */
public interface Writable {
    /**
     * Write this entity to a network socket. The byte conversion mechanism is
     * up to the implementation to decide.
     * 
     * @return no of bytes written
     * @param socket
     * @throws IOException
     */
    public long writeTo(Socket socket) throws IOException;

    /**
     * Write this entity to a {@link SocketChannel}
     * 
     * @return no of bytes written
     * @param socketChannel
     * @throws IOException
     */
    public long writeTo(SocketChannel socketChannel) throws IOException;

    /**
     * Write to an {@link OutputStream}
     * 
     * @return no of bytes written
     * @param os
     * @throws IOException
     */
    public long writeTo(OutputStream os) throws IOException;

    /**
     * Write this entity to a {@link ChannelStream}. The byte conversion
     * mechanism is up to the implementation to decide.
     * 
     * @return no of bytes written
     * @param socket
     * @throws IOException
     */
    public long writeTo(ChannelStream cs) throws IOException;

}
