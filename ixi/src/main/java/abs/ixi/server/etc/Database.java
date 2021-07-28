package abs.ixi.server.etc;

import java.util.List;
import java.util.Map;

import abs.ixi.server.common.Initializable;
import abs.ixi.server.common.SimpleQueue;
import abs.ixi.server.common.Triplet;
import abs.ixi.server.muc.ChatRoom;
import abs.ixi.server.packet.Roster;
import abs.ixi.server.packet.xmpp.BareJID;
import abs.ixi.server.packet.xmpp.Message;
import abs.ixi.server.packet.xmpp.Stanza;
import abs.ixi.server.packet.xmpp.UserProfileData;
import abs.ixi.server.packet.xmpp.UserRegistrationData;
import abs.ixi.server.packet.xmpp.UserSearchData.Item;

/**
 * An abstraction layer around the database. In order to support messaging(chat)
 * among users, server makes use of database to store user data such as user
 * roster, user group information, exchanged messages etc. By design, ixi is
 * database agnostic server; therefore we need to build support for as many
 * databases as possible.
 * <p>
 * While we build support for different databases in ixi, it's imperative for us
 * to have a database abstraction layer so that all the database user components
 * remain unaffected. {@code Database} is the interface which abstract the
 * underlying database interaction details from server components.
 * </p>
 */
public interface Database extends Initializable {
	public int getRosterVersion(String userJID);

	public Roster getUserFullRoster(String userJID);

	public Roster getRoster(String userJID, int lastVersion);

	public void updateRoster(String userJID, String contactJID, String contactName);

	public void deleteRosterItem(String userJID, String contactJID, String contactName);

	public List<String> getPresenceSubscribers(String userJID);

	public void addPresenceSubscriber(String userJID, String subscriberJID);

	public void deletePresenceSubscription(String userJID, String subscriberJID);

	public void persistMessage(Message packet);

	public void persistStanzaPacketCache(List<Stanza> packets);

	public void persistUndeliverdStanza(Stanza stanza);

	public void persistUndeliverdStanza(SimpleQueue<Stanza> stanzaQ);

	public SimpleQueue<Stanza> getAllUndeliverdStanzas(String userJID);

	public void deleteAllUndeliverdStanzas(String userJID);

	public void persistUserSession(String bareJID, String resourceId);

	public void sessionLogout(String bareJId, String resourceId);

	public void discardDeviceToken(String bareJID, String deviceToken);

	public boolean authenticate(String bareJid, String password);

	public void addChatRoom(ChatRoom chatRoom);

	public void deleteChatRoom(String roomJID);

	public void addChatRoomMember(String roomJId, String memberJID, String nickName, String affiliation, String role);

	public void updateChatRoomMember(String roomJId, String memberJID, String affiliation, String role);

	public void removeChatRoomMember(String roomJId, String memberJID);

	public ChatRoom getChatRoomDetails(String roomJID);

	public Map<BareJID, ChatRoom> getChatRooms();

	public void updateNickName(String roomJID, String memberJID, String nickName);

	public void updateRoomSubject(String roomJID, String subject);

	public void updateRoomAccessMode(ChatRoom room);

	public void updateDeviceToken(String bareJID, String deviceId, String deviceToken, String notificationService,
			String deviceType);

	/**
	 * Fetch the list of device token for the user given {@link BareJID}
	 */
	public List<Triplet<String, String, String>> fetchDeviceTokens(String bareJID) throws DatabaseException;

	public boolean storeMedia(String mediaName, String senderBareJId, String receiverBareJID);

	public boolean authenticateUserMediaAccess(String mediaName, String userBareJId);

	public String getMediaReceiver(String mediaId);

	public String getUserName(String userJID);

	public String getUserRosterItemName(String userJID, String contactJID);

	public boolean isUserExist(String string);

	public void registerNewUser(String userJID, String password, String email);

	public UserRegistrationData getUserRegistrationInfo(String userJID);

	public void unRegisterUser(String userJID);

	public void changeUserPassword(String userJID, String password);

	public void updateProfile(UserProfileData userProfile);

	public UserProfileData getUserProfile(String userJID);

	public List<Item> serchUserByFirstName(String firstName);

	public List<Item> serchUserByLastName(String lastName);

	public List<Item> serchUserByNickName(String nickName);

	public List<Item> serchUserByEmail(String email);

	public void close();

}
