package abs.ixi.server.packet.xmpp;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.channels.SocketChannel;

import abs.ixi.server.AbstractPacket;
import abs.ixi.server.common.ChannelStream;
import abs.ixi.server.packet.InvalidJabberId;
import abs.ixi.server.packet.JID;

public abstract class ControlPacket extends AbstractPacket {
	private static final long serialVersionUID = 2232803007313730940L;

	@Override
	public PacketXmlElement getXmlElementName() {
		return null;
	}

	@Override
	public JID getDestination() {
		return null;
	}

	@Override
	public void setDestination(String dest) throws InvalidJabberId {

	}

	@Override
	public void setDestination(JID dest) {

	}

	@Override
	public long writeTo(Socket socket) throws IOException {
		return 0;
	}

	@Override
	public long writeTo(SocketChannel socketChannel) throws IOException {
		return 0;
	}

	@Override
	public long writeTo(OutputStream os) throws IOException {
		return 0;
	}

	@Override
	public long writeTo(ChannelStream cs) throws IOException {
		return 0;
	}

	@Override
	public final boolean hasMime() {
		return false;
	}

	@Override
	public final boolean isRoutable() {
		return true;
	}

	@Override
	public final boolean isBoshBodyPacket() {
		return false;
	}
}
