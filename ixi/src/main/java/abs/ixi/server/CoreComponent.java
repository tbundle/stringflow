package abs.ixi.server;

import abs.ixi.server.packet.JID;

/**
 * A tagging interface to mark a component core server component.
 * 
 * @author Yogi
 *
 */
public interface CoreComponent extends ServerComponent {
	/**
	 * @return {@link ServerFunction} that this component is responsible for.
	 */
	public ServerFunction getServerFunction();

	/**
	 * Each of the core server component is responsible for a server function.
	 * The enum allows discovery of core server component based on its function
	 * instead of its name or {@link JID}.
	 * 
	 * @author Yogi
	 *
	 */
	public enum ServerFunction {
		NETWORK_TRANSPORT,

		IO_CONTROL,

		PUSH_NOTIFICATION,

		PRESENCE_MANAGEMENT,

		MUC_HANDLING,

		MEDIA_STORE_MANAGEMENT,

		DISCO_HANDLING,

		PACKET_ROUTING;

	}

}
