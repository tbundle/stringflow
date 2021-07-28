package abs.ixi.server;

import abs.ixi.server.packet.Packet;

/**
 * Components which can receive user level packets for processing from other
 * components (probably from Router)
 */
public interface PacketConsumer extends ServerComponent {

	/**
	 * Submit a packet for processing
	 * 
	 * @param envelope
	 */
	public boolean submit(PacketEnvelope<? extends Packet> envelope);

}
