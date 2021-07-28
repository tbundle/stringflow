package abs.ixi.server;

import abs.ixi.xml.XmlParser;

/**
 * This is a marker interface introduced in server to bring both
 * {@link XmlParser} and {@link AppRequestParser} under one type hierarchy. At
 * some point we will do away with this interface as we must merge the whole
 * parsing process as one for both messaging and application.
 * <p>
 * As server is XMPP protocol compliant, it is imperative that all incoming byte
 * stream would translate into xml format; however application requests add
 * complexity as these requests may carry binary data. Given that XML is a
 * character based mark-up, the binary data to character conversion is bound to
 * break.
 * </p>
 * <p>
 * We can reach a common parsing model if we build something where bytes to char
 * conversion is delayed until we know that byte stream carrying an application
 * request or a char based packet(message/iq/presence). Just a tip: application
 * requests are wrapped in a message packet with no attribute.
 * </p>
 */
public interface ByteStreamParser {

}
