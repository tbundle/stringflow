package abs.ixi.server.io;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.Selector;
import java.nio.charset.CharsetDecoder;

import abs.ixi.server.common.ChannelStream;
import abs.ixi.server.io.net.IOPort;
import abs.ixi.server.io.net.NetworkTransport;
import abs.ixi.server.packet.Packet;

/**
 * A facade which gives access around operations on network {@link Socket}
 */
public interface ChannelFacade {
	/**
	 * Returns the remote address of the device. This may be a NATed address
	 * 
	 * @return
	 * @throws IOException
	 */
	public SocketAddress getRemoteAddress() throws IOException;

	/**
	 * check if the channel is connected
	 */
	public boolean isConnected();

	/**
	 * Reads all the available incoming network data from associated
	 * {@link ChannelStream}
	 * 
	 * @return returns an array of bytes
	 */
	public byte[] readAllBytes();

	/**
	 * Reads all the bytes pending processing from associated
	 * {@link ChannelStream}. The method takes leftover bytes which could not be
	 * decoded by {@link CharsetDecoder} for multi-byte characters
	 * 
	 * @param partial
	 * @return
	 */
	public byte[] readAllBytes(byte[] partial);

	/**
	 * writes output data on the channel
	 */
	public void write(byte[] data) throws IOException;

	/**
	 * writes output packet on the channel
	 */
	public void write(Packet packet) throws IOException;

	/**
	 * Disables write operation on the socket. This is different than shutting
	 * down the output interface on the Socket. This operation simply removes
	 * this socket from selection process.
	 * 
	 * After this method execution, the {@link NetworkTransport} will overlook
	 * write-ready state of the socket. So even when socket is ready for content
	 * to be written, socket will never be shown as write-ready. The effect of
	 * this method can be reverted using the method
	 * {@link ChannelFacade#enableWrite()}
	 */
	public void disableWrite();

	/**
	 * Close the underlying channel, unregister channel from {@link Selector}
	 * and also marks the {@link IOPort} dead
	 */
	public void close();

}
