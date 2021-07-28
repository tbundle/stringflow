package abs.ixi.server.packet.xmpp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import abs.ixi.server.io.MalformedXMPPRequestException;
import abs.ixi.server.muc.ChatRoom;
import abs.ixi.server.muc.ChatRoom.AccessMode;
import abs.ixi.server.muc.ChatRoom.Affiliation;
import abs.ixi.server.muc.ChatRoom.ChatRoomMember;
import abs.ixi.server.muc.ChatRoom.Role;
import abs.ixi.server.packet.InvalidJabberId;
import abs.ixi.server.packet.JID;
import abs.ixi.server.packet.PresenceSubscription;
import abs.ixi.server.packet.Roster;
import abs.ixi.server.packet.XMPPNamespaces;
import abs.ixi.server.packet.Roster.RosterItem;
import abs.ixi.server.packet.xmpp.IQ.IQType;
import abs.ixi.server.packet.xmpp.UserSearchData.Item;
import abs.ixi.util.CollectionUtils;
import abs.ixi.util.ObjectUtils;
import abs.ixi.util.StringUtils;
import abs.ixi.xml.Element;
import abs.ixi.xml.XMLUtils;

public class IQQuery extends AbstractIQContent {
	private static final long serialVersionUID = -229804307457896223L;

	public static final String XML_ELM_NAME = "query";

	private static final String VER_ATTRIBUTE = "ver";

	private static final String ITEM = "item";
	private static final String JID_ATTRIBUTE = "jid";
	private static final String NAME_ATTRIBUTE = "name";
	private static final String SUBSCRIPTION_ATTRIBUTE = "subscription";
	private static final String ROLE_ATTRIBUTE = "role";
	private static final String AFFILIATION_ATTRIBUTE = "affiliation";
	private static final String NICK_ATTRIBUTE = "nick";

	private static final String X = "x";
	private static final String TYPE_ATTRIBUTE = "type";

	private static final String SUBMIT = "submit";
	private static final String FIELD = "field";
	private static final String VAR = "var";
	private static final String VALUE = "value";

	private static final String IDENTITY = "identity";

	private static final String DESTROY = "destroy";
	private static final String REASON = "reason";

	private static final String REMOVE = "remove";
	private static final String USER_NAME = "username";
	private static final String EMAIL = "email";
	private static final String PASSWORD = "password";

	private static final String REGISTERED_XML = "<registered/>";
	private static final String USER_NAME_OPEN_TAG = "<username>";
	private static final String USER_NAME_CLOSE_TAG = "</username>";
	private static final String USER_EMAIL_OPEN_TAG = "<email>";
	private static final String USER_EMAIL_CLOSE_TAG = "</email>";

	private static final String QUERY_CLOSE_TAG = "</query>";
	private static final String FORM_DATA_X_OPEN_TAG = "<x xmlns='jabber:x:data' type='result'>";
	private static final String X_CLOSE_TAG = "</x>";

	private static final String FIELD_ROOM_SUBJECT_OPEN_TAG = "<field var='muc#roominfo_subject' label='Subject'>";
	private static final String FIELD_ROOM_ACCESS_MODE_OPEN_TAG = "<field var='muc#roominfo_accessmode' label='accessmode'>";
	private static final String FIELD_CLOSE_TAG = "</field>";
	private static final String VALUE_OPEN_TAG = "<value>";
	private static final String VALUE_CLOSE_TAG = "</value>";

	private static final String MEDIA_TAG = "media";
	private static final String MEDIA_ID_ATTRIBUTE = "media-id";
	private static final String SID_ATTRIBUTE = "sid";

	// For now it is fixed. we will make it variable later
	private static final String USER_REGISTRATION_ATTRIBUTES_QUERY_XML = "<query xmlns='jabber:iq:register'>"
			+ "<instructions>" + "Choose a username and password for use with this service."
			+ "Please also provide your email address." + "</instructions>" + "<username />" + "<password />"
			+ "<email />" + "</query>";

	private static final String ROOM_CONFIG_FORM_QUERY_XML = "<query xmlns='http://jabber.org/protocol/muc#owner'>"
			+ "<x xmlns='jabber:x:data' type='form' >"
			+ "<field label='Room Access mode' type='text-single' var='muc#roomconfig_accessmode'/>" + "</x></query>";

	private static final String FIRST_OPEN_TAG = "<first>";
	private static final String FIRST_CLOSE_TAG = "</first>";
	private static final String LAST_OPEN_TAG = "<last>";
	private static final String LAST_CLOSE_TAG = "</last>";
	private static final String NICK_OPEN_TAG = "<nick>";
	private static final String NICK_CLOSE_TAG = "</nick>";
	private static final String EMAIL_OPEN_TAG = "<email>";
	private static final String EMAIL_CLOSE_TAG = "</email>";

	private static final String ITEM_CLOSE_TAG = "</item>";

	// For now it is fixed. we will make it variable later
	private static final String JABBER_SEARCH_ATTRIBUTES_QUERY_XML = "<query xmlns='jabber:iq:search'>"
			+ "<instructions>" + "Fill in one or more fields to search for any matching Jabber users.."
			+ "</instructions>" + "<first />" + "<last />" + "<nick />" + "<email />" + "</query>";

	private static final String JABBER_SEARCH_QUERY_OPEN_TAG = "<query xmlns='jabber:iq:search'>";

	private Roster roster;
	private UserRegistrationData userRegistrationData;
	private UserSearchData userSearchData;

	private List<ChatRoom> rooms;
	private boolean destroyRoom;
	private String reason;

	private boolean isRoomListResponse;
	private boolean isRoomItemListResponse;
	private boolean isRoomConfigFormResponse;

	private String mediaId;
	private String sid;

	public IQQuery(String xmlns) {
		super(xmlns, IQContentType.QUERY);
		this.rooms = new ArrayList<ChatRoom>();
	}

	public IQQuery(String xmlns, Roster roster) {
		super(xmlns, IQContentType.QUERY);
		this.roster = roster;
		this.rooms = new ArrayList<ChatRoom>();
	}

	public IQQuery(String xmlns, UserRegistrationData userData) {
		super(xmlns, IQContentType.QUERY);
		this.userRegistrationData = userData;
		this.rooms = new ArrayList<ChatRoom>();
	}

	public IQQuery(String xmlns, ChatRoom room) {
		super(xmlns, IQContentType.QUERY);
		this.rooms = Arrays.asList(room);
	}

	public IQQuery(Element element, JID iqFromJID, JID iqToJID, IQType iqType)
			throws InvalidJabberId, MalformedXMPPRequestException {
		this(element.getAttribute(XMLUtils.XMLNS_ATTRIBUTE));

		if (StringUtils.safeEquals(XMPPNamespaces.ROSTER_NAMESPACE, xmlns, false)) {
			this.roster = generateRoster(element, iqFromJID, iqToJID, iqType);

		} else if (StringUtils.safeEquals(XMPPNamespaces.USER_REGISTER_NAMESPACE, xmlns, false)) {

			this.userRegistrationData = generateUserRegistrationData(element, iqFromJID, iqToJID, iqType);

		} else if (StringUtils.safeEquals(XMPPNamespaces.JABBER_SEARCH_NAMESPACE, xmlns, false)) {

			this.userSearchData = generateUserSearchData(element);

		} else if (StringUtils.safeEquals(XMPPNamespaces.MUC_ADMIN_NAMESPACE, xmlns, false)
				|| StringUtils.safeEquals(XMPPNamespaces.MUC_OWNER_NAMESPACE, xmlns, false)) {

			generateChatRoomDetails(element, iqFromJID, iqToJID, iqType);

		} else if (StringUtils.safeEquals(XMPPNamespaces.DISCO_ITEM_NAMESPACE, xmlns, false)
				|| StringUtils.safeEquals(XMPPNamespaces.DISCO_INFO_NAMESPACE, xmlns, false)) {
			// Do nothing

		} else if (StringUtils.safeEquals(XMPPNamespaces.STRINGFLOW_MEDIA_NAMESPACE, xmlns, false)) {
			Element mediaElm = element.getChild(MEDIA_TAG);
			this.mediaId = mediaElm.getAttribute(MEDIA_ID_ATTRIBUTE);
			this.sid = mediaElm.getAttribute(SID_ATTRIBUTE);
		}
	}

	private UserSearchData generateUserSearchData(Element queryElm) {
		if (!CollectionUtils.isNullOrEmpty(queryElm.getChildren())) {
			UserSearchData userSearchData = new UserSearchData();

			Map<String, String> map = new HashMap<>();

			for (Element child : queryElm.getChildren()) {
				map.put(child.getName(), child.val());
			}

			userSearchData.setSearchRequestData(map);

			return userSearchData;
		}

		return null;
	}

	public void setRoster(Roster roster) {
		this.roster = roster;
	}

	public Roster getRoster() {
		return roster;
	}

	public void setUserRegistrationData(UserRegistrationData userRegistrationData) {
		this.userRegistrationData = userRegistrationData;
	}

	public UserRegistrationData getUserRegistrationData() {
		return userRegistrationData;
	}

	public UserSearchData getUserSearchData() {
		return userSearchData;
	}

	public void setUserSearchData(UserSearchData userSearchData) {
		this.userSearchData = userSearchData;
	}

	public List<ChatRoom> getRooms() {
		return rooms;
	}

	public void setRooms(List<ChatRoom> rooms) {
		this.rooms = rooms;
	}

	public boolean isRoomListResponse() {
		return isRoomListResponse;
	}

	public void setRoomListResponse(boolean isRoomListResponse) {
		this.isRoomListResponse = isRoomListResponse;
	}

	public boolean isRoomItemListResponse() {
		return isRoomItemListResponse;
	}

	public void setRoomItemListResponse(boolean isRoomItemListResponse) {
		this.isRoomItemListResponse = isRoomItemListResponse;
	}

	public boolean isRoomConfigFormResponse() {
		return isRoomConfigFormResponse;
	}

	public void setRoomConfigFormResponse(boolean isRoomConfigFormResponse) {
		this.isRoomConfigFormResponse = isRoomConfigFormResponse;
	}

	public String getMediaId() {
		return mediaId;
	}

	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public boolean isDestroyRoom() {
		return destroyRoom;
	}

	public void setDestroyRoom(boolean destroyRoom) {
		this.destroyRoom = destroyRoom;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	private void generateChatRoomDetails(Element queryElm, JID iqFromJID, JID iqToJID, IQType iqType)
			throws InvalidJabberId {

		if (iqType == IQType.SET) {

			for (Element child : queryElm.getChildren()) {
				if (StringUtils.safeEquals(child.getName(), ITEM)) {
					ChatRoom room = new ChatRoom(iqToJID.getBareJID());

					JID userJID = new JID(child.getAttribute(JID_ATTRIBUTE));

					ChatRoomMember member = room.new ChatRoomMember(userJID, userJID.getNode());

					if (child.getAttribute(AFFILIATION_ATTRIBUTE) != null) {
						member.setAffiliation(Affiliation.valueFrom(child.getAttribute(AFFILIATION_ATTRIBUTE)));
					}

					if (child.getAttribute(ROLE_ATTRIBUTE) != null) {
						member.setRole(Role.valueFrom(child.getAttribute(ROLE_ATTRIBUTE)));
					}

					if (child.getAttribute(NICK_ATTRIBUTE) != null) {
						member.setNickName(child.getAttribute(NICK_ATTRIBUTE));
					}

					room.addMember(member);

					this.rooms.add(room);

				} else if (StringUtils.safeEquals(child.getName(), X)) {

					if (StringUtils.safeEquals(child.getAttribute(XMLUtils.XMLNS_ATTRIBUTE),
							XMPPNamespaces.JABBER_X_DATA)
							&& StringUtils.safeEquals(child.getAttribute(TYPE_ATTRIBUTE), SUBMIT)) {

						ChatRoom room = new ChatRoom(iqToJID.getBareJID());

						if (!CollectionUtils.isNullOrEmpty(child.getChildren())) {
							for (Element field : child.getChildren()) {
								if (StringUtils.safeEquals(field.getName(), FIELD) && StringUtils
										.safeEquals(field.getAttribute(VAR), XMPPNamespaces.ROMM_CONFIG_ACCESS_MODE)) {

									Element value = field.getChild(VALUE);

									if (value != null) {
										String accessMode = value.val();

										if (accessMode != null)
											room.setAccessMode(AccessMode.valueFrom(accessMode));
									}
								}
							}
						}

						this.rooms.add(room);
					}

				} else if (StringUtils.safeEquals(child.getName(), DESTROY)) {
					this.destroyRoom = true;

					Element reason = child.getChild(REASON);

					if (reason != null) {
						this.reason = reason.val();
					}
				}

			}

		}
	}

	private UserRegistrationData generateUserRegistrationData(Element queryElm, JID iqFromJID, JID iqToJID,
			IQType iqType) {
		if (iqType == IQType.SET) {
			UserRegistrationData userRegistrationData = new UserRegistrationData();

			for (Element child : queryElm.getChildren()) {
				if (StringUtils.safeEquals(child.getName(), REMOVE)) {
					userRegistrationData.setRemove(true);

				} else if (StringUtils.safeEquals(child.getName(), USER_NAME)) {
					userRegistrationData.setUserName(child.val());

				} else if (StringUtils.safeEquals(child.getName(), EMAIL)) {
					userRegistrationData.setEmail(child.val());

				} else if (StringUtils.safeEquals(child.getName(), PASSWORD)) {
					userRegistrationData.setPassword(child.val());
				}
			}

			return userRegistrationData;
		}

		return null;
	}

	private Roster generateRoster(Element queryElm, JID iqFromJID, JID iqToJID, IQType iqType) throws InvalidJabberId {
		Roster roster = new Roster();

		if (iqType == IQType.GET) {
			if (queryElm.getAttribute(VER_ATTRIBUTE) != null) {
				roster.setVersion(ObjectUtils.parseToInt(queryElm.getAttribute(VER_ATTRIBUTE)));
			}

		} else if (iqType == IQType.SET) {
			Element itemElm = queryElm.getChild(ITEM);

			if (itemElm != null) {
				RosterItem rosterItem = roster.new RosterItem(new JID(itemElm.getAttribute(JID_ATTRIBUTE)));
				rosterItem.setName(itemElm.getAttribute(NAME_ATTRIBUTE));

				if (itemElm.getAttribute(SUBSCRIPTION_ATTRIBUTE) != null) {
					rosterItem.setSubscription(
							PresenceSubscription.valueOf(itemElm.getAttribute(SUBSCRIPTION_ATTRIBUTE).toUpperCase()));

				} else {
					rosterItem.setSubscription(PresenceSubscription.NONE);
				}

				roster.addItem(rosterItem);
			}
		}

		return roster;
	}

	@Override
	public String xml() {
		StringBuilder sb = new StringBuilder();

		if (StringUtils.safeEquals(this.xmlns, XMPPNamespaces.ROSTER_NAMESPACE)) {
			if (this.roster != null) {
				appendRosterQueryXML(sb);
			}

		} else if (StringUtils.safeEquals(this.xmlns, XMPPNamespaces.JABBER_SEARCH_NAMESPACE)) {
			if (userSearchData != null && userSearchData.isSendSearchAttributes()) {
				return JABBER_SEARCH_ATTRIBUTES_QUERY_XML;

			} else {
				sb.append(JABBER_SEARCH_QUERY_OPEN_TAG);

				if (this.userSearchData != null
						&& !CollectionUtils.isNullOrEmpty(this.userSearchData.getSearchedItems())) {

					for (Item userItem : this.userSearchData.getSearchedItems()) {

						sb.append(XMLUtils.OPEN_BRACKET).append(ITEM).append(XMLUtils.SPACE).append(JID_ATTRIBUTE)
								.append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE)
								.append(userItem.getUserJID().toString()).append(XMLUtils.SINGLE_QUOTE)
								.append(XMLUtils.SPACE).append(XMLUtils.CLOSE_BRACKET);

						if (!StringUtils.isNullOrEmpty(userItem.getFirstName())) {
							sb.append(FIRST_OPEN_TAG).append(userItem.getFirstName()).append(FIRST_CLOSE_TAG);
						}

						if (!StringUtils.isNullOrEmpty(userItem.getLastName())) {
							sb.append(LAST_OPEN_TAG).append(userItem.getLastName()).append(LAST_CLOSE_TAG);
						}

						if (!StringUtils.isNullOrEmpty(userItem.getNickName())) {
							sb.append(NICK_OPEN_TAG).append(userItem.getNickName()).append(NICK_CLOSE_TAG);
						}

						if (!StringUtils.isNullOrEmpty(userItem.getEmail())) {
							sb.append(EMAIL_OPEN_TAG).append(userItem.getEmail()).append(EMAIL_CLOSE_TAG);
						}

						sb.append(ITEM_CLOSE_TAG);
					}

				}

				sb.append(QUERY_CLOSE_TAG);
			}

		} else if (StringUtils.safeEquals(this.xmlns, XMPPNamespaces.USER_REGISTER_NAMESPACE)) {
			if (userRegistrationData == null) {
				return USER_REGISTRATION_ATTRIBUTES_QUERY_XML;

			} else {
				appendUserDataQueryXML(sb);
			}

		} else if (StringUtils.safeEquals(this.xmlns, XMPPNamespaces.MUC_OWNER_NAMESPACE)) {
			if (isRoomConfigFormResponse) {
				sb.append(ROOM_CONFIG_FORM_QUERY_XML);
			}

		} else if (StringUtils.safeEquals(this.xmlns, XMPPNamespaces.DISCO_INFO_NAMESPACE)) {
			appendRoomInfoResponse(sb);

		} else if (StringUtils.safeEquals(this.xmlns, XMPPNamespaces.DISCO_ITEM_NAMESPACE)) {
			if (this.isRoomListResponse) {
				appendRoomsListResponse(sb);

			} else if (this.isRoomItemListResponse) {
				appendRoomItemsListResponse(sb);
			}

		} else if (StringUtils.safeEquals(this.xmlns, XMPPNamespaces.STRINGFLOW_MEDIA_NAMESPACE)) {
			appendMediaResponseXml(sb);

		}

		return sb.toString();
	}

	private void appendMediaResponseXml(StringBuilder sb) {
		sb.append(XMLUtils.OPEN_BRACKET).append(XML_ELM_NAME).append(XMLUtils.SPACE).append(XMLUtils.XMLNS_ATTRIBUTE)
				.append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE).append(XMPPNamespaces.STRINGFLOW_MEDIA_NAMESPACE)
				.append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE).append(XMLUtils.CLOSE_BRACKET);

		if (!StringUtils.isNullOrEmpty(mediaId) && !StringUtils.isNullOrEmpty(sid)) {
			sb.append(XMLUtils.OPEN_BRACKET).append(MEDIA_TAG).append(XMLUtils.SPACE).append(MEDIA_ID_ATTRIBUTE)
					.append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE).append(this.mediaId)
					.append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE).append(SID_ATTRIBUTE).append(XMLUtils.EQUALS)
					.append(XMLUtils.SINGLE_QUOTE).append(this.sid).append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE)
					.append(XMLUtils.SPACE).append(XMLUtils.SLASH).append(XMLUtils.CLOSE_BRACKET);
		}

		sb.append(QUERY_CLOSE_TAG);

	}

	private void appendRoomsListResponse(StringBuilder sb) {
		sb.append(XMLUtils.OPEN_BRACKET).append(XML_ELM_NAME).append(XMLUtils.SPACE).append(XMLUtils.XMLNS_ATTRIBUTE)
				.append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE).append(XMPPNamespaces.DISCO_ITEM_NAMESPACE)
				.append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE).append(XMLUtils.CLOSE_BRACKET);

		if (!CollectionUtils.isNullOrEmpty(this.rooms)) {
			for (ChatRoom room : this.rooms) {
				sb.append(XMLUtils.OPEN_BRACKET).append(ITEM).append(XMLUtils.SPACE)

						.append(JID_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE)
						.append(room.getRoomJID().toString()).append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE)

						.append(NAME_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE)
						.append(room.getName()).append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE)

						.append(XMLUtils.SLASH).append(XMLUtils.CLOSE_BRACKET);
			}
		}

		sb.append(QUERY_CLOSE_TAG);
	}

	private void appendRoomItemsListResponse(StringBuilder sb) {
		sb.append(XMLUtils.OPEN_BRACKET).append(XML_ELM_NAME).append(XMLUtils.SPACE).append(XMLUtils.XMLNS_ATTRIBUTE)
				.append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE).append(XMPPNamespaces.DISCO_ITEM_NAMESPACE)
				.append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE).append(XMLUtils.CLOSE_BRACKET);

		if (!CollectionUtils.isNullOrEmpty(this.rooms)) {
			ChatRoom room = this.rooms.get(0);

			if (!CollectionUtils.isNullOrEmpty(room.getMembers())) {
				for (ChatRoomMember member : room.getMembers()) {
					sb.append(XMLUtils.OPEN_BRACKET).append(ITEM).append(XMLUtils.SPACE)

							.append(JID_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE)
							.append(member.getUserJID().toString()).append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE)

							.append(NAME_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE)
							.append(member.getNickName()).append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE)

							.append(AFFILIATION_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE)
							.append(member.getAffiliation().val()).append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE)

							.append(ROLE_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE)
							.append(member.getRole().val()).append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE)

							.append(XMLUtils.SLASH).append(XMLUtils.CLOSE_BRACKET);
				}
			}

		}

		sb.append(QUERY_CLOSE_TAG);
	}

	private void appendRoomInfoResponse(StringBuilder sb) {
		sb.append(XMLUtils.OPEN_BRACKET).append(XML_ELM_NAME).append(XMLUtils.SPACE).append(XMLUtils.XMLNS_ATTRIBUTE)
				.append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE).append(XMPPNamespaces.DISCO_INFO_NAMESPACE)
				.append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE).append(XMLUtils.CLOSE_BRACKET);

		if (!CollectionUtils.isNullOrEmpty(this.rooms)) {
			ChatRoom room = this.rooms.get(0);

			sb.append(XMLUtils.OPEN_BRACKET).append(IDENTITY).append(XMLUtils.SPACE).append(NAME_ATTRIBUTE)
					.append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE).append(room.getName())
					.append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE).append(XMLUtils.SLASH)
					.append(XMLUtils.CLOSE_BRACKET);

			sb.append(FORM_DATA_X_OPEN_TAG);

			sb.append(FIELD_ROOM_SUBJECT_OPEN_TAG).append(VALUE_OPEN_TAG);
			sb.append(room.getSubject());
			sb.append(VALUE_CLOSE_TAG).append(FIELD_CLOSE_TAG);

			sb.append(FIELD_ROOM_ACCESS_MODE_OPEN_TAG).append(VALUE_OPEN_TAG);
			sb.append(room.getAccessMode().val());
			sb.append(VALUE_CLOSE_TAG).append(FIELD_CLOSE_TAG);

			sb.append(X_CLOSE_TAG);
		}

		sb.append(QUERY_CLOSE_TAG);
	}

	private void appendUserDataQueryXML(StringBuilder sb) {
		sb.append(XMLUtils.OPEN_BRACKET).append(XML_ELM_NAME).append(XMLUtils.SPACE).append(XMLUtils.XMLNS_ATTRIBUTE)
				.append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE).append(XMPPNamespaces.USER_REGISTER_NAMESPACE)
				.append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE).append(XMLUtils.CLOSE_BRACKET)
				.append(REGISTERED_XML);

		if (!StringUtils.isNullOrEmpty(userRegistrationData.getUserName())) {
			sb.append(USER_NAME_OPEN_TAG).append(userRegistrationData.getUserName()).append(USER_NAME_CLOSE_TAG);
		}

		if (!StringUtils.isNullOrEmpty(userRegistrationData.getEmail())) {
			sb.append(USER_EMAIL_OPEN_TAG).append(userRegistrationData.getEmail()).append(USER_EMAIL_CLOSE_TAG);
		}

		sb.append(QUERY_CLOSE_TAG);
	}

	private StringBuilder appendRosterQueryXML(StringBuilder sb) {
		sb.append(XMLUtils.OPEN_BRACKET).append(XML_ELM_NAME).append(XMLUtils.SPACE).append(XMLUtils.XMLNS_ATTRIBUTE)
				.append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE).append(XMPPNamespaces.ROSTER_NAMESPACE)
				.append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);

		if (roster.getVersion() != 0) {
			sb.append(VER_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE).append(roster.getVersion())
					.append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);
		}

		sb.append(XMLUtils.CLOSE_BRACKET);

		if (!CollectionUtils.isNullOrEmpty(this.roster.getItems())) {
			for (RosterItem item : this.roster.getItems()) {
				sb.append(XMLUtils.OPEN_BRACKET).append(ITEM).append(XMLUtils.SPACE).append(JID_ATTRIBUTE)
						.append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE).append(item.getJid().getBareJID())
						.append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);

				if (!StringUtils.isNullOrEmpty(item.getName())) {
					sb.append(NAME_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE)
							.append(item.getName()).append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);
				}

				if (item.getSubscription() != null) {
					sb.append(SUBSCRIPTION_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE)
							.append(item.getSubscription().val()).append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);
				}

				sb.append(XMLUtils.SLASH).append(XMLUtils.CLOSE_BRACKET);
			}
		}

		sb.append(QUERY_CLOSE_TAG);

		return sb;
	}

	@Override
	public StringBuilder appendXml(StringBuilder sb) {
		sb.append(xml());
		return sb;
	}

}
