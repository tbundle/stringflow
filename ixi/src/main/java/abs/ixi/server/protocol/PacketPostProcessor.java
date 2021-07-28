package abs.ixi.server.protocol;

import abs.ixi.server.io.StreamContext;
import abs.ixi.server.packet.Packet;

public interface PacketPostProcessor {

    public void postProcess(Packet packet, StreamContext context) throws Exception;

}
