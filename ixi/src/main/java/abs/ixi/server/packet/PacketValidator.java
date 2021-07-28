package abs.ixi.server.packet;

import abs.ixi.server.ValidationError;

public interface PacketValidator<PACKET extends Packet> {
    public void validate(PACKET packet, String trigger, Object... args) throws ValidationError;
}
