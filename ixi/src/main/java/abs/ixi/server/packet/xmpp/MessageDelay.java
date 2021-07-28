package abs.ixi.server.packet.xmpp;

import java.util.Date;
import java.util.TimeZone;

import abs.ixi.util.DateUtils;
import abs.ixi.xml.Element;
import abs.ixi.xml.XMLUtils;

public class MessageDelay implements MessageContent {
    private static final long serialVersionUID = 5808489192543151511L;

    public static final String XML_ELM_NAME = "delay".intern();

    public static final String STAMP_ATTRIBUTE = "stamp".intern();

    public static final String FROM_ATTRIBUTE = "from".intern();

    public static final String TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'".intern();

    public static final String DELAY_XML = "<delay xmlns='urn:xmpp:delay' stamp='%s'/>".intern();

    public static final String UTC_TIMEZONE = "UTC".intern();

    private String xmlns;
    private String from;
    private String stamp;

    public MessageDelay(String xmlns) {
	this.xmlns = xmlns;
	this.stamp = DateUtils.getTimeString(new Date(), TimeZone.getTimeZone(UTC_TIMEZONE), TIME_FORMAT);
    }

    public MessageDelay(Element delayElm) {
	MessageDelay delay = new MessageDelay(delayElm.getAttribute(XMLUtils.XMLNS_ATTRIBUTE));
	delay.setStamp(delayElm.getAttribute(STAMP_ATTRIBUTE));
	delay.setFrom(delayElm.getAttribute(FROM_ATTRIBUTE));
    }

    @Override
    public boolean isContentType(MessageContentType type) {
	return type == MessageContentType.DELAY;
    }

    public String getXmlns() {
	return xmlns;
    }

    public void setXmlns(String xmlns) {
	this.xmlns = xmlns;
    }

    public String getFrom() {
	return from;
    }

    public void setFrom(String from) {
	this.from = from;
    }

    public String getStamp() {
	return stamp;
    }

    public void setStamp(String stamp) {
	this.stamp = stamp;
    }

    @Override
    public String xml() {
	return String.format(DELAY_XML, this.stamp);
    }

    @Override
    public StringBuilder appendXml(StringBuilder sb) {
	return sb.append(String.format(DELAY_XML, this.stamp));
    }
}
