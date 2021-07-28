package abs.ixi.server.etc;

public class DatabaseQuery {

	protected static final String SQL_GET_USER_ROSTER = " SELECT " + " ur.contact_jid AS jid, "
			+ " ur.contact_name AS cname, "
			+ " (SELECT count(*) FROM sf_presence_subscription WHERE user_jid = ur.user_jid  and subscriber_jid = ur.contact_jid) AS user_contact_subscription , "
			+ " (SELECT count(*) FROM sf_presence_subscription WHERE user_jid = ur.contact_jid and subscriber_jid = ur.user_jid ) AS contact_user_subscription, "
			+ " ur.item_status, " + " ur.version " + " FROM sf_user_roster ur  " + "  WHERE ur.user_jid = ?  "
			+ " AND ur.version > ? ";

	protected static final String GET_USER_FULL_ROSTER = " SELECT " + " ur.contact_jid AS jid, "
			+ " ur.contact_name AS cname, "
			+ " (SELECT count(*) FROM sf_presence_subscription WHERE user_jid = ur.user_jid  and subscriber_jid = ur.contact_jid) AS user_contact_subscription , "
			+ " (SELECT count(*) FROM sf_presence_subscription WHERE user_jid = ur.contact_jid and subscriber_jid = ur.user_jid ) AS contact_user_subscription, "
			+ " ur.item_status " + " FROM sf_user_roster ur " + " WHERE ur.user_jid = ? ";

	protected static final String SQL_GET_USER_ROSTER_ITEM_NAME = "SELECT contact_name FROM sf_user_roster "
			+ " WHERE user_jid = ? " + " AND contact_jid = ? order by uuid desc limit 1";

	protected static final String SQL_GET_USER_NAME = "SELECT first_name, middle_name, last_name FROM sf_user WHERE jabber_id = ?";

	protected static final String SQL_INSERT_ROSTER_ITEM = " INSERT INTO sf_user_roster (user_jid, contact_jid, contact_name, version, item_status) VALUES (?, ?, ?, ?, ?) ";

	protected static final String SQL_GET_USER_PRESENCE_SUBSCRIPTION = " SELECT subscriber_jid FROM sf_presence_subscription WHERE user_jid = ? ";

	protected static final String SQL_GET_PRESENCE_SUBSCRIPTION = " SELECT "
			+ " (SELECT ur.jabber_id FROM sf_user ur WHERE ur.user_id = ps.subscriber_id) AS userJID, "
			+ "  group_concat(u.jabber_id) AS subscriberJIDs "
			+ "  FROM sf_presence_subscription ps INNER JOIN user u ON ps.subscriber_id = u.user_id "
			+ "  group by userJID ";

	protected static final String SQL_INSERT_USER_PRESENCE_SUBSCRIPTION = " INSERT INTO sf_presence_subscription (user_jid, subscriber_jid) VALUES (?, ?) ";

	protected static final String SQL_CHECK_USER_PRESENCE_SUBSCRIPTION = "SELECT count(*) FROM sf_presence_subscription WHERE user_jid = ? AND subscriber_jid = ?";

	protected static final String SQL_DELETE_USER_PRESENCE_SUBSCRIPTION = " DELETE FROM sf_presence_subscription "
			+ " WHERE user_jid = ? AND subscriber_jid = ? ";

	// protected static final String SQL_INSERT_MESSAGE = " INSERT INTO
	// sf_message (message, sender_id, receiver_id) VALUES (?, ?, ?) ";

	protected static final String SQL_INSERT_MESSAGE = "  INSERT INTO sf_message (message, sender_jid, receiver_jid, create_time) VALUES(?, ?, ?, ?) ";

	protected static final String SQL_INSERT_UNDELIVERED_STANZAS = "INSERT INTO sf_undelivered_stanzas(stanza, receiver_jid) VALUES (?, ?)";

	protected static final String SQL_GET_ALL_UNDELIVERED_STANZAS = "SELECT stanza FROM sf_undelivered_stanzas where receiver_jid = ?";

	protected static final String SQL_DELETE_ALL_UNDELIVERED_STANZAS = "DELETE FROM sf_undelivered_stanzas where receiver_jid = ?";

	protected static final String SQL_GET_USER_ROSTER_VERSION = " SELECT roster_version from sf_user Where jabber_id = ? ";

	protected static final String SQL_UPDATE_USER_ROSTER_VERSION = " UPDATE sf_user SET roster_version = ? "
			+ " WHERE jabber_id = ? ";

	protected static final String SQL_CHECK_IS_ROSTER_ITEM_AVAILABLE = " SELECT count(*) FROM sf_user_roster WHERE user_jid = ? and contact_jid = ? ";

	protected static final String SQL_UPDATE_ROSTER_USER_NAME = " UPDATE sf_user_roster SET contact_name = ? WHERE user_jid = ? AND contact_jid = ? ";

	protected static final String SQL_CHECK_USER_EXIST = " SELECT count(*) FROM sf_user where jabber_id = ? ";

	protected static final String SQL_DELETE_USER = " UPDATE sf_user SET status = 0 WHERE jabber_id = ? ";

	protected static final String SQL_INSERT_SESSION_LOG = " INSERT INTO sf_user_session (user_jid, resource_id) VALUES (?, ?)";

	protected static final String SQL_UPDATE_SESSION_LOGOUT = "UPDATE sf_user_session SET logout_time = ?, status = 0 WHERE user_jid = ? and resource_id = ?";

	protected static final String SQL_CHECK_PLAIN_AUTH = " SELECT count(*) FROM sf_user where jabber_id = ? and password = MD5(?) and status = 1 ";

	protected static final String SQL_CREATE_CHAT_ROOM = " INSERT INTO sf_chat_room (jabber_id, name, subject, access_mode) VALUES (?, ?, ?, ?) ";

	protected static final String SQL_ADD_CHAT_ROOM_MEMBER = " INSERT INTO sf_chat_room_members (room_jid, user_jid, nick_name, affiliation, role) VALUES (?, ?, ?, ?, ?) ";

	protected static final String SQL_UPDATE_CHAT_ROOM_MEMBER = "UPDATE sf_chat_room_members SET affiliation = ?, role = ? WHERE  room_jid = ? AND user_jid = ?";

	protected static final String SQL_DELETE_CHAT_ROOM = " UPDATE sf_chat_room SET status = 0 WHERE jabber_id = ? ";

	protected static final String SQL_DELETE_CHAT_ROOM_MEMBER = " DELETE from sf_chat_room_members WHERE room_jid = ? "
			+ " AND  user_jid = ? ";

	protected static final String SQL_GET_CHAT_ROOM_DETAILS = " SELECT name, subject, access_mode FROM sf_chat_room WHERE jabber_id = ? ";

	protected static final String SQL_GET_CHAT_ROOM_MEMBERS = " SELECT " + " crm.user_jid, "
			+ " crm.nick_name, crm.affiliation, crm.role FROM sf_chat_room_members crm WHERE crm.room_jid = ?";

	protected static final String SQL_GET_CHAT_ROOMS = " SELECT jabber_id, name, subject, access_mode FROM sf_chat_room WHERE status = 1";

	protected static final String SQL_UPDATE_CHAT_ROOM_MEMBER_NICK_NAME = " UPDATE sf_chat_room_members SET nick_name = ? WHERE room_jid = ? "
			+ " AND  user_jid = ? ";

	protected static final String SQL_UPDATE_CHAT_ROOM_SUBJECT = "UPDATE sf_chat_room SET subject = ? WHERE jabber_id = ?";

	protected static final String SQL_UPDATE_ROOM_ACCESS_MODE = "UPDATE sf_chat_room SET access_mode = ? WHERE jabber_id = ?";

	protected static final String SQL_GET_USER_ID = "SELECT user_id FROM sf_user WHERE jabber_id = ?";

	protected static final String SQL_GET_DEVICE_TOKEN_DETAILS = "SELECT DISTINCT notification_service, device_token, device_type FROM sf_user_device "
			+ " WHERE user_jid = ? AND status = 1";

	protected static final String SQL_CHEK_DEVICE_TOKEN_AVAILABLE = "SELECT count(*) FROM sf_user_device "
			+ " WHERE user_jid = ? AND device_id = ?";

	protected static final String SQL_UPDATE_DEVICE_TOKEN = "UPDATE sf_user_device SET device_token = ?, notification_service = ?, device_type = ?, status = 1 "
			+ " WHERE user_jid = ? AND device_id = ? ";

	protected static final String SQL_SAVE_DEVICE_TOKEN = "INSERT INTO sf_user_device (user_jid, device_id, device_token, notification_service, device_type) "
			+ " VALUES(?, ?, ?, ?, ?) ";

	protected static final String SQL_DISCARD_DEVICE_TOKEN = "UPDATE sf_user_device SET status = 0 "
			+ " WHERE user_jid = ? AND device_token = ? ";

	protected static final String SQL_INSERT_UNDELIVERED_STANZA = "INSERT INTO sf_undelivered_stanzas (stanza) VALUES (?)";

	protected static final String SQL_GET_UNDELIVERED_STANZAS = "SELECT stanza FROM sf_undelivered_stanzas";

	protected static final String SQL_TRUNCATE_UNDELIVERED_STANZAS = "TRUNCATE sf_undelivered_stanzas";

	protected static final String SQL_STORE_MEDIA = "INSERT INTO sf_media_store (media_name, sender_jid, receiver_jid) VALUES (?, ?, ?)";

	protected static final String SQL_USER_MEDIA_AVAILABLE = "SELECT count(*) FROM sf_media_store WHERE media_name = ? and receiver_jid = ?";

	protected static final String SQL_GET_MEDIA_RECEIVER = "SELECT receiver_jid FROM sf_media_store WHERE media_name = ?";

	protected static final String SQL_CHECK_USER_JID_EXIST = "SELECT count(*) FROM sf_user WHERE jabber_id = ?";

	protected static final String SQL_REGISTER_NEW_USER = "INSERT INTO sf_user (jabber_id, email, password) VALUES (?, ?, md5(?))";

	protected static final String SQL_GET_USER_REGISTRATION_INFO = " SELECT email FROM sf_user where jabber_id = ? and status = 1";

	protected static final String SQL_UNREGISTER_USER = "UPDATE sf_user SET status = 0 WHERE jabber_id = ?";

	protected static final String SQL_CHANGE_USER_PASSWORD = "UPDATE sf_user SET password = md5(?) WHERE jabber_id = ?";

	protected static final String SQL_GET_USER_PROFILE = "SELECT jabber_id, first_name, middle_name, last_name, nick_name, email, contact_number, gender, bday, "
			+ " avatar_base64_encoded, avatar_mime_type, "
			+ " address_home, address_street, address_locality, address_state, address_city, address_country, address_pcode, description "
			+ " FROM sf_user where jabber_id = ?";

	protected static final String SQL_UPDATE_USER_PROFILE = "UPDATE sf_user "
			+ " SET first_name = ifnull(?, first_name), middle_name = ifnull(?, middle_name), last_name = ifnull(?, last_name), nick_name = ifnull(?, nick_name),"
			+ " email = ifnull(?, email), "
			+ " contact_number = ifnull(?, contact_number), gender = ifnull(?, gender), bday = ifnull(?, bday), "
			+ " avatar_base64_encoded = ifnull(?, avatar_base64_encoded), avatar_mime_type = ifnull(?, avatar_mime_type), "
			+ " address_home = ifnull(?, address_home), address_street = ifnull(?, address_street), address_locality = ifnull(?, address_locality), "
			+ " address_city = ifnull(?, address_city), address_state = ifnull(?, address_state), address_country = ifnull(?, address_country), "
			+ " address_pcode = ifnull(?, address_pcode), "
			+ " description = ifnull(?, description) where jabber_id = ?";

	protected static final String SQL_SEARCHUSER_BY_FIRST_NAME = "SELECT jabber_id, first_name, last_name, nick_name, email FROM sf_user WHERE first_name like ? ";

	protected static final String SQL_SEARCHUSER_BY_LAST_NAME = "SELECT jabber_id, first_name, last_name, nick_name, email FROM sf_user WHERE last_name like ? ";

	protected static final String SQL_SEARCHUSER_BY_NICK_NAME = "SELECT jabber_id, first_name, last_name, nick_name, email FROM sf_user WHERE nick_name like ? ";

	protected static final String SQL_SEARCHUSER_BY_EMAIL = "SELECT jabber_id, first_name, last_name, nick_name, email FROM sf_user WHERE email = ?";
}
