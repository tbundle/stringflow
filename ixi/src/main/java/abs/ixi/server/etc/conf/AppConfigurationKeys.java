package abs.ixi.server.etc.conf;

/**
 * One place where all the config property names are kept.
 */
public interface AppConfigurationKeys {
	// Socket buffer size for media sockets
	public static final String PROP_MEDIA_SERVER_SEND_BUFFER_SIZE = "ixi.server.media.send.buffer.size";

	// database ip address which XMPP server uses
	public static final String PROP_SERVER_DB_IP = "ixi.server.db.ip";

	// database port to which server connects
	public static final String PROP_SERVER_DB_PORT = "ixi.server.db.port";

	// Application name in deploy config file
	public static final String PROP_APPLICATION_NAME = "ixi.application.name";

	// Application jid node in deploy config file
	public static final String PROP_APPLICATION_JID = "ixi.application.jid.node";

	// Application publish address in deploy config file
	public static final String PROP_PUBLISH_ADDRESS = "ixi.application.publish.address";

	// Application After INIT callback in deploy config file
	public static final String PROP_AFTER_INIT_CALLBACK = "ixi.application.afterInit";

	// Application Before START callback in deploy config file
	public static final String PROP_BEFORE_START_CALLBACK = "ixi.application.beforeStart";

	// Application Before SHUTDOWN callback in deploy config file
	public static final String PROP_BEFORE_SHUTDOWN_CALLBACK = "ixi.application.beforeShutdown";
}
