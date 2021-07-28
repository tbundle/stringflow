package abs.ixi.server.muc;

import java.util.List;
import java.util.Set;

import abs.ixi.server.muc.ChatRoom.Affiliation;
import abs.ixi.server.muc.ChatRoom.ChatRoomMember;
import abs.ixi.server.muc.ChatRoom.Role;
import abs.ixi.server.packet.JID;
import abs.ixi.util.UUIDGenerator;

public class MucXmppUtil {
	public static final String MUC_PRESENCE_TAG = "<presence " + "from='%s' " + "id='%s' " + "to='%s'>"
			+ "<x xmlns='http://jabber.org/protocol/muc#user'>" + "<item affiliation='%s' role='%s' jid='%s'/>" + "</x>"
			+ "</presence>";

	public static final String MUC_PRESENCE_UNAVALABLE_TAG = "<presence " + "from='%s' " + "id='%s' "
			+ "to='%s' type='unavailable'>" + "<x xmlns='http://jabber.org/protocol/muc#user'>"
			+ "<item affiliation='%s' role='%s' jid='%s'/>" + "</x>" + "</presence>";

	public static final String MUC_MESSAGE_BODY_TAG = "<message" + " from='%s '" + "id='%s '" + "to='%s '"
			+ "type='groupchat'>" + "<body>" + "%s" + "</body>" + "</message>";

	public static final String MUC_MESSAGE_MEDIA_TAG = "<message" + " from='%s '" + "id='%s '" + "to='%s '"
			+ "type='groupchat'>" + "<media-id>" + "%s" + "</media-id>" + "</message>";

	public static final String CHAT_ROOM_SUBJECT = "<message " + "from='%s' id='%s' to='%s' type='%s'>"
			+ "<subject>%s</subject></message>";

	public static String MUC_ERROR_RESPONSE_TAG = "<presence " + "from='%s' " + "id='%s' " + "to='%s' "
			+ "type='error'>" + "<x xmlns='http://jabber.org/protocol/muc'/>" + "<error by='%s' type='%s'>" + "%s"
			+ "</error>" + "</presence>";

	public static String CONFLICT_ERROR_TAG = "<conflict xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>";

	public static String ITEM_NOT_FOUND_ERROR_TAG = "<item-not-found xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>";

	public static String IQ_RESPONSE = "<iq from='%s' id='%s' to='%s' type='result'><query xmlns='http://jabber.org/protocol/disco#items'>"
			+ "%s" + "</query></iq>";

	public static String CHAT_ROOM_CONFIG_FORM_RESPONSE = "<iq from='%s' id='%s' to='%s' type='result'>"
			+ "<query xmlns='http://jabber.org/protocol/muc#owner'>" + "<x xmlns='jabber:x:data' type='form'>"
			+ " <field label='Room Access mode' type='text-single' var='muc#roomconfig_accessmode'/>"
			+ "</x></query></iq>";

	public static final String ROOM_CONFIG_SUCCESS_RESPONSE = "<iq from='%s' id='%s' to='%s' type='result'>"
			+ "<query xmlns='http://jabber.org/protocol/muc#owner'>" + "</query></iq>";

	public static final String ROOM_INFO_RESPONSE = "<iq from='%s' id='%s' to='%s' type='result'>"
			+ "<query xmlns='http://jabber.org/protocol/disco#info'>" + " <identity name='%s' type='text'/>"
			+ "<field var='muc#roominfo_accessmode'>" + "<value>%s</value>" + "</field>" + "</query></iq>";

	public enum ErrorType {
		CANCEL("cancel"), AUTH("auth");

		String value;

		private ErrorType(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}

	}

	public static String getMucPresenceTag(String from, String id, String to, Affiliation affilication, Role role,
			String memberJID) {
		return String.format(MUC_PRESENCE_TAG, from, id, to, affilication.val(), role.val(), memberJID).toString();
	}

	public static String getMucUnavailabePresenceTag(String from, String id, String to, Affiliation affilication,
			Role role, String memberJid) {
		return String.format(MUC_PRESENCE_UNAVALABLE_TAG, from, id, to, affilication.val(), role.val(), memberJid)
				.toString();
	}

	public static String getMucMessageBodyTag(JID from, String id, JID to, String body) {
		return String.format(MUC_MESSAGE_BODY_TAG, from.getFullJID(), id, to.getBareJID(), body).toString();
	}

	public static String getMucMessageMediaTag(JID from, String id, JID to, String mediaId) {
		return String.format(MUC_MESSAGE_MEDIA_TAG, from.getFullJID(), id, to.getBareJID(), mediaId).toString();
	}

	public static String getMucErrorRespponseTag(JID from, String id, JID to, String errorType, String errorMsg) {
		return String.format(MUC_ERROR_RESPONSE_TAG, from.getBareJID(), id, to.getBareJID(), from, errorType, errorMsg)
				.toString();
	}

	public static String getChatRoomSubjectMessage(ChatRoom room, ChatRoomMember member) {
		return String.format(CHAT_ROOM_SUBJECT, room.getRoomJID().toString(), UUIDGenerator.uuid(),
				member.getUserJID().toString(), "groupchat", room.getSubject());
	}

	public static String getChatRoomsResponse(String from, String id, String to, List<ChatRoom> rooms) {
		StringBuilder sb = new StringBuilder();

		for (ChatRoom room : rooms) {
			sb.append("<item jid='").append(room.getRoomJID().toString()).append("' name='").append(room.getName())
					.append("'/>");
		}

		return String.format(IQ_RESPONSE, from, id, to, sb.toString());
	}

	public static String getRoomMembersResponse(String from, String id, String to, Set<ChatRoomMember> members) {
		StringBuilder sb = new StringBuilder();

		for (ChatRoomMember member : members) {
			sb.append("<item jid='").append(member.getUserJID().toString()).append("' name='")
					.append(member.getNickName()).append("' affiliation='").append(member.getAffiliation().val())
					.append("' role='").append(member.getRole().val()).append("'/>");
		}

		return String.format(IQ_RESPONSE, from, id, to, sb.toString());
	}

	public static String getRoomConfigFormResponse(String from, String id, String to) {

		return String.format(CHAT_ROOM_CONFIG_FORM_RESPONSE, from, id, to);
	}

	public static String getRoomConfigSuccessResponse(String from, String id, String to) {

		return String.format(ROOM_CONFIG_SUCCESS_RESPONSE, from, id, to);
	}

	public static String getRoomInfoResponse(String from, String id, String to, ChatRoom room) {

		return String.format(ROOM_INFO_RESPONSE, from, id, to, room.getName(), room.getAccessMode().val());
	}
}
