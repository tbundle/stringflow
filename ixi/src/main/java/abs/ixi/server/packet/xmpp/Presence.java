
package abs.ixi.server.packet.xmpp;

import abs.ixi.server.muc.ChatRoom.ChatRoomMember;
import abs.ixi.server.packet.InvalidJabberId;
import abs.ixi.server.packet.XMPPNamespaces;
import abs.ixi.util.StringUtils;
import abs.ixi.xml.Element;
import abs.ixi.xml.XMLUtils;

public class Presence extends Stanza {
	private static final long serialVersionUID = -3191704613068292244L;

	public static final String MUC_SERVICE_DOMAIN = "chat.alterbasics.com";

	public static final PacketXmlElement XML_ELM_NAME = PacketXmlElement.PRESENCE;

	private static final String TYPE_ATTRIBUTE = "type";

	private static final String SHOW_OPEN_TAG = "<show>";
	private static final String SHOW_CLOSE_TAG = "</show>";
	private static final String STATUS_OPEN_TAG = "<status>";
	private static final String STATUS_CLOSE_TAG = "</status>";
	private static final String PRESENCE_CLOSE_TAG = "</presence>";
	private static final String X_OPEN_TAG = "<x xmlns='http://jabber.org/protocol/muc#user'>";
	private static final String X_CLOSE_TAG = "</x>";

	private static final String ITEM = "item";
	private static final String AFFILIATION_ATTRIBUTE = "affiliation";
	private static final String ROLE_ATTRIBUTE = "role";
	private static final String JID = "jid";
	private static final String NICK_ATTRIBUTE = "nick";

	private static final String X = "x";
	private static final String SHOW = "show";
	private static final String STATUS = "status";

	private static final String PHOTO = "photo";
	private static final String X_WITH_VCARD_UPDATE_XML = "<x xmlns='vcard-temp:x:update'/>";
	private static final String X_WITH_VCARD_OPEN_TAG = "<x xmlns='vcard-temp:x:update'>";
	private static final String PHOTO_XML = "<photo />";
	private static final String PHOTO_OPEN_TAG = "<photo>";
	private static final String PHOTO_CLOSE_TAG = "</photo>";

	public static final String INITIAL_PRESENCE_XML = "<presence />";

	private static final Presence PRESENCE_OFFLINE = new Presence(PresenceType.UNAVAILABLE);

	private PresenceType type;
	private PresenceStatus status;
	private String mood;

	private ChatRoomMember roomMember;
	private boolean muc;

	private String photoHash;
	private boolean isPhotoUpdate;
	private boolean isVCardUpdate;

	private boolean isError;
	private String errorXml;

	public Presence() {
		this(PresenceType.AVAILABLE);
	}

	public Presence(PresenceType pType) {
		super();
		this.type = pType;
	}

	@Override
	public void setTo(abs.ixi.server.packet.JID to) {
		// TODO Auto-generated method stub
		super.setTo(to);
	}

	public Presence(Element element) throws InvalidJabberId {
		super(element);

		if (this.element.getAttribute(TYPE_ATTRIBUTE) != null) {
			this.type = PresenceType.valueFrom(this.element.getAttribute(TYPE_ATTRIBUTE));

		} else {
			this.type = PresenceType.AVAILABLE;
		}

		// TODO: Correct it. By getting muc service name from configration
		// manager
		if (this.to != null && StringUtils.safeEquals(getTo().getDomain(), MUC_SERVICE_DOMAIN)) {

			this.muc = true;
		}

		generateContent(element);
	}

	private void generateContent(Element element) {
		if (element.getChildren() == null)
			return;

		for (Element elm : element.getChildren()) {
			if (StringUtils.safeEquals(elm.getName(), STATUS, false)) {
				this.mood = elm.val();

			} else if (StringUtils.safeEquals(elm.getName(), SHOW, false)) {
				this.status = PresenceStatus.valueFrom(elm.val());

			} else if (StringUtils.safeEquals(elm.getName(), X, false)) {

				if (StringUtils.safeEquals(elm.getAttribute(XMLUtils.XMLNS_ATTRIBUTE),
						XMPPNamespaces.VCARD_UPDATE_NAMESPACE, false)) {

					this.isVCardUpdate = true;

					Element photoElement = elm.getChild(PHOTO);

					if (photoElement != null) {
						this.isPhotoUpdate = true;
						this.photoHash = photoElement.val();
					}

				} else {

					this.muc = true;
				}

			}

		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public PresenceType getType() {
		return type;
	}

	public void setType(PresenceType type) {
		this.type = type;
	}

	public PresenceStatus getStatus() {
		return status;
	}

	public void setStatus(PresenceStatus status) {
		this.status = status;
	}

	public String getMood() {
		return mood;
	}

	public void setMood(String mood) {
		this.mood = mood;
	}

	public boolean isMuc() {
		return muc;
	}

	public void setMuc(boolean muc) {
		this.muc = muc;
	}

	public ChatRoomMember getRoomMember() {
		return roomMember;
	}

	public void setRoomMember(ChatRoomMember roomMember) {
		this.roomMember = roomMember;
	}

	public boolean isError() {
		return isError;
	}

	public void setError(boolean isError) {
		this.isError = isError;
	}

	public String getErrorXml() {
		return errorXml;
	}

	public void setErrorXml(String errorXml) {
		this.errorXml = errorXml;
	}

	public boolean isInitialPresence() {
		return (this.id == null && this.getTo() == null && this.getType() == PresenceType.AVAILABLE
				&& this.status == null && this.mood == null) ? true : false;
	}

	/**
	 * Return a {@link Presence} pacjet with {@link PresenceType#UNAVAILABLE}.
	 * <p>
	 * <b>Please note, this packet is shared across components; therefore MUST
	 * be treated as immutable </b>
	 * </p>
	 */
	public static Presence getOfflinePresence() {
		return PRESENCE_OFFLINE;
	}

	@Override
	public boolean isInsurancedDeliveryRequired() {
		if (this.to.isFullJId()) {
			return false;
		}

		if (isMuc() || isVCardUpdate) {
			return true;
		}

		if (this.type != null && this.type != PresenceType.AVAILABLE && this.type != PresenceType.UNAVAILABLE
				&& this.type != PresenceType.ERROR)
			return true;

		return false;
	}

	@Override
	public String xml() {
		StringBuilder sb = new StringBuilder();
		sb.append(XMLUtils.OPEN_BRACKET).append(XML_ELM_NAME.elementNameString()).append(XMLUtils.SPACE);

		if (!StringUtils.isNullOrEmpty(this.getId())) {
			sb.append(ID_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE).append(this.getId())
					.append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);
		}

		if (this.getFrom() != null) {
			sb.append(FROM_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE)
					.append(this.getFrom().getFullJID()).append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);
		}

		if (this.getTo() != null) {
			sb.append(TO_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE)
					.append(this.getTo().getFullJID()).append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);
		}

		if (this.getType() != null && this.getType() != PresenceType.AVAILABLE) {
			sb.append(TYPE_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE).append(this.getType().val())
					.append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);
		}

		sb.append(XMLUtils.CLOSE_BRACKET);

		if (this.getStatus() != null) {
			sb.append(SHOW_OPEN_TAG).append(this.getStatus().val()).append(SHOW_CLOSE_TAG);
		}

		if (!StringUtils.isNullOrEmpty(this.getMood())) {
			sb.append(STATUS_OPEN_TAG).append(this.getMood()).append(STATUS_CLOSE_TAG);
		}

		if (this.isMuc()) {
			sb.append(X_OPEN_TAG);

			if (this.roomMember != null) {
				sb.append(XMLUtils.OPEN_BRACKET).append(ITEM).append(XMLUtils.SPACE).append(JID).append(XMLUtils.EQUALS)
						.append(XMLUtils.SINGLE_QUOTE).append(roomMember.getUserJID().toString())
						.append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);

				if (roomMember.getAffiliation() != null) {
					sb.append(AFFILIATION_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE)
							.append(roomMember.getAffiliation().val()).append(XMLUtils.SINGLE_QUOTE)
							.append(XMLUtils.SPACE);
				}

				if (roomMember.getRole() != null) {
					sb.append(ROLE_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE)
							.append(roomMember.getRole().val()).append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);
				}

				if (roomMember.getNickName() != null) {
					sb.append(NICK_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE)
							.append(roomMember.getNickName()).append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);
				}

				sb.append(XMLUtils.SLASH).append(XMLUtils.CLOSE_BRACKET);
			}

			sb.append(X_CLOSE_TAG);

		}

		if (this.isVCardUpdate) {
			if (this.isPhotoUpdate) {

				sb.append(X_WITH_VCARD_OPEN_TAG);

				if (StringUtils.isNullOrEmpty(this.photoHash)) {
					sb.append(PHOTO_XML);

				} else {
					sb.append(PHOTO_OPEN_TAG).append(this.photoHash).append(PHOTO_CLOSE_TAG);
				}

				sb.append(X_CLOSE_TAG);

			} else {
				sb.append(X_WITH_VCARD_UPDATE_XML);
			}
		}

		if (this.isError && this.errorXml != null) {
			sb.append(this.errorXml);
		}

		sb.append(PRESENCE_CLOSE_TAG);

		return sb.toString();
	}

	public enum PresenceType {
		ERROR("error "),

		PROBE("probe"),

		SUBSCRIBE("subscribe"),

		SUBSCRIBED("subscribed"),

		UNAVAILABLE("unavailable"),

		AVAILABLE("available"),

		UNSUBSCRIBE("unsubscribe"),

		UNSUBSCRIBED("unsubscribed");

		private String val;

		private PresenceType(String val) {
			this.val = val;
		}

		public String val() {
			return val;
		}

		public static PresenceType valueFrom(String val) throws IllegalArgumentException {
			for (PresenceType type : values()) {
				if (type.val().equalsIgnoreCase(val)) {
					return type;
				}
			}

			throw new IllegalArgumentException("No PresenceType for value [" + val + "]");
		}
	}

	public enum PresenceStatus {
		AWAY("away"), CHAT("chat"), DND("dnd"), XA("xa");

		private String val;

		private PresenceStatus(String val) {
			this.val = val;
		}

		public String val() {
			return val;
		}

		public static PresenceStatus valueFrom(String val) {
			for (PresenceStatus type : values()) {
				if (type.val().equalsIgnoreCase(val)) {
					return type;
				}
			}

			throw new IllegalArgumentException("No PresenceStatus for value [" + val + "]");
		}
	}

	@Override
	public PacketXmlElement getXmlElementName() {
		return XML_ELM_NAME;
	}

}
