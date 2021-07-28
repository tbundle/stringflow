package abs.ixi.server.io;

import static abs.ixi.util.StringUtils.safeEquals;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.common.CharArray;
import abs.ixi.server.common.LineReader;
import abs.ixi.server.common.ParserException;
import abs.ixi.server.common.SingletonLineReaderFactory;
import abs.ixi.server.common.LineReader.Line;
import abs.ixi.server.packet.XMPPError;
import abs.ixi.server.packet.XMPPUtil;
import abs.ixi.server.packet.XmppPacketFactory;
import abs.ixi.server.packet.Packet.PacketXmlElement;
import abs.ixi.server.packet.xmpp.BOSHBody;
import abs.ixi.server.packet.xmpp.StreamError;
import abs.ixi.server.packet.xmpp.XMPPPacket;
import abs.ixi.server.packet.xmpp.BOSHBody.Type;
import abs.ixi.server.protocol.BOSHProtocol;
import abs.ixi.xml.DomEventHandler;
import abs.ixi.xml.Element;
import abs.ixi.xml.ParseEventHandler.ParseEventCallback;
import abs.ixi.xml.ParserState.Error;

public class BOSHStreamProcessor extends CharStreamProcessor<BOSHBody> {
	private static final Logger LOGGER = LoggerFactory.getLogger(BOSHProtocol.class);

	private static final LineReader<Line> lineReader = SingletonLineReaderFactory.getLineReader();

	private BoshParser boshParser;

	public BOSHStreamProcessor(PacketCollector<BOSHBody> collector) {
		super(collector);
		this.boshParser = new BoshParser();
		this.xmlParserHandler = new DomEventHandler(new BoshDomEventCallback());
	}

	/**
	 * Parse characters stored in {@link CharArray} object. It ignores HTTP
	 * Headers and parse xml from <body> to </body>. Parsed elements generate
	 * {@link XMPPPacket} and these packets are handled in Callback invocation.
	 * Each time character source exhausts or parser quits processing, we check
	 * if there is an error in parser. For parser errors,
	 * {@link ParserException} is thrown.
	 * 
	 * @throws Exception
	 */
	@Override
	public boolean processCharStream() throws Exception {
		this.boshParser.parse(charSource);

		if (this.xmlParserHandler.getParserState().isError()) {
			handleStreamError();
		}

		return this.packetProcessingError;
	}

	/**
	 * It handles XML Parser error state.
	 * 
	 * @throws Exception
	 */
	protected void handleStreamError() throws Exception {
		try {
			Error error = this.xmlParserHandler.getParserState().getErrorType();

			if (error == Error.CLOSE_ELEMENT_BEFORE_OPEN && xmlParserHandler.getParserState().getElementName() != null
					&& safeEquals(xmlParserHandler.getParserState().getElementName().toString(),
							PacketXmlElement.STREAM_HEADER.elementNameString())) {

				BOSHBody boshBody = new BOSHBody();
				boshBody.setType(Type.TERMINATE);

				collectPacket(boshBody);

			} else {
				LOGGER.info("XMLParser is in Error state. Stopping stream processing");
				XMPPError xmppError = getXmppError(error);
				StreamError streamError = new StreamError(XMPPUtil.getXMPPErrorResponse(xmppError));

				BOSHBody boshBody = new BOSHBody();
				boshBody.addXmppPacket(streamError);

				this.collectPacket(boshBody);
			}
		} catch (Exception e) {
			LOGGER.error("Unexpected exception while processing stream error", e);
		} finally {
			stopProcessing();
		}
	}

	private void collectPacket(BOSHBody boshBodyPacket) {
		this.collector.collect(boshBodyPacket);
	}

	class BoshParser {
		private ParseState parserState;

		BoshParser() {
			this.parserState = ParseState.PARSING_HEADER;
		}

		void parse(CharArray charSource) throws ParserException {

			if (this.parserState == ParseState.PARSING_HEADER) {
				escapeHTTPHeaders(charSource);

			} else if (this.parserState == ParseState.PARSING_BODY) {
				xmlParser.parse(xmlParserHandler, charSource);

			} else if (this.parserState == ParseState.END_OF_BODY) {
				Line line = lineReader.readLine(charSource);

				if (line.isBlankLine()) {
					this.reset();
				}

			}
		}

		private void escapeHTTPHeaders(CharArray charSource) {
			while (charSource.hasNext()) {
				Line line = lineReader.readLine(charSource);

				if (line.isBlankLine()) {
					this.parserState = ParseState.PARSING_BODY;
					break;
				}
			}
		}

		void reset() {
			this.parserState = ParseState.PARSING_HEADER;
		}

		void setState(ParseState parseState) {
			this.parserState = parseState;
		}
	}

	/**
	 * States of Bosh parser
	 */
	enum ParseState {
		PARSING_HEADER,

		PARSING_BODY,

		END_OF_BODY;
	}

	/**
	 * Callback event handler for {@link DomEventHandler}.
	 * {@link DomEventHandler} invokes the callback whenever it has a new
	 * element or there is a parsing error.
	 */
	class BoshDomEventCallback implements ParseEventCallback {
		@Override
		public boolean onParsedElement(Element elm) {
			try {
				Element element = xmlParserHandler.getParsedElement();

				boshParser.setState(ParseState.END_OF_BODY);

				BOSHBody boshBodyPacket = XmppPacketFactory.generateBoshBodyPacket(element);

				if (boshBodyPacket != null) {
					collectPacket(boshBodyPacket);
				}	

			} catch (Exception e1) {
				LOGGER.error("failed to handle parsed element", e1);
				// swallowing it for now.
			}

			return false;
		}

	}
}
