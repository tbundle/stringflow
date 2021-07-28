package abs.ixi.server.etc.conf;

import abs.ixi.server.packet.JID;

/**
 * Interface with all the system level configurations. Configuration names
 * starting with ENV are read from environment. The values specified in
 * 
 * @author Yogi
 *
 */
public interface SystemConfigAware {
	/**
	 * Server sets the value of this varibale as Java System property.
	 */
	String SF_HOME = "sf.home";

	/**
	 * Server cluster {@link JID}
	 */
	String SYS_CLUSTER_JID = "sf.server.jid";

	String DB_IP = "ixi.server.db.ip";
	String DB_PORT = "ixi.server.db.port";
	String DB_NAME = "ixi.server.db.name";

	// JDBC Properties
	String JDBC_DRIVER_CLASS_NAME = "jdbc.driverClassName";
	String JDBC_URL = "jdbc.url";
	String JDBC_USER_NAME = "jdbc.username";
	String JDBC_PASSWORD = "jdbc.password";
	String JDBC_MINIMUM_CONNECTION_COUNT = "jdbc.minimum.connection.count";
	String JDBC_MAXIMUM_CONNECTION_COUNT = "jdbc.maximum.connection.count";

	// mongodb Properties
	String MONGODB_URL = "mongodb.url";
	String MONGODB_DATABASE_NAME = "mongodb.dbname";
	String MONGODB_USER_NAME = "mongodb.username";
	String MONGODB_USER_PASSWORD = "mongodb.password";

	String PARSER_PROVIDER = "ixi.server.xmpp.parser-provider";
	String PARTIAL_XML_PARSER = "abs.ixi.util.xml.XmlPartialParser";

	String PROTOCOL_PROVIDER = "ixi.server.xmpp.protocol-provider";

	// TLS config
	String IS_TLS_SUPPORTED = "ixi.server.tls.supported";

	// Chat App configuration
	String FCM_SERVER_KEY = "fcm.server.key";
	String APNS_CERT_FILE_PATH = "apns.cert.file.path";
	String APNS_CERT_PASSPHRASE = "apns.cert.passphrase";
	String APNS_CERT_ENVIRONMENT = "apns.cert.environment";

	// MEDIA Store path
	String MEDIA_STORE_PATH = "media.store.path";

	// Db service config
	String DATABASE = "ixi.server.dbservice.prefered.database";
	String DBSERVICE_PACKET_CACHE_SIZE = "ixi.server.dbservice.packetcache.size";

	// Stream configurations
	String MAX_STREAM_RESUMPTION_TIME_IN_SECONDS = "ixi.server.max.streamResumptionTimeInSeconds";

	// File System properties
	String FILE_SYSTEM_DIR_LENGTH = "ixi.filesystem.dir.length";
	String FILE_SYSTEM_DIR_LEVEL = "ixi.filesystem.dir.level";

}
