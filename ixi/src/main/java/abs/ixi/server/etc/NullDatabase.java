package abs.ixi.server.etc;

import java.util.List;
import java.util.Map;

import abs.ixi.server.common.InitializationException;
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

public class NullDatabase implements Database {

	@Override
	public void init() throws InitializationException {

	}

	@Override
	public boolean isInitialized() {
		return true;
	}

	@Override
	public Roster getRoster(String userJID, int lastVersion) {
		return null;
	}

	@Override
	public Roster getUserFullRoster(String userJID) {
		return null;
	}

	@Override
	public int getRosterVersion(String userJID) {
		return 0;
	}

	@Override
	public void updateRoster(String userJID, String contactJID, String contactName) {

	}

	@Override
	public void deleteRosterItem(String userJID, String contactJID, String contactName) {

	}

	@Override
	public List<String> getPresenceSubscribers(String userJID) {
		return null;
	}

	@Override
	public void addPresenceSubscriber(String userJID, String subscriberJID) {

	}

	@Override
	public void deletePresenceSubscription(String userJID, String subscriberJID) {

	}

	@Override
	public void addChatRoom(ChatRoom chatRoom) {

	}

	@Override
	public void deleteChatRoom(String jid) {

	}

	@Override
	public void addChatRoomMember(String roomJId, String memberJID, String nickName, String affiliation, String role) {

	}

	@Override
	public void updateChatRoomMember(String roomJId, String memberJID, String affiliation, String role) {

	}

	@Override
	public void removeChatRoomMember(String roomJId, String memberJID) {

	}

	@Override
	public ChatRoom getChatRoomDetails(String roomJID) {
		return null;
	}

	@Override
	public Map<BareJID, ChatRoom> getChatRooms() {
		return null;
	}

	@Override
	public void close() {

	}

	@Override
	public boolean authenticate(String bareJid, String password) {
		return false;
	}

	@Override
	public void updateNickName(String roomJID, String memberJID, String nickName) {

	}

	@Override
	public void updateRoomSubject(String roomJID, String string) {

	}

	@Override
	public void persistUserSession(String bareJID, String resourceId) {

	}

	@Override
	public void sessionLogout(String bareJId, String resourceId) {

	}

	@Override
	public void updateDeviceToken(String bareJID, String resourceId, String deviceTocken, String noyificationService,
			String deviceType) {

	}

	@Override
	public List<Triplet<String, String, String>> fetchDeviceTokens(String bareJID) throws DatabaseException {
		return null;
	}

	@Override
	public void updateRoomAccessMode(ChatRoom room) {

	}

	@Override
	public void discardDeviceToken(String bareJID, String deviceToken) {

	}

	@Override
	public boolean storeMedia(String mediaName, String senderBareJId, String receiverBareJID) {
		return false;
	}

	@Override
	public boolean authenticateUserMediaAccess(String mediaName, String userBareJId) {
		return false;
	}

	@Override
	public String getMediaReceiver(String mediaId) {
		return null;
	}

	@Override
	public void persistMessage(Message packet) {
		// TODO Auto-generated method stub

	}

	@Override
	public void persistStanzaPacketCache(List<Stanza> stanzas) {
		// TODO Auto-generated method stub

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

	@Override
	public void registerNewUser(String userJID, String password, String email) {
		// TODO Auto-generated method stub

	}

}
