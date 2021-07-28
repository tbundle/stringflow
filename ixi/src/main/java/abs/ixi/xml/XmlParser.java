package abs.ixi.xml;

import abs.ixi.server.common.CharArray;
import abs.ixi.server.common.NameAware;
import abs.ixi.server.common.ParserException;

/**
 * XML Parser interface which will be used by XML based protocols. This is not a
 * full fledged XML parser interface; it's a customized version of XML parser
 * which suits XMPP packet parsing.
 * 
 * <p>
 * As this interface does not follow XML standards, it will require massive
 * amount of work to replace this parser with some other parser
 * </p>
 */
public interface XmlParser extends NameAware<String> {
    /**
     * Returns name of the Parser
     */
    public String getName();

    /**
     * 
     * @param handler
     * @param charSource
     * @throws ParserException
     */
    public void parse(ParseEventHandler handler, CharArray charSource) throws ParserException;

}
