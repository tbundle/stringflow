package abs.ixi.server.packet.xmpp;

import abs.ixi.xml.Element;

/**
 * Represents body element within a message packet
 */
public class MessageBody implements MessageContent {
    private static final long serialVersionUID = 3052666005955448064L;

    public static final String XML_ELM_NAME = "body".intern();

    public static final String BODY_XML = "<body>%s</body>".intern();

    private String content;

    public MessageBody(String content) {
	this.content = content;
    }

    public MessageBody(Element bodyElem) {
	this(bodyElem.val());
    }

    @Override
    public boolean isContentType(MessageContentType type) {
	return MessageContentType.BODY == type;
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

	return String.format(BODY_XML, this.content);
    }

    @Override
    public StringBuilder appendXml(StringBuilder sb) {
	if (content == null)
	    return sb;

	return sb.append(String.format(BODY_XML, this.content));
    }
}
