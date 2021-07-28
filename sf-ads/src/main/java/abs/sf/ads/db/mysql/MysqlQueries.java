package abs.sf.ads.db.mysql;

class MysqlQueries {
	public static final String SQL_CREATE_USER = "INSERT INTO sf_user (jabber_id, first_name, email, password) VALUES (?, ?, ?, md5(?))";

	public static final String SQL_UPDATE_USER = "UPDATE sf_user " + "set first_name = ifnull(?,first_name)," + " "
			+ " middle_name = ifnull(?,middle_name)," + " " + " last_name = ifnull(?,last_name)," + " "
			+ " nick_name = ifnull(?,nick_name)," + " " + " contact_number = ifnull(?,contact_number)," + " "
			+ " email = ifnull(?,email)," + " " + " gender = ifnull(?,gender)," + " " + " bday = ifnull(?,bday)," + " "
			+ " avatar_base64_encoded = ifnull(?,avatar_base64_encoded)," + " "
			+ " avatar_base64_encoded = ifnull(?,avatar_base64_encoded)," + " "
			+ " address_home = ifnull(?,address_home)," + " " + " address_street = ifnull(?,address_street)," + " "
			+ " address_locality = ifnull(?,address_locality)," + " " + " address_state = ifnull(?,address_state),"
			+ " " + " address_city = ifnull(?,address_city)," + " " + " address_country = ifnull(?,address_country),"
			+ " " + " address_pcode = ifnull(?,address_pcode)," + " " + " description = ifnull(?, description)" + " "
			+ "where jabber_id = ?";

	public static final String SQL_GET_USER_DETAIL = "Select " + " * " + "from sf_user " + "where jabber_id = ?";

	public static final String SQL_DEACTIVATTE_USER = "UPDATE sf_user " + "set status = 0 " + "where jabber_id = ?";

	public static final String SQL_ACTIVATE_USER = "UPDATE sf_user " + " set status = 1 " + "where jabber_id = ?";

	public static final String SQL_IS_USER_EXIST = "SELECT " + "count(*) " + " FROM sf_user " + " WHERE jabber_id = ?";

	public static final String SQL_CHANGE_PASSWORD = "UPDATE sf_user " + " set password = ? " + " where jabber_id = ?";

	public static final String SQL_GET_PASSWORD = "Select " + "password " + "from sf_user " + "where jabber_id = ?";

	public static final String SQL_CREATE_GROUP = "INSERT INTO sf_chat_room (jabber_id,name,subject,access_mode) VALUES(?, ?, ?, ?)";

	public static final String SQL_UPDATE_GROUP_SUBJECT = "UPDATE sf_chat_room " + " set subject = ifnull(?, subject) "
			+ "where jabber_id =?";

	public static final String SQL_UPDATE_GROUP_ACCESS_MODE = "UPDATE sf_chat_room "
			+ " set access_mode = ifnull(?, access_mode) " + "where jabber_id =?";

	public static final String SQL_ADD_GROUP_MEMBER = "INSERT INTO sf_chat_room_members (room_jid, user_jid, nick_name, affiliation, role) VALUES(?, ?, ?, ?, ?)";

	public static final String SQL_REMOVE_GROUP_MEMBER = "DELETE FROM sf_chat_room_members" + " where room_jid = ? "
			+ " and " + " user_jid = ?";

	public static final String SQL_UPDTAE_GROUP_MEMBER_DETAILS = "UPDATE sf_chat_room_members "
			+ " set nick_name = ifnull(?, nick_name)," + " " + " affiliation = ifnull(?, affiliation)," + " "
			+ " role = ifnull(?, role)," + " " + "where room_jid = ? and user_jid = ?";

	public static final String SQL_DELETE_GROUP = "UPDATE sf_chat_room " + " set status = 0 " + "where jabber_id = ?";

	public static final String SQL_GET_CURRENT_ROSTER_VERSION = "Select " + " roster_version " + " from sf_user "
			+ "where jabber_id = ?";

	public static final String SQL_IS_USER_IN_ROSTER = "SELECT " + "count(*) " + " FROM sf_user_roster "
			+ " WHERE user_jid = ? and contact_jid = ?";

	public static final String SQL_INSERTING_MEMBER_IN_ROSTER = "INSERT INTO sf_user_roster (user_jid, contact_jid,contact_name,version,item_status) VALUES(?, ?, ?, ?, ?)";

	public static final String SQL_UPDATE_USER_ROSTER_VERSION = "UPDATE sf_user " + "set roster_version = ?  "
			+ "where jabber_id = ?";

	public static final String SQL_GET_ROSTER_CONTACT_NAME = "Select " + "contact_name " + "from sf_user_roster "
			+ "where user_jid = ? and contact_jid = ?";

	public static final String SQL_GET_ROSTER_MEMBERS = "Select " + "contact_jid, contact_name, item_status "
			+ "from sf_user_roster " + "where user_jid = ?";

	public static final String SQL_INSERTING_PRESENCE_SUBSCRIBER = "INSERT INTO sf_presence_subscription (user_jid, subscriber_jid) VALUES(?, ?)";

	public static final String SQL_REMOVING_PRESENCE_SUBSCRIBER = "DELETE from sf_presence_subscription "
			+ " where user_jid = ? " + "and " + " subscriber_jid = ?";

	public static final String SQL_IS_ALREADY_SUBSCRIBED_FOR_PRESENCE = "Select " + "count(*) "
			+ "from sf_presence_subscription " + "where user_jid = ? and subscriber_jid = ?";

	protected static final String SQL_GET_USER_NAME = "SELECT first_name, middle_name, last_name FROM sf_user WHERE jabber_id = ?";

	protected static final String SQL_IS_GROUP_EXIST = "Select " + "count(*) " + " from sf_chat_room where "
			+ "jabber_id = ?";

	protected static final String SQL_IS_GROUP_MEMBER = "Select " + "count(*) " + " from sf_chat_room_members where "
			+ "room_jid = ? " + "and " + "user_jid = ?";

}
