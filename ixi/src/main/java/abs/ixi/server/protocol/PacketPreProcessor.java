package abs.ixi.server.protocol;

import abs.ixi.server.etc.conf.ProcessConfigAware;
import abs.ixi.server.io.PacketCollector;
import abs.ixi.server.io.StreamContext;
import abs.ixi.server.packet.Packet;
import abs.ixi.server.session.LocalSession;

/**
 * {@code PacketPreProcessor} is a unit of work required to process one of the
 * Stream management packets. There will be at least one
 * {@link PacketPreProcessor} for each of the stream management packets that
 * server supports. {@codes StreamPacketProcessor} is contract for all such
 * processors within server.
 */
public interface PacketPreProcessor<T extends Packet, R extends Packet> extends ProcessConfigAware {
	/**
	 * Process a single stream management packet.
	 * 
	 * @param packet
	 * @return
	 * @throws Exception
	 */
	public void preProcess(T packet, LocalSession ls, StreamContext context, PacketCollector<R> collector)
			throws Exception;
}
