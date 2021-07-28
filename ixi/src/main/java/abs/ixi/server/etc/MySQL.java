package abs.ixi.server.etc;

import static abs.ixi.server.etc.DatabaseQuery.GET_USER_FULL_ROSTER;
import static abs.ixi.server.etc.DatabaseQuery.SQL_ADD_CHAT_ROOM_MEMBER;
import static abs.ixi.server.etc.DatabaseQuery.SQL_CHANGE_USER_PASSWORD;
import static abs.ixi.server.etc.DatabaseQuery.SQL_CHECK_IS_ROSTER_ITEM_AVAILABLE;
import static abs.ixi.server.etc.DatabaseQuery.SQL_CHECK_PLAIN_AUTH;
import static abs.ixi.server.etc.DatabaseQuery.SQL_CHECK_USER_JID_EXIST;
import static abs.ixi.server.etc.DatabaseQuery.SQL_CHECK_USER_PRESENCE_SUBSCRIPTION;
import static abs.ixi.server.etc.DatabaseQuery.SQL_CHEK_DEVICE_TOKEN_AVAILABLE;
import static abs.ixi.server.etc.DatabaseQuery.SQL_CREATE_CHAT_ROOM;
import static abs.ixi.server.etc.DatabaseQuery.SQL_DELETE_ALL_UNDELIVERED_STANZAS;
import static abs.ixi.server.etc.DatabaseQuery.SQL_DELETE_CHAT_ROOM;
import static abs.ixi.server.etc.DatabaseQuery.SQL_DELETE_CHAT_ROOM_MEMBER;
import static abs.ixi.server.etc.DatabaseQuery.SQL_DELETE_USER_PRESENCE_SUBSCRIPTION;
import static abs.ixi.server.etc.DatabaseQuery.SQL_DISCARD_DEVICE_TOKEN;
import static abs.ixi.server.etc.DatabaseQuery.SQL_GET_ALL_UNDELIVERED_STANZAS;
import static abs.ixi.server.etc.DatabaseQuery.SQL_GET_CHAT_ROOMS;
import static abs.ixi.server.etc.DatabaseQuery.SQL_GET_CHAT_ROOM_DETAILS;
import static abs.ixi.server.etc.DatabaseQuery.SQL_GET_CHAT_ROOM_MEMBERS;
import static abs.ixi.server.etc.DatabaseQuery.SQL_GET_DEVICE_TOKEN_DETAILS;
import static abs.ixi.server.etc.DatabaseQuery.SQL_GET_MEDIA_RECEIVER;
import static abs.ixi.server.etc.DatabaseQuery.SQL_GET_USER_NAME;
import static abs.ixi.server.etc.DatabaseQuery.SQL_GET_USER_PRESENCE_SUBSCRIPTION;
import static abs.ixi.server.etc.DatabaseQuery.SQL_GET_USER_PROFILE;
import static abs.ixi.server.etc.DatabaseQuery.SQL_GET_USER_REGISTRATION_INFO;
import static abs.ixi.server.etc.DatabaseQuery.SQL_GET_USER_ROSTER;
import static abs.ixi.server.etc.DatabaseQuery.SQL_GET_USER_ROSTER_ITEM_NAME;
import static abs.ixi.server.etc.DatabaseQuery.SQL_GET_USER_ROSTER_VERSION;
import static abs.ixi.server.etc.DatabaseQuery.SQL_INSERT_MESSAGE;
import static abs.ixi.server.etc.DatabaseQuery.SQL_INSERT_ROSTER_ITEM;
import static abs.ixi.server.etc.DatabaseQuery.SQL_INSERT_SESSION_LOG;
import static abs.ixi.server.etc.DatabaseQuery.SQL_INSERT_UNDELIVERED_STANZAS;
import static abs.ixi.server.etc.DatabaseQuery.SQL_INSERT_USER_PRESENCE_SUBSCRIPTION;
import static abs.ixi.server.etc.DatabaseQuery.SQL_REGISTER_NEW_USER;
import static abs.ixi.server.etc.DatabaseQuery.SQL_SAVE_DEVICE_TOKEN;
import static abs.ixi.server.etc.DatabaseQuery.SQL_SEARCHUSER_BY_EMAIL;
import static abs.ixi.server.etc.DatabaseQuery.SQL_SEARCHUSER_BY_FIRST_NAME;
import static abs.ixi.server.etc.DatabaseQuery.SQL_SEARCHUSER_BY_LAST_NAME;
import static abs.ixi.server.etc.DatabaseQuery.SQL_SEARCHUSER_BY_NICK_NAME;
import static abs.ixi.server.etc.DatabaseQuery.SQL_STORE_MEDIA;
import static abs.ixi.server.etc.DatabaseQuery.SQL_UNREGISTER_USER;
import static abs.ixi.server.etc.DatabaseQuery.SQL_UPDATE_CHAT_ROOM_MEMBER;
import static abs.ixi.server.etc.DatabaseQuery.SQL_UPDATE_CHAT_ROOM_MEMBER_NICK_NAME;
import static abs.ixi.server.etc.DatabaseQuery.SQL_UPDATE_CHAT_ROOM_SUBJECT;
import static abs.ixi.server.etc.DatabaseQuery.SQL_UPDATE_DEVICE_TOKEN;
import static abs.ixi.server.etc.DatabaseQuery.SQL_UPDATE_ROOM_ACCESS_MODE;
import static abs.ixi.server.etc.DatabaseQuery.SQL_UPDATE_ROSTER_USER_NAME;
import static abs.ixi.server.etc.DatabaseQuery.SQL_UPDATE_SESSION_LOGOUT;
import static abs.ixi.server.etc.DatabaseQuery.SQL_UPDATE_USER_PROFILE;
import static abs.ixi.server.etc.DatabaseQuery.SQL_UPDATE_USER_ROSTER_VERSION;
import static abs.ixi.server.etc.DatabaseQuery.SQL_USER_MEDIA_AVAILABLE;
import static abs.ixi.server.etc.MySqlHelper.closeConnection;
import static abs.ixi.server.etc.conf.Configurations.Bundle.SYSTEM;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.Stringflow;
import abs.ixi.server.common.InitializationException;
import abs.ixi.server.common.SimpleQueue;
import abs.ixi.server.common.Triplet;
import abs.ixi.server.etc.conf.Configurations;
import abs.ixi.server.etc.conf.SystemConfigAware;
import abs.ixi.server.muc.ChatRoom;
import abs.ixi.server.muc.ChatRoom.AccessMode;
import abs.ixi.server.muc.ChatRoom.Affiliation;
import abs.ixi.server.muc.ChatRoom.ChatRoomMember;
import abs.ixi.server.muc.ChatRoom.Role;
import abs.ixi.server.packet.JID;
import abs.ixi.server.packet.PresenceSubscription;
import abs.ixi.server.packet.Roster;
import abs.ixi.server.packet.Roster.RosterItem;
import abs.ixi.server.packet.xmpp.BareJID;
import abs.ixi.server.packet.xmpp.Message;
import abs.ixi.server.packet.xmpp.Stanza;
import abs.ixi.server.packet.xmpp.UserProfileData;
import abs.ixi.server.packet.xmpp.UserProfileData.Address;
import abs.ixi.server.packet.xmpp.UserProfileData.UserAvtar;
import abs.ixi.server.packet.xmpp.UserRegistrationData;
import abs.ixi.server.packet.xmpp.UserSearchData.Item;
import abs.ixi.util.DateUtils;
import abs.ixi.util.ObjectUtils;
import abs.ixi.util.StringUtils;

public class MySQL implements Database, SystemConfigAware {
	private static final Logger LOGGER = LoggerFactory.getLogger(MySQL.class);

	/**
	 * Validation command to test if the connection to MySQL is alive.
	 */
	private static final String SQL_VALIDATE_CONNECTION = "SELECT 1";

	/**
	 * Datasource instance
	 */
	private BasicDataSource dataSource;

	/**
	 * Flag to indicate if MySQL has been initialized
	 */
	private volatile boolean initialized = false;

	@Override
	public void init() throws InitializationException {
		LOGGER.info("Initilizing MySql Database...");

		try {
			Configurations configurations = Stringflow.runtime().configurations();

			this.dataSource = new BasicDataSource();

			dataSource.setUrl(configurations.get(JDBC_URL, SYSTEM));
			dataSource.setDriverClassName(configurations.get(JDBC_DRIVER_CLASS_NAME, SYSTEM));
			dataSource.setUsername(configurations.get(JDBC_USER_NAME, SYSTEM));
			dataSource.setPassword(configurations.get(JDBC_PASSWORD, SYSTEM));
			dataSource.setInitialSize(Integer.parseInt(configurations.get(JDBC_MINIMUM_CONNECTION_COUNT, SYSTEM)));
			dataSource.setMaxActive(Integer.parseInt(configurations.get(JDBC_MAXIMUM_CONNECTION_COUNT, SYSTEM)));
			dataSource.setTestOnBorrow(true);
			dataSource.setValidationQuery(SQL_VALIDATE_CONNECTION);

			initialized = true;

			LOGGER.info("MySQL persistence has been initialized");

		} catch (Exception e) {
			LOGGER.error("Failed to initilize MySQL DataSource: {}", e.getLocalizedMessage());
			throw new InitializationException(e);
		}
	}

	@Override
	public boolean isInitialized() {
		return this.initialized;
	}

	/**
	 * Borrow a connection from managed datasource. if available, connection
	 * will be returned from pool otherwise a new connection will be created and
	 * returned.
	 * 
	 * @return database connection
	 * @throws DatabaseException
	 */
	private Connection getConnection() throws DatabaseException {
		LOGGER.trace("Borrowing database connection");

		try {
			return this.dataSource.getConnection();

		} catch (SQLException e) {
			LOGGER.error("Error while borrowing database connection", e);
			throw new DatabaseException("Failed to get database connection", e);
		}
	}

	@Override
	public int getRosterVersion(String userJID) {
		LOGGER.debug("Getting user roster version for userJId {}", userJID);

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int version = 0;

		try {

			con = getConnection();
			ps = MySqlHelper.createPreparedStatement(con, SQL_GET_USER_ROSTER_VERSION, new Object[] { userJID });
			rs = ps.executeQuery();

			if (rs.next()) {
				version = rs.getInt(1);
			}

		} catch (Exception e) {
			LOGGER.error("Error while getting roster version for userJID {}", userJID);

		} finally {
			closeConnection(con, ps, rs);
		}

		return version;
	}

	@Override
	public Roster getUserFullRoster(String userJID) {
		LOGGER.debug("Fetching full roster for user jid {}", userJID);

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		Roster roster = new Roster();

		try {

			con = getConnection();
			ps = MySqlHelper.createPreparedStatement(con, GET_USER_FULL_ROSTER, new Object[] { userJID });
			rs = ps.executeQuery();

			while (rs.next()) {

				RosterItem item = roster.new RosterItem(new JID(rs.getString(1)));
				item.setName(rs.getString(2));

				int fromSubscription = rs.getInt(3);
				int toSubscription = rs.getInt(4);

				if (fromSubscription == 1 && toSubscription == 1) {
					item.setSubscription(PresenceSubscription.BOTH);

				} else if (fromSubscription == 1) {
					item.setSubscription(PresenceSubscription.FROM);

				} else if (toSubscription == 1) {
					item.setSubscription(PresenceSubscription.TO);

				} else {
					item.setSubscription(PresenceSubscription.NONE);
				}

				if (rs.getInt(5) == 1) {
					roster.addItem(item);

				} else if (rs.getInt(5) == -1) {
					roster.removeItem(item);

				} else if (rs.getInt(5) == 1) {
					roster.removeItem(item);
					roster.addItem(item);
				}

			}

		} catch (Exception e) {

			LOGGER.error("Error while Getting roster for user jid {}", userJID, e);

		} finally {

			MySqlHelper.closeConnection(con, ps, rs);
		}

		return roster;
	}

	@Override
	public Roster getRoster(String userJID, int lastVersion) {
		LOGGER.debug("Fetching roster for user jid {} and lastversion {}", userJID, lastVersion);

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		Roster roster = new Roster();

		try {

			con = getConnection();
			ps = MySqlHelper.createPreparedStatement(con, SQL_GET_USER_ROSTER, new Object[] { userJID, lastVersion });
			rs = ps.executeQuery();

			while (rs.next()) {

				RosterItem item = roster.new RosterItem(new JID(rs.getString(1)));

				item.setName(rs.getString(2));

				int fromSubscription = rs.getInt(3);
				int toSubscription = rs.getInt(4);

				if (rs.getInt(5) == -1) {
					item.setSubscription(PresenceSubscription.REMOVE);

				} else {

					if (fromSubscription == 1 && toSubscription == 1) {
						item.setSubscription(PresenceSubscription.BOTH);

					} else if (fromSubscription == 1) {
						item.setSubscription(PresenceSubscription.FROM);

					} else if (toSubscription == 1) {
						item.setSubscription(PresenceSubscription.TO);

					} else {
						item.setSubscription(PresenceSubscription.NONE);

					}
				}

				item.setItemVersion(rs.getInt(6));

				roster.addItem(item);
			}

		} catch (Exception e) {

			LOGGER.error("Error while Getting roster for user jid {}", userJID, e);

		} finally {

			MySqlHelper.closeConnection(con, ps, rs);
		}

		return roster;
	}

	@Override
	public void updateRoster(String userJID, String contactJID, String contactName) {
		LOGGER.debug("updating roster item for userJID {} and contectJID {} contactName {}", userJID, contactJID,
				contactName);
		try {
			int version = updateUserRosterVersion(userJID);

			if (isRosterItemAvailable(userJID, contactJID)) {
				updateRosterItem(userJID, contactJID, contactName, version);
				addRosterItem(userJID, userJID, contactName, version, 0);

			} else {

				addRosterItem(userJID, contactJID, contactName, version, 1);

			}

			LOGGER.debug("Roster item is added for userJID {}, contectJID {} and contactName {}", userJID, contactJID,
					contactName);
		} catch (Exception e) {

			LOGGER.error("Error while adding roster item for userJID {}, contectJID {} and contactName {}", userJID,
					contactJID, contactJID);

		}

	}

	private void updateRosterItem(String userJID, String contactJID, String contactName, int version) throws Exception {
		// right now we are only updating user name
		// in future we need to update groups also

		Connection con = null;
		PreparedStatement ps = null;

		try {
			con = getConnection();
			ps = MySqlHelper.createPreparedStatement(con, SQL_UPDATE_ROSTER_USER_NAME,
					new Object[] { contactName, userJID, contactJID });
			ps.executeUpdate();

		} catch (Exception e) {

			LOGGER.error("Error while updating roster item  for userID {}, contectID {}", userJID, contactJID);
			throw new DatabaseException("Failed to update roster item", e);

		} finally {

			MySqlHelper.closeConnection(con, ps);
		}

	}

	private boolean isRosterItemAvailable(String userJID, String contactJID) throws Exception {
		LOGGER.debug("checking roster item is available for userID {} and contectID {} ", userJID, contactJID);

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = getConnection();
			ps = MySqlHelper.createPreparedStatement(con, SQL_CHECK_IS_ROSTER_ITEM_AVAILABLE,
					new Object[] { userJID, contactJID });
			rs = ps.executeQuery();

			rs.next();

			return rs.getInt(1) > 0;

		} catch (Exception e) {

			LOGGER.error("Error while checking roster item is available for userID {}, contectID {}", userJID,
					contactJID);
			throw new DatabaseException("Failed to check roster item is available", e);

		} finally {

			MySqlHelper.closeConnection(con, ps, rs);
		}
	}

	private int updateUserRosterVersion(String userJID) {
		LOGGER.debug("Updating user roster version for userJID {}", userJID);

		Connection con = null;
		PreparedStatement ps = null;

		int lastVersion = getRosterVersion(userJID);
		int currentVersion = ++lastVersion;

		try {

			con = getConnection();
			ps = MySqlHelper.createPreparedStatement(con, SQL_UPDATE_USER_ROSTER_VERSION,
					new Object[] { currentVersion, userJID });
			ps.executeUpdate();

		} catch (Exception e) {

			LOGGER.error("Error while update user roster version for userJID {}", userJID);

		} finally {

			MySqlHelper.closeConnection(con, ps);
		}

		return currentVersion;
	}

	public void addRosterItem(String userJID, String contactJID, String contactName, int version, int itemStatus)
			throws Exception {
		LOGGER.debug("Adding roster item for userJID {}, contactJID {} and contactName {}", userJID, contactJID,
				contactName);

		Connection con = null;
		PreparedStatement ps = null;

		try {

			con = getConnection();
			ps = MySqlHelper.createPreparedStatement(con, SQL_INSERT_ROSTER_ITEM,
					new Object[] { userJID, contactJID, contactName, version, itemStatus });
			ps.executeUpdate();

			LOGGER.debug("Roster item is added for userID {} and contectID {}", userJID, contactJID);

		} catch (Exception e) {

			LOGGER.error("Error while adding roster item for userID {} and contactID {}", userJID, contactJID);
			throw new DatabaseException("Failed to add roster item", e);

		} finally {

			MySqlHelper.closeConnection(con, ps);
		}

	}

	public void deleteRosterItem(int userJID, int contectJID, String name, int version) {
		LOGGER.debug("Deleting user roster item for userJID {} contectJID {}", userJID, contectJID);

		Connection con = null;
		PreparedStatement ps = null;

		try {

			con = getConnection();
			ps = MySqlHelper.createPreparedStatement(con, SQL_INSERT_ROSTER_ITEM,
					new Object[] { userJID, contectJID, name, version, -1 });
			ps.executeUpdate();

			LOGGER.debug("User roster item is deleted for userJID {} and contectJID", userJID, contectJID);

		} catch (Exception e) {

			LOGGER.error("Error while deleting roster item for userJID {} and contectJID {}", userJID, contectJID, e);

		} finally {

			MySqlHelper.closeConnection(con, ps);
		}

	}

	@Override
	public void deleteRosterItem(String userJID, String subscriberJID, String contactName) {
		LOGGER.debug("Adding user presence subscription for userJID {} and subscriberJID {}", userJID, subscriberJID);
		try {

			int version = updateUserRosterVersion(userJID);

			delteRosterItem(userJID, subscriberJID, contactName, version);

			LOGGER.debug("User presence is subscribed for userJID and subscriberJID {}", userJID, subscriberJID);

		} catch (Exception e) {

			LOGGER.error("Error while presence subscription for userJID {} and subscriberJID {}", userJID,
					subscriberJID, e);

		}
	}

	public void delteRosterItem(String userJID, String contectJID, String name, int version) {
		LOGGER.debug("Deleting user roster item for userJID {} contectJID {}", userJID, contectJID);

		Connection con = null;
		PreparedStatement ps = null;

		try {

			con = getConnection();
			ps = MySqlHelper.createPreparedStatement(con, SQL_INSERT_ROSTER_ITEM,
					new Object[] { userJID, contectJID, name, version, -1 });
			ps.executeUpdate();

			LOGGER.debug("User roster item is deleted for userJID {} and contectJID", userJID, contectJID);

		} catch (Exception e) {

			LOGGER.error("Error while deleting roster item for userJID {} and contectJID {}", userJID, contectJID, e);

		} finally {

			MySqlHelper.closeConnection(con, ps);
		}

	}

	public String getUserRosterItemName(String userJID, String contactJID) {
		LOGGER.debug("Getting user roster item name for userJID {} and subscriberJID {}", userJID, contactJID);

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = getConnection();
			ps = MySqlHelper.createPreparedStatement(con, SQL_GET_USER_ROSTER_ITEM_NAME,
					new Object[] { userJID, contactJID });
			rs = ps.executeQuery();

			if (rs.next())
				return rs.getString(1);

		} catch (Exception e) {

			LOGGER.error("Error while getting roster item name for userJID {} and subscriberJID {}", userJID,
					contactJID, e);

		} finally {

			MySqlHelper.closeConnection(con, ps, rs);
		}

		return null;
	}

	@Override
	public List<String> getPresenceSubscribers(String userJID) {
		LOGGER.debug("Getting presence subscribers for userJID {}", userJID);

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		List<String> subscribers = new ArrayList<String>();

		try {

			con = getConnection();
			ps = MySqlHelper.createPreparedStatement(con, SQL_GET_USER_PRESENCE_SUBSCRIPTION, new Object[] { userJID });
			rs = ps.executeQuery();

			while (rs.next()) {
				subscribers.add(rs.getString(1));
			}

		} catch (Exception e) {

			LOGGER.error("Error while getting presence subscription list for userJID {}", userJID, e);

		} finally {

			MySqlHelper.closeConnection(con, ps, rs);
		}

		return subscribers;
	}

	@Override
	public void addPresenceSubscriber(String userJID, String subscriberJID) {
		LOGGER.debug("Adding user presence subscription for userJID {} and subscriberJID {}", userJID, subscriberJID);

		Connection con = null;
		PreparedStatement ps = null;

		try {

			if (!isAlreadySubscribed(userJID, subscriberJID)) {
				con = getConnection();
				ps = MySqlHelper.createPreparedStatement(con, SQL_INSERT_USER_PRESENCE_SUBSCRIPTION,
						new Object[] { userJID, subscriberJID });
				ps.executeUpdate();
			}

		} catch (Exception e) {

			LOGGER.error("Error while presence subscription for userJID {} and subscriberJID {}", userJID,
					subscriberJID, e);

		} finally {

			MySqlHelper.closeConnection(con, ps);
		}

	}

	private boolean isAlreadySubscribed(String userJID, String subscriberJID) throws Exception {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = getConnection();
			ps = MySqlHelper.createPreparedStatement(con, SQL_CHECK_USER_PRESENCE_SUBSCRIPTION,
					new Object[] { userJID, subscriberJID });
			rs = ps.executeQuery();

			return rs.next() && rs.getInt(1) > 0;

		} catch (Exception e) {

			LOGGER.error("Error while presence subscription for userId {} and subscriberJID {}", userJID, subscriberJID,
					e);
			throw new DatabaseException("Failed to subscribe user presence", e);

		} finally {

			MySqlHelper.closeConnection(con, ps);
		}
	}

	@Override
	public void deletePresenceSubscription(String userJID, String subscriberJID) {
		LOGGER.debug("Deleting presence subscription for userJId {} and subscriberJID {}", userJID, subscriberJID);

		Connection con = null;
		PreparedStatement ps = null;

		try {

			con = getConnection();
			ps = MySqlHelper.createPreparedStatement(con, SQL_DELETE_USER_PRESENCE_SUBSCRIPTION,
					new Object[] { userJID, subscriberJID });
			ps.executeUpdate();

			LOGGER.debug("user presence subscription is deleted for userJID {} and subscriberJID {}", userJID,
					subscriberJID);

		} catch (Exception e) {

			LOGGER.debug("Error while Deleting presence subscription for userJId {} and subscriberJID {}", userJID,
					subscriberJID, e);

		} finally {

			MySqlHelper.closeConnection(con, ps);
		}

	}

	@Override
	public void close() {
		LOGGER.info("Closing Database connection pool...");

		try {

			dataSource.close();

		} catch (Exception e) {

			LOGGER.error("Error while Closing Database connection pool", e);
		}

	}

	@Override
	public void persistMessage(Message message) {
		LOGGER.debug("Persisting mesage msg {}", message);
		Connection con = null;
		PreparedStatement ps = null;

		try {
			String from = message.getFrom() == null ? null : message.getFrom().getBareJID().toString();

			con = getConnection();
			ps = MySqlHelper.createPreparedStatement(con, SQL_INSERT_MESSAGE, new Object[] { message.xml(), from,
					message.getDestination().getBareJID().toString(), message.getCreateTime() });

			ps.executeUpdate();

		} catch (Exception e) {

			LOGGER.error("Error while inserting message", e);

		} finally {

			MySqlHelper.closeConnection(con, ps);
		}

	}

	@Override
	public void persistStanzaPacketCache(List<Stanza> stanzas) {
		LOGGER.debug("Persisting stanza packet ache {}", stanzas);

		Connection con = null;
		PreparedStatement ps = null;

		try {
			con = getConnection();
			ps = con.prepareStatement(SQL_INSERT_MESSAGE);

			for (Stanza stanza : stanzas) {

				String from = stanza.getFrom() == null ? null : stanza.getFrom().getBareJID().toString();

				ps.setString(1, stanza.xml());

				ps.setString(2, from);

				if (stanza.getDestination() != null) {
					ps.setString(3, stanza.getDestination().getBareJID().toString());

				} else {
					ps.setString(3, null);
				}

				ps.setTimestamp(4, stanza.getCreateTime());

				ps.addBatch();
			}

			ps.executeBatch();

		} catch (Exception e) {

			LOGGER.error("Error while inserting batch message", e);

		} finally {

			MySqlHelper.closeConnection(con, ps);
		}
	}

	@Override
	public void persistUndeliverdStanza(Stanza stanza) {
		LOGGER.debug("Persisting undelivered stanza stanza {}", stanza.xml());
		Connection con = null;
		PreparedStatement ps = null;

		try {
			con = getConnection();
			ps = MySqlHelper.createPreparedStatement(con, SQL_INSERT_UNDELIVERED_STANZAS,
					new Object[] { stanza, stanza.getTo().getBareJID().toString() });

			ps.executeUpdate();

		} catch (Exception e) {

			LOGGER.error("Error while persisting undelivered stanza", e);

		} finally {

			MySqlHelper.closeConnection(con, ps);
		}

	}

	@Override
	public void persistUndeliverdStanza(SimpleQueue<Stanza> stanzaQ) {
		LOGGER.debug("Persisting undelivered stanza packet Q {}", stanzaQ);

		Connection con = null;
		PreparedStatement ps = null;

		try {
			con = getConnection();
			ps = con.prepareStatement(SQL_INSERT_UNDELIVERED_STANZAS);

			while (stanzaQ.isEmpty()) {
				Stanza stanza = stanzaQ.drop();

				ps.setObject(1, stanza);
				ps.setString(2, stanza.getTo().getBareJID().toString());

				ps.addBatch();
			}

			ps.executeBatch();

		} catch (Exception e) {

			LOGGER.error("Error while inserting batch undelivered stanzas message", e);

		} finally {

			MySqlHelper.closeConnection(con, ps);
		}

	}

	@Override
	public SimpleQueue<Stanza> getAllUndeliverdStanzas(String userJID) {
		LOGGER.debug("Getting All undelivered stanzass for userJID {}", userJID);

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		SimpleQueue<Stanza> undeliveredStanzaQ = new SimpleQueue<>();

		try {

			con = getConnection();
			ps = MySqlHelper.createPreparedStatement(con, SQL_GET_ALL_UNDELIVERED_STANZAS, new Object[] { userJID });
			rs = ps.executeQuery();

			while (rs.next()) {
				Stanza stanza = (Stanza) ObjectUtils.deserialize(rs.getBlob(1).getBinaryStream());
				undeliveredStanzaQ.add(stanza);
			}

		} catch (Exception e) {

			LOGGER.error("Error while getting all undelivered stanzas for userJID {}", userJID, e);

		} finally {

			MySqlHelper.closeConnection(con, ps, rs);
		}

		return undeliveredStanzaQ;
	}

	@Override
	public void deleteAllUndeliverdStanzas(String userJID) {
		LOGGER.debug("Deleting All undelivered stanzass for userJID {}", userJID);

		Connection con = null;
		PreparedStatement ps = null;

		try {
			con = getConnection();
			ps = MySqlHelper.createPreparedStatement(con, SQL_DELETE_ALL_UNDELIVERED_STANZAS, new Object[] { userJID });
			ps.executeUpdate();

		} catch (Exception e) {

			LOGGER.error("Error while deleting all undelivered stanzas for userJID {}", userJID, e);

		} finally {

			MySqlHelper.closeConnection(con, ps);
		}

	}

	@Override
	public UserProfileData getUserProfile(String userJID) {
		LOGGER.debug("Getting user profile data for userJID {}", userJID);

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = getConnection();
			ps = MySqlHelper.createPreparedStatement(con, SQL_GET_USER_PROFILE, new Object[] { userJID });

			rs = ps.executeQuery();

			if (rs.next()) {
				UserProfileData ud = new UserProfileData();

				ud.setJabberId(new BareJID(rs.getString(1)));
				ud.setFirstName(rs.getString(2));
				ud.setMiddleName(rs.getString(3));
				ud.setLastName(rs.getString(4));
				ud.setNickName(rs.getString(5));
				ud.setEmail(rs.getString(6));
				ud.setPhone(rs.getString(7));
				ud.setGender(rs.getString(8));
				ud.setBday(rs.getString(9));

				if (rs.getString(10) != null) {
					UserAvtar avtar = ud.new UserAvtar(rs.getString(10), rs.getString(11));
					ud.setAvtar(avtar);
				}

				String home = rs.getString(12);
				String street = rs.getString(13);
				String locality = rs.getString(14);
				String city = rs.getString(15);
				String state = rs.getString(16);
				String country = rs.getString(17);
				String pcode = rs.getString(18);

				if (home != null || street != null || locality != null || city != null || state != null
						|| country != null || pcode != null) {
					Address address = ud.new Address();
					ud.setAddress(address);

					address.setHome(home);
					address.setStreet(street);
					address.setLocality(locality);
					address.setCity(city);
					address.setState(state);
					address.setCountry(country);
					address.setPcode(pcode);
				}

				ud.setDescription(rs.getString(19));

				return ud;
			}

		} catch (Exception e) {

			LOGGER.error("Error while getting user profile data for userJID {}", userJID, e);

		} finally {

			MySqlHelper.closeConnection(con, ps, rs);
		}

		return null;
	}

	@Override
	public void updateProfile(UserProfileData userProfile) {
		LOGGER.debug("updating user profile data ....");
		Connection con = null;
		PreparedStatement ps = null;

		try {
			con = getConnection();
			ps = MySqlHelper.createPreparedStatement(con, SQL_UPDATE_USER_PROFILE, new Object[] {});

			ps.setString(1, userProfile.getFirstName());
			ps.setString(2, userProfile.getMiddleName());
			ps.setString(3, userProfile.getLastName());
			ps.setString(4, userProfile.getNickName());
			ps.setString(5, userProfile.getEmail());
			ps.setString(6, userProfile.getPhone());
			ps.setString(7, userProfile.getGender());
			ps.setString(8, userProfile.getBday());

			if (userProfile.getAvtar() != null) {
				ps.setString(9, userProfile.getAvtar().getBase64EncodedImage());
				ps.setString(10, userProfile.getAvtar().getImageType());

			} else {
				ps.setString(9, null);
				ps.setString(10, null);
			}

			if (userProfile.getAddress() != null) {
				Address address = userProfile.getAddress();

				ps.setString(11, address.getHome());
				ps.setString(12, address.getStreet());
				ps.setString(13, address.getLocality());
				ps.setString(14, address.getCity());
				ps.setString(15, address.getState());
				ps.setString(16, address.getCountry());
				ps.setString(17, address.getPcode());

			} else {
				ps.setString(11, null);
				ps.setString(12, null);
				ps.setString(13, null);
				ps.setString(14, null);
				ps.setString(15, null);
				ps.setString(16, null);
				ps.setString(17, null);
			}

			ps.setString(18, userProfile.getDescription());
			ps.setString(19, userProfile.getJabberId().toString());

			ps.executeUpdate();

		} catch (Exception e) {

			LOGGER.error("Error while updating user profile", e);

		} finally {

			MySqlHelper.closeConnection(con, ps);
		}

	}

	@Override
	public void persistUserSession(String userJID, String resourceId) {
		LOGGER.debug("persisting user session for userJID {}", userJID);

		Connection con = null;
		PreparedStatement ps = null;

		try {
			con = getConnection();
			ps = MySqlHelper.createPreparedStatement(con, SQL_INSERT_SESSION_LOG, new Object[] { userJID, resourceId });
			ps.executeUpdate();

		} catch (Exception e) {

			LOGGER.error("Error while persisting user session for userJID {}", userJID, e);

		} finally {

			MySqlHelper.closeConnection(con, ps);
		}
	}

	@Override
	public void sessionLogout(String userJID, String resourceId) {
		LOGGER.debug("logout user session for userJID {} and resourceId {}", userJID, resourceId);

		Connection con = null;
		PreparedStatement ps = null;

		try {
			con = getConnection();

			ps = MySqlHelper.createPreparedStatement(con, SQL_UPDATE_SESSION_LOGOUT,
					new Object[] { DateUtils.currentTimestamp(), userJID, resourceId });

			ps.executeUpdate();

		} catch (Exception e) {

			LOGGER.error("Error while updating user session logout for userJID {}", userJID, e);

		} finally {

			MySqlHelper.closeConnection(con, ps);
		}
	}

	@Override
	public void discardDeviceToken(String userJID, String deviceToken) {
		Connection con = null;
		PreparedStatement ps = null;

		try {
			con = getConnection();

			ps = MySqlHelper.createPreparedStatement(con, SQL_DISCARD_DEVICE_TOKEN,
					new Object[] { userJID, deviceToken });

			ps.executeUpdate();

		} catch (Exception e) {

			LOGGER.error("Error while removing push notification token associated with userJID {} and token {}",
					userJID, deviceToken, e);

		} finally {

			MySqlHelper.closeConnection(con, ps);
		}
	}

	@Override
	public boolean authenticate(String bareJID, String password) {
		LOGGER.debug("Authenticating for userJID {}", bareJID);
		boolean result = false;

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = getConnection();
			ps = MySqlHelper.createPreparedStatement(con, SQL_CHECK_PLAIN_AUTH, new Object[] { bareJID, password });
			rs = ps.executeQuery();

			if (rs.next() && rs.getInt(1) > 0)
				result = true;

		} catch (Exception e) {

			LOGGER.error("Error while authenticating userJID {}", bareJID, e);

		} finally {

			MySqlHelper.closeConnection(con, ps, rs);
		}

		return result;
	}

	@Override
	public void addChatRoom(ChatRoom chatRoom) {
		LOGGER.debug("Adding chat room for jid {} and name {}", chatRoom.getRoomJID(), chatRoom.getName());

		Connection con = null;
		PreparedStatement ps = null;

		try {
			con = getConnection();
			ps = MySqlHelper.createPreparedStatement(con, SQL_CREATE_CHAT_ROOM,
					new Object[] { chatRoom.getRoomJID().toString(), chatRoom.getName(), chatRoom.getSubject(),
							chatRoom.getAccessMode().val() });

			ps.executeUpdate();

		} catch (Exception e) {

			LOGGER.error("Error while adding for jid {} and name {}", chatRoom.getRoomJID(), chatRoom.getName(), e);

		} finally {

			MySqlHelper.closeConnection(con, ps);
		}
	}

	@Override
	public void deleteChatRoom(String roomJID) {
		LOGGER.debug("Deleting chat room for jid {} ", roomJID);

		Connection con = null;
		PreparedStatement ps = null;

		try {
			con = getConnection();
			ps = MySqlHelper.createPreparedStatement(con, SQL_DELETE_CHAT_ROOM, new Object[] { roomJID });
			ps.executeUpdate();

		} catch (Exception e) {

			LOGGER.error("Error while deleting chat room for jid {}", roomJID, e);

		} finally {

			MySqlHelper.closeConnection(con, ps);
		}
	}

	@Override
	public void updateChatRoomMember(String roomJID, String memberJID, String affiliation, String role) {
		LOGGER.debug("Updating chat room member for roomJID {} memberJID {} affiliation {} and role {} ", roomJID,
				memberJID, affiliation, role);

		Connection con = null;
		PreparedStatement ps = null;

		try {
			con = getConnection();
			ps = MySqlHelper.createPreparedStatement(con, SQL_UPDATE_CHAT_ROOM_MEMBER,
					new Object[] { affiliation, role, roomJID, memberJID });
			ps.executeUpdate();

		} catch (Exception e) {

			LOGGER.error("Error while updating chat room member for roomJID {} and memberJID {}", roomJID, memberJID,
					e);

		} finally {

			MySqlHelper.closeConnection(con, ps);
		}

	}

	@Override
	public void addChatRoomMember(String roomJID, String memberJID, String nickName, String affiliation, String role) {
		LOGGER.debug("Adding chat room member for roomJID {} memberJID {} nickName {} affiliation {} and role {} ",
				roomJID, memberJID, nickName, affiliation, role);

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = getConnection();
			ps = MySqlHelper.createPreparedStatement(con, SQL_ADD_CHAT_ROOM_MEMBER,
					new Object[] { roomJID, memberJID, nickName, affiliation, role });
			ps.executeUpdate();

		} catch (Exception e) {

			LOGGER.error("Error while deleting chat room member for roomJID {} and memberJID {}", roomJID, memberJID,
					e);

		} finally {

			MySqlHelper.closeConnection(con, ps, rs);
		}
	}

	@Override
	public void removeChatRoomMember(String roomJID, String memberJID) {
		LOGGER.debug("removing chat room member for roomJID {} and memberJID {} ", roomJID, memberJID);

		Connection con = null;
		PreparedStatement ps = null;

		try {
			con = getConnection();
			ps = MySqlHelper.createPreparedStatement(con, SQL_DELETE_CHAT_ROOM_MEMBER,
					new Object[] { roomJID, memberJID });
			ps.executeUpdate();

		} catch (Exception e) {

			LOGGER.error("Error while removing chat room member for roomJID {} and memberJID {}", roomJID, memberJID,
					e);

		} finally {

			MySqlHelper.closeConnection(con, ps);
		}

	}

	@Override
	public ChatRoom getChatRoomDetails(String roomJID) {
		LOGGER.debug("Getting chat room details roomJID {}", roomJID);

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = getConnection();
			ps = MySqlHelper.createPreparedStatement(con, SQL_GET_CHAT_ROOM_DETAILS, new Object[] { roomJID });
			rs = ps.executeQuery();

			ChatRoom room = null;
			if (rs.next()) {
				room = new ChatRoom(new BareJID(roomJID), rs.getString(1), rs.getString(2),
						AccessMode.valueFrom(rs.getString(3)));
			}

			if (room != null) {
				addChatRoomMembers(room);
			}

		} catch (Exception e) {

			LOGGER.error("Error while presence subscription for userJID {} and subscriberJID {}", e);

		} finally {

			MySqlHelper.closeConnection(con, ps, rs);
		}

		return null;
	}

	private void addChatRoomMembers(ChatRoom room) {
		LOGGER.debug("Adding members to chat room for roomId {}", room.getRoomJID());

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = getConnection();
			ps = MySqlHelper.createPreparedStatement(con, SQL_GET_CHAT_ROOM_MEMBERS,
					new Object[] { room.getRoomJID().toString() });
			rs = ps.executeQuery();

			Set<ChatRoomMember> members = new HashSet<>();

			while (rs.next()) {
				ChatRoomMember member = room.new ChatRoomMember(new JID(rs.getString(1)), rs.getString(2),
						Affiliation.valueFrom(rs.getString(3)), Role.valueFrom(rs.getString(4)));

				members.add(member);
			}

			room.setMembers(members);

		} catch (Exception e) {

			LOGGER.error("Error while adding chat room member  for roomJID {}", room.getRoomJID(), e);

		} finally {

			MySqlHelper.closeConnection(con, ps, rs);
		}

	}

	@Override
	public Map<BareJID, ChatRoom> getChatRooms() {
		LOGGER.debug("Getting chat rooms...");

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		Map<BareJID, ChatRoom> rooms = new HashMap<>();

		try {
			con = getConnection();
			ps = MySqlHelper.createPreparedStatement(con, SQL_GET_CHAT_ROOMS);
			rs = ps.executeQuery();

			while (rs.next()) {

				ChatRoom room = new ChatRoom(new BareJID(rs.getString(1)), rs.getString(2), rs.getString(3),
						AccessMode.valueFrom(rs.getString(4)));

				addChatRoomMembers(room);

				rooms.put(room.getRoomJID(), room);
			}

		} catch (Exception e) {

			LOGGER.error("Error while presence subscription for userJID {} and subscriberJID {}", e);

		} finally {

			MySqlHelper.closeConnection(con, ps, rs);
		}

		return rooms;
	}

	@Override
	public void updateNickName(String roomJID, String memberJID, String nickName) {
		LOGGER.debug("updating nick name chat room member for roomJID {} and memberJID {} ", roomJID, memberJID);

		Connection con = null;
		PreparedStatement ps = null;

		try {
			con = getConnection();
			ps = MySqlHelper.createPreparedStatement(con, SQL_UPDATE_CHAT_ROOM_MEMBER_NICK_NAME,
					new Object[] { nickName, roomJID, memberJID });
			ps.executeUpdate();

		} catch (Exception e) {

			LOGGER.error("Error while updating nickname for roomJID {} and memberJID {}", roomJID, memberJID, e);

		} finally {

			MySqlHelper.closeConnection(con, ps);
		}

	}

	@Override
	public void updateRoomSubject(String roomJID, String subject) {
		LOGGER.debug("updating chat room subject for roomJID {} and subject {} ", roomJID, subject);

		Connection con = null;
		PreparedStatement ps = null;

		try {
			con = getConnection();
			ps = MySqlHelper.createPreparedStatement(con, SQL_UPDATE_CHAT_ROOM_SUBJECT,
					new Object[] { subject, roomJID });
			ps.executeUpdate();

		} catch (Exception e) {

			LOGGER.error("Error while updating room subject for roomJID {} and memberJID {}", roomJID, subject, e);

		} finally {

			MySqlHelper.closeConnection(con, ps);
		}

	}

	@Override
	public void updateDeviceToken(String bareJID, String deviceId, String deviceToken, String notificationService,
			String deviceType) {
		boolean isTokenAvailable = isDeviceTokenAvailable(bareJID, deviceId);

		if (isTokenAvailable) {
			updateToken(bareJID, deviceId, deviceToken, notificationService, deviceType);

		} else {
			insertToken(bareJID, deviceId, deviceToken, notificationService, deviceType);
		}
	}

	private void insertToken(String bareJID, String deviceId, String deviceToken, String notificationService,
			String deviceType) {
		LOGGER.debug("insert device token for userJID {}", bareJID);

		Connection con = null;
		PreparedStatement ps = null;

		try {
			con = getConnection();
			ps = MySqlHelper.createPreparedStatement(con, SQL_SAVE_DEVICE_TOKEN,
					new Object[] { bareJID, deviceId, deviceToken, notificationService, deviceType });
			ps.executeUpdate();

		} catch (Exception e) {

			LOGGER.error("Error while updating user device token for userJID {}", bareJID, e);

		} finally {

			MySqlHelper.closeConnection(con, ps);
		}

	}

	public void updateToken(String bareJID, String deviceId, String deviceToken, String notificationService,
			String deviceType) {
		LOGGER.debug("updating device token for userJID {}", bareJID);

		Connection con = null;
		PreparedStatement ps = null;

		try {
			con = getConnection();
			ps = MySqlHelper.createPreparedStatement(con, SQL_UPDATE_DEVICE_TOKEN,
					new Object[] { deviceToken, notificationService, deviceType, bareJID, deviceId });
			ps.executeUpdate();

		} catch (Exception e) {

			LOGGER.error("Error while updating user device token for userJID {}", bareJID, e);

		} finally {

			MySqlHelper.closeConnection(con, ps);
		}
	}

	private boolean isDeviceTokenAvailable(String bareJID, String deviceId) {
		LOGGER.debug("chacking device token availablity for userJID {}, resourceId {}", bareJID, deviceId);

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = getConnection();
			ps = MySqlHelper.createPreparedStatement(con, SQL_CHEK_DEVICE_TOKEN_AVAILABLE,
					new Object[] { bareJID, deviceId });
			rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getInt(1) > 0;
			}

		} catch (Exception e) {
			LOGGER.error("Error while updating user device tocken for userJID {}", bareJID, e);

		} finally {
			closeConnection(con, ps);
		}
		
		return false;
	}

	@Override
	public List<Triplet<String, String, String>> fetchDeviceTokens(String bareJID) throws DatabaseException {
		LOGGER.debug("Fetching device tokens for {}", bareJID);

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = getConnection();
			ps = MySqlHelper.createPreparedStatement(con, SQL_GET_DEVICE_TOKEN_DETAILS, new Object[] { bareJID });
			rs = ps.executeQuery();

			List<Triplet<String, String, String>> tokens = new ArrayList<>();

			while (rs.next()) {
				tokens.add(new Triplet<String, String, String>(rs.getString(1), rs.getString(2), rs.getString(3)));
			}

			return tokens;

		} catch (Exception e) {
			throw new DatabaseException(e);

		} finally {
			closeConnection(con, ps, rs);
		}

	}

	@Override
	public boolean storeMedia(String mediaName, String senderJID, String receiverJID) {
		LOGGER.debug("Storing media for mediaName {} and fromJID {} and toJID {}", mediaName, senderJID, receiverJID);

		Connection con = null;
		PreparedStatement ps = null;

		try {
			con = getConnection();
			ps = MySqlHelper.createPreparedStatement(con, SQL_STORE_MEDIA,
					new Object[] { mediaName, senderJID, receiverJID });
			ps.executeUpdate();

			return true;
		} catch (Exception e) {
			LOGGER.error("Error while Storing media for mediaName {} and fromJID {} and toJID {}", mediaName, senderJID,
					receiverJID, e);

		} finally {
			closeConnection(con, ps);
		}

		return false;
	}

	@Override
	public boolean authenticateUserMediaAccess(String mediaName, String userBareJID) {
		LOGGER.debug("authenticating user media Access for media name{} and user bare jid {}", mediaName, userBareJID);

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = getConnection();
			ps = MySqlHelper.createPreparedStatement(con, SQL_USER_MEDIA_AVAILABLE,
					new Object[] { mediaName, userBareJID });

			rs = ps.executeQuery();

			if (rs.next() && rs.getInt(1) > 0)
				return true;

		} catch (Exception e) {

			LOGGER.error("authenticating user media Access for media name{} and user bare jid {}", mediaName,
					userBareJID, e);

		} finally {

			MySqlHelper.closeConnection(con, ps, rs);
		}

		return false;
	}

	@Override
	public void updateRoomAccessMode(ChatRoom room) {
		Connection con = null;
		PreparedStatement ps = null;

		try {
			con = getConnection();

			ps = MySqlHelper.createPreparedStatement(con, SQL_UPDATE_ROOM_ACCESS_MODE,
					new Object[] { room.getAccessMode().val(), room.getRoomJID().toString() });

			ps.executeUpdate();

		} catch (Exception e) {
			LOGGER.error("Failed to update room access mode", e);

		} finally {

			MySqlHelper.closeConnection(con, ps);
		}
	}

	@Override
	public String getUserName(String userJID) {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = getConnection();

			ps = MySqlHelper.createPreparedStatement(con, SQL_GET_USER_NAME, new Object[] { userJID });

			rs = ps.executeQuery();

			if (rs.next()) {
				StringBuilder sb = new StringBuilder();
				if (!StringUtils.isNullOrEmpty(rs.getString(1))) {
					sb.append(rs.getString(1));
				}

				if (!StringUtils.isNullOrEmpty(rs.getString(2))) {
					sb.append(" ").append(rs.getString(2));
				}

				if (!StringUtils.isNullOrEmpty(rs.getString(3))) {
					sb.append(" ").append(rs.getString(3));
				}

				return sb.toString();
			}

		} catch (Exception e) {
			LOGGER.error("Failed to get user name from {}", userJID, e);

		} finally {

			MySqlHelper.closeConnection(con, ps, rs);
		}

		return null;
	}

	@Override
	public String getMediaReceiver(String mediaId) {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = getConnection();

			ps = MySqlHelper.createPreparedStatement(con, SQL_GET_MEDIA_RECEIVER, new Object[] { mediaId });

			rs = ps.executeQuery();

			if (rs.next())
				return rs.getString(1);

		} catch (Exception e) {
			LOGGER.error("Failed to get media receiver{} for mediaId", mediaId, e);

		} finally {

			MySqlHelper.closeConnection(con, ps, rs);
		}

		return null;
	}

	@Override
	public boolean isUserExist(String userJID) {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = getConnection();

			ps = MySqlHelper.createPreparedStatement(con, SQL_CHECK_USER_JID_EXIST, new Object[] { userJID });

			rs = ps.executeQuery();

			if (rs.next())
				return rs.getInt(1) > 0;

		} catch (Exception e) {
			LOGGER.error("Failed to check user JID {} exist or not", userJID, e);

		} finally {

			MySqlHelper.closeConnection(con, ps, rs);
		}

		return false;
	}

	@Override
	public void registerNewUser(String userJID, String password, String email) {
		LOGGER.debug("Registering an new user with userName {} and email {}", userJID, email);

		Connection con = null;
		PreparedStatement ps = null;

		try {
			con = getConnection();

			ps = MySqlHelper.createPreparedStatement(con, SQL_REGISTER_NEW_USER,
					new Object[] { userJID, email, password });

			ps.executeUpdate();

		} catch (Exception e) {
			LOGGER.error("Failed to un register a user with user name {} and email {}", userJID, email, e);

		} finally {

			MySqlHelper.closeConnection(con, ps);
		}

	}

	@Override
	public UserRegistrationData getUserRegistrationInfo(String userJID) {
		LOGGER.debug("Getting user registration info for userJID {}", userJID);

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = getConnection();
			ps = MySqlHelper.createPreparedStatement(con, SQL_GET_USER_REGISTRATION_INFO, new Object[] { userJID });
			rs = ps.executeQuery();

			if (rs.next()) {
				UserRegistrationData ur = new UserRegistrationData();
				ur.setEmail(rs.getString(1));

				return ur;
			}

		} catch (Exception e) {

			LOGGER.error("Error while getting user profile for userJID {}", userJID, e);

		} finally {

			MySqlHelper.closeConnection(con, ps, rs);
		}

		return null;
	}

	@Override
	public void unRegisterUser(String userJID) {
		LOGGER.debug("Unregistering a user {}", userJID);
		Connection con = null;
		PreparedStatement ps = null;

		try {
			con = getConnection();

			ps = MySqlHelper.createPreparedStatement(con, SQL_UNREGISTER_USER, new Object[] { userJID });

			ps.executeUpdate();

		} catch (Exception e) {
			LOGGER.error("Failed to un registering a user {}", userJID, e);

		} finally {

			MySqlHelper.closeConnection(con, ps);
		}

	}

	@Override
	public void changeUserPassword(String userJID, String password) {
		LOGGER.debug("Changeing password for a  user {}", userJID);
		Connection con = null;
		PreparedStatement ps = null;

		try {
			con = getConnection();

			ps = MySqlHelper.createPreparedStatement(con, SQL_CHANGE_USER_PASSWORD, new Object[] { password, userJID });

			ps.executeUpdate();

		} catch (Exception e) {
			LOGGER.error("Failed to change password for user {}", userJID, e);

		} finally {

			MySqlHelper.closeConnection(con, ps);
		}
	}

	@Override
	public List<Item> serchUserByFirstName(String firstName) {
		LOGGER.debug("Searching user by first name : {}", firstName);

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = getConnection();

			ps = con.prepareStatement(SQL_SEARCHUSER_BY_FIRST_NAME);
			ps.setString(1, MySqlHelper.prepareLikeClause(firstName, MySqlHelper.MatchType.BOTH));

			rs = ps.executeQuery();

			List<Item> items = new ArrayList<>();

			if (rs.next()) {
				Item item = new Item();

				item.setUserJID(new BareJID(rs.getString(1)));
				item.setFirstName(rs.getString(2));
				item.setLastName(rs.getString(3));
				item.setNickName(rs.getString(4));
				item.setEmail(rs.getString(5));

				items.add(item);

			}

			return items;

		} catch (Exception e) {

			LOGGER.error("Error while searching user by first name {}", firstName, e);

		} finally {

			MySqlHelper.closeConnection(con, ps, rs);
		}

		return null;
	}

	@Override
	public List<Item> serchUserByLastName(String lastName) {
		LOGGER.debug("Searching user by last name : {}", lastName);

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = getConnection();

			ps = con.prepareStatement(SQL_SEARCHUSER_BY_LAST_NAME);
			ps.setString(1, MySqlHelper.prepareLikeClause(lastName, MySqlHelper.MatchType.BOTH));

			rs = ps.executeQuery();

			List<Item> items = new ArrayList<>();

			if (rs.next()) {
				Item item = new Item();

				item.setUserJID(new BareJID(rs.getString(1)));
				item.setFirstName(rs.getString(2));
				item.setLastName(rs.getString(3));
				item.setNickName(rs.getString(4));
				item.setEmail(rs.getString(5));

				items.add(item);

			}

			return items;

		} catch (Exception e) {

			LOGGER.error("Error while searching user by last name {}", lastName, e);

		} finally {

			MySqlHelper.closeConnection(con, ps, rs);
		}

		return null;
	}

	@Override
	public List<Item> serchUserByNickName(String nickName) {
		LOGGER.debug("Searching user by nick name : {}", nickName);

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = getConnection();

			ps = con.prepareStatement(SQL_SEARCHUSER_BY_NICK_NAME);
			ps.setString(1, MySqlHelper.prepareLikeClause(nickName, MySqlHelper.MatchType.BOTH));

			rs = ps.executeQuery();

			List<Item> items = new ArrayList<>();

			if (rs.next()) {
				Item item = new Item();

				item.setUserJID(new BareJID(rs.getString(1)));
				item.setFirstName(rs.getString(2));
				item.setLastName(rs.getString(3));
				item.setNickName(rs.getString(4));
				item.setEmail(rs.getString(5));

				items.add(item);

			}

			return items;

		} catch (Exception e) {

			LOGGER.error("Error while searching user by nick name {}", nickName, e);

		} finally {

			MySqlHelper.closeConnection(con, ps, rs);
		}

		return null;
	}

	@Override
	public List<Item> serchUserByEmail(String email) {
		LOGGER.debug("Searching user by email : {}", email);

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = getConnection();
			ps = MySqlHelper.createPreparedStatement(con, SQL_SEARCHUSER_BY_EMAIL, new Object[] { email });
			rs = ps.executeQuery();

			List<Item> items = new ArrayList<>();

			if (rs.next()) {
				Item item = new Item();

				item.setUserJID(new BareJID(rs.getString(1)));
				item.setFirstName(rs.getString(2));
				item.setLastName(rs.getString(3));
				item.setNickName(rs.getString(4));
				item.setEmail(rs.getString(5));

				items.add(item);

			}

			return items;

		} catch (Exception e) {

			LOGGER.error("Error while searching user by email {}", email, e);

		} finally {

			MySqlHelper.closeConnection(con, ps, rs);
		}

		return null;
	}

}
