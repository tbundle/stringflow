package abs.ixi.server.packet.xmpp;

import java.util.ArrayList;
import java.util.List;

import abs.ixi.server.XMLConvertible;
import abs.ixi.server.packet.JID;
import abs.ixi.util.CollectionUtils;
import abs.ixi.util.StringUtils;
import abs.ixi.xml.Element;
import abs.ixi.xml.XMLUtils;

public class IQJingle extends AbstractIQContent {
    private static final long serialVersionUID = 1L;

    public static final String XML_ELM_NAME = "jingle";

    private static final String JINGLE_NAMESPACE = "urn:xmpp:jingle:1";
    private static final String JINGLE_DESCRIPTION_NAMESPACE = "urn:xmpp:jingle:apps:file-transfer:5";
    private static final String JINGLE_TRANSPORT_NAMESPACE = "urn:xmpp:jingle:transports:s5b:1";
    private static final String HASH_NAMESPACE = "urn:xmpp:hashes:2";

    private static final String SID_ATTRIBUTE = "sid";
    private static final String INITIATOR_ATTRIBUTE = "initiator";
    private static final String ACTION_ATTRIBUTE = "action";

    private static final String CREATOR_ATTRIBUTE = "creator";
    private static final String NAME_ATTRIBUTE = "name";
    private static final String SENDERS_ATTRIBUTE = "senders";

    private JingleAction action;
    private String sid;
    private JID initiator;
    private JingleContent content;
    private Reason reason;

    public IQJingle(String xmlns) {
	super(xmlns, IQContentType.JINGLE);
    }

    public IQJingle() {
	super(JINGLE_NAMESPACE, IQContentType.JINGLE);
    }

    public IQJingle(Element element) throws Exception {
	this(element.getAttribute(XMLUtils.XMLNS_ATTRIBUTE));

	this.sid = element.getAttribute(SID_ATTRIBUTE);

	if (!StringUtils.isNullOrEmpty(element.getAttribute(INITIATOR_ATTRIBUTE))) {
	    this.initiator = new JID(element.getAttribute(INITIATOR_ATTRIBUTE));
	}

	if (!StringUtils.isNullOrEmpty(element.getAttribute(ACTION_ATTRIBUTE))) {
	    this.action = JingleAction.valueOf(element.getAttribute(ACTION_ATTRIBUTE));
	}

	if (!CollectionUtils.isNullOrEmpty(element.getChildren())) {
	    for (Element child : element.getChildren()) {
		if (StringUtils.safeEquals(child.getName(), JingleContent.XML_ELM_NAME)) {
		    this.content = new JingleContent(child);

		} else if (StringUtils.safeEquals(child.getName(), Reason.XML_ELM_NAME)) {
		    this.reason = new Reason(child);

		}
	    }
	}
    }

    public JingleAction getAction() {
	return action;
    }

    public void setAction(JingleAction action) {
	this.action = action;
    }

    public String getSid() {
	return sid;
    }

    public void setSid(String sid) {
	this.sid = sid;
    }

    public JID getInitiator() {
	return initiator;
    }

    public void setInitiator(JID initiator) {
	this.initiator = initiator;
    }

    public JingleContent getContent() {
	return content;
    }

    public void setContent(JingleContent content) {
	this.content = content;
    }

    public Reason getReason() {
	return reason;
    }

    public void setReason(Reason reason) {
	this.reason = reason;
    }

    @Override
    public String xml() {
	StringBuilder sb = new StringBuilder();
	this.appendXml(sb);

	return sb.toString();
    }

    @Override
    public StringBuilder appendXml(StringBuilder sb) {
	sb.append(XMLUtils.OPEN_BRACKET).append(XML_ELM_NAME).append(XMLUtils.SPACE).append(XMLUtils.XMLNS_ATTRIBUTE)
		.append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE).append(JINGLE_NAMESPACE)
		.append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);

	if (this.action != null) {
	    sb.append(ACTION_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE).append(action.name())
		    .append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);
	}

	if (!StringUtils.isNullOrEmpty(this.sid)) {
	    sb.append(SID_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE).append(sid)
		    .append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);
	}

	if (this.initiator != null) {
	    sb.append(INITIATOR_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE)
		    .append(this.initiator.getFullJID()).append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);
	}

	sb.append(XMLUtils.CLOSE_BRACKET);

	if (this.content != null) {
	    this.content.appendXml(sb);
	}

	if (this.reason != null) {
	    this.reason.appendXml(sb);
	}

	sb.append(XMLUtils.OPEN_BRACKET).append(XMLUtils.SLASH).append(XML_ELM_NAME).append(XMLUtils.CLOSE_BRACKET);

	return sb;
    }

    public enum JingleAction {
	SESSION_ACCEPT, SESSION_INITIATE, SESSION_TERMINATE

    }

    public class JingleContent implements XMLConvertible {
	private static final String XML_ELM_NAME = "content";

	private SessionRole creator;
	private String name;
	private SessionRole senders;
	private FileDescription description;
	private Transport transport;

	public JingleContent() {

	}

	public JingleContent(Element element) throws Exception {

	    if (!StringUtils.isNullOrEmpty(element.getAttribute(CREATOR_ATTRIBUTE))) {
		this.creator = SessionRole.valueOf(element.getAttribute(CREATOR_ATTRIBUTE));
	    }

	    if (!StringUtils.isNullOrEmpty(element.getAttribute(SENDERS_ATTRIBUTE))) {
		this.senders = SessionRole.valueOf(element.getAttribute(SENDERS_ATTRIBUTE));
	    }

	    if (!StringUtils.isNullOrEmpty(element.getAttribute(NAME_ATTRIBUTE))) {
		this.name = element.getAttribute(NAME_ATTRIBUTE);
	    }

	    if (!CollectionUtils.isNullOrEmpty(element.getChildren())) {
		for (Element child : element.getChildren()) {
		    if (StringUtils.safeEquals(child.getName(), FileDescription.XML_ELM_NAME)) {
			this.description = new FileDescription(child);

		    } else if (StringUtils.safeEquals(child.getName(), Transport.XML_ELM_NAME)) {
			this.transport = new Transport(child);

		    }
		}
	    }
	}

	public SessionRole getCreator() {
	    return creator;
	}

	public void setCreator(SessionRole creator) {
	    this.creator = creator;
	}

	public String getName() {
	    return name;
	}

	public void setName(String name) {
	    this.name = name;
	}

	public SessionRole getSenders() {
	    return senders;
	}

	public void setSenders(SessionRole senders) {
	    this.senders = senders;
	}

	public FileDescription getDescription() {
	    return description;
	}

	public void setDescription(FileDescription description) {
	    this.description = description;
	}

	public Transport getTransport() {
	    return transport;
	}

	public void setTransport(Transport transport) {
	    this.transport = transport;
	}

	@Override
	public String xml() {
	    StringBuilder sb = new StringBuilder();
	    this.appendXml(sb);
	    return sb.toString();
	}

	@Override
	public StringBuilder appendXml(StringBuilder sb) {
	    sb.append(XMLUtils.OPEN_BRACKET).append(XML_ELM_NAME).append(XMLUtils.SPACE);

	    if (this.creator != null) {
		sb.append(CREATOR_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE)
			.append(creator.name()).append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);
	    }

	    if (!StringUtils.isNullOrEmpty(this.name)) {
		sb.append(NAME_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE).append(this.name)
			.append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);
	    }

	    if (this.senders != null) {
		sb.append(SENDERS_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE)
			.append(senders.name()).append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);
	    }

	    sb.append(XMLUtils.CLOSE_BRACKET);

	    if (this.description != null) {
		this.description.appendXml(sb);
	    }

	    if (this.transport != null) {
		this.transport.appendXml(sb);
	    }

	    sb.append(XMLUtils.OPEN_BRACKET).append(XMLUtils.SLASH).append(XML_ELM_NAME).append(XMLUtils.CLOSE_BRACKET);

	    return sb;
	}

    }

    public class Reason implements XMLConvertible {
	private static final String XML_ELM_NAME = "reason";

	private static final String TEXT = "text";

	private String reasonText;
	private String reason;

	public Reason(Element element) throws Exception {
	    if (!CollectionUtils.isNullOrEmpty(element.getChildren())) {
		for (Element child : element.getChildren()) {

		    if (StringUtils.safeEquals(child.getName(), TEXT)) {
			this.reasonText = child.val();

		    } else {
			this.reason = child.getName();
		    }

		}
	    }
	}

	public String getReasonText() {
	    return reasonText;
	}

	public void setReasonText(String reasonText) {
	    this.reasonText = reasonText;
	}

	public String getReason() {
	    return reason;
	}

	public void setReason(String reason) {
	    this.reason = reason;
	}

	@Override
	public String xml() {
	    StringBuilder sb = new StringBuilder();
	    this.appendXml(sb);
	    return sb.toString();
	}

	@Override
	public StringBuilder appendXml(StringBuilder sb) {
	    sb.append(XMLUtils.OPEN_BRACKET).append(XML_ELM_NAME).append(XMLUtils.CLOSE_BRACKET);

	    if (!StringUtils.isNullOrEmpty(this.reason)) {
		sb.append(XMLUtils.OPEN_BRACKET).append(this.reason).append(XMLUtils.SPACE).append(XMLUtils.SLASH)
			.append(XMLUtils.CLOSE_BRACKET);
	    }

	    if (!StringUtils.isNullOrEmpty(this.reasonText)) {
		sb.append(XMLUtils.OPEN_BRACKET).append(TEXT).append(XMLUtils.CLOSE_BRACKET).append(this.reasonText)
			.append(XMLUtils.OPEN_BRACKET).append(XMLUtils.SLASH).append(TEXT)
			.append(XMLUtils.CLOSE_BRACKET);
	    }

	    sb.append(XMLUtils.OPEN_BRACKET).append(XMLUtils.SLASH).append(XML_ELM_NAME).append(XMLUtils.CLOSE_BRACKET);

	    return sb;
	}

    }

    public class FileDescription implements XMLConvertible {
	private static final String XML_ELM_NAME = "description";

	private static final String FILE_OPEN_TAG = "<file>";
	private static final String FILE_CLOSE_TAG = "</file>";

	private static final String DATE_OPEN_TAG = "<date>";
	private static final String DATE_CLOSE_TAG = "</date>";

	private static final String DESC_OPEN_TAG = "<desc>";
	private static final String DESC_CLOSE_TAG = "</desc>";

	private static final String MEDIA_TYPE_OPEN_TAG = "<media-type>";
	private static final String MEDIA_TYPE_CLOSE_TAG = "</media-type>";

	private static final String NAME_OPEN_TAG = "<name>";
	private static final String NAME_CLOSE_TAG = "</name>";

	private static final String SIZE_OPEN_TAG = "<size>";
	private static final String SIZE_CLOSE_TAG = "</size>";

	private static final String HASH_CLOSE_TAG = "</hash>";

	private static final String RANGE_XML = "<range/>";

	private static final String DESCRIPTION_CLOSE_TAG = "</description>";

	private static final String FILE = "file";
	private static final String MEDIA_TYPE = "media-type";
	private static final String DESC = "desc";
	private static final String NAME = "name";
	private static final String SIZE = "size";
	private static final String DATE = "date";
	private static final String RANGE = "range";
	private static final String HASH = "hash";

	private static final String OFFSET = "offset";
	private static final String ALGO_ATTRIBUTE = "algo";

	private String xmlns;
	private String fileName;
	private String fileDescription;
	private String mediaType;
	private long size;
	private String hashAlgo;
	private String hashValue;
	private String date;
	private boolean range;
	private long rangeOffset;

	public FileDescription(Element element) throws Exception {
	    this.xmlns = element.getAttribute(XMLUtils.XMLNS_ATTRIBUTE);

	    Element fileElem = element.getChild(FILE);

	    if (fileElem != null) {
		for (Element child : fileElem.getChildren()) {
		    if (StringUtils.safeEquals(child.getName(), MEDIA_TYPE)) {
			this.mediaType = child.val();

		    } else if (StringUtils.safeEquals(child.getName(), DESC)) {
			this.fileDescription = child.val();

		    } else if (StringUtils.safeEquals(child.getName(), NAME)) {
			this.fileName = child.val();

		    } else if (StringUtils.safeEquals(child.getName(), SIZE)) {
			this.size = Long.parseLong(child.val());

		    } else if (StringUtils.safeEquals(child.getName(), DATE)) {
			this.date = child.val();

		    } else if (StringUtils.safeEquals(child.getName(), MEDIA_TYPE)) {
			this.mediaType = child.val();

		    } else if (StringUtils.safeEquals(child.getName(), HASH)) {
			this.hashValue = child.val();
			this.hashAlgo = child.getAttribute(ALGO_ATTRIBUTE);

		    } else if (StringUtils.safeEquals(child.getName(), RANGE)) {
			this.range = true;
			this.rangeOffset = Long.parseLong(child.getAttribute(OFFSET));
		    }
		}
	    }
	}

	public String getXmlns() {
	    return xmlns;
	}

	public void setXmlns(String xmlns) {
	    this.xmlns = xmlns;
	}

	public String getFileName() {
	    return fileName;
	}

	public void setFileName(String fileName) {
	    this.fileName = fileName;
	}

	public String getFileDescription() {
	    return fileDescription;
	}

	public void setFileDescription(String fileDescription) {
	    this.fileDescription = fileDescription;
	}

	public String getMediaType() {
	    return mediaType;
	}

	public void setMediaType(String mediaType) {
	    this.mediaType = mediaType;
	}

	public long getSize() {
	    return size;
	}

	public void setSize(long size) {
	    this.size = size;
	}

	public String getHashAlgo() {
	    return hashAlgo;
	}

	public void setHashAlgo(String hashAlgo) {
	    this.hashAlgo = hashAlgo;
	}

	public String getHashValue() {
	    return hashValue;
	}

	public void setHashValue(String hashValue) {
	    this.hashValue = hashValue;
	}

	public String getDate() {
	    return date;
	}

	public void setDate(String date) {
	    this.date = date;
	}

	public boolean isRange() {
	    return range;
	}

	public void setRange(boolean range) {
	    this.range = range;
	}

	public long getRangeOffset() {
	    return rangeOffset;
	}

	public void setRangeOffset(long rangeOffset) {
	    this.rangeOffset = rangeOffset;
	}

	@Override
	public String xml() {
	    StringBuilder sb = new StringBuilder();
	    this.appendXml(sb);
	    return sb.toString();
	}

	@Override
	public StringBuilder appendXml(StringBuilder sb) {
	    sb.append(XMLUtils.OPEN_BRACKET).append(XML_ELM_NAME).append(XMLUtils.SPACE)
		    .append(XMLUtils.XMLNS_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE)
		    .append(JINGLE_DESCRIPTION_NAMESPACE).append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE)
		    .append(XMLUtils.CLOSE_BRACKET);

	    sb.append(FILE_OPEN_TAG);

	    if (!StringUtils.isNullOrEmpty(this.fileName)) {
		sb.append(NAME_OPEN_TAG).append(this.fileName).append(NAME_CLOSE_TAG);
	    }

	    if (!StringUtils.isNullOrEmpty(this.date)) {
		sb.append(DATE_OPEN_TAG).append(this.date).append(DATE_CLOSE_TAG);
	    }

	    if (!StringUtils.isNullOrEmpty(this.fileDescription)) {
		sb.append(DESC_OPEN_TAG).append(this.fileDescription).append(DESC_CLOSE_TAG);
	    }

	    if (!StringUtils.isNullOrEmpty(this.mediaType)) {
		sb.append(MEDIA_TYPE_OPEN_TAG).append(this.mediaType).append(MEDIA_TYPE_CLOSE_TAG);
	    }

	    sb.append(SIZE_OPEN_TAG).append(this.size).append(SIZE_CLOSE_TAG);

	    if (this.range) {
		if (this.rangeOffset == 0) {
		    sb.append(RANGE_XML);

		} else {
		    sb.append(XMLUtils.OPEN_BRACKET).append(RANGE).append(XMLUtils.SPACE).append(OFFSET)
			    .append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE).append(this.rangeOffset)
			    .append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE).append(XMLUtils.SLASH)
			    .append(XMLUtils.CLOSE_BRACKET);
		}

	    }

	    if (!StringUtils.isNullOrEmpty(this.hashValue)) {
		sb.append(XMLUtils.OPEN_BRACKET).append(HASH).append(XMLUtils.SPACE).append(XMLUtils.XMLNS_ATTRIBUTE)
			.append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE).append(HASH_NAMESPACE)
			.append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE)

			.append(ALGO_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE)
			.append(this.hashAlgo).append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE)

			.append(XMLUtils.CLOSE_BRACKET)

			.append(this.hashValue).append(HASH_CLOSE_TAG);
	    }

	    sb.append(FILE_CLOSE_TAG);

	    sb.append(DESCRIPTION_CLOSE_TAG);

	    return sb;
	}

    }

    public class Transport implements XMLConvertible {
	public static final String XML_ELM_NAME = "transport";

	private static final String TRANSPORT_CLOSE_TAG = "</transport>";

	private static final String MODE_ATTRIBUTE = "mode";
	private static final String SID_ATTRIBUTE = "sid";

	private String xmlns;
	private String mode;
	private String sid;
	private List<Candidate> candidates;

	public Transport() {
	}

	public Transport(Element element) throws Exception {
	    this.xmlns = element.getAttribute(XMLUtils.XMLNS_ATTRIBUTE);
	    this.mode = element.getAttribute(MODE_ATTRIBUTE);
	    this.sid = element.getAttribute(SID_ATTRIBUTE);

	    if (!CollectionUtils.isNullOrEmpty(element.getChildren())) {

		this.candidates = new ArrayList<>();

		for (Element child : element.getChildren()) {
		    if (StringUtils.safeEquals(child.getName(), Candidate.XML_ELM_NAME)) {
			this.candidates.add(new Candidate(child));
		    }
		}
	    }
	}

	public String getXmlns() {
	    return xmlns;
	}

	public void setXmlns(String xmlns) {
	    this.xmlns = xmlns;
	}

	public String getMode() {
	    return mode;
	}

	public void setMode(String mode) {
	    this.mode = mode;
	}

	public String getSid() {
	    return sid;
	}

	public void setSid(String sid) {
	    this.sid = sid;
	}

	public List<Candidate> getCandidates() {
	    return candidates;
	}

	public void setCandidates(List<Candidate> candidates) {
	    this.candidates = candidates;
	}

	@Override
	public String xml() {
	    StringBuilder sb = new StringBuilder();
	    this.appendXml(sb);
	    return sb.toString();
	}

	@Override
	public StringBuilder appendXml(StringBuilder sb) {
	    sb.append(XMLUtils.OPEN_BRACKET).append(XML_ELM_NAME).append(XMLUtils.SPACE)
		    .append(XMLUtils.XMLNS_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE)
		    .append(JINGLE_TRANSPORT_NAMESPACE).append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);

	    if (!StringUtils.isNullOrEmpty(this.sid)) {
		sb.append(SID_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE).append(this.sid)
			.append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);
	    }

	    if (!StringUtils.isNullOrEmpty(this.mode)) {
		sb.append(MODE_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE).append(this.mode)
			.append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);
	    }

	    sb.append(XMLUtils.CLOSE_BRACKET);

	    if (this.candidates != null) {
		for (Candidate candidate : this.candidates) {
		    candidate.appendXml(sb);
		}
	    }

	    sb.append(TRANSPORT_CLOSE_TAG);

	    return sb;
	}

    }

    public class Candidate implements XMLConvertible {
	private static final String XML_ELM_NAME = "candidate";

	private static final String CID_ATTRIBUTE = "cid";
	private static final String HOST_ATTRIBUTE = "host";
	private static final String PORT_ATTRIBUTE = "port";
	private static final String PRIORITY_ATTRIBUTE = "priority";
	private static final String TYPE_ATTRIBUTE = "type";
	private static final String JID_ATTRIBUTE = "jid";

	private String cid;
	private String host;
	private int port;
	private JID jid;
	private CandidateType type;
	private int priority;

	public Candidate() {

	}

	public Candidate(Element element) throws Exception {
	    this.cid = element.getAttribute(CID_ATTRIBUTE);
	    this.host = element.getAttribute(HOST_ATTRIBUTE);
	    this.port = Integer.parseInt(element.getAttribute(PORT_ATTRIBUTE));
	    this.priority = Integer.parseInt(element.getAttribute(PRIORITY_ATTRIBUTE));
	    this.type = CandidateType.valueOf(element.getAttribute(TYPE_ATTRIBUTE));
	    this.jid = new JID(element.getAttribute(JID_ATTRIBUTE));
	}

	public String getCid() {
	    return cid;
	}

	public void setCid(String cid) {
	    this.cid = cid;
	}

	public String getHost() {
	    return host;
	}

	public void setHost(String host) {
	    this.host = host;
	}

	public int getPort() {
	    return port;
	}

	public void setPort(int port) {
	    this.port = port;
	}

	public JID getJid() {
	    return jid;
	}

	public void setJid(JID jid) {
	    this.jid = jid;
	}

	public CandidateType getType() {
	    return type;
	}

	public void setType(CandidateType type) {
	    this.type = type;
	}

	public int getPriority() {
	    return priority;
	}

	public void setPriority(int priority) {
	    this.priority = priority;
	}

	@Override
	public String xml() {
	    StringBuilder sb = new StringBuilder();
	    appendXml(sb);
	    return sb.toString();
	}

	@Override
	public StringBuilder appendXml(StringBuilder sb) {
	    sb.append(XMLUtils.OPEN_BRACKET).append(XML_ELM_NAME).append(XMLUtils.SPACE);

	    if (!StringUtils.isNullOrEmpty(this.cid)) {
		sb.append(CID_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE).append(this.cid)
			.append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);
	    }

	    if (!StringUtils.isNullOrEmpty(this.host)) {
		sb.append(HOST_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE).append(this.host)
			.append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);
	    }

	    if (this.port != 0) {
		sb.append(PORT_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE).append(this.port)
			.append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);
	    }

	    if (this.priority != 0) {
		sb.append(PRIORITY_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE)
			.append(this.priority).append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);
	    }

	    if (this.jid != null) {
		sb.append(JID_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE)
			.append(this.jid.getFullJID()).append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);
	    }

	    if (this.type != null) {
		sb.append(TYPE_ATTRIBUTE).append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE).append(this.type.name())
			.append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.SPACE);
	    }

	    sb.append(XMLUtils.SLASH).append(XMLUtils.CLOSE_BRACKET);
	    
	    return sb;
	}

    }

    public enum CandidateType {
	DIRECT, ASSISTED
    }

    public enum SessionRole {
	INITIATOR, RESPONDER
    }

}
