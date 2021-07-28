package abs.ixi.server.packet.xmpp;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

import abs.ixi.server.AbstractPacket;
import abs.ixi.server.ByteConvertible;
import abs.ixi.server.XMLConvertible;
import abs.ixi.server.common.ChannelStream;
import abs.ixi.util.DateUtils;
import abs.ixi.util.StringUtils;
import abs.ixi.xml.Element;

/**
 * Represents any data packet exchanged using XMPP
 */
public abstract class XMPPPacket extends AbstractPacket implements ByteConvertible, XMLConvertible {
	private static final long serialVersionUID = -5458308207058278913L;

	protected Element element;

	public XMPPPacket() {
		this.setCreateTime(DateUtils.currentTimestamp());
	}

	public XMPPPacket(Element element) {
		this();
		this.element = element;
	}

	public Element getElement() {
		return element;
	}

	public void setElement(Element element) {
		this.element = element;
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
		byte[] bytes = this.getBytes();

		if (bytes != null && bytes.length > 0) {
			cs.enqueue(getBytes());
		}

		return bytes.length;
	}

	@Override
	public boolean hasMime() {
		return false;
	}

	@Override
	public byte[] getBytes() {
		if (StringUtils.isNullOrEmpty(this.xml())) {
			return null;
		}

		return this.xml().getBytes(StandardCharsets.UTF_8);
	}

	@Override
	public int getBytes(byte[] dest, int offset) {
		byte[] bytes = this.getBytes();

		if (bytes != null) {
			System.arraycopy(bytes, 0, dest, offset, bytes.length);
			offset += bytes.length;
		}

		return offset;
	}

	@Override
	public StringBuilder appendXml(StringBuilder sb) {
		return sb.append(this.xml());
	}

	@Override
	public String xml() {
		return null;
	}

	@Override
	public boolean isRoutable() {
		return false;
	}

	@Override
	public boolean isBoshBodyPacket() {
		return false;
	}
}
