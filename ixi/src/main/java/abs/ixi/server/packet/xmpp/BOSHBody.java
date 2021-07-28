package abs.ixi.server.packet.xmpp;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import abs.ixi.server.ByteConvertible;
import abs.ixi.server.XMLConvertible;
import abs.ixi.server.common.ChannelStream;
import abs.ixi.server.packet.JID;
import abs.ixi.server.packet.XmppPacketFactory;
import abs.ixi.util.CollectionUtils;
import abs.ixi.util.StringUtils;
import abs.ixi.xml.Element;
import abs.ixi.xml.XMLUtils;

/**
 * Represents Http Request body.
 */
public class BOSHBody extends XMPPPacket implements ByteConvertible, XMLConvertible {
	private static final long serialVersionUID = 735006563715611709L;

	public static final PacketXmlElement XML_ELM_NAME = PacketXmlElement.BOSH_BODY;

	private static final String XMLNS = "http://jabber.org/protocol/httpbind";
	private static final String XMLNS_XMPP = "urn:xmpp:xbosh";
	private static final String XMLNS_STREAM = "http://etherx.jabber.org/streams";

	private static final String TRUE = "true";

	private static final String SID_ATTRIBUTE = "sid".intern();
	private static final String RID_ATTRIBUTE = "rid".intern();
	private static final String FROM_ATTRIBUTE = "from".intern();
	private static final String TO_ATTRIBUTE = "to".intern();
	private static final String HOLD_ATTRIBUTE = "hold".intern();
	private static final String WAIT_ATTRIBUTE = "wait".intern();
	private static final String ACK_ATTRIBUTE = "ack".intern();
	private static final String POLLING_ATTRIBUTE = "polling".intern();
	private static final String INACTIVITY_ATTRIBUTE = "inactivity".intern();
	private static final String REQUESTS_ATTRIBUTE = "requests".intern();
	private static final String MAXPAUSE_ATTRIBUTE = "maxpause".intern();
	private static final String CONTENT_ATTRIBUTE = "content".intern();
	private static final String VERSION_ATTRIBUTE = "xmpp:version".intern();
	private static final String ACCEPT_ATTRIBUTE = "accept".intern();
	private static final String CHARSETS_ATTRIBUTE = "charsets".intern();
	private static final String XMLNS_ATTRIBUTE = "xmlns".intern();
	private static final String XMLNS_XMPP_ATTRIBUTE = "xmlns:xmpp".intern();
	private static final String XMLNS_STREAM_ATTRIBUTE = "xmlns:stream".intern();
	private static final String AUTH_ID_ATTRIBUTE = "authid".intern();
	private static final String TYPE_ATTRIBUTE = "type".intern();
	private static final String CONDITION_ATTRIBUTE = "condition".intern();
	private static final String XMPP_RESTART_ATTRIBUTE = "xmpp:restart".intern();

	private static final String BODY_CLOSE_TAG = "</body>";

	private String sid;
	private String rid;
	private JID from;
	private JID to;
	private Type type;
	private int hold;
	private int wait;
	private int ack;
	private int pollingTime;
	private int inactivityTime;
	private int maxpause;
	private int requests;
	private String contentType;
	private String condition;
	private String ver;
	private String accept;
	private String charsets;
	private String authId;
	private String xmlns;
	private String xmlnsXmpp;
	private String xmlnsStream;
	private boolean streamRestart;
	private boolean emptyBodyResponse;
	private List<XMPPPacket> xmppPackets;

	public BOSHBody() {
		super();
		this.xmppPackets = new ArrayList<XMPPPacket>();
	}

	public BOSHBody(Element element) throws Exception {
		this();

		if (!StringUtils.isNullOrEmpty(element.getAttribute(SID_ATTRIBUTE))) {
			this.sid = element.getAttribute(SID_ATTRIBUTE);

		}

		if (!StringUtils.isNullOrEmpty(element.getAttribute(RID_ATTRIBUTE))) {
			this.rid = element.getAttribute(RID_ATTRIBUTE);

		}

		if (!StringUtils.isNullOrEmpty(element.getAttribute(FROM_ATTRIBUTE))) {
			this.from = new JID(element.getAttribute(FROM_ATTRIBUTE));

		}

		if (!StringUtils.isNullOrEmpty(element.getAttribute(TYPE_ATTRIBUTE))) {
			this.type = Type.valueOf(element.getAttribute(TYPE_ATTRIBUTE));

		}

		if (!StringUtils.isNullOrEmpty(element.getAttribute(CONDITION_ATTRIBUTE))) {
			this.condition = element.getAttribute(CONDITION_ATTRIBUTE);

		}
		if (!StringUtils.isNullOrEmpty(element.getAttribute(TO_ATTRIBUTE))) {
			this.to = new JID(element.getAttribute(TO_ATTRIBUTE));

		}

		if (!StringUtils.isNullOrEmpty(element.getAttribute(HOLD_ATTRIBUTE))) {
			this.hold = Integer.parseInt(element.getAttribute(HOLD_ATTRIBUTE));

		}

		if (!StringUtils.isNullOrEmpty(element.getAttribute(WAIT_ATTRIBUTE))) {
			this.wait = Integer.parseInt(element.getAttribute(WAIT_ATTRIBUTE));

		}

		if (!StringUtils.isNullOrEmpty(element.getAttribute(POLLING_ATTRIBUTE))) {
			this.pollingTime = Integer.parseInt(element.getAttribute(POLLING_ATTRIBUTE));

		}

		if (!StringUtils.isNullOrEmpty(element.getAttribute(INACTIVITY_ATTRIBUTE))) {
			this.inactivityTime = Integer.parseInt(element.getAttribute(INACTIVITY_ATTRIBUTE));

		}

		if (!StringUtils.isNullOrEmpty(element.getAttribute(ACK_ATTRIBUTE))) {
			this.ack = Integer.parseInt(element.getAttribute(ACK_ATTRIBUTE));

		}

		if (!StringUtils.isNullOrEmpty(element.getAttribute(MAXPAUSE_ATTRIBUTE))) {
			this.maxpause = Integer.parseInt(element.getAttribute(MAXPAUSE_ATTRIBUTE));

		}

		if (!StringUtils.isNullOrEmpty(element.getAttribute(REQUESTS_ATTRIBUTE))) {
			this.requests = Integer.parseInt(element.getAttribute(REQUESTS_ATTRIBUTE));

		}

		if (!StringUtils.isNullOrEmpty(element.getAttribute(XMLNS_ATTRIBUTE))) {
			this.xmlns = element.getAttribute(XMLNS_ATTRIBUTE);

		}

		if (!StringUtils.isNullOrEmpty(element.getAttribute(XMLNS_STREAM_ATTRIBUTE))) {
			this.xmlnsStream = element.getAttribute(XMLNS_STREAM_ATTRIBUTE);

		}
		if (!StringUtils.isNullOrEmpty(element.getAttribute(XMLNS_XMPP_ATTRIBUTE))) {
			this.xmlnsXmpp = element.getAttribute(XMLNS_XMPP_ATTRIBUTE);

		}

		if (!StringUtils.isNullOrEmpty(element.getAttribute(AUTH_ID_ATTRIBUTE))) {
			this.authId = element.getAttribute(AUTH_ID_ATTRIBUTE);

		}

		if (!StringUtils.isNullOrEmpty(element.getAttribute(CONTENT_ATTRIBUTE))) {
			this.contentType = element.getAttribute(CONTENT_ATTRIBUTE);

		}

		if (!StringUtils.isNullOrEmpty(element.getAttribute(CHARSETS_ATTRIBUTE))) {
			this.charsets = element.getAttribute(CHARSETS_ATTRIBUTE);

		}

		if (!StringUtils.isNullOrEmpty(element.getAttribute(VERSION_ATTRIBUTE))) {
			this.ver = element.getAttribute(VERSION_ATTRIBUTE);

		}

		if (!StringUtils.isNullOrEmpty(element.getAttribute(ACCEPT_ATTRIBUTE))) {
			this.accept = element.getAttribute(ACCEPT_ATTRIBUTE);

		}

		if (!StringUtils.isNullOrEmpty(element.getAttribute(XMPP_RESTART_ATTRIBUTE))) {
			String restart = element.getAttribute(XMPP_RESTART_ATTRIBUTE);

			if (StringUtils.safeEquals(restart, TRUE, false))
				this.streamRestart = true;
		}

		if (!CollectionUtils.isNullOrEmpty(element.getChildren())) {
			for (Element child : element.getChildren()) {
				XMPPPacket xmppPacket = XmppPacketFactory.createPacket(child);
				this.xmppPackets.add(xmppPacket);
			}
		}

	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getRid() {
		return rid;
	}

	public void setRid(String rid) {
		this.rid = rid;
	}

	public JID getFrom() {
		return from;
	}

	public void setFrom(JID from) {
		this.from = from;
	}

	public JID getTo() {
		return to;
	}

	public void setTo(JID to) {
		this.to = to;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getVer() {
		return ver;
	}

	public void setVer(String ver) {
		this.ver = ver;
	}

	public int getHold() {
		return hold;
	}

	public void setHold(int hold) {
		this.hold = hold;
	}

	public int getWait() {
		return wait;
	}

	public void setWait(int wait) {
		this.wait = wait;
	}

	public int getAck() {
		return ack;
	}

	public void setAck(int ack) {
		this.ack = ack;
	}

	public int getPollingTime() {
		return pollingTime;
	}

	public void setPollingTime(int pollingTime) {
		this.pollingTime = pollingTime;
	}

	public int getInactivityTime() {
		return inactivityTime;
	}

	public void setInactivityTime(int inactivityTime) {
		this.inactivityTime = inactivityTime;
	}

	public int getMaxpause() {
		return maxpause;
	}

	public void setMaxpause(int maxpause) {
		this.maxpause = maxpause;
	}

	public int getRequests() {
		return requests;
	}

	public void setRequests(int requests) {
		this.requests = requests;
	}

	public String getAccept() {
		return accept;
	}

	public void setAccept(String accept) {
		this.accept = accept;
	}

	public String getCharsets() {
		return charsets;
	}

	public void setCharsets(String charsets) {
		this.charsets = charsets;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getAuthId() {
		return authId;
	}

	public void setAuthId(String authId) {
		this.authId = authId;
	}

	public boolean isStreamRestart() {
		return streamRestart;
	}

	public void setStreamRestart(boolean streamRestart) {
		this.streamRestart = streamRestart;
	}

	public String getXmlns() {
		return xmlns;
	}

	public void setXmlns(String xmlns) {
		this.xmlns = xmlns;
	}

	public String getXmlnsXmpp() {
		return xmlnsXmpp;
	}

	public void setXmlnsXmpp(String xmlnsXmpp) {
		this.xmlnsXmpp = xmlnsXmpp;
	}

	public String getXmlnsStream() {
		return xmlnsStream;
	}

	public void setXmlnsStream(String xmlnsStream) {
		this.xmlnsStream = xmlnsStream;
	}

	public boolean isEmptyBodyResponse() {
		return emptyBodyResponse;
	}

	public void setEmptyBodyResponse(boolean emptyBodyResponse) {
		this.emptyBodyResponse = emptyBodyResponse;
	}

	public List<XMPPPacket> getXmppPackets() {
		return xmppPackets;
	}

	public void setXmppPackets(List<XMPPPacket> xmppPackets) {
		this.xmppPackets = xmppPackets;
	}

	public void addXmppPacket(XMPPPacket xmppPacket) {
		this.xmppPackets.add(xmppPacket);
	}

	@Override
	public String getSourceId() {
		return null;
	}

	@Override
	public long writeTo(Socket socket) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long writeTo(SocketChannel socketChannel) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long writeTo(OutputStream os) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long writeTo(ChannelStream cs) throws IOException {
		byte[] bytes = this.getBytes();

		if (bytes != null && bytes.length > 0) {
			cs.enqueue(getBytes());
		}

		return bytes.length;
	}

	@Override
	public boolean hasMime() {
		return false;
	}

	public boolean isCloseStream() {
		return this.type != null && this.type == Type.TERMINATE ? true : false;
	}

	public boolean isStreamStartRequest() {

		return StringUtils.isNullOrEmpty(this.sid) && CollectionUtils.isNullOrEmpty(this.xmppPackets) ? true : false;
	}

	public boolean isEmptyBodyRequest() {

		return (!StringUtils.isNullOrEmpty(this.sid)) && CollectionUtils.isNullOrEmpty(this.xmppPackets)
				&& (!isCloseStream()) && (!isStreamRestart()) ? true : false;
	}

	public enum Type {
		TERMINATE("terminate"), ERROR("error");

		private String value;

		Type(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}
	}

	@Override
	public String xml() {
		StringBuilder sb = new StringBuilder();
		appendXml(sb);
		return sb.toString();
	}

	@Override
	public StringBuilder appendXml(StringBuilder sb) {
		sb.append(XMLUtils.OPEN_BRACKET).append(XML_ELM_NAME.elementNameString()).append(XMLUtils.SPACE);

		if (!StringUtils.isNullOrEmpty(this.sid)) {
			sb.append(SID_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE).append(this.sid)
					.append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);
		}

		if (!StringUtils.isNullOrEmpty(this.rid)) {
			sb.append(RID_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE).append(this.rid)
					.append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);
		}

		if (this.from != null) {
			sb.append(FROM_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE)
					.append(this.from.getFullJID()).append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);
		}

		if (this.to != null) {
			sb.append(TO_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE).append(this.to.getFullJID())
					.append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);
		}

		sb.append(XMLNS_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE).append(XMLNS)
				.append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);

		sb.append(XMLNS_STREAM_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE).append(XMLNS_STREAM)
				.append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);

		sb.append(XMLNS_XMPP_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE).append(XMLNS_XMPP)
				.append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);

		if (this.emptyBodyResponse) {

			sb.append(XMLUtils.SLASH).append(XMLUtils.CLOSE_BRACKET);

		} else {
			if (this.type != null) {
				sb.append(TYPE_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE)
						.append(this.type.getValue()).append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);
			}

			if (this.hold != 0) {
				sb.append(HOLD_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE).append(this.hold)
						.append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);
			}

			if (this.wait != 0) {
				sb.append(WAIT_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE).append(this.wait)
						.append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);
			}

			if (this.inactivityTime != 0) {
				sb.append(INACTIVITY_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE)
						.append(this.inactivityTime).append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);
			}

			if (this.pollingTime != 0) {
				sb.append(POLLING_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE)
						.append(this.pollingTime).append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);
			}

			if (this.maxpause != 0) {
				sb.append(MAXPAUSE_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE)
						.append(this.maxpause).append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);
			}

			if (this.requests != 0) {
				sb.append(REQUESTS_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE)
						.append(this.requests).append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);
			}

			if (!StringUtils.isNullOrEmpty(this.contentType)) {
				sb.append(CONTENT_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE)
						.append(this.contentType).append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);
			}

			if (!StringUtils.isNullOrEmpty(this.condition)) {
				sb.append(CONDITION_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE)
						.append(this.condition).append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);
			}

			if (!StringUtils.isNullOrEmpty(this.ver)) {
				sb.append(VERSION_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE).append(this.ver)
						.append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);
			}

			if (!StringUtils.isNullOrEmpty(this.accept)) {
				sb.append(ACCEPT_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE).append(this.accept)
						.append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);
			}

			if (!StringUtils.isNullOrEmpty(this.authId)) {
				sb.append(AUTH_ID_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE).append(this.authId)
						.append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);
			}

			if (!StringUtils.isNullOrEmpty(this.charsets)) {
				sb.append(CHARSETS_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE)
						.append(this.charsets).append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);
			}

			sb.append(XMLUtils.CLOSE_BRACKET);

			if (!CollectionUtils.isNullOrEmpty(this.xmppPackets)) {
				for (XMPPPacket packet : this.xmppPackets) {
					packet.appendXml(sb);
				}
			}

			sb.append(BODY_CLOSE_TAG);
		}

		return sb;
	}

	@Override
	public byte[] getBytes() {
		if (StringUtils.isNullOrEmpty(this.xml())) {
			return null;
		}

		return this.xml().getBytes(StandardCharsets.UTF_8);
	}

	@Override
	public int getBytes(byte[] dest, int offset) {
		byte[] bytes = this.getBytes();

		if (bytes != null) {
			System.arraycopy(bytes, 0, dest, offset, bytes.length);
			offset += bytes.length;
		}

		return offset;
	}

	@Override
	public boolean isBoshBodyPacket() {
		return true;
	}

}
