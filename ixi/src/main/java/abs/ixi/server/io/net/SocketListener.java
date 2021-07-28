package abs.ixi.server.io.net;

import abs.ixi.server.ServerListener;

/**
 * A contract for all the listeners in server which are listening on a socket.
 * As it extends from {@link ServerListener}, it inherits all the behaviours of
 * a generic listener already; this interface provides ability to extend that
 * interface for Socket listening.
 * 
 * @author Yogi
 *
 */
public interface SocketListener extends ServerListener {
    // Currently there is nothing here'; expecitng to get added soon
}
