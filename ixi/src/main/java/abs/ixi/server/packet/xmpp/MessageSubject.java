package abs.ixi.server.packet.xmpp;

import abs.ixi.xml.Element;

/**
 * Represents subject within a message stanza
 */
public class MessageSubject implements MessageContent {
    private static final long serialVersionUID = -3996957445534993515L;

    public static String XML_ELM_NAME = "subject".intern();

    public static final String SUBJECT_XML = "<subject>%s</subject>".intern();

    private String content;

    public MessageSubject(String content) {
	this.content = content;
    }

    public MessageSubject(Element subjectElem) {
	this(subjectElem.val());
    }

    @Override
    public boolean isContentType(MessageContentType type) {
	return MessageContentType.SUBJECT == type;
    }

    @Override
    public String toString() {
	return content;
    }

    public String getContent() {
	return content;
    }

    @Override
    public String xml() {
	if (this.content == null)
	    return null;

	return String.format(SUBJECT_XML, this.content);
    }

    @Override
    public StringBuilder appendXml(StringBuilder sb) {
	if (content == null)
	    return sb;

	return sb.append(String.format(SUBJECT_XML, this.content));
    }
}
