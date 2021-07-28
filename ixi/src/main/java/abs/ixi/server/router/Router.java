package abs.ixi.server.router;

import abs.ixi.server.PacketEnvelope;
import abs.ixi.server.packet.Packet;
import abs.ixi.server.PacketConsumer;

/**
 * Root interface for packet router server components.
 */
public interface Router {
	/**
	 * Submit a packet for routing within server.
	 * 
	 * @param envelope packet envelope to be routed
	 * @return true if the packet was submitted successfully otherwise false
	 */
	public <P extends Packet> boolean route(PacketEnvelope<P> envelope);

	/**
	 * Subscribe to locally running {@code Router} component to receive packets.
	 * In order to receive packets, a {@link PacketConsumer} component must
	 * subscribe with {@code Router}.
	 * 
	 * @param component {@link PacketConsumer} component instance
	 */
	public void subscribe(PacketConsumer component);

}