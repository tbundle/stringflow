package abs.ixi.server.router;

import abs.ixi.server.packet.Packet;

/**
 * {@link Router} is the central component in server which routes packets to
 * destination server components. As we support custom componenets (which
 * developers can write as an extension to server build), it is imperative to
 * offer ability to define/modify packet routes.
 * <p>
 * {@code RouteFinder} manages route definitions in server; {@link Router} uses
 * {@code RouteFinder} to route packets.
 * </p>
 * 
 * @author Yogi
 *
 */
public class RouteFinder {
	/**
	 * Configuration file which stores packet route definitions in server
	 */
	private static final String ROUTE_DEFINITION_STORE = "routes.conf";

	public Route findRoute(Packet packet) {
		return null;
	}

}
