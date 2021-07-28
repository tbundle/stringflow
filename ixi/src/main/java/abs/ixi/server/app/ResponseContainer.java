package abs.ixi.server.app;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.channels.SocketChannel;

import abs.ixi.server.AbstractPacket;
import abs.ixi.server.common.ChannelStream;
import abs.ixi.server.packet.InvalidJabberId;
import abs.ixi.server.packet.JID;

/**
 * {@code ResponseContainer} is a holder for application responses.
 */

public class ResponseContainer<T extends ApplicationResponse> extends AbstractPacket {
	private static final long serialVersionUID = -947217979250877497L;
	private T response;
	private JID source;
	private JID destination;

	public ResponseContainer(T response) {
		this.response = response;

	}

	public T getResponse() {
		return response;
	}

	@Override
	public JID getDestination() {
		return this.destination;
	}

	@Override
	public void setDestination(String dest) throws InvalidJabberId {
		this.destination = new JID(dest);

	}

	@Override
	public void setDestination(JID dest) {
		this.destination = dest;
	}

	@Override
	public long writeTo(Socket socket) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public long writeTo(OutputStream os) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public long writeTo(SocketChannel socketChannel) throws IOException {
		XmppResponse xmppResponse = (XmppResponse) this.response;
		return xmppResponse.writeTo(socketChannel);
	}

	@Override
	public long writeTo(ChannelStream cs) throws IOException {
		XmppResponse xmppResponse = (XmppResponse) this.response;
		return xmppResponse.writeTo(cs);
	}

	@Override
	public boolean hasMime() {
		return ((ApplicationMessage) this.response).getMultipartMessage() != null;
	}

	@Override
	public String getSourceId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public final boolean isBoshBodyPacket() {
		return false;
	}

}
