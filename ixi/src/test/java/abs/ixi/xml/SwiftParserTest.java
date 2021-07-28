package abs.ixi.xml;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import abs.ixi.server.common.CharArray;
import abs.ixi.server.common.ParserException;
import abs.ixi.xml.ParserState.Error;

public class SwiftParserTest {
	private static final String XML_START_STREAM = "<?xml version=\"1.0\" encoding=\'UTF-8\' ?>"
			+ "<stream:stream from=\'test@sf.com\' to=\'server@sf.com\' version='1.0' xml:lang='en' "
			+ "xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams'>";

	private static final String XML_CLOSE_STREAM = "</stream:stream>";

	// Swift Parser is state-less
	private SwiftParser parser;
	private ParseEventHandler handler;

	@Before
	public void setUp() {
		this.parser = new SwiftParser();
		this.handler = new DomEventHandler(new ParseEventHandler.ParseEventCallback() {

			@Override
			public boolean onParsedElement(Element element) {
				return false;
			}
		});
	}

	@Test
	public void testCloseStream() throws ParserException {
		this.parser.parse(this.handler, new CharArray(XML_START_STREAM.toCharArray()));

		// remove the element from stack
		this.handler.getParsedElement();

		this.parser.parse(this.handler, new CharArray(XML_CLOSE_STREAM.toCharArray()));

		assertEquals(true, this.handler.getParserState().isError());
		assertEquals(Error.CLOSE_ELEMENT_BEFORE_OPEN, this.handler.getParserState().getErrorType());
		assertEquals("stream:stream", this.handler.getParserState().getElementName().toString());
	}

}
