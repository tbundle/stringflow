package abs.ixi.server.packet.xmpp;

import java.util.ArrayList;
import java.util.List;

import abs.ixi.server.io.MalformedXMPPRequestException;
import abs.ixi.server.packet.InvalidJabberId;
import abs.ixi.server.packet.MDRRequest;
import abs.ixi.server.packet.XMPPNamespaces;
import abs.ixi.server.packet.xmpp.MessageContent.MessageContentType;
import abs.ixi.util.CollectionUtils;
import abs.ixi.util.StringUtils;
import abs.ixi.xml.Element;
import abs.ixi.xml.XMLUtils;

/**
 * Represents xmpp message stanza. According to XMPP protocol, message MAY
 * contain any extended element; therefore
 * 
 * @param <T> type of the content held by this message packet
 */
public class Message extends Stanza {
	private static final long serialVersionUID = -7898086163058062260L;

	public static final PacketXmlElement XML_ELM_NAME = PacketXmlElement.MESSAGE;

	private static final String TYPE_ATTRIBUTE = "type";
	private static final String MESSAGE_CLOSE_TAG = "</message>";

	private MessageType type;
	private List<MessageContent> contents;

	public Message() {
		super();
	}

	public Message(String id) {
		this.id = id;
	}

	public Message(Element element) throws InvalidJabberId, MalformedXMPPRequestException {
		super(element);

		if (this.element.getAttribute(TYPE_ATTRIBUTE) != null) {
			setType(MessageType.valueFrom(this.element.getAttribute(TYPE_ATTRIBUTE)));
		}

		generateContent(element);

	}

	private void generateContent(Element element) throws MalformedXMPPRequestException {
		for (Element elm : element.getChildren()) {
			if (StringUtils.safeEquals(elm.getName(), MessageBody.XML_ELM_NAME)) {
				this.addContent(new MessageBody(elm));

			} else if (StringUtils.safeEquals(elm.getName(), MessageSubject.XML_ELM_NAME)) {
				this.addContent(new MessageSubject(elm));

			} else if (StringUtils.safeEquals(elm.getName(), MessageThread.XML_ELM_NAME)) {
				this.addContent(new MessageThread(elm));

			} else if (StringUtils.safeEquals(elm.getName(), MessageMedia.XML_ELM_NAME)) {
				this.addContent(new MessageMedia(elm));

			} else if (StringUtils.safeEquals(elm.getName(), MDRRequest.XML_ELM_NAME)) {
				this.addContent(new MDRRequest(elm));

			} else if (StringUtils.safeEquals(elm.getName(), MDRReceived.XML_ELM_NAME) && StringUtils.safeEquals(
					elm.getAttribute(XMLUtils.XMLNS_ATTRIBUTE), XMPPNamespaces.MESSAGE_DELIVERY_RECEIPT_NAMESPACE)) {
				this.addContent(new MDRReceived(elm));

			} else if (StringUtils.safeEquals(elm.getName(), CMMarkable.XML_ELM_NAME)) {
				this.addContent(new CMMarkable(elm));

			} else if (StringUtils.safeEquals(elm.getName(), CMReceived.XML_ELM_NAME) && StringUtils
					.safeEquals(elm.getAttribute(XMLUtils.XMLNS_ATTRIBUTE), XMPPNamespaces.CHAT_MARKER_NAMESPACE)) {
				this.addContent(new CMReceived(elm));

			} else if (StringUtils.safeEquals(elm.getName(), CMDisplayed.XML_ELM_NAME)) {
				this.addContent(new CMDisplayed(elm));

			} else if (StringUtils.safeEquals(elm.getName(), CMAcknowledged.XML_ELM_NAME)) {
				this.addContent(new CMAcknowledged(elm));

			} else if (StringUtils.safeEquals(elm.getName(), CSNActive.XML_ELM_NAME)) {
				this.addContent(new CSNActive(elm));

			} else if (StringUtils.safeEquals(elm.getName(), CSNInactive.XML_ELM_NAME)) {
				this.addContent(new CSNInactive(elm));

			} else if (StringUtils.safeEquals(elm.getName(), CSNComposing.XML_ELM_NAME)) {
				this.addContent(new CSNComposing(elm));

			} else if (StringUtils.safeEquals(elm.getName(), CSNPaused.XML_ELM_NAME)) {
				this.addContent(new CSNPaused(elm));

			} else if (StringUtils.safeEquals(elm.getName(), CSNGone.XML_ELM_NAME)) {
				this.addContent(new CSNGone(elm));

			}
		}

	}

	public List<MessageContent> getContents() {
		return contents;
	}

	public void setContents(List<MessageContent> contents) {
		this.contents = contents;
	}

	public void addContent(MessageContent content) {
		if (this.contents == null)
			this.contents = new ArrayList<MessageContent>();

		this.contents.add(content);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public boolean haveDelayContent() {
		if (!CollectionUtils.isNullOrEmpty(this.getContents())) {

			for (MessageContent messageContent : this.getContents()) {
				if (messageContent.isContentType(MessageContentType.DELAY))
					return true;
			}

		}

		return false;
	}

	public boolean isNotifyableMessage() {
		if (!CollectionUtils.isNullOrEmpty(this.getContents())) {

			for (MessageContent content : this.getContents()) {
				if (content.isContentType(MessageContentType.BODY) || content.isContentType(MessageContentType.MEDIA)) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean isInsurancedDeliveryRequired() {
		if (!CollectionUtils.isNullOrEmpty(this.getContents())) {

			for (MessageContent content : this.getContents()) {

				if (content.isContentType(MessageContentType.BODY)
						|| content.isContentType(MessageContentType.CM_DISPLAYED)
						|| content.isContentType(MessageContentType.CM_RECEIVED)
						|| content.isContentType(MessageContentType.MDR_RECEIVED)
						|| content.isContentType(MessageContentType.CM_ACKNOWLEDGED)
						|| content.isContentType(MessageContentType.SUBJECT)
						|| content.isContentType(MessageContentType.MEDIA)) {
					return true;
				}

			}
		}

		return false;
	}

	@Override
	public PacketXmlElement getXmlElementName() {
		return XML_ELM_NAME;
	}

	@Override
	public String xml() {
		StringBuilder sb = new StringBuilder();

		sb.append(XMLUtils.OPEN_BRACKET).append(XML_ELM_NAME.elementNameString()).append(XMLUtils.SPACE)
				.append(TYPE_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE).append(this.type.val())
				.append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);

		if (this.id != null) {
			sb.append(ID_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE).append(this.id)
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

		sb.append(XMLUtils.CLOSE_BRACKET);

		if (!CollectionUtils.isNullOrEmpty(contents)) {
			for (MessageContent content : contents) {
				content.appendXml(sb);
			}
		}

		sb.append(MESSAGE_CLOSE_TAG);

		return sb.toString();
	}

	public enum MessageType {
		CHAT("chat"), 
		
		GROUP_CHAT("groupchat");

		private String val;

		private MessageType(String val) {
			this.val = val;
		}

		public String val() {
			return this.val;
		}

		public static MessageType valueFrom(String val) throws IllegalArgumentException {
			for (MessageType type : values()) {
				if (type.val().equalsIgnoreCase(val)) {
					return type;
				}
			}
			throw new IllegalArgumentException("No MsgType for value [" + val + "]");
		}
	}

}
