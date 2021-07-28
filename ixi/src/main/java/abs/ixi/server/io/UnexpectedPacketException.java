package abs.ixi.server.io;

import abs.ixi.server.packet.Packet;

public class UnexpectedPacketException extends Exception {
    private static final long serialVersionUID = 1L;

    private Packet packet;

    public Packet getPacket() {
	return packet;
    }

    public void setPacket(Packet packet) {
	this.packet = packet;
    }
}
