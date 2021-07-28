package abs.ixi.server.packet.xmpp;

import abs.ixi.util.StringUtils;
import abs.ixi.xml.Element;
import abs.ixi.xml.XMLUtils;

public class MessageThread implements MessageContent {
	private static final long serialVersionUID = -2363352900623534427L;

	public static final String XML_ELM_NAME = "thread";
	public static final String PARENT_ATTRIBUTE = "parent";
	public static final String THREAD_XML = "<thread>%s</thread>";

	private String parentId;
	private String threadId;

	public MessageThread(String threadId) {
		this.threadId = threadId;
	}

	public MessageThread(Element threadElem) {
		this(threadElem.val());
		this.parentId = threadElem.getAttribute(PARENT_ATTRIBUTE);
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getThreadId() {
		return threadId;
	}

	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}

	@Override
	public boolean isContentType(MessageContentType type) {
		return MessageContentType.THREAD == type;
	}

	@Override
	public String xml() {
		return appendXml(new StringBuilder()).toString();
	}

	@Override
	public StringBuilder appendXml(StringBuilder sb) {
		if (threadId == null)
			return sb;

		if (StringUtils.isNullOrEmpty(this.parentId))
			return sb.append(String.format(THREAD_XML, this.threadId));

		sb.append(XMLUtils.OPEN_BRACKET).append(XML_ELM_NAME).append(XMLUtils.SPACE).append(PARENT_ATTRIBUTE)
				.append(XMLUtils.EQUALS).append(XMLUtils.SINGLE_QUOTE).append(this.parentId)
				.append(XMLUtils.SINGLE_QUOTE).append(XMLUtils.CLOSE_BRACKET).append(this.threadId)
				.append(XMLUtils.OPEN_BRACKET).append(XMLUtils.SLASH).append(XML_ELM_NAME)
				.append(XMLUtils.CLOSE_BRACKET);

		return sb;

	}
}
