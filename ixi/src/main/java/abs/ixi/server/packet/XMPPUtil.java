package abs.ixi.server.packet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import abs.ixi.server.Stringflow;
import abs.ixi.server.packet.xmpp.CMAcknowledged;
import abs.ixi.server.packet.xmpp.CMDisplayed;
import abs.ixi.server.packet.xmpp.CMMarkable;
import abs.ixi.server.packet.xmpp.CMReceived;
import abs.ixi.server.packet.xmpp.CSNActive;
import abs.ixi.server.packet.xmpp.CSNComposing;
import abs.ixi.server.packet.xmpp.CSNGone;
import abs.ixi.server.packet.xmpp.CSNInactive;
import abs.ixi.server.packet.xmpp.CSNPaused;
import abs.ixi.server.packet.xmpp.MessageContent;
import abs.ixi.server.packet.xmpp.MessageDelay;
import abs.ixi.server.packet.xmpp.MessageMedia;
import abs.ixi.server.packet.xmpp.MessageThread;
import abs.ixi.server.packet.xmpp.Presence;
import abs.ixi.server.packet.xmpp.Message.MessageType;
import abs.ixi.server.packet.xmpp.MessageContent.MessageContentType;
import abs.ixi.server.session.UserPresence;
import abs.ixi.util.StringUtils;

public class XMPPUtil {

	public static final String INVALID_MECHANISM_XML = "<invalid-mechanism />".intern();

	public static final String INVALID_AUTH_JID_XML = "<invalid-authzid />".intern();

	private static Map<Integer, String> validationErrorResponseMap = new HashMap<Integer, String>();

	private static String DEFAULT_CHARSET = "UTF-8";

	public static final String XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"%s\" ?>";
	public static final String STREAM_START = "<stream:stream ";
	public static final String STREAM_ATTR_FROM = "from=\"%s\" ";
	public static final String STREAM_ATTR_TO = "to=\"%s\" ";
	public static final String STREAM_ATTR_ID = "id=\"%s\" ";
	public static final String STREAM_ATTR_VERSION = "version='1.0' ";
	public static final String STREAM_ATTR_LANG = "xml:lang='en' ";
	public static final String STREAM_NS_DEFAULT = "xmlns='jabber:client' ";
	public static final String STREAM_NS_STREAM = "xmlns:stream='http://etherx.jabber.org/streams' ";
	public static final String EMPTY_FEATURE = "<stream:features></stream:features>";
	public static final String FEATURE_START = "<stream:features>";
	public static final String FEATURE_END = "</stream:features>";
	public static final String STREAM_END = ">";
	public static final String AUTH_SUCCESS_RESPONSE = "<success xmlns='urn:ietf:params:xml:ns:xmpp-sasl'/>";

	public static final String JINGLE_SESSION_RESPONSE = "<iq from='%s' id='%s' to='%s' type='result'/>";

	public static final String MD5_NEXT_CHALLENGE = "<challenge xmlns='urn:ietf:params:xml:ns:xmpp-sasl'>%s</challenge>";

	public static final String TLS_START_STREAM_RESPONSE = "<stream:stream from=\"%s\" id=\"FAB2D7C85CC860BB\" version=\"1.0\""
			+ " xmlns:stream=\"http://etherx.jabber.org/streams\" xmlns=\"jabber:client\"><stream:features><starttls"
			+ " xmlns=\"urn:ietf:params:xml:ns:xmpp-tls\"><required/></starttls><mechanisms xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\">"
			+ "<mechanism>PLAIN</mechanism></mechanisms></stream:features>";

	public static final String TLS_START_FEATURE = "<stream:features><starttls xmlns='urn:ietf:params:xml:ns:xmpp-tls'><required/></starttls></stream:features>";

	public static final String RESOURCE_BIND_FEATURE = "<stream:features><bind xmlns='urn:ietf:params:xml:ns:xmpp-bind'/></stream:features>";

	public static final String BIND_AND_SM_FEATURE = "<stream:features><bind xmlns='urn:ietf:params:xml:ns:xmpp-bind'/><sm xmlns='urn:xmpp:sm:3'/></stream:features>";

	public static final String TLS_SUCCESS_RESPONSE = "<proceed xmlns='urn:ietf:params:xml:ns:xmpp-tls'/>";

	public static final String TLS_FAILURE_RESPONSE = "<failure xmlns='urn:ietf:params:xml:ns:xmpp-tls'/>";

	public static final String RESOURCE_BIND_SUCCESS_RESPONSE = "<iq id='%s' type='result'><bind xmlns='urn:ietf:params:xml:ns:xmpp-bind'><jid>%s</jid></bind></iq>";

	public static final String RESOURCE_BIND_FAILURE_RESPONSE = "<iq id='%s' type='error'>"
			+ "<error type='cancel'><not-allowed xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/></error></iq>";

	public static final String AUTH_FAILURE_RESPONSE = "<failure xmlns='urn:ietf:params:xml:ns:xmpp-sasl'>"
			+ "<not-authorized/></failure>";

	public static final String STREAM_NOT_FOUND_TO_RESUME_RESPONSE = "<failed xmlns='urn:xmpp:sm:3'>"
			+ "<item-not-found xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>" + "</failed>";

	public static final String BIND_RESPONSE_TAG = "<bind xmlns='urn:ietf:params:xml:ns:xmpp-bind'>";
	public static final String BIND_END_TAG = "</bind>";
	public static final String IQ_START_TAG = "<iq id='%s' type='%s'>";
	public static final String IQ_END_TAG = "</iq>";
	public static final String JID_START_TAG = "<jid>";
	public static final String JID_END_TAG = "</jid>";
	public static final String IQ_TAG = "<iq id='%s' type='%s'/>";
	public static final String PRESENCE_TAG = "<presence id='%s' from='%s' to='%s'/>";
	public static final String MESSAGE_TAG = "<message id='%s' from='%s' to='%s' type='%s'>%s</message>";
	public static final String STREAM_CLOSE_TAG = "</stream:stream>";
	public static final String SUCCESS_RESPONSE = "<iq id='%s' type='result'/>";

	public static final String STREAM_RESUME_SUCCESS_RESPONSE = "<resumed xmlns='urn:xmpp:sm:3' previd='%s' h='%s'/>";

	public static final String INVALID_ADDRESS_RESPONSE = "<stream:error><improper-addressing "
			+ "xmlns='urn:ietf:params:xml:ns:xmpp-streams'/></stream:error>";

	public static final String SASL_MECHANISM = "<mechanisms xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\">"
			+ "<mechanism>PLAIN</mechanism><required/></mechanisms>";

	public static final String SASL_FEATURE = "<stream:features><mechanisms xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\">"
			+ "<mechanism>PLAIN</mechanism><required/></mechanisms></stream:features>";

	public static final String SASL_ERROR_RESPONSE = "<failure xmlns='urn:ietf:params:xml:ns:xmpp-sasl'>"
			+ "<%s/></failure>";

	public static final String REGISTRATION_INFO = "<iq type='result' id='%s'>" + "<query xmlns='jabber:iq:register'>"
			+ "<instructions>" + "hoose a username and password for use with this service."
			+ "Please also provide your email address." + "</instructions>" + "<username/>" + "<password/>" + "<email/>"
			+ "<contactNo/>" + "</query>" + "</iq>";

	public static final String REGISTRATION_SUCCESS_RESPONSE = "<iq type='result' id='%s'/>";

	public static final String STANZA_TOO_BIG_ERROR_RESPONSE = "<stream:error>"
			+ "<policy-violation  xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>"
			+ "<stanza-too-big xmlns='urn:xmpp:errors'/>" + "</stream:error>";

	public static final String INVALID_TO_ADDRESS_RESPONSE = "<stream:error>"
			+ "<invalid-to xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>" + "</stream:error>";

	public static final String INVALID_FROM_ADDRESS_RESPONSE = "<stream:error>"
			+ "<invalid-from xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>" + "</stream:error>";

	public static final String BAD_FORMAT_RESPONSE = "<stream:error>"
			+ " <bad-format xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>" + "</stream:error>";

	public static final String INTERNAL_SERVER_ERROR_RESPONSE = "<stream:error>"
			+ "<internal-server-error xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>" + "</stream:error>";

	public static final String STREAM_HEADER_NOT_EXPECTED_ERROR_RESPONSE = "<stream:error>"
			+ "<not-well-formed xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>"
			+ "<text xml:lang='en' xmlns='urn:ietf:params:xml:ns:xmpp-streams'>" + "STREAM_HEADER_NOT_EXPECTED"
			+ "</text>" + "</stream:error>";

	public static final String STREAM_MANAGEMENT_NOT_EXPECTED_ERROR_RESPONSE = "<stream:error>"
			+ "<not-well-formed xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>"
			+ "<text xml:lang='en' xmlns='urn:ietf:params:xml:ns:xmpp-streams'>" + "STREAM_MANAGEMENT_NOT_EXPECTED"
			+ "</text>" + "</stream:error>";

	public static final String AUTH_NOT_EXPECTED_ERROR_RESPONSE = "<stream:error>"
			+ "<not-well-formed xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>"
			+ "<text xml:lang='en' xmlns='urn:ietf:params:xml:ns:xmpp-streams'>" + "AUTH_NOT_EXPECTED" + "</text>"
			+ "</stream:error>";

	public static final String IQ_NOT_EXPECTED_ERROR_RESPONSE = "<stream:error>"
			+ "<not-well-formed xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>"
			+ "<text xml:lang='en' xmlns='urn:ietf:params:xml:ns:xmpp-streams'>" + "IQ_NOT_EXPECTED" + "</text>"
			+ "</stream:error>";

	public static final String MESSAGE_NOT_EXPECTED_ERROR_RESPONSE = "<stream:error>"
			+ "<not-well-formed xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>"
			+ "<text xml:lang='en' xmlns='urn:ietf:params:xml:ns:xmpp-streams'>" + "MESSAGE_NOT_EXPECTED" + "</text>"
			+ "</stream:error>";

	public static final String PRESENCE_NOT_EXPECTED_ERROR_RESPONSE = "<stream:error>"
			+ "<not-well-formed xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>"
			+ "<text xml:lang='en' xmlns='urn:ietf:params:xml:ns:xmpp-streams'>" + "PRESENCE_NOT_EXPECTED" + "</text>"
			+ "</stream:error>";

	public static final String CHALLENGE_RESPONSE_NOT_EXPECTED_ERROR_RESPONSE = "<stream:error>"
			+ "<not-well-formed xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>"
			+ "<text xml:lang='en' xmlns='urn:ietf:params:xml:ns:xmpp-streams'>" + "PRESENCE_NOT_EXPECTED" + "</text>"
			+ "</stream:error>";

	public static final String USER_REGISTRATION_NOT_EXPECTED_ERROR_RESPONSE = "<stream:error>"
			+ "<not-well-formed xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>"
			+ "<text xml:lang='en' xmlns='urn:ietf:params:xml:ns:xmpp-streams'>" + "USER_REGISTRATION_NOT_EXPECTED"
			+ "</text>" + "</stream:error>";

	public static final String RESOURCE_BIND_EXPECTED_ERROR_RESPONSE = "<stream:error>"
			+ "<not-well-formed xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>"
			+ "<text xml:lang='en' xmlns='urn:ietf:params:xml:ns:xmpp-streams'>" + "RESOURCE_BIND_EXPECTED" + "</text>"
			+ "</stream:error>";

	public static final String SESSION_EXPECTED_ERROR_RESPONSE = "<stream:error>"
			+ "<not-well-formed xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>"
			+ "<text xml:lang='en' xmlns='urn:ietf:params:xml:ns:xmpp-streams'>" + " SESSION_EXPECTED" + "</text>"
			+ "</stream:error>";

	public static final String NOT_AUTHORISED_ERROR_RESPONSE = "<stream:error>"
			+ "<not-authorized xmlns='urn:ietf:params:xml:ns:xmpp-streams'/> " + "</stream:error>";

	public static final String IMPROPER_ADDRESSING_ERROR_RESPONSE = "<stream:error>" + "<improper-addressing"
			+ "xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>" + "</stream:error>";

	public static final String UNSUPPORTED_VERSION_ERROR_RESPONSE = "<stream:error>"
			+ "<unsupported-version xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>" + "</stream:error>";

	public static final String UNSUPPORTED_ENCODING_ERROR_RESPONSE = "<stream:error>"
			+ "<unsupported-encoding xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>" + "</stream:error>";

	public static final String INVALID_XML_ERROR_RESPONSE = "<stream:error>"
			+ "<invalid-xml xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>" + "</stream:error>";

	public static final String START_TLS_NOT_EXPECTED_ERROR_RESPONSE = "<stream:error>"
			+ "<not-well-formed xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>"
			+ "<text xml:lang='en' xmlns='urn:ietf:params:xml:ns:xmpp-streams'>" + "START_TLS_NOT_EXPECTED" + "</text>"
			+ "</stream:error>";

	public static final String ATTR_COUNT_LIMIT_EXCEEDED_ERROR_RESPONSE = "<stream:error>"
			+ "<not-well-formed xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>"
			+ "<text xml:lang='en' xmlns='urn:ietf:params:xml:ns:xmpp-streams'>" + "ATTR_COUNT_LIMIT_EXCEEDED"
			+ "</text>" + "</stream:error>";

	public static final String ATTR_NAME_LENGTH_LIMIT_EXCEEDED_ERROR_RESPONSE = "<stream:error>"
			+ "<not-well-formed xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>"
			+ "<text xml:lang='en' xmlns='urn:ietf:params:xml:ns:xmpp-streams'>" + "ATTR_NAME_LENGTH_LIMIT_EXCEEDED"
			+ "</text>" + "</stream:error>";

	public static final String ATTR_VALUE_LENGTH_LIMIT_EXCEEDED_ERROR_RESPONSE = "<stream:error>"
			+ "<not-well-formed xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>"
			+ "<text xml:lang='en' xmlns='urn:ietf:params:xml:ns:xmpp-streams'>" + "ATTR_VALUE_LENGTH_LIMIT_EXCEEDED"
			+ "</text>" + "</stream:error>";

	public static final String ELEMENT_NAME_SIZE_LIMIT_EXCEEDED_ERROR_RESPONSE = "<stream:error>"
			+ "<not-well-formed xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>"
			+ "<text xml:lang='en' xmlns='urn:ietf:params:xml:ns:xmpp-streams'>" + "ELEMENT_NAME_SIZE_LIMIT_EXCEEDED"
			+ "</text>" + "</stream:error>";

	public static final String SASL_RESPONSE_NOT_EXPECTED_ERROR_RESPONSE = "<stream:error>"
			+ "<not-well-formed xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>"
			+ "<text xml:lang='en' xmlns='urn:ietf:params:xml:ns:xmpp-streams'>" + "SASL_RESPONSE_NOT_EXPECTED"
			+ "</text>" + "</stream:error>";

	public static final String STREAM_RESUME_NOT_EXPECTED_ERROR_RESPONSE = "<stream:error>"
			+ "<not-well-formed xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>"
			+ "<text xml:lang='en' xmlns='urn:ietf:params:xml:ns:xmpp-streams'>"
			+ "STREAM_RESUME_NOT_EXPECTED_ERROR_RESPONSE" + "</text>" + "</stream:error>";

	public static final String ACK_NOT_EXPECTED_ERROR_RESPONSE = "<stream:error>"
			+ "<not-well-formed xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>"
			+ "<text xml:lang='en' xmlns='urn:ietf:params:xml:ns:xmpp-streams'>" + "ACK_NOT_EXPECTED" + "</text>"
			+ "</stream:error>";

	public static final String ACK_REQUEST_NOT_EXPECTED_ERROR_RESPONSE = "<stream:error>"
			+ "<not-well-formed xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>"
			+ "<text xml:lang='en' xmlns='urn:ietf:params:xml:ns:xmpp-streams'>" + "ACK_REQUEST_NOT_EXPECTED"
			+ "</text>" + "</stream:error>";

	public static final String IN_BAND_TRANSFER_SUCCESS_RESPONSE = "<iq from='%s'" + "id='%s'" + "to='%s'"
			+ "type='result'/>";

	public static final String PRESENCE_RESPONSE = "<presence from='%s' id='%s' to='%s' type='%s'/>";

	public static final String INVALID_MECHANISM_ERROR_RESPONSE = "<failure xmlns='urn:ietf:params:xml:ns:xmpp-sasl'>"
			+ "<invalid-mechanism /></failure>";

	public static final String INVALID_AUTHZID_ERROR_RESPONSE = "<failure xmlns='urn:ietf:params:xml:ns:xmpp-sasl'>"
			+ "<invalid-authzid/></failure>";

	public static final String CHALLANGE_RESPONSE = "<challenge xmlns='urn:ietf:params:xml:ns:xmpp-sasl'>%s</challenge>";

	public static final String SM_ENABLED_RESPONSE = "<enabled xmlns='urn:xmpp:sm:3' id='%s' resume='true' max='%s'/>";

	public static final String SM_ACK_RESPONSE = "<a xmlns='urn:xmpp:sm:3' h='%s' />";

	public static final String STREAM_RESUME_UNEXPECTED = "<failed xmlns='urn:xmpp:sm:3'> "
			+ " <unexpected-request xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/> </failed>";

	public static final String IQ_PING_XML = "<iq from='%s' to='%s' id='%s' type='get'>"
			+ " <ping xmlns='urn:xmpp:ping'/></iq> ";

	static {
		validationErrorResponseMap.put(XMPPError.STANZA_TOO_BIG.getCode(), STANZA_TOO_BIG_ERROR_RESPONSE);
		validationErrorResponseMap.put(XMPPError.BAD_FORMAT.getCode(), BAD_FORMAT_RESPONSE);
		validationErrorResponseMap.put(XMPPError.INVALID_TO_ADDRESS.getCode(), INVALID_TO_ADDRESS_RESPONSE);
		validationErrorResponseMap.put(XMPPError.INVALID_FROM_ADDRESS.getCode(), INVALID_FROM_ADDRESS_RESPONSE);
		validationErrorResponseMap.put(XMPPError.INTERNAL_SERVER.getCode(), INTERNAL_SERVER_ERROR_RESPONSE);
		validationErrorResponseMap.put(XMPPError.STREAM_HEADER_NOT_EXPECTED.getCode(),
				STREAM_HEADER_NOT_EXPECTED_ERROR_RESPONSE);
		validationErrorResponseMap.put(XMPPError.AUTH_NOT_EXPECTED.getCode(), AUTH_NOT_EXPECTED_ERROR_RESPONSE);
		validationErrorResponseMap.put(XMPPError.IQ_NOT_EXPECTED.getCode(), IQ_NOT_EXPECTED_ERROR_RESPONSE);
		validationErrorResponseMap.put(XMPPError.MESSAGE_NOT_EXPECTED.getCode(), MESSAGE_NOT_EXPECTED_ERROR_RESPONSE);
		validationErrorResponseMap.put(XMPPError.PRESENCE_NOT_EXPECTED.getCode(), PRESENCE_NOT_EXPECTED_ERROR_RESPONSE);
		validationErrorResponseMap.put(XMPPError.CHALLENGE_RESPONSE_NOT_EXPECTED.getCode(),
				CHALLENGE_RESPONSE_NOT_EXPECTED_ERROR_RESPONSE);
		validationErrorResponseMap.put(XMPPError.USER_REGISTRATION_NOT_EXPECTED.getCode(),
				USER_REGISTRATION_NOT_EXPECTED_ERROR_RESPONSE);
		validationErrorResponseMap.put(XMPPError.RESOURCE_BIND_NOT_EXPECTED.getCode(),
				RESOURCE_BIND_EXPECTED_ERROR_RESPONSE);
		validationErrorResponseMap.put(XMPPError.SESSION_EXPECTED.getCode(), SESSION_EXPECTED_ERROR_RESPONSE);
		validationErrorResponseMap.put(XMPPError.NOT_AUTHORISED.getCode(), NOT_AUTHORISED_ERROR_RESPONSE);
		validationErrorResponseMap.put(XMPPError.IMPROPER_ADDRESSING.getCode(), IMPROPER_ADDRESSING_ERROR_RESPONSE);
		validationErrorResponseMap.put(XMPPError.UNSUPPORTED_ENCODING.getCode(), UNSUPPORTED_ENCODING_ERROR_RESPONSE);
		validationErrorResponseMap.put(XMPPError.UNSUPPORTED_VERSION.getCode(), UNSUPPORTED_VERSION_ERROR_RESPONSE);
		validationErrorResponseMap.put(XMPPError.INVALID_XML.getCode(), INVALID_XML_ERROR_RESPONSE);
		validationErrorResponseMap.put(XMPPError.START_TLS_NOT_EXPECTED.getCode(),
				START_TLS_NOT_EXPECTED_ERROR_RESPONSE);

		validationErrorResponseMap.put(XMPPError.STREAM_MANAGEMENT_NOT_EXPECTED.getCode(),
				STREAM_MANAGEMENT_NOT_EXPECTED_ERROR_RESPONSE);

		validationErrorResponseMap.put(XMPPError.ATTR_COUNT_LIMIT_EXCEEDED.getCode(),
				ATTR_COUNT_LIMIT_EXCEEDED_ERROR_RESPONSE);
		validationErrorResponseMap.put(XMPPError.ATTR_NAME_LENGTH_LIMIT_EXCEEDED.getCode(),
				ATTR_NAME_LENGTH_LIMIT_EXCEEDED_ERROR_RESPONSE);
		validationErrorResponseMap.put(XMPPError.ATTR_VALUE_LENGTH_LIMIT_EXCEEDED.getCode(),
				ATTR_VALUE_LENGTH_LIMIT_EXCEEDED_ERROR_RESPONSE);
		validationErrorResponseMap.put(XMPPError.ELEMENT_NAME_SIZE_LIMIT_EXCEEDED.getCode(),
				ELEMENT_NAME_SIZE_LIMIT_EXCEEDED_ERROR_RESPONSE);
		validationErrorResponseMap.put(XMPPError.INVALID_AUTHZID.getCode(), INVALID_AUTHZID_ERROR_RESPONSE);
		validationErrorResponseMap.put(XMPPError.INVALID_MECHANISM.getCode(), INVALID_MECHANISM_ERROR_RESPONSE);
		validationErrorResponseMap.put(XMPPError.SASL_RESPONSE_NOT_EXPECTED.getCode(),
				SASL_RESPONSE_NOT_EXPECTED_ERROR_RESPONSE);

		validationErrorResponseMap.put(XMPPError.STREAM_RESUME_NOT_EXPECTED.getCode(),
				STREAM_RESUME_NOT_EXPECTED_ERROR_RESPONSE);

		validationErrorResponseMap.put(XMPPError.ACK_NOT_EXPECTED.getCode(), ACK_NOT_EXPECTED_ERROR_RESPONSE);

		validationErrorResponseMap.put(XMPPError.ACK_REQUEST_NOT_EXPECTED.getCode(),
				ACK_REQUEST_NOT_EXPECTED_ERROR_RESPONSE);
	}

	public static String getInBandFileTransferSuccessResponse(JID from, JID to, String id) {
		return String.format(IN_BAND_TRANSFER_SUCCESS_RESPONSE, from.getBareJID(), to, id);
	}

	public static String getDeclaration() {
		return String.format(XML_DECLARATION, DEFAULT_CHARSET);
	}

	public static String getDeclaration(String charset) {
		return String.format(XML_DECLARATION, charset);
	}

	public static String getPresenceTag(String id, JID from, String to) {
		return String.format(PRESENCE_TAG, id, from.getFullJID(), to);

	}

	public static String getMessageTag(String id, JID from, JID to, MessageType type, List<MessageContent> contents) {
		StringBuilder sb = new StringBuilder(" ");

		for (MessageContent content : contents) {
			if (content.isContentType(MessageContentType.BODY)) {
				sb.append("<body>").append(content.toString()).append("</body>");

			} else if (content.isContentType(MessageContentType.SUBJECT)) {
				sb.append("<subject>").append(content.toString()).append("</subject>");

			} else if (content.isContentType(MessageContentType.THREAD)) {
				MessageThread thread = (MessageThread) content;
				sb.append("<thread>").append(thread.getThreadId()).append("</thread>");

			} else if (content.isContentType(MessageContentType.CM_MARKABLE)) {
				CMMarkable markable = (CMMarkable) content;
				sb.append("<").append(CMMarkable.XML_ELM_NAME).append(" ").append("xmlns='").append(markable.getXmlns())
						.append("'").append(" />");

			} else if (content.isContentType(MessageContentType.CM_RECEIVED)) {
				CMReceived received = (CMReceived) content;
				sb.append("<").append(CMReceived.XML_ELM_NAME).append(" ").append("xmlns='").append(received.getXmlns())
						.append("'").append(" />");

			} else if (content.isContentType(MessageContentType.CM_ACKNOWLEDGED)) {
				CMAcknowledged acknowledged = (CMAcknowledged) content;
				sb.append("<").append(CMAcknowledged.XML_ELM_NAME).append(" ").append("xmlns='")
						.append(acknowledged.getXmlns()).append("'").append(" />");

			} else if (content.isContentType(MessageContentType.CM_DISPLAYED)) {
				CMDisplayed displayed = (CMDisplayed) content;
				sb.append("<").append(CMDisplayed.XML_ELM_NAME).append(" ").append("xmlns='")
						.append(displayed.getXmlns()).append("'").append(" />");

			} else if (content.isContentType(MessageContentType.CSN_ACTIVE)) {
				CSNActive active = (CSNActive) content;
				sb.append("<").append(CSNActive.XML_ELM_NAME).append(" ").append("xmlns='").append(active.getXmlns())
						.append("'").append(" />");

			} else if (content.isContentType(MessageContentType.CSN_INACTIVE)) {
				CSNInactive inactive = (CSNInactive) content;
				sb.append("<").append(CSNInactive.XML_ELM_NAME).append(" ").append("xmlns='")
						.append(inactive.getXmlns()).append("'").append(" />");

			} else if (content.isContentType(MessageContentType.CSN_COMPOSING)) {
				CSNComposing composing = (CSNComposing) content;
				sb.append("<").append(CSNComposing.XML_ELM_NAME).append(" ").append("xmlns='")
						.append(composing.getXmlns()).append("'").append(" />");

			} else if (content.isContentType(MessageContentType.CSN_PAUSED)) {
				CSNPaused paused = (CSNPaused) content;
				sb.append("<").append(CSNPaused.XML_ELM_NAME).append(" ").append("xmlns='").append(paused.getXmlns())
						.append("'").append(" />");

			} else if (content.isContentType(MessageContentType.CSN_GONE)) {
				CSNGone gone = (CSNGone) content;
				sb.append("<").append(CSNGone.XML_ELM_NAME).append(" ").append("xmlns='").append(gone.getXmlns())
						.append("'").append(" />");

			} else if (content.isContentType(MessageContentType.DELAY)) {
				MessageDelay delay = (MessageDelay) content;
				sb.append("<").append(MessageDelay.XML_ELM_NAME).append(" ").append("xmlns='").append(delay.getXmlns())
						.append("' ").append("stamp='").append(delay.getStamp()).append("'").append(" />");

			} else if (content.isContentType(MessageContentType.MEDIA)) {
				MessageMedia media = (MessageMedia) content;
				sb.append("<media id='").append(media.getMediaId()).append("'>");

				if (media.getContentType() != null) {
					sb.append("<content-type>").append(media.getContentType().getMimeType()).append("</content-type>");
				}

				if (StringUtils.isNullOrEmpty(media.getThumb())) {
					sb.append("<thumb>").append(media.getThumb()).append("</thumb>");
				}

				sb.append("</media>");

			}
		}

		return String.format(MESSAGE_TAG, id, from.getFullJID(), to.getBareJID(), type.val(), sb.toString());
	}

	public static String getStreamStartResponse(JID to, String streamId) {
		StringBuilder streamXML = new StringBuilder().append(getDeclaration()).append(STREAM_START)
				.append(getFromAttribute(Stringflow.runtime().jid())).append(getStreamId(streamId));

		if (to != null) {
			streamXML.append(getToAttribute(to));
		}

		streamXML.append(STREAM_ATTR_VERSION).append(STREAM_ATTR_LANG).append(STREAM_NS_DEFAULT)
				.append(STREAM_NS_STREAM).append(">");

		return streamXML.toString();
	}

	public static String getTLSfeatureResponse() {
		return TLS_START_FEATURE;
	}

	public static String getSASLFeatureResponse() {
		return SASL_FEATURE;
	}

	public static String getResourceBindFeatureResponse() {
		return RESOURCE_BIND_FEATURE;
	}

	public static String getBindAndSMFeatureResponse() {
		return BIND_AND_SM_FEATURE;
	}

	public static String getTlsStreamStartResponse(JID to, JID from) {
		return String.format(TLS_START_STREAM_RESPONSE, from.getBareJID());
	}

	private static final String getIQTag(String id, String type) {
		return String.format(IQ_TAG, id, type);
	}

	private static final String getFromAttribute(JID from) {
		return String.format(STREAM_ATTR_FROM, from.getBareJID());
	}

	private static final String getToAttribute(JID to) {
		return String.format(STREAM_ATTR_TO, to.getBareJID());
	}

	private static final String getStreamId(String streamId) {
		return String.format(STREAM_ATTR_ID, streamId);
	}

	public static String getChallengeResponse(String challenge) {
		return String.format(CHALLANGE_RESPONSE, challenge);
	}

	public static String getMD5AuthChallenge() {
		// Add mechanism to authenticate the client
		String authXML = new StringBuilder().append(AUTH_SUCCESS_RESPONSE).toString();
		return authXML;

	}

	public static String getSASLErrorResponse(String error) {
		return String.format(SASL_ERROR_RESPONSE, error);
	}

	public static String getNextMD5AuthChallenge(String challenge) {
		// Add mechanism to authenticate the client
		String authXML = new StringBuilder().append(String.format(MD5_NEXT_CHALLENGE, challenge)).toString();
		return authXML;

	}

	public static String getSASLSuccessResponse() {
		return AUTH_SUCCESS_RESPONSE;
	}

	public static String getSASLFailureResponse() {
		return AUTH_FAILURE_RESPONSE;

	}

	public static String getJingleSessionResponse(String id, JID to, JID from) {
		return String.format(JINGLE_SESSION_RESPONSE, id, to.getFullJID(), from.getFullJID());

	}

	public static String getSuccessResponse(String iqId) {
		return String.format(SUCCESS_RESPONSE, iqId);
	}

	public static String getPingSuccessResponse(String id) {
		String pingResponseXML = new StringBuilder().append(getIQTag(id, "result")).toString();
		return pingResponseXML;
	}

	public static String getRosterSuccessResponse(String iqId, String to) {
		StringBuilder sb = new StringBuilder();
		sb = sb.append("<iq to='" + to + "' from='" + Stringflow.runtime().jid().getBareJID() + "' type='result' id='"
				+ iqId + "' />");

		return sb.toString();
	}

	// public static String getRosterPushResponse(String iqId, String to,
	// Roster1 item, int version) {
	// StringBuilder sb = new StringBuilder();
	//
	// sb = sb.append("<iq to='" + to + "' from='" +
	// StringflowServer.getInstance().getJID().getBareJID()
	// + "' type='set' id='" + iqId + "'>").append("<query
	// xmlns='jabber:iq:roster' ver='" + version + "' >");
	//
	// sb.append("<item jid='" + item.getItemJID().getBareJID() + "' name='" +
	// item.getItemName() + "' subscription='"
	// + item.getSubscription().name() + "' />");
	//
	// sb = sb.append("</query>").append("</iq>");
	//
	// return sb.toString();
	// }

	// public static String getFullRosterResponse(String iqId, String to,
	// List<Roster1> userFullRoster,
	// int currrentVersion) {
	// StringBuilder sb = new StringBuilder();
	//
	// sb = sb.append("<iq to='" + to + "' from='" +
	// StringflowServer.getInstance().getJID().getBareJID()
	// + "' type='result' id='" + iqId + "'>");
	//
	// if (!CollectionUtils.isNullOrEmpty(userFullRoster)) {
	// sb.append("<query xmlns='jabber:iq:roster' ver='" + currrentVersion + "'
	// >");
	//
	// for (Roster1 item : userFullRoster) {
	// sb.append("<item jid='" + item.getItemJID().getBareJID() + "' name='" +
	// item.getItemName()
	// + "' subscription='" + item.getSubscription().val() + "' />");
	// }
	//
	// sb = sb.append("</query>");
	// } else {
	// sb.append("<query xmlns='jabber:iq:roster' ver='" + currrentVersion + "'
	// />");
	// }
	//
	// sb.append("</iq>");
	//
	// return sb.toString();
	// }

	public static String getRosterNoElementResponse(String iqId, String to, int version) {
		StringBuilder sb = new StringBuilder();

		sb = sb.append("<iq to='" + to + "' from='" + Stringflow.runtime().jid().getBareJID() + "' type='result' id='"
				+ iqId + "'>").append("<query xmlns='jabber:iq:roster' ver='" + version + "' />");

		sb = sb.append("</iq>");

		return sb.toString();
	}

	public static String getMessage(String id, JID from, JID to, MessageType type, List<MessageContent> contents) {
		return getMessageTag(id, from, to, type, contents);
	}

	public static String getPresenceResponse(String id, JID from, String to) {
		return getPresenceTag(id, from, to);
	}

	public static String getAddOrDelItemResponse(String id, JID to) {
		return String.format("<iq id='%s' to='%s' type='result'/>", id, to.getFullJID());
	}

	public static String getRegistrationInfoResponse(String id) {
		return String.format(REGISTRATION_INFO, id);
	}

	public static String getRegistrationResponse(String id) {
		return String.format(REGISTRATION_SUCCESS_RESPONSE, id);
	}

	public static String getProfileDetailResponse(String iqId, String userName, String email, String contactNo,
			int profileId) {
		StringBuilder sb = new StringBuilder();

		sb = sb.append("<iq type='result' id='" + iqId + "'>");

		sb = sb.append("<registered/>");

		if (!StringUtils.isNullOrEmpty(userName)) {
			sb = sb.append("<username>" + userName + "<username>");
		}

		if (!StringUtils.isNullOrEmpty(email)) {
			sb = sb.append("<email>" + email + "<email>");
		}

		if (!StringUtils.isNullOrEmpty(contactNo)) {
			sb = sb.append("<contactNo>" + contactNo + "<contactNo>");
		}

		sb = sb.append("<profileId>" + profileId + "<profileId>");

		sb = sb.append("</query>");
		sb = sb.append("</iq>");

		return sb.toString();
	}

	public static String getStreamCloseResponse(String errorResponse) {
		StringBuilder sb = new StringBuilder();

		if (!StringUtils.isNullOrEmpty(errorResponse))
			sb.append(errorResponse);

		sb.append(STREAM_CLOSE_TAG);

		return sb.toString();
	}

	public static String getXMPPErrorResponse(XMPPError error) {

		return validationErrorResponseMap.get(error.getCode());
	}

	public static String getBindResourceSuccessResponse(String iqId, String from) {

		return String.format(RESOURCE_BIND_SUCCESS_RESPONSE, iqId, from);
	}

	public static String getBindResourceFailureResponse(String iqId) {

		return String.format(RESOURCE_BIND_FAILURE_RESPONSE, iqId);
	}

	public static String getTLSSuccessResponse() {

		return TLS_SUCCESS_RESPONSE;
	}

	public static String getTLSFailureResponse() {

		return TLS_FAILURE_RESPONSE;
	}

	public static String getInvalidAdderssResponse() {
		return INVALID_ADDRESS_RESPONSE;
	}

	public static String getPresenceResponse(String from, String id, String to, String type) {

		return String.format(PRESENCE_RESPONSE, from, id, to, type);
	}

	public static String buildPresenceString(Presence presence) {
		StringBuilder sb = new StringBuilder();
		sb.append("<presence");

		if (!StringUtils.isNullOrEmpty(presence.getId())) {
			sb.append(" id='").append(presence.getId()).append("'");
		}

		if (presence.getFrom() != null) {
			sb.append(" from='").append(presence.getFrom().getFullJID()).append("'");
		}

		if (presence.getTo() != null) {
			sb.append(" to='").append(presence.getTo().getBareJID()).append("'");
		}

		if (presence.getType() != null) {
			sb.append(" type='").append(presence.getType().val()).append("'");
		}

		sb.append(">");

		if (presence.getStatus() != null) {
			sb.append(" <show>").append(presence.getStatus().val()).append("</show>");
		}

		if (!StringUtils.isNullOrEmpty(presence.getMood())) {
			sb.append(" <status>").append(presence.getMood()).append("</status>");
		}

		sb.append(" </presence>");

		return sb.toString();
	}

	public static String buildPresenceString(JID userJID, String subscriber, UserPresence userPresence) {
		StringBuilder sb = new StringBuilder();
		sb.append("<presence");

		sb.append(" from='").append(subscriber).append("'");

		sb.append(" to='").append(userJID.getFullJID()).append("'");

		if (userPresence != null) {
			if (!userPresence.isOnline()) {
				sb.append(" type='").append("unavailable").append("'");
			}

			sb.append(">");

			if (userPresence.getStatus() != null) {
				sb.append(" <show>").append(userPresence.getStatus().val()).append("</show>");
			}

			if (!StringUtils.isNullOrEmpty(userPresence.getMood())) {
				sb.append(" <status>").append(userPresence.getMood()).append("</status>");
			}

			sb.append(" </presence>");

		} else {
			sb.append(" type='").append("unavailable").append("'").append("/>");
		}

		return sb.toString();
	}

	public static String getAckPacketString(int handledStanzaCount) {

		return String.format(SM_ACK_RESPONSE, handledStanzaCount);
	}

	public static String getNotFoundStreamResponse() {
		return STREAM_NOT_FOUND_TO_RESUME_RESPONSE;
	}

	public static String streamResumeSuccessResponse(String streamId, int handledStanzaCount) {
		return String.format(STREAM_RESUME_SUCCESS_RESPONSE, streamId, handledStanzaCount);
	}

	public static String getSmEnabledStream(String id, int maxResumptionTime) {
		return String.format(SM_ENABLED_RESPONSE, id, maxResumptionTime);
	}

	public static String smUnExpectedRequest() {
		return STREAM_RESUME_UNEXPECTED;
	}

	public static String getPingXmlString(String fromJID, String toJID, String pingId) {
		return String.format(IQ_PING_XML, fromJID, toJID, pingId);
	}

}
