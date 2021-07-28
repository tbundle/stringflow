package abs.ixi.server.etc;

import static abs.ixi.server.etc.conf.Configurations.Bundle.SYSTEM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import abs.ixi.server.Stringflow;
import abs.ixi.server.common.InitializationException;
import abs.ixi.server.common.SimpleQueue;
import abs.ixi.server.common.Triplet;
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
import abs.ixi.server.packet.xmpp.UserRegistrationData;
import abs.ixi.server.packet.xmpp.UserSearchData.Item;
import abs.ixi.util.DateUtils;
import abs.ixi.util.StringUtils;

public class Mongo implements Database, SystemConfigAware {
	private static final Logger LOGGER = LoggerFactory.getLogger(Mongo.class);

	private static final String MONGO_ARRAY_PUSH_OPERATION = "$push";
	private static final String MONGO_ARRAY_PULL_OPERATION = "$pull";
	private static final String MONGO_SET_OPERATION = "$set";

	// User collection
	private static final String COLLECTION_USER = "SfUser";

	private static final String USER_JID = "_id";

	// SF UserRoster collection
	private static final String COLLECTION_USER_ROSTER = "sfUserRoster";

	private static final String ROSTER_JID = "_id";
	private static final String ROSTER_LATEST_VERSION = "latest_version";
	private static final String ROSTER_ITEMS = "roster_items";
	private static final String ROSTER_ITEM_JID = "_id";
	private static final String ROSTER_ITEM_NAME = "name";
	private static final String ROSTER_ITEM_STATUS = "status";
	private static final String ROSTER_VERSION = "version";
	private static final String ROSTER_ITEM_CREATE_TIME = "create_time";

	// Presence subscribers
	private static final String COLLECTION_PRESENCE_SUBSCRIBERS = "sfPresenceSubscribers";

	private static final String PRESENCE_USER_JID = "_id";
	private static final String PRESENCE_SUBSCRIBERS = "subscribers";

	// Message
	private static final String COLLECTION_MESSAGE = "sfMessage";

	private static final String MESSAGE_TEXT = "message";
	private static final String MESSAGE_SENDER = "sender";
	private static final String MESSAGE_RECEIVER = "receiver";
	private static final String MESSAGE_CREATE_TIME = "create_time";
	private static final String MESSAGE_DELIVERY_TIME = "delivery_time";
	private static final String MESSAGE_IS_DELIVERED = "is_delivered";

	// User Session
	private static final String COLLECTION_USER_SESSION = "sfUserSession";

	private static final String SESSION_BARE_JID = "bare_jid";
	private static final String SESSION_RESOURCE_ID = "resource_id";
	private static final String SESSION_LOGIN_TIME = "login_time";
	private static final String SESSION_LOGOUT_TIME = "logout_time";
	private static final String SESSION_STATUS = "status";

	// User Device
	private static final String COLLECTION_USER_DEVICE = "sfUserDevice";

	private static final String DEVICE_BARE_JID = "bare_jid";
	private static final String DEVICE_RESOURCE_ID = "resource_id";
	private static final String DEVICE_TOKEN = "device_token";
	private static final String DEVICE_NOTIFICATION_SERVICE = "notification_service";
	private static final String DEVICE_TYPE = "device_type";
	private static final String DEVICE_LAST_UPDATE_TIME = "last_update_time";
	private static final String DEVICE_TOKEN_STATUS = "status";

	// ChatRoom
	private static final String COLLECTION_CHAT_ROOM = "sfChatRoom";

	private static final String ROOM_JID = "_id";
	private static final String ROOM_NAME = "name";
	private static final String ROOM_SUBJECT = "subject";
	private static final String ROOM_ACCESS_MODE = "access_mode";
	private static final String ROOM_CREATE_TIME = "create_time";
	private static final String ROOM_STATUS = "status";
	private static final String ROOM_MEMBERS = "members";
	private static final String MEMBER_JID = "_id";
	private static final String MEMBER_NICK_NAME = "nick_name";
	private static final String MEMBER_AFFILIATION = "affiliation";
	private static final String MEMBER_ROLE = "role";
	private static final String MEMBER_STATUS = "status";

	private MongoClient client;
	private String dbName;

	private boolean initialized = false;

	public Mongo() {

	}

	@Override
	public void init() throws InitializationException {
		this.dbName = Stringflow.runtime().configurations().get(MONGODB_DATABASE_NAME, SYSTEM);
		this.client = new MongoClient(
				new MongoClientURI(Stringflow.runtime().configurations().get(MONGODB_URL, SYSTEM)));

		this.initialized = true;
	}

	@Override
	public boolean isInitialized() {
		return this.initialized;
	}

	@Override
	public int getRosterVersion(String userJID) {
		MongoCollection<BasicDBObject> rosterCollection = getCollection(COLLECTION_USER_ROSTER);
		BasicDBObject queryObj = new BasicDBObject(ROSTER_JID, userJID);

		BasicDBObject roster = rosterCollection.find(queryObj).first();

		if (roster != null) {
			return roster.getInt(ROSTER_LATEST_VERSION, 0);
		}

		return 0;
	}

	@Override
	public Roster getUserFullRoster(String userJID) {
		try {
			MongoCollection<BasicDBObject> rosterCollection = getCollection(COLLECTION_USER_ROSTER);
			BasicDBObject queryObj = new BasicDBObject(ROSTER_JID, userJID);

			BasicDBObject rosterObj = rosterCollection.find(queryObj).first();

			if (rosterObj != null) {
				Roster roster = new Roster();
				BasicDBList itemsArray = (BasicDBList) rosterObj.get(ROSTER_ITEMS);

				if (itemsArray != null) {
					for (int i = 0; i < itemsArray.size(); i++) {
						BasicDBObject itemObj = (BasicDBObject) itemsArray.get(i);
						if (itemObj.getInt(ROSTER_ITEM_STATUS) == 1) {
							RosterItem rosterItem = roster.new RosterItem(new JID(itemObj.getString(ROSTER_ITEM_JID)));
							rosterItem.setName(itemObj.getString(ROSTER_ITEM_NAME));
							rosterItem.setItemVersion(itemObj.getInt(ROSTER_VERSION));
							rosterItem.setSubscription(PresenceSubscription.BOTH);

							roster.addItem(rosterItem);

						} else if (itemObj.getInt(ROSTER_ITEM_STATUS) == -1) {

							for (RosterItem rosterItem : roster.getItems()) {
								if (StringUtils.safeEquals(rosterItem.getJid().getBareJID().toString(),
										itemObj.getString(ROSTER_ITEM_JID))) {

									roster.getItems().remove(rosterItem);
									break;
								}
							}

						} else if (itemObj.getInt(ROSTER_ITEM_STATUS) == 0) {
							for (RosterItem rosterItem : roster.getItems()) {
								if (StringUtils.safeEquals(rosterItem.getJid().getBareJID().toString(),
										itemObj.getString(ROSTER_ITEM_JID))) {
									roster.getItems().remove(rosterItem);
									break;
								}
							}

							RosterItem rosterItem = roster.new RosterItem(new JID(itemObj.getString(ROSTER_ITEM_JID)));
							rosterItem.setName(itemObj.getString(ROSTER_ITEM_NAME));
							rosterItem.setItemVersion(itemObj.getInt(ROSTER_VERSION));
							rosterItem.setSubscription(PresenceSubscription.BOTH);

							roster.addItem(rosterItem);
						}
					}
				}

				return roster;
			}

		} catch (Exception e) {
			LOGGER.error("Error while Getting full roster for user jid {}", userJID, e);
		}

		return null;
	}

	@Override
	public Roster getRoster(String userJID, int lastVersion) {
		try {
			MongoCollection<BasicDBObject> rosterCollection = getCollection(COLLECTION_USER_ROSTER);
			BasicDBObject queryObj = new BasicDBObject(ROSTER_JID, userJID);
			BasicDBObject rosterObj = rosterCollection.find(queryObj).first();

			if (rosterObj != null) {
				Roster roster = new Roster();

				BasicDBList itemsArray = (BasicDBList) rosterObj.get(ROSTER_ITEMS);

				if (itemsArray != null) {
					for (int i = 0; i < itemsArray.size(); i++) {
						BasicDBObject itemObj = (BasicDBObject) itemsArray.get(i);

						if (itemObj.getInt(ROSTER_VERSION) > lastVersion) {
							RosterItem rosterItem = roster.new RosterItem(new JID(itemObj.getString(ROSTER_ITEM_JID)));

							rosterItem.setName(itemObj.getString(ROSTER_ITEM_NAME));
							rosterItem.setItemVersion(itemObj.getInt(ROSTER_VERSION));
							rosterItem.setSubscription(PresenceSubscription.BOTH);

							if (itemObj.getInt(ROSTER_ITEM_STATUS) == -1) {
								rosterItem.setSubscription(PresenceSubscription.REMOVE);
							}

							roster.addItem(rosterItem);
						}
					}
				}

				return roster;
			}

		} catch (Exception e) {
			LOGGER.error("Error while Getting full roster for user jid {}", userJID, e);
		}

		return null;
	}

	@Override
	public void updateRoster(String userJID, String contactJID, String contactName) {
		try {
			boolean isUserAvailable = isUserAvailable(userJID);
			boolean isContactAvailable = isUserAvailable(contactJID);

			if (!isContactAvailable || !isUserAvailable) {
				LOGGER.warn("Provided Jabber Ids {} or {} are not in system. So Failed to add roster item", userJID,
						contactJID);
				return;
			}

			MongoCollection<BasicDBObject> rosterCollection = getCollection(COLLECTION_USER_ROSTER);
			BasicDBObject queryObj = new BasicDBObject(ROSTER_JID, userJID);

			BasicDBObject roster = rosterCollection.find(queryObj).first();

			if (roster == null) {

				insertNewRosterDocument(userJID, contactJID, contactName);

			} else {

				int prevRosterVersion = roster.getInt(ROSTER_LATEST_VERSION);
				int latestRosterVersion = ++prevRosterVersion;

				BasicDBList itemsList = (BasicDBList) roster.get(ROSTER_ITEMS);

				boolean isAvailable = false;

				if (itemsList != null) {
					for (int position = 0; position < itemsList.size(); position++) {
						BasicDBObject itemObj = (BasicDBObject) itemsList.get(position);

						if (StringUtils.safeEquals(itemObj.getString(ROSTER_ITEM_JID), contactJID)) {
							if (itemObj.getInt(ROSTER_ITEM_STATUS) == 1) {
								isAvailable = true;

							} else if (itemObj.getInt(ROSTER_ITEM_STATUS) == -1) {
								isAvailable = false;
							}
						}
					}
				}

				BasicDBObject itemObj = new BasicDBObject();
				itemObj.put(ROSTER_ITEM_JID, contactJID);
				itemObj.put(ROSTER_ITEM_NAME, contactName);
				itemObj.put(ROSTER_VERSION, latestRosterVersion);

				if (isAvailable) {
					itemObj.put(ROSTER_ITEM_STATUS, 0);
				} else {
					itemObj.put(ROSTER_ITEM_STATUS, 1);
				}

				itemObj.put(ROSTER_ITEM_CREATE_TIME, DateUtils.currentTimestamp());

				BasicDBObject modifiDbObject = new BasicDBObject();
				modifiDbObject.put(MONGO_SET_OPERATION, new BasicDBObject(ROSTER_LATEST_VERSION, latestRosterVersion));

				modifiDbObject.put(MONGO_ARRAY_PUSH_OPERATION, new BasicDBObject().append(ROSTER_ITEMS, itemObj));

				rosterCollection.updateOne(new BasicDBObject(ROSTER_JID, userJID), modifiDbObject);
			}

		} catch (Exception e) {
			LOGGER.error("Error while updating roster item for userJID {}, contectJID {} and contactName {}", userJID,
					contactJID, contactJID);
		}

	}

	@Override
	public void deleteRosterItem(String userJID, String contactJID, String contactName) {
		try {
			boolean isUserAvailable = isUserAvailable(userJID);
			boolean isContactAvailable = isUserAvailable(contactJID);

			if (!isContactAvailable || !isUserAvailable) {
				LOGGER.warn("Provided Jabber Ids {} or {} are not in system. So Failed to add roster item", userJID,
						contactJID);
				return;
			}

			MongoCollection<BasicDBObject> rosterCollection = getCollection(COLLECTION_USER_ROSTER);
			BasicDBObject queryObj = new BasicDBObject(ROSTER_JID, userJID);

			BasicDBObject roster = rosterCollection.find(queryObj).first();

			if (roster != null) {
				int prevRosterVersion = roster.getInt(ROSTER_LATEST_VERSION);
				int latestRosterVersion = ++prevRosterVersion;

				BasicDBObject itemObj = new BasicDBObject();
				itemObj.put(ROSTER_ITEM_JID, contactJID);
				itemObj.put(ROSTER_ITEM_NAME, contactName);
				itemObj.put(ROSTER_VERSION, latestRosterVersion);
				itemObj.put(ROSTER_ITEM_STATUS, -1);
				itemObj.put(ROSTER_ITEM_CREATE_TIME, DateUtils.currentTimestamp());

				BasicDBObject modifiDbObject = new BasicDBObject();
				modifiDbObject.put(MONGO_SET_OPERATION, new BasicDBObject(ROSTER_LATEST_VERSION, latestRosterVersion));

				modifiDbObject.put(MONGO_ARRAY_PUSH_OPERATION, new BasicDBObject().append(ROSTER_ITEMS, itemObj));

				rosterCollection.updateOne(new BasicDBObject(ROSTER_JID, userJID), modifiDbObject);
			}

		} catch (Exception e) {
			LOGGER.error("Error while deleting roster item for userJID {}, contectJID {} and contactName {}", userJID,
					contactJID, contactJID);
		}

	}

	@Override
	public List<String> getPresenceSubscribers(String userJID) {
		try {
			BasicDBObject queryObj = new BasicDBObject(PRESENCE_USER_JID, userJID);

			MongoCollection<BasicDBObject> presenceCollection = getCollection(COLLECTION_PRESENCE_SUBSCRIBERS);
			BasicDBObject presenceDbObj = presenceCollection.find(queryObj).first();

			if (presenceDbObj != null) {
				BasicDBList subscribsDbList = (BasicDBList) presenceDbObj.get(PRESENCE_SUBSCRIBERS);

				if (subscribsDbList != null) {
					List<String> subscribers = new ArrayList<>();

					for (int position = 0; position < subscribsDbList.size(); position++) {
						subscribers.add((String) subscribsDbList.get(position));
					}

					return subscribers;
				}
			}

		} catch (Exception e) {

			LOGGER.error("Error while getting presence subscription list for userJID {}", userJID, e);

		}

		return null;
	}

	@Override
	public void addPresenceSubscriber(String userJID, String subscriberJID) {
		try {
			BasicDBObject queryObj = new BasicDBObject(PRESENCE_USER_JID, userJID);

			MongoCollection<BasicDBObject> presenceCollection = getCollection(COLLECTION_PRESENCE_SUBSCRIBERS);
			BasicDBObject presenceDbObj = presenceCollection.find(queryObj).first();

			if (presenceDbObj == null) {
				BasicDBList subscribers = new BasicDBList();
				subscribers.add(subscriberJID);

				queryObj.put(PRESENCE_SUBSCRIBERS, subscribers);
				presenceCollection.insertOne(queryObj);

			} else {
				BasicDBList subsciberDbObj = (BasicDBList) presenceDbObj.get(PRESENCE_SUBSCRIBERS);

				if (!subsciberDbObj.contains(subscriberJID)) {
					BasicDBObject modifingDbObj = new BasicDBObject(MONGO_ARRAY_PUSH_OPERATION,
							new BasicDBObject(PRESENCE_SUBSCRIBERS, subscriberJID));

					presenceCollection.updateOne(queryObj, modifingDbObj);
				}
			}

		} catch (Exception e) {

			LOGGER.error("Error while adding presence subscription list for userJID {} and subscribersJID {}", userJID,
					subscriberJID, e);

		}

	}

	@Override
	public void deletePresenceSubscription(String userJID, String subscriberJID) {
		try {
			BasicDBObject filterObj = new BasicDBObject(PRESENCE_USER_JID, userJID);
			BasicDBObject modifingDbObj = new BasicDBObject(MONGO_ARRAY_PULL_OPERATION,
					new BasicDBObject(PRESENCE_SUBSCRIBERS, subscriberJID));

			MongoCollection<BasicDBObject> presenceCollection = getCollection(COLLECTION_PRESENCE_SUBSCRIBERS);
			presenceCollection.updateOne(filterObj, modifingDbObj);

		} catch (Exception e) {

			LOGGER.error("Error while adding presence subscription list for userJID {} and subscribersJID {}", userJID,
					subscriberJID, e);

		}

	}

	@Override
	public void persistMessage(Message message) {
		try {
			String from = message.getFrom() == null ? null : message.getFrom().getBareJID().toString();
			String to = message.getTo() == null ? null : message.getTo().getBareJID().toString();

			BasicDBObject messageObj = new BasicDBObject();
			messageObj.put(MESSAGE_TEXT, message.xml());
			messageObj.put(MESSAGE_SENDER, from);
			messageObj.put(MESSAGE_RECEIVER, to);
			messageObj.put(MESSAGE_CREATE_TIME, message.getCreateTime());

			MongoCollection<BasicDBObject> messageCollection = getCollection(COLLECTION_MESSAGE);
			messageCollection.insertOne(messageObj);

		} catch (Exception e) {

			LOGGER.error("Error while persisting message {}", message, e);

		}
	}

	@Override
	public void persistStanzaPacketCache(List<Stanza> stanzas) {
		try {
			List<BasicDBObject> objects = new ArrayList<>();

			for (Stanza message : stanzas) {
				String from = message.getFrom() == null ? null : message.getFrom().getBareJID().toString();
				String to = message.getTo() == null ? null : message.getTo().getBareJID().toString();

				BasicDBObject messageObj = new BasicDBObject();
				messageObj.put(MESSAGE_TEXT, message.xml());
				messageObj.put(MESSAGE_SENDER, from);
				messageObj.put(MESSAGE_RECEIVER, to);
				messageObj.put(MESSAGE_CREATE_TIME, message.getCreateTime());

				objects.add(messageObj);
			}

			MongoCollection<BasicDBObject> messageCollection = getCollection(COLLECTION_MESSAGE);
			messageCollection.insertMany(objects);

		} catch (Exception e) {

			LOGGER.error("Error while persisting packets {}", stanzas, e);

		}
	}

	@Override
	public void persistUserSession(String bareJID, String resourceId) {
		try {
			BasicDBObject queryObj = new BasicDBObject();
			queryObj.put(SESSION_BARE_JID, bareJID);
			queryObj.put(SESSION_RESOURCE_ID, resourceId);
			queryObj.put(SESSION_LOGIN_TIME, DateUtils.currentTimestamp());
			queryObj.put(SESSION_STATUS, 1);

			MongoCollection<BasicDBObject> userSession = getCollection(COLLECTION_USER_SESSION);
			userSession.insertOne(queryObj);

		} catch (Exception e) {
			LOGGER.error("Error while persisting user session for userJID {}", bareJID, e);
		}

	}

	@Override
	public void sessionLogout(String bareJId, String resourceId) {
		try {
			BasicDBObject filterObj = new BasicDBObject();
			filterObj.put(SESSION_BARE_JID, bareJId);
			filterObj.put(SESSION_RESOURCE_ID, resourceId);

			BasicDBObject modifingObj = new BasicDBObject();
			modifingObj.put(SESSION_LOGOUT_TIME, DateUtils.currentTimestamp());
			modifingObj.put(SESSION_STATUS, 0);

			MongoCollection<BasicDBObject> userSession = getCollection(COLLECTION_USER_SESSION);
			userSession.updateOne(filterObj, new BasicDBObject(MONGO_SET_OPERATION, modifingObj));

		} catch (Exception e) {
			LOGGER.error("Error while log out user session for userJID {}", bareJId, e);
		}
	}

	@Override
	public boolean authenticate(String bareJID, String password) {
		// BasicDBObject queryObj = new BasicDBObject();
		// queryObj.put(USER_JID, bareJID);
		// queryObj.put(USER_STATUS, 1);
		//
		// MongoCollection<BasicDBObject> user = getCollection(COLLECTION_USER);
		// BasicDBObject dbObj = user.find(queryObj).first();
		//
		// if (dbObj != null) {
		// String dbPwd = dbObj.getString(USER_PASSWORD);
		//
		// if (StringUtils.safeEquals(dbPwd, StringUtils.stringToMD5(password)))
		// return true;
		//
		// }
		//
		return false;
	}

	@Override
	public void discardDeviceToken(String bareJID, String deviceToken) {
		try {
			BasicDBObject queryObj = new BasicDBObject();
			queryObj.put(DEVICE_BARE_JID, bareJID);
			queryObj.put(DEVICE_TOKEN, deviceToken);

			MongoCollection<BasicDBObject> userDevice = getCollection(COLLECTION_USER_DEVICE);
			BasicDBObject dbObj = userDevice.find(queryObj).first();

			if (dbObj != null) {
				BasicDBObject modifingObj = new BasicDBObject();
				modifingObj.put(DEVICE_TOKEN_STATUS, 0);

				userDevice.updateOne(queryObj, new BasicDBObject(MONGO_SET_OPERATION, modifingObj));
			}

		} catch (Exception e) {
			LOGGER.error("Error while updating device token for userJID {}", bareJID, e);
		}
	}

	@Override
	public void updateDeviceToken(String bareJID, String resourceId, String deviceTocken, String notificationService,
			String device_type) {
		try {
			BasicDBObject queryObj = new BasicDBObject();
			queryObj.put(DEVICE_BARE_JID, bareJID);
			queryObj.put(DEVICE_RESOURCE_ID, resourceId);

			MongoCollection<BasicDBObject> userDevice = getCollection(COLLECTION_USER_DEVICE);
			BasicDBObject dbObj = userDevice.find(queryObj).first();

			if (dbObj == null) {
				queryObj.put(DEVICE_TOKEN, deviceTocken);
				queryObj.put(DEVICE_NOTIFICATION_SERVICE, notificationService);
				queryObj.put(DEVICE_TYPE, device_type);
				queryObj.put(DEVICE_TOKEN_STATUS, 1);
				queryObj.put(DEVICE_LAST_UPDATE_TIME, DateUtils.currentTimestamp());

				userDevice.insertOne(queryObj);

			} else {
				BasicDBObject modifingObj = new BasicDBObject();
				modifingObj.put(DEVICE_TOKEN, deviceTocken);
				modifingObj.put(DEVICE_NOTIFICATION_SERVICE, notificationService);
				queryObj.put(DEVICE_TYPE, device_type);
				modifingObj.put(DEVICE_TOKEN_STATUS, 1);
				modifingObj.put(DEVICE_LAST_UPDATE_TIME, DateUtils.currentTimestamp());

				userDevice.updateOne(queryObj, new BasicDBObject(MONGO_SET_OPERATION, modifingObj));
			}

		} catch (Exception e) {
			LOGGER.error("Error while updating device token for userJID {}", bareJID, e);
		}

	}

	@Override
	public List<Triplet<String, String, String>> fetchDeviceTokens(String bareJID) throws DatabaseException {
		try {
			BasicDBObject queryObj = new BasicDBObject();
			queryObj.put(DEVICE_BARE_JID, bareJID);

			MongoCollection<BasicDBObject> userDevice = getCollection(COLLECTION_USER_DEVICE);
			MongoCursor<BasicDBObject> cursor = userDevice.find(queryObj).iterator();

			List<Triplet<String, String, String>> deviceTokens = new ArrayList<Triplet<String, String, String>>();

			while (cursor.hasNext()) {
				BasicDBObject dbObj = (BasicDBObject) cursor.next();
				String notificationService = dbObj.getString(DEVICE_NOTIFICATION_SERVICE);
				String token = dbObj.getString(DEVICE_TOKEN);
				String deviceType = dbObj.getString(DEVICE_TYPE);

				if (!StringUtils.isNullOrEmpty(notificationService) && !StringUtils.isNullOrEmpty(token)) {
					deviceTokens.add(new Triplet<String, String, String>(notificationService, token, deviceType));
				}
			}

			return deviceTokens;

		} catch (Exception e) {
			LOGGER.error("Error while updating device token for userJID {}", bareJID, e);
		}

		return null;
	}

	@Override
	public void addChatRoom(ChatRoom chatRoom) {
		try {
			BasicDBObject queryObj = new BasicDBObject();
			queryObj.put(ROOM_JID, chatRoom.getRoomJID().toString());
			queryObj.put(ROOM_NAME, chatRoom.getName());
			queryObj.put(ROOM_SUBJECT, chatRoom.getName());
			queryObj.put(ROOM_ACCESS_MODE, chatRoom.getAccessMode().val());
			queryObj.put(ROOM_CREATE_TIME, DateUtils.currentTimestamp());
			queryObj.put(ROOM_STATUS, 1);

			MongoCollection<BasicDBObject> chatRoomCollection = getCollection(COLLECTION_CHAT_ROOM);
			chatRoomCollection.insertOne(queryObj);

		} catch (Exception e) {

			LOGGER.error("Error while adding for jid {} and name {}", chatRoom.getRoomJID(), chatRoom.getName(), e);

		}
	}

	@Override
	public void deleteChatRoom(String roomJID) {
		try {
			MongoCollection<BasicDBObject> chatRoomCollection = getCollection(COLLECTION_CHAT_ROOM);
			chatRoomCollection.updateOne(new BasicDBObject(ROOM_JID, roomJID),
					new BasicDBObject(MONGO_SET_OPERATION, new BasicDBObject(ROOM_STATUS, 0)));

		} catch (Exception e) {

			LOGGER.error("Error while deleting chatroom for jid {}", roomJID, e);

		}

	}

	@Override
	public void addChatRoomMember(String roomJID, String memberJID, String nickName, String affiliation, String role) {
		try {
			boolean isRoomAvailable = isChatRoomAvailable(roomJID);
			boolean isMemberAvailable = isUserAvailable(memberJID);

			if (!isRoomAvailable || !isMemberAvailable) {
				LOGGER.warn("Provided Jabber Ids are not in System. So adding room member is not done");
				return;
			}

			BasicDBObject memberObj = new BasicDBObject();
			memberObj.put(MEMBER_JID, memberJID);
			memberObj.put(MEMBER_NICK_NAME, nickName);
			memberObj.put(MEMBER_AFFILIATION, affiliation);
			memberObj.put(MEMBER_ROLE, role);
			memberObj.put(MEMBER_STATUS, 1);

			BasicDBObject modifingObj = new BasicDBObject(MONGO_ARRAY_PUSH_OPERATION,
					new BasicDBObject(ROOM_MEMBERS, memberObj));

			MongoCollection<BasicDBObject> chatRoomCollection = getCollection(COLLECTION_CHAT_ROOM);
			chatRoomCollection.updateOne(new BasicDBObject(ROOM_JID, roomJID), modifingObj);

		} catch (Exception e) {

			LOGGER.error("Error while adding room member for roomJID {} and member jid {}", roomJID, memberJID, e);

		}

	}

	@Override
	public void updateChatRoomMember(String roomJID, String memberJID, String affiliation, String role) {
		try {
			boolean isRoomAvailable = isChatRoomAvailable(roomJID);
			boolean isMemberAvailable = isUserAvailable(memberJID);

			if (!isRoomAvailable || !isMemberAvailable) {
				LOGGER.warn("Provided Jabber Ids are not in System. So updating room member details are not saved");
				return;
			}

			BasicDBObject memberObj = new BasicDBObject();
			memberObj.put(MEMBER_JID, memberJID);
			memberObj.put(MEMBER_AFFILIATION, affiliation);
			memberObj.put(MEMBER_ROLE, role);
			memberObj.put(MEMBER_STATUS, 1);

			BasicDBObject modifingObj = new BasicDBObject(MONGO_ARRAY_PUSH_OPERATION,
					new BasicDBObject(ROOM_MEMBERS, memberObj));

			MongoCollection<BasicDBObject> chatRoomCollection = getCollection(COLLECTION_CHAT_ROOM);
			chatRoomCollection.updateOne(new BasicDBObject(ROOM_JID, roomJID), modifingObj);

		} catch (Exception e) {

			LOGGER.error("Error while updating room member for roomJID {} and member jid {}", roomJID, memberJID, e);

		}
	}

	@Override
	public void removeChatRoomMember(String roomJID, String memberJID) {
		try {

			BasicDBObject memberObj = new BasicDBObject();
			memberObj.put(MEMBER_JID, memberJID);

			BasicDBObject modifingObj = new BasicDBObject(MONGO_ARRAY_PULL_OPERATION,
					new BasicDBObject(ROOM_MEMBERS, memberObj));

			MongoCollection<BasicDBObject> chatRoomCollection = getCollection(COLLECTION_CHAT_ROOM);
			chatRoomCollection.updateOne(new BasicDBObject(ROOM_JID, roomJID), modifingObj);

		} catch (Exception e) {

			LOGGER.error("Error while deleting chatroom member for jid {}", roomJID, e);

		}

	}

	@Override
	public ChatRoom getChatRoomDetails(String roomJID) {
		try {
			MongoCollection<BasicDBObject> chatRoomCollection = getCollection(COLLECTION_CHAT_ROOM);
			BasicDBObject dbObj = chatRoomCollection.find(new BasicDBObject(ROOM_JID, roomJID)).first();

			if (dbObj != null) {
				ChatRoom room = new ChatRoom(new BareJID(roomJID), dbObj.getString(ROOM_NAME),
						dbObj.getString(ROOM_SUBJECT), AccessMode.valueFrom(dbObj.getString(ROOM_ACCESS_MODE)));

				BasicDBList dbList = (BasicDBList) dbObj.get(ROOM_MEMBERS);

				if (dbList != null) {
					for (int position = 0; position < dbList.size(); position++) {
						BasicDBObject memberDbObj = (BasicDBObject) dbList.get(position);

						ChatRoomMember member = room.new ChatRoomMember(new JID(memberDbObj.getString(MEMBER_JID)),
								memberDbObj.getString(MEMBER_NICK_NAME),
								Affiliation.valueFrom(memberDbObj.getString(MEMBER_AFFILIATION)),
								Role.valueFrom(memberDbObj.getString(MEMBER_ROLE)));

						room.addMember(member);
					}
				}

				return room;
			}

		} catch (Exception e) {
			LOGGER.error("Error while getting chat room details  for roomJID {}", roomJID, e);
		}

		return null;
	}

	@Override
	public Map<BareJID, ChatRoom> getChatRooms() {
		try {
			MongoCollection<BasicDBObject> chatRoomCollection = getCollection(COLLECTION_CHAT_ROOM);
			MongoCursor<BasicDBObject> cursor = chatRoomCollection.find(new BasicDBObject(ROOM_STATUS, 1)).iterator();

			Map<BareJID, ChatRoom> chatRooms = new HashMap<>();

			if (cursor.hasNext()) {
				BasicDBObject dbObj = (BasicDBObject) cursor.next();
				ChatRoom room = new ChatRoom(new BareJID(dbObj.getString(ROOM_JID)), dbObj.getString(ROOM_NAME),
						dbObj.getString(ROOM_SUBJECT), AccessMode.valueFrom(dbObj.getString(ROOM_ACCESS_MODE)));

				BasicDBList dbList = (BasicDBList) dbObj.get(ROOM_MEMBERS);

				if (dbList != null) {
					for (int position = 0; position < dbList.size(); position++) {
						BasicDBObject memberDbObj = (BasicDBObject) dbList.get(position);

						ChatRoomMember member = room.new ChatRoomMember(new JID(memberDbObj.getString(MEMBER_JID)),
								memberDbObj.getString(MEMBER_NICK_NAME),
								Affiliation.valueFrom(memberDbObj.getString(MEMBER_AFFILIATION)),
								Role.valueFrom(memberDbObj.getString(MEMBER_ROLE)));

						room.addMember(member);
					}
				}

				chatRooms.put(room.getRoomJID(), room);
			}

			return chatRooms;

		} catch (Exception e) {
			LOGGER.error("Error while getting rooms", e);
		}

		return null;
	}

	@Override
	public void updateNickName(String roomJID, String memberJID, String nickName) {
		try {
			BasicDBObject match = new BasicDBObject();
			match.put(ROOM_JID, roomJID);
			match.put(ROOM_MEMBERS + "." + MEMBER_JID, memberJID);

			BasicDBObject memberObj = new BasicDBObject();
			memberObj.put(ROOM_MEMBERS + ".$." + MEMBER_NICK_NAME, nickName);

			BasicDBObject updateObj = new BasicDBObject(MONGO_SET_OPERATION, memberObj);

			MongoCollection<BasicDBObject> chatRoomCollection = getCollection(COLLECTION_CHAT_ROOM);
			chatRoomCollection.updateOne(match, updateObj);

		} catch (Exception e) {
			LOGGER.error("Error while updating room nick name for roomJId {}", roomJID, e);
		}

	}

	@Override
	public void updateRoomSubject(String roomJID, String subject) {
		try {
			MongoCollection<BasicDBObject> chatRoomCollection = getCollection(COLLECTION_CHAT_ROOM);
			chatRoomCollection.updateOne(new BasicDBObject(ROOM_JID, roomJID),
					new BasicDBObject(MONGO_SET_OPERATION, new BasicDBObject(ROOM_SUBJECT, subject)));

		} catch (Exception e) {
			LOGGER.error("Error while updating room subject for roomJId {}", roomJID, e);
		}
	}

	@Override
	public void updateRoomAccessMode(ChatRoom room) {
		try {
			MongoCollection<BasicDBObject> chatRoomCollection = getCollection(COLLECTION_CHAT_ROOM);
			chatRoomCollection.updateOne(new BasicDBObject(ROOM_JID, room.getRoomJID().toString()), new BasicDBObject(
					MONGO_SET_OPERATION, new BasicDBObject(ROOM_ACCESS_MODE, room.getAccessMode().val())));

		} catch (Exception e) {
			LOGGER.error("Error while updating room access mode for roomJId {}", room.getRoomJID().toString(), e);
		}

	}

	@Override
	public void close() {
		this.client.close();
	}

	protected void insertNewRosterDocument(String userJID, String contactJID, String contactName) {
		BasicDBObject rosterObj = new BasicDBObject();
		rosterObj.put(ROSTER_JID, userJID);
		rosterObj.put(ROSTER_LATEST_VERSION, 1);

		BasicDBList itemsListObj = new BasicDBList();
		BasicDBObject itemObj = new BasicDBObject();
		itemObj.put(ROSTER_ITEM_JID, contactJID);
		itemObj.put(ROSTER_ITEM_NAME, contactName);
		itemObj.put(ROSTER_VERSION, 1);
		itemObj.put(ROSTER_ITEM_STATUS, 1);
		itemObj.put(ROSTER_ITEM_CREATE_TIME, DateUtils.currentTimestamp());

		itemsListObj.add(itemObj);

		rosterObj.put(ROSTER_ITEMS, itemsListObj);

		insertRosterDocument(rosterObj);
	}

	protected void insertRosterDocument(BasicDBObject document) {
		MongoCollection<BasicDBObject> rosterCollection = getCollection(COLLECTION_USER_ROSTER);
		rosterCollection.insertOne(document);
	}

	protected void deleteRosterDocument(String userJID) {
		BasicDBObject deleteFilter = new BasicDBObject(ROSTER_JID, userJID);
		deleteRosterDocument(deleteFilter);
	}

	protected void deleteRosterDocument(BasicDBObject filter) {
		MongoCollection<BasicDBObject> rosterCollection = getCollection(COLLECTION_USER_ROSTER);
		rosterCollection.deleteOne(filter);
	}

	protected boolean isUserAvailable(String userBareJID) {
		MongoCollection<BasicDBObject> rosterCollection = getCollection(COLLECTION_USER);
		BasicDBObject queryObj = new BasicDBObject(USER_JID, userBareJID);

		BasicDBObject roster = rosterCollection.find(queryObj).first();

		return roster == null ? false : true;
	}

	protected boolean isChatRoomAvailable(String roomJID) {
		MongoCollection<BasicDBObject> rosterCollection = getCollection(COLLECTION_CHAT_ROOM);
		BasicDBObject queryObj = new BasicDBObject(ROOM_JID, roomJID);

		BasicDBObject roster = rosterCollection.find(queryObj).first();

		return roster == null ? false : true;
	}

	public MongoDatabase getDatabase() {
		return this.client.getDatabase(this.dbName);
	}

	public MongoCollection<BasicDBObject> getCollection(String collectionName) {
		return this.getDatabase().getCollection(collectionName, BasicDBObject.class);
	}

	@Override
	public boolean storeMedia(String mediaName, String senderBareJId, String receiverBareJID) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean authenticateUserMediaAccess(String mediaName, String userBareJId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getMediaReceiver(String mediaId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUserName(String userJID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUserExist(String string) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void registerNewUser(String userJID, String password, String email) {
		// TODO Auto-generated method stub

	}

	@Override
	public UserRegistrationData getUserRegistrationInfo(String userJID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unRegisterUser(String userJID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void changeUserPassword(String userJID, String password) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getUserRosterItemName(String userJID, String contactJID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateProfile(UserProfileData userProfile) {
		// TODO Auto-generated method stub

	}

	@Override
	public UserProfileData getUserProfile(String userJID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Item> serchUserByFirstName(String firstName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Item> serchUserByLastName(String lastName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Item> serchUserByNickName(String nickName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Item> serchUserByEmail(String email) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void persistUndeliverdStanza(Stanza stanza) {
		// TODO Auto-generated method stub

	}

	@Override
	public void persistUndeliverdStanza(SimpleQueue<Stanza> stanzaQ) {
		// TODO Auto-generated method stub

	}

	@Override
	public SimpleQueue<Stanza> getAllUndeliverdStanzas(String userJID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteAllUndeliverdStanzas(String userJID) {
		// TODO Auto-generated method stub

	}

}
