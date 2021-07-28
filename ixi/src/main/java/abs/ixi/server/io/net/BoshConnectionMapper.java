package abs.ixi.server.io.net;

import java.nio.channels.SocketChannel;

public class BoshConnectionMapper {
	private SocketChannel incommingChannel;
	private SocketChannel outgoingChannel;
	
	public BoshConnectionMapper(SocketChannel incommingChannel, SocketChannel outgoingChannel){
		this.incommingChannel = incommingChannel;
		this.outgoingChannel = outgoingChannel;
	}
	public SocketChannel getIncommingChannel() {
		return incommingChannel;
	}
	public void setIncommingChannel(SocketChannel incommingChannel) {
		this.incommingChannel = incommingChannel;
	}
	public SocketChannel getOutgoingChannel() {
		return outgoingChannel;
	}
	public void setOutgoingChannel(SocketChannel outgoingChannel) {
		this.outgoingChannel = outgoingChannel;
	}
	
}
