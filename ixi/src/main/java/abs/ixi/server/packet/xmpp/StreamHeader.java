package abs.ixi.server.packet.xmpp;

import java.util.ArrayList;
import java.util.List;

import abs.ixi.server.packet.InvalidJabberId;
import abs.ixi.server.packet.JID;
import abs.ixi.server.packet.Namespace;
import abs.ixi.util.StringUtils;
import abs.ixi.xml.Element;
import abs.ixi.xml.XMLUtils;

public class StreamHeader extends XMPPPacket {
	private static final long serialVersionUID = -762427024977566835L;

	public static final PacketXmlElement XML_ELM_NAME = PacketXmlElement.STREAM_HEADER;

	private static final String ID_ATTRIBUTE = "id".intern();
	private static final String TO_ATTRIBUTE = "to".intern();
	private static final String FROM_ATTRIBUTE = "from".intern();

	private static final String DEFAULT_VERSION = "1.0".intern();
	private static final String DEFAULT_LANGUAGE = "en".intern();

	private static final String STREAM_HEADER_CLOSE_TAG = "</stream:stream>".intern();

	private static final String STREAM_HEADER_OPEN_TAG_WITH_TO = "<?xml version='1.0'?>"
			+ "<stream:stream from='%s' id='%s' to='%s' version='1.0' xml:lang='en' "
			+ "xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams'>".intern();

	private static final String STREAM_HEADER_OPEN_TAG_WITHOUT_TO = "<?xml version='1.0'?>"
			+ "<stream:stream from='%s' id='%s' version='1.0' xml:lang='en' "
			+ "xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams'>".intern();

	private static final StreamHeader CLOSE_STREAM_PACKET = new StreamHeader(true);

	private JID from;
	private JID to;
	private String streamId;
	private String version;
	private String defaultNamespace;
	private List<Namespace> nameSpaces;
	private String language;
	private boolean closeStream;

	private String sourceId;

	public StreamHeader() {
		super();
		this.language = DEFAULT_LANGUAGE;
		this.version = DEFAULT_VERSION;
	}

	public StreamHeader(boolean closeStream) {
		this();
		this.closeStream = closeStream;
	}

	public StreamHeader(Element element) throws InvalidJabberId {
		super(element);

		if (!StringUtils.isNullOrEmpty(element.getAttribute(FROM_ATTRIBUTE))) {
			this.from = new JID(element.getAttribute(FROM_ATTRIBUTE));
		}

		if (!StringUtils.isNullOrEmpty(element.getAttribute(TO_ATTRIBUTE))) {
			this.to = new JID(element.getAttribute(TO_ATTRIBUTE));
		}

		if (!StringUtils.isNullOrEmpty(element.getAttribute(XMLUtils.XMLNS_ATTRIBUTE))) {
			this.defaultNamespace = element.getAttribute(XMLUtils.XMLNS_ATTRIBUTE);
		}

		if (!StringUtils.isNullOrEmpty(element.getAttribute(ID_ATTRIBUTE))) {
			this.streamId = element.getAttribute(ID_ATTRIBUTE);
		}
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDefaultNamespace() {
		return defaultNamespace;
	}

	public void setDefaultNamespace(String defaultNamespace) {
		this.defaultNamespace = defaultNamespace;
	}

	public List<Namespace> getNameSpaces() {
		return nameSpaces;
	}

	public void setNameSpaces(List<Namespace> nameSpaces) {
		this.nameSpaces = nameSpaces;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getStreamId() {
		return streamId;
	}

	public void setStreamId(String id) {
		this.streamId = id;
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

	public boolean addNamespace(Namespace ns) {
		if (nameSpaces == null) {
			nameSpaces = new ArrayList<>();
		}

		return nameSpaces.add(ns);
	}

	public void setCloseStream(boolean closeStream) {
		this.closeStream = closeStream;
	}

	@Override
	public boolean isCloseStream() {
		return closeStream;
	}

	@Override
	public String getSourceId() {
		return this.sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public static final StreamHeader getCloseStreamHeader() {
		return CLOSE_STREAM_PACKET;
	}

	@Override
	public PacketXmlElement getXmlElementName() {
		return XML_ELM_NAME;
	}

	@Override
	public String xml() {
		if (isCloseStream()) {
			return STREAM_HEADER_CLOSE_TAG;

		} else {
			if (this.to != null)
				return String.format(STREAM_HEADER_OPEN_TAG_WITH_TO, this.from.getFullJID(), this.streamId,
						this.to.getFullJID());

			return String.format(STREAM_HEADER_OPEN_TAG_WITHOUT_TO, this.from.getFullJID(), this.streamId);
		}
	}

}
