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
 * {@code RequestContainer} is a holder for application requests. Application
 * request object can not hold data other than what belongs to the request
 * itself; it is the {@code RequestContainer} which holds associated data which
 * later translates to {@link RequestContext}
 * 
 * @param <T>
 */
public class RequestContainer<T extends ApplicationRequest> extends AbstractPacket {
	private static final long serialVersionUID = -1291292975961546926L;
	private T request;
	private JID source;
	private JID destination;

	public RequestContainer(T request) {
		this.request = request;
	}

	public T getRequest() {
		return request;
	}

	@Override
	public JID getDestination() {
		return destination;
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
	public long writeTo(SocketChannel socketChannel) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public long writeTo(OutputStream os) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public long writeTo(ChannelStream cs) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasMime() {
		// TODO Implementation is flawed
		// We must check if the multipart has body parts of non-zero length
		// within it
		return ((ApplicationMessage) this.request).getMultipartMessage() != null;
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
