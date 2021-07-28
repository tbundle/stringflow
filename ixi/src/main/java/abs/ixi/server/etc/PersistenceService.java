package abs.ixi.server.etc;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.httpclient.util.ObjectUtils;
import abs.ixi.server.Stringflow;
import abs.ixi.server.common.SimpleQueue;
import abs.ixi.server.common.TaskRunner;
import abs.ixi.server.common.Triplet;
import abs.ixi.server.etc.conf.Configurations.Bundle;
import abs.ixi.server.etc.conf.SystemConfigAware;
import abs.ixi.server.muc.ChatRoom;
import abs.ixi.server.muc.ChatRoom.Affiliation;
import abs.ixi.server.muc.ChatRoom.Role;
import abs.ixi.server.packet.InvalidJabberId;
import abs.ixi.server.packet.Roster;
import abs.ixi.server.packet.xmpp.BareJID;
import abs.ixi.server.packet.xmpp.Stanza;
import abs.ixi.server.packet.xmpp.UserProfileData;
import abs.ixi.server.packet.xmpp.UserRegistrationData;
import abs.ixi.server.packet.xmpp.UserSearchData.Item;

/**
 * An implementation of {@link DbService}. As server supports many databases,
 * {@link DbService} abstracts database operations.
 * <p>
 * At the time of instantiation, {@link DbService} gets the {@link Database}
 * object created based on the configurations.
 * </p>
 * <p>
 * {@code DbServiceImpl} maintains an in-memory cache to reduce the number of
 * database operations; therefore any query executed by {@link DbService} is
 * likely to probe in-memory cache as well to maintain data integrity.
 * </p>
 * <p>
 * {@code DbServiceImpl} is a singleton by design so there can be just one
 * instance in server runtime.
 * </p>
 */
public final class PersistenceService implements SystemConfigAware {
	private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceService.class);

	private static final String ENTITY_NAME = "DbService";

	/**
	 * Default capacity of the ephemeral store
	 */
	public static final int EPHEMERAL_STORE_CAPACITY = 1000;

	/**
	 * Database instance
	 */
	private Database database;

	/**
	 * A list which stores packets flowing through the server. Periodically
	 * these packets are stored into persistent storage (database).
	 */
	private List<Stanza> ephemeralStore;

	private int packetCacheSize;

	private static PersistenceService instance;

	/**
	 * Private Constructor to avoid direct instantiation
	 * 
	 * @throws InstantiationException
	 */
	private PersistenceService() throws abs.ixi.server.common.InstantiationException {
		try {
			String valString = Stringflow.runtime().configurations().get(DBSERVICE_PACKET_CACHE_SIZE, Bundle.SYSTEM);

			int val = ObjectUtils.parseToInt(valString);
			this.packetCacheSize = val <= 0 ? EPHEMERAL_STORE_CAPACITY : val;
			this.ephemeralStore = new ArrayList<>(this.packetCacheSize);

			Class<?> clz = Class.forName(Stringflow.runtime().configurations().get(DATABASE, Bundle.SYSTEM));

			if (Database.class.isAssignableFrom(clz)) {
				this.database = (Database) clz.newInstance();
				LOGGER.info("loaded database {}", clz.getCanonicalName());

			} else {
				LOGGER.warn("{} is not a valid Database", clz.getCanonicalName());
				throw new InstantiationException(clz.getCanonicalName() + " is not a valid Database");
			}

			this.database.init();

		} catch (Exception e) {
			LOGGER.error("Failed to load DbService", e);
			throw new abs.ixi.server.common.InstantiationException(e);
		}
	}

	public static PersistenceService getInstance() throws abs.ixi.server.common.InstantiationException {
		if (instance == null) {
			synchronized (PersistenceService.class) {
				if (instance == null) {
					instance = new PersistenceService();
				}
			}
		}

		return instance;
	}

	/**
	 * Fetch device tokens for a given {@link BareJID}.
	 * 
	 * @param bareJID
	 * @return
	 * @throws DatabaseException
	 */
	public List<Triplet<String, String, String>> getDeviceTokens(BareJID bareJID) throws DatabaseException {
		requireNonNull(bareJID);
		return this.getDeviceTokens(bareJID.toString());
	}

	public List<Triplet<String, String, String>> getDeviceTokens(String bareJID) throws DatabaseException {
		requireNonNull(bareJID);
		return this.database.fetchDeviceTokens(bareJID);
	}

	public void persistStanzaPacket(Stanza stanza) {
		LOGGER.info("persisting message {} to cache", stanza.xml());

		ephemeralStore.add(stanza);

		if (ephemeralStore.size() >= packetCacheSize) {
			persistStanzaPacketCache();
		}
	}

	private void persistStanzaPacketCache() {
		TaskRunner.getInstance().execute(new Runnable() {

			@Override
			public void run() {
				database.persistStanzaPacketCache(ephemeralStore);
				ephemeralStore.removeAll(ephemeralStore);
			}
		});
	}

	public void persistUndeliverdStanza(Stanza stanza) {
		LOGGER.debug("Persisting {} to db for {} ", stanza.xml(), stanza.getDestination().getBareJID());
		this.database.persistUndeliverdStanza(stanza);

	}

	public void persistUndeliverdStanzas(SimpleQueue<Stanza> stanzaQ) {
		this.database.persistUndeliverdStanza(stanzaQ);
	}

	public SimpleQueue<Stanza> getUndeliveredStanzas(BareJID userJID) {
		SimpleQueue<Stanza> undeliveredStanzaQ = this.database.getAllUndeliverdStanzas(userJID.toString());

		if (!undeliveredStanzaQ.isEmpty()) {
			this.database.deleteAllUndeliverdStanzas(userJID.toString());
		}

		return undeliveredStanzaQ;
	}

	public int getRosterVersion(BareJID userJID) {
		return this.database.getRosterVersion(userJID.toString());
	}

	public Roster getUserFullRoster(BareJID userJID) {
		return this.database.getUserFullRoster(userJID.toString());
	}

	public Roster getRoster(BareJID userJID, int lastVersion) {
		return this.database.getRoster(userJID.toString(), lastVersion);
	}

	public void updateRoster(BareJID userJID, BareJID contactJID, String contactName) {
		this.database.updateRoster(userJID.toString(), contactJID.toString(), contactName);
	}

	public void deleteRosterItem(BareJID userJID, BareJID contactJID, String contactName) {
		this.database.deleteRosterItem(userJID.toString(), contactJID.toString(), contactName);
	}

	public List<BareJID> getPresenceSubscribers(BareJID userBareJid) {
		List<String> subscribers = this.database.getPresenceSubscribers(userBareJid.toString());
		List<BareJID> subscribersBareJIDs = new ArrayList<>();

		subscribers.forEach((v) -> {
			try {

				subscribersBareJIDs.add(new BareJID(v));

			} catch (InvalidJabberId e) {
				LOGGER.warn("Invalid JID {}", v);
				// Swallow Exception
			}
		});

		return subscribersBareJIDs;
	}

	public void addPresenceSubscriber(BareJID userJID, BareJID subscriberJID) {
		this.database.addPresenceSubscriber(userJID.toString(), subscriberJID.toString());
	}

	public void deletePresenceSubscription(BareJID userJID, BareJID subscriberJID) {
		this.database.deletePresenceSubscription(userJID.toString(), subscriberJID.toString());
	}

	public void updateUserProfile(UserProfileData userProfile) {
		this.database.updateProfile(userProfile);
	}

	public UserProfileData getUserProfile(BareJID userJID) {
		return this.database.getUserProfile(userJID.toString());
	}

	public void persistUserSession(BareJID bareJID, String resourceId) {
		this.database.persistUserSession(bareJID.toString(), resourceId);
	}

	public void sessionLogout(BareJID bareJID, String resourceId) {
		this.database.sessionLogout(bareJID.toString(), resourceId);
	}

	public boolean authenticate(BareJID bareJid, String password) {
		return this.database.authenticate(bareJid.toString(), password);
	}

	public void addChatRoom(ChatRoom chatRoom) {
		this.database.addChatRoom(chatRoom);
	}

	public void deleteChatRoom(BareJID roomJID) {
		this.database.deleteChatRoom(roomJID.toString());
	}

	public void addChatRoomMember(BareJID roomJId, BareJID memberJID, String nickName, Affiliation affiliation,
			Role role) {
		this.database.addChatRoomMember(roomJId.toString(), memberJID.toString(), nickName, affiliation.val(),
				role.val());
	}

	public void updateRoomMemberDetails(BareJID roomJID, BareJID memberJID, Affiliation affiliation, Role role) {
		this.database.updateChatRoomMember(roomJID.toString(), memberJID.toString(), affiliation.val(), role.val());
	}

	public void removeChatRoomMember(BareJID roomJId, BareJID memberJID) {
		this.database.removeChatRoomMember(roomJId.toString(), memberJID.toString());
	}

	public ChatRoom getChatRoomDetails(String roomJID) {
		return this.database.getChatRoomDetails(roomJID);
	}

	public Map<BareJID, ChatRoom> getChatRooms() {
		return this.database.getChatRooms();
	}

	public void updateNickName(BareJID roomJID, BareJID memberJID, String nickName) {
		this.database.updateNickName(roomJID.toString(), memberJID.toString(), nickName);
	}

	public void updateRoomSubject(BareJID roomJID, String subject) {
		this.database.updateRoomSubject(roomJID.toString(), subject);
	}

	public void updateRoomAccessMode(ChatRoom room) {
		this.database.updateRoomAccessMode(room);
	}

	public void updateDeviceTocken(BareJID bareJID, String resourceId, String deviceTocken, String notificationService,
			String deviceType) {
		this.database.updateDeviceToken(bareJID.toString(), resourceId, deviceTocken, notificationService, deviceType);
	}

	public void discardDeviceToken(BareJID bareJID, String deviceToken) {
		this.database.discardDeviceToken(bareJID.toString(), deviceToken);
	}

	public boolean storeMedia(String mediaName, BareJID senderBareJID, BareJID receiverBareJID) {
		return this.database.storeMedia(mediaName, senderBareJID.toString(), receiverBareJID.toString());
	}

	public boolean authenticateUserMediaAccess(String mediaName, BareJID bareJID) {
		return this.database.authenticateUserMediaAccess(mediaName, bareJID.toString());
	}

	public BareJID getMediaReceiverJID(String mediaId) {
		String receiver = this.database.getMediaReceiver(mediaId);

		if (receiver != null) {
			try {

				return new BareJID(receiver);

			} catch (InvalidJabberId e) {
				// swallow exception
			}
		}

		return null;
	}

	public String getUserName(BareJID userJID) {
		return this.database.getUserName(userJID.toString());
	}

	public String getUserRosterItemName(BareJID userJID, BareJID contactJID) {
		return this.database.getUserRosterItemName(userJID.toString(), contactJID.toString());
	}

	public boolean isUserExist(BareJID userJID) {
		return this.database.isUserExist(userJID.toString());
	}

	public void registerNewUser(BareJID userJID, String password, String email) {
		this.database.registerNewUser(userJID.toString(), password, email);
	}

	public UserRegistrationData getUserRegistrationInfo(BareJID userJID) {
		return this.database.getUserRegistrationInfo(userJID.toString());
	}

	public void unRegisterUser(BareJID userJID) {
		this.database.unRegisterUser(userJID.toString());
	}

	public void changeUserPassword(BareJID userJID, String password) {
		this.database.changeUserPassword(userJID.toString(), password);
	}

	public List<Item> serchUserByFirstName(String firstName) {
		return this.database.serchUserByFirstName(firstName);
	}

	public List<Item> serchUserByLastName(String lastName) {
		return this.database.serchUserByLastName(lastName);
	}

	public List<Item> serchUserByNickName(String nickName) {
		return this.database.serchUserByNickName(nickName);
	}

	public List<Item> serchUserByEmail(String email) {
		return this.database.serchUserByEmail(email);
	}

	public void shutdown() {
		LOGGER.warn("Shutting down Db Service");
		saveDbState();
		this.database.close();
	}

	public void close() {
		this.database.close();
	}

	private void saveDbState() {
		LOGGER.info("Saving DbService state on disk");
		persistStanzaPacketCache();
	}

	@Override
	public String toString() {
		return ENTITY_NAME;
	}

}
