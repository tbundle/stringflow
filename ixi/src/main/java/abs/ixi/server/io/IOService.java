package abs.ixi.server.io;

import java.nio.channels.SelectionKey;

import abs.ixi.server.common.ChannelStream;
import abs.ixi.server.common.Identifiable;
import abs.ixi.server.io.net.IOPort;
import abs.ixi.server.io.net.LocalSocket;
import abs.ixi.server.io.net.NetworkTransport;
import abs.ixi.server.packet.JID;
import abs.ixi.server.packet.Packet;
import abs.ixi.server.packet.xmpp.BareJID;
import abs.ixi.server.protocol.Protocol;

/**
 * Generally {@link IOService} will be available for each protocol that server
 * supports. {@link IOService} works as bridge between server application layer
 * and {@link NetworkTransport}
 * <p>
 * {@Code IOService} by design are not thread-safe. However as there is one to
 * one mapping between {@link LocalSocket} to IOService; its unlikely to be
 * subjected to very high level of concurrency.
 * </p>
 */
public interface IOService<PROTOCOL extends Protocol<PACKET>, PACKET extends Packet>
		extends Identifiable<String>, IOSignalReceiver, Runnable, Pingable {
	/**
	 * Return node value from associated XMPP {@link JID}
	 */
	public String getNode();

	/**
	 * @return domain of associated the XMPP {@link JID}
	 */
	public String getDomain();

	/**
	 * @return User {@link BareJID} connected to this {@code IOService} instance
	 */
	public BareJID getBareJID();

	/**
	 * return ResourceId related to this {@link IOService} instance.
	 */
	public String getResourceID();

	/**
	 * Inform this service that new bytes has been read and pushed into inbound
	 * {@link ChannelStream}
	 */
	public void bytesArrived();

	/**
	 * Informs this {@link IOService} that incoming bytes have been read from
	 * socket and are awaiting processing. The operation will lead to
	 * {@link IOService} submission for execution if it is not executing already
	 * 
	 * @param count
	 */
	public void bytesArrived(int count);

	/**
	 * Adds packet to {@link IOService} waiting packet queue. Essentially here
	 * service fetches the {@link SocketIO} object from {@link SelectionKey} and
	 * adds it for output drain operation
	 * 
	 * @param packet
	 * @return false, if the waiting packet queue is full; true if the packet
	 *         was added to the queue successfully
	 */
	public boolean writePacket(Packet packet);

	/**
	 * {@link IOService} wraps the data and JID into a packet and invokes
	 * {@link #writePacket(Packet)}
	 * 
	 * @param data
	 * @param destination
	 */
	public boolean writeData(String data) throws Exception;

	/**
	 * {@link IOService} wraps the data and JID into a packet and invokes
	 * {@link #writePacket(Packet)}
	 * 
	 * @param data
	 * @param destination
	 */
	public boolean writeData(byte[] data) throws Exception;

	/**
	 * It enforce outbound protocol and write packet.
	 * 
	 * @param packet
	 * @return
	 */
	public boolean handleOutboundPacket(Packet packet);

	/**
	 * Returns last activity time on this session.
	 * 
	 * @return
	 */
	public long getLastActivityTime();

	/**
	 * shutdown {@link IOService} operations. This will trigger shutdown on
	 * {@link IOPort}, underlying network resources and also will trigger
	 * deregistration of channels and other related resources from
	 * {@link NetworkTransport} and also unregister itself from IOController cache.
	 */
	public void destroy();

	/**
	 * It will close this session stream. This will trigger shutdown on
	 * {@link IOPort}, underlying network resources and also will trigger
	 * deregistration of channels and other related resources from
	 * {@link NetworkTransport} and also unregister itself from IOController cache.
	 */
	public void closeStream();

}
