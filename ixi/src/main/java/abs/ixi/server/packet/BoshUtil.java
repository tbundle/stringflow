package abs.ixi.server.packet;

public class BoshUtil {
	public static final String WAIT = "60";
	public static final String INACTIVITY = "30";
	public static final String POLLING = "5";
	public static final String HOLD = "1";
	public static final String REQUESTS = "2";
	public static final String MAX_PAUSE = "120";
	public static final String SESSION_BODY = "<body wait='%s' " + "inactivity='%s' " + "polling='%s' "
			+ "requests='%s' " + "hold='%s' " + "ack='%s' " + "accept='deflate,gzip' " + "maxpause='%s' " + "sid='%s' "
			+ "charsets='ISO_8859-1 ISO-2022-JP' " + "ver='1.6' " + "from='%s' "
			+ "xmlns='http://jabber.org/protocol/httpbind'>%s</body>\r\n";
	public static final String SESSION_RESPONSE = "HTTP/1.1 200 OK\r\n" + "Content-Type: text/xml; charset=utf-8\r\n"
			+ "Content-Length: %s\r\n\r\n" + "%s";

	public static final String BOSH_RESPONSE = "HTTP/1.1 200 OK\r\n" + "Content-Type: text/xml; charset=utf-8\r\n"
			+ "Content-Length: %s\r\n\r\n" + "%s\r\n";

	public static final String EMPTY_RESPOSNE = "HTTP/1.1 200 OK\r\n" + "Content-Type: text/xml; charset=utf-8\r\n"
			+ "Content-Length: 52\r\n\r\n"

			+ "<body xmlns='http://jabber.org/protocol/httpbind'/>";

	public static final String STREAM_FEATURES = "<stream:features>"
			+ "<mechanisms xmlns='urn:ietf:params:xml:ns:xmpp-sasl'>" + "<mechanism>PLAIN</mechanism>" + "</mechanisms>"
			+ "</stream:features>";

	public static String getSessionBody(String rid, String from, String sessionId) {
		return String.format(SESSION_BODY, WAIT, INACTIVITY, POLLING, REQUESTS, HOLD, rid, MAX_PAUSE, sessionId, from,
				STREAM_FEATURES);
	}

	public static String getSessionResponse(String body) {
		return String.format(SESSION_RESPONSE, body.length(), body);
	}

	public static String getBoshResponse(String body) {
		return String.format(BOSH_RESPONSE, body.length(), body);
	}
}
