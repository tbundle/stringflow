package abs.ixi.server.packet.xmpp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.io.MalformedXMPPRequestException;
import abs.ixi.server.packet.InvalidJabberId;
import abs.ixi.server.packet.JID;
import abs.ixi.server.packet.XMPPNamespaces;
import abs.ixi.server.packet.xmpp.IQContent.IQContentType;
import abs.ixi.util.StringUtils;
import abs.ixi.util.UUIDGenerator;
import abs.ixi.xml.Element;
import abs.ixi.xml.XMLUtils;

/**
 * Representation of IQ stanza as stated in XMPP protocol.
 */
public class IQ extends Stanza {
	private static final long serialVersionUID = 9191356439730855037L;

	private static final Logger LOGGER = LoggerFactory.getLogger(IQ.class);

	public static final PacketXmlElement XML_ELM_NAME = PacketXmlElement.IQ;

	private static final String SID_ATTRIBUTE = "sid".intern();
	private static final String TYPE_ATTRIBUTE = "type".intern();
	private static final String IQ_CLOSE_TAG = "</iq>".intern();

	private IQType type;
	private IQContent content;
	private String sid;

	public IQ(String id, IQType type) {
		this(id, type, null);
	}

	public IQ(String id, IQType type, IQContent content) {
		super();
		this.id = id;
		this.type = type;
		this.content = content;
	}

	public IQ(Element element) throws InvalidJabberId, MalformedXMPPRequestException {
		super(element);

		this.sid = this.element.getAttribute(SID_ATTRIBUTE);

		if (!StringUtils.isNullOrEmpty(this.element.getAttribute(TYPE_ATTRIBUTE))) {
			this.type = IQType.valueFrom(this.element.getAttribute(TYPE_ATTRIBUTE));
		}

		this.content = generateContent(element);
	}

	private IQContent generateContent(Element element) throws InvalidJabberId, MalformedXMPPRequestException {
		Element elm;

		if ((elm = this.element.getChild(Request.XML_ELM_NAME)) != null) {
			return new Request(elm);

		} else if ((elm = this.element.getChild(Response.XML_ELM_NAME)) != null) {
			return new Response(elm);

		} else if ((elm = this.element.getChild(IQQuery.XML_ELM_NAME)) != null) {
			return new IQQuery(elm, from, to, type);

		} else if ((elm = this.element.getChild(IQVCardContent.XML_ELM_NAME)) != null) {
			return new IQVCardContent(elm);

		} else if ((elm = this.element.getChild(IQInbandOpen.XML_ELM_NAME)) != null) {
			return new IQInbandOpen(elm);

		} else if ((elm = this.element.getChild(IQInbandData.XML_ELM_NAME)) != null) {
			return new IQInbandData(elm);

		} else if ((elm = this.element.getChild(IQInbandClose.XML_ELM_NAME)) != null) {
			return new IQInbandClose(elm);

		} else if ((elm = this.element.getChild(IQPing.XML_ELM_NAME)) != null) {
			return new IQPing(elm);

		} else if ((elm = this.element.getChild(IQResourceBind.XML_ELM_NAME)) != null) {
			return new IQResourceBind(elm);

		} else if ((elm = this.element.getChild(IQSession.XML_ELM_NAME)) != null) {
			return new IQSession(elm);

		} else if ((elm = this.element.getChild(IQPushRegistration.XML_ELM_NAME)) != null) {
			return new IQPushRegistration(elm);
		}

		LOGGER.info("IQ query stanza does not contain supported child tags . so escaping.");

		return null;
	}

	public IQType getType() {
		return type;
	}

	public void setType(IQType type) {
		this.type = type;
	}

	public String getSid() {
		return this.sid;
	}

	public IQContent getContent() {
		return content;
	}

	public void setContent(IQContent content) {
		this.content = content;
	}

	public enum IQType {
		SET("set"), GET("get"), RESULT("result"), ERROR("error");

		String val;

		private IQType(String val) {
			this.val = val;
		}

		public String val() {
			return val;
		}

		public static IQType valueFrom(String val) throws IllegalArgumentException {
			for (IQType type : values()) {
				if (type.val().equalsIgnoreCase(val)) {
					return type;
				}
			}

			throw new IllegalArgumentException("No IQType for value [" + val + "]");
		}
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

		if (this.getType() != null) {
			sb.append(TYPE_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE).append(this.getType().val())
					.append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);
		}

		sb.append(XMLUtils.CLOSE_BRACKET);

		if (this.content != null) {
			this.content.appendXml(sb);
		}

		sb.append(IQ_CLOSE_TAG);

		return sb.toString();
	}

	public static IQ getPingPacket(JID toJID) {
		IQ iq = new IQ(UUIDGenerator.secureId(), IQType.SET);
		iq.setTo(toJID);

		IQPing ping = new IQPing();
		iq.setContent(ping);

		return iq;
	}

	@Override
	public boolean isRoutable() {
		return this.type == IQType.RESULT ? false : true;
	}

	@Override
	public PacketXmlElement getXmlElementName() {
		return XML_ELM_NAME;
	}

	public boolean isMediaRequestIQ() {
		if (this.type == IQType.GET && this.content != null && this.content.getType() == IQContentType.QUERY) {
			IQQuery query = (IQQuery) content;

			if (StringUtils.safeEquals(query.getXmlns(), XMPPNamespaces.STRINGFLOW_MEDIA_NAMESPACE, false)) {

				return query.getMediaId() != null && query.getSid() != null;
			}

		}

		return false;
	}

	@Override
	public boolean isInsurancedDeliveryRequired() {
		return !this.to.isFullJId();
	}

}
