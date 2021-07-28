package abs.ixi.server.packet;

public interface XMPPNamespaces {
	public static String STREAM_NAMESPACE = "http://etherx.jabber.org/streams";
	public static String RESOURCE_BIND_NAMESPACE = "urn:ietf:params:xml:ns:xmpp-bind";
	public static String ROSTER_NAMESPACE = "jabber:iq:roster";
	public static String USER_REGISTER_NAMESPACE = "jabber:iq:register";
	public static String SESSION_NAMESPACE = "urn:ietf:params:xml:ns:xmpp-session";
	public static String MUC_NAMESPACE = "http://jabber.org/protocol/muc";
	public static String DISCO_INFO_NAMESPACE = "http://jabber.org/protocol/disco#info";
	public static String DISCO_ITEM_NAMESPACE = "http://jabber.org/protocol/disco#items";
	public static String MUC_ADMIN_NAMESPACE = "http://jabber.org/protocol/muc#admin";
	public static String MUC_OWNER_NAMESPACE = "http://jabber.org/protocol/muc#owner";
	public static String JABBER_X_DATA = "jabber:x:data";
	public static String ROMM_CONFIG_ACCESS_MODE = "muc#roomconfig_accessmode";
	public static String DELAY_NAMESPACE = "urn:xmpp:delay";
	public static String CHAT_MARKER_NAMESPACE = "urn:xmpp:chat-markers:0";
	public static String CHAT_STATE_NOTIFICATION_NAMESPACE = "http://jabber.org/protocol/chatstates";
	public static String STREAM_MANAGEMENT_NAMESPACE = "urn:xmpp:sm:3";
	public static String TLS_NAMESPACE = "urn:ietf:params:xml:ns:xmpp-tls";
	public static String SASL_NAMESPACE = "urn:ietf:params:xml:ns:xmpp-sasl";
	public static String STRINGFLOW_MEDIA_NAMESPACE = "stringflow:media";
	public static String VCARD_NAME_SPACE = "vcard-temp";
	public static String VCARD_UPDATE_NAMESPACE = "vcard-temp:x:update";
	public static String JABBER_SEARCH_NAMESPACE = "jabber:iq:search";
	public static String MESSAGE_DELIVERY_RECEIPT_NAMESPACE = "urn:xmpp:receipts";
}
