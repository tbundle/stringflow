package abs.ixi.server.protocol;

import abs.ixi.server.io.InputStreamProcessor;
import abs.ixi.server.io.PacketCollector;
import abs.ixi.server.packet.Packet;
import abs.ixi.server.packet.xmpp.IQ;
import abs.ixi.server.session.LocalSession;

/**
 * Root interface for supported protocols in server. We don't expect to support
 * different protocols except variants and extensions of XMPP
 */
public interface Protocol<PACKET extends Packet> {
	/**
	 * Enforces protocol rules on the packet stream. Protocol instances are
	 * singleton by design; therefore it operates on the data within the context
	 * supplied
	 * 
	 * @param packets packet stream
	 * @param context context in which protocol to be enforced
	 * @return envelope list which can be routed
	 */
	public void enforceInbound(PACKET packet, LocalSession ls) throws Exception;

	/**
	 * Enforce protocol on outbound packet
	 * 
	 * @param packet
	 */
	public PACKET enforceOutbound(Packet packet) throws Exception;

	/**
	 * Provides {@link PacketCollector} for collecting processed packet.
	 * 
	 * @param packetCollector
	 */
	public void addPacketCollector(PacketCollector<PACKET> packetCollector);

	/**
	 * Protocol dictates rules for {@link Packet} generation from raw bytes.
	 * Therefore each {@link Protocol} returns required
	 * {@link InputStreamProcessor} to generate packets complying to this
	 * protocol from raw bytes.
	 */
	public InputStreamProcessor<PACKET> getInputStreamProcessor();

	/**
	 * @return true if the protocol supports Ping messages otherwise false.
	 */
	public boolean isPingable();

	/**
	 * @return Ping request IQ
	 */
	public IQ getPingRequestIQ();

	/**
	 * 
	 * @return underline stream will be mime or not.
	 */
	default boolean isMimeStream() {
		return false;
	}

}
