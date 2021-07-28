package abs.ixi.server.protocol;

import abs.ixi.server.io.InputStreamProcessor;
import abs.ixi.server.io.PacketCollector;
import abs.ixi.server.io.multipart.MimePacket;
import abs.ixi.server.io.multipart.MimeStreamProcessor;
import abs.ixi.server.packet.Packet;
import abs.ixi.server.packet.xmpp.IQ;
import abs.ixi.server.session.LocalSession;

public class MimeProtocol implements Protocol<MimePacket> {

	/**
	 * A collector for generated packets from this protocol
	 */
	private PacketCollector<MimePacket> packetCollector;

	@Override
	public void enforceInbound(MimePacket packet, LocalSession ls) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public MimePacket enforceOutbound(Packet packet) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addPacketCollector(PacketCollector<MimePacket> packetCollector) {
		this.packetCollector = packetCollector;

	}

	@Override
	public InputStreamProcessor<MimePacket> getInputStreamProcessor() {
		return new MimeStreamProcessor(this.packetCollector);
	}

	@Override
	public boolean isPingable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IQ getPingRequestIQ() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isMimeStream() {
		return true;
	}
}
