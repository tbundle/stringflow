package abs.ixi.server.router;

import abs.ixi.server.ServerComponent;
import abs.ixi.server.packet.Packet;

/**
 * There are multiple policies supported for routing a {@link Packet} to various
 * {@link ServerComponent}s.
 * 
 * @author Yogi
 *
 */
public enum RoutingPolicy {
    PACKET_TYPE,

    REGEX,

    CUSTOM;
}
