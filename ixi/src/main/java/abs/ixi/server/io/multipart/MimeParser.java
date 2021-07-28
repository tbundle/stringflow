package abs.ixi.server.io.multipart;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.common.ByteArray;
import abs.ixi.server.common.LineReader;
import abs.ixi.server.common.ParserException;
import abs.ixi.server.common.SingletonLineReaderFactory;
import abs.ixi.server.common.LineReader.Line;
import abs.ixi.server.io.multipart.MultipartParser.ParseState;
import abs.ixi.util.ArrayUtils;

public class MimeParser {
	private static final LineReader<Line> reader = SingletonLineReaderFactory.getLineReader();

	private static final Logger LOGGER = LoggerFactory.getLogger(MimeParser.class);

	public static MimeParser instance;

	/**
	 * Character hyphen
	 */
	private static final byte DASH = '-';

	/**
	 * Colon Character
	 */
	private static final String COLON = ":";

	/**
	 * Parses mime bytes from given {@link ByteArray} using the handler given.
	 * As the parser is state less, the parsing process state is maintained by
	 * {@link MultipartParserContextOld}
	 * 
	 * @param byteSource
	 * @param handler
	 * @throws MalformedMimeException
	 * @throws IOException
	 * @throws ParserException
	 */
	public static void parse(ByteArray byteSource, MimeEventHandler eventHandler)
			throws MalformedMimeException, IOException, ParserException {
		while (byteSource.hasNext()) {
			ParserState state = eventHandler.getParserState();

			switch (state) {
			case PARSING_PREAMBLE:
				parsePreamble(byteSource, eventHandler);
				break;

			case PARSING_MESSAGE_HEADER:
				parseHeaders(byteSource, eventHandler);
				break;

			case PARSING_BODY:
				parseBody(byteSource, eventHandler);
				break;

			case END_OF_MESSAGE:
				eventHandler.reset();
				break;

			case ERROR:
				throw new ParserException("mime parser is in Error state");
			}
		}
	}

	/**
	 * Processes preamble section of multipart. Changes the parser state to
	 * {@link ParseState#PARSING_HEADER}
	 * 
	 * @param byteSource
	 * @param eventHandler
	 * @throws MalformedMimeException
	 */
	private static void parsePreamble(ByteArray byteSource, MimeEventHandler eventHandler)
			throws MalformedMimeException {
		Line line = reader.readLine(byteSource, true);

		if (line.isPartialLine()) {
			savePartialLineBytes(byteSource, line, eventHandler);

		} else if (!line.isBlankLine()) {

			if (line.isMessageEnd()) {
				LOGGER.info("Message has ended...");

			} else {
				eventHandler.setParserState(ParserState.PARSING_MESSAGE_HEADER);
				prepareHeader(byteSource, line, eventHandler);
			}
		}
	}

	private static void savePartialLineBytes(ByteArray byteSource, Line line, MimeEventHandler eventHandler) {
		byte[] partialLineBytes = new byte[line.length()];
		System.arraycopy(byteSource.bytes(), line.start(), partialLineBytes, 0, line.length());
		eventHandler.setPartialLineBytes(partialLineBytes);
	}

	/**
	 * Parses header section of a multipart. At this point, we assume that
	 * boundary has already been parsed.
	 * 
	 * @param src
	 * @param eventHandler
	 * @throws IOException
	 */
	private static void parseHeaders(ByteArray src, MimeEventHandler eventHandler)
			throws MalformedMimeException, IOException {
		Line line = reader.readLine(src, false);

		if (line.isPartialLine()) {
			savePartialLineBytes(src, line, eventHandler);

		} else if (line.isBlankLine()) {
			eventHandler.addMessageHeaderBytes(src, line.start(), line.length(), line.getLineBreak().getBytes());
			eventHandler.messageHeaderParsed();
			eventHandler.setParserState(ParserState.PARSING_BODY);

		} else {
			prepareHeader(src, line, eventHandler);
		}
	}

	/**
	 * Processes header line received in byte stream and generates header
	 * key-value pairs
	 * 
	 * @param headerLine header line received for a mime
	 * @param eventHandler parser context
	 * @throws MalformedMimeException
	 */
	private static void prepareHeader(ByteArray src, Line line, MimeEventHandler eventHandler)
			throws MalformedMimeException {
		String headerLine = new String(src.bytes(), line.start(), line.length(), StandardCharsets.US_ASCII);

		String[] header = headerLine.split(COLON);

		if (header.length == 2) {
			eventHandler.addHeader(header[0].trim(), header[1].trim());
			eventHandler.addMessageHeaderBytes(src, line.start(), line.length(), line.getLineBreak().getBytes());

		} else {
			throw new MalformedMimeException("Could not preapre header");
		}
	}

	/**
	 * Parses mime body for a multipart in the incoming byte stream.
	 * 
	 * @param byteSource
	 * @param eventHandler
	 * @throws IOException
	 */
	private static void parseBody(ByteArray byteSource, MimeEventHandler eventHandler) throws IOException {
		Line line = reader.readLine(byteSource, false);
		if (line.isPartialLine()) {
			savePartialLineBytes(byteSource, line, eventHandler);

		} else {
			eventHandler.persistMessageBody(byteSource, line.start(), line.length(), line.getLineBreak().getBytes());

			boolean isEndBoundary = isEndBoundary(byteSource, line.start(), line.end(), eventHandler);

			if (isEndBoundary) {
				eventHandler.endOfMimeMessage();
				eventHandler.setParserState(ParserState.END_OF_MESSAGE);
			}
		}

	}

	/**
	 * Checks if the {@link Line} is start boundary or end boundary
	 * 
	 * @param line
	 * @param byteSource
	 * @param eventHandler
	 * @return 0 if not a boundary, 1 if it is a start boundary and 2 if it is a
	 *         end boundary
	 */
	private static boolean isEndBoundary(ByteArray byteSource, int startIndex, int endIndex,
			MimeEventHandler eventHandler) {
		if (byteSource.get(startIndex++) == DASH && byteSource.get(startIndex++) == DASH) {
			if (ArrayUtils.areEqual(byteSource.bytes(), startIndex, endIndex - 3, eventHandler.getBoundary(), 0,
					(eventHandler.getBoundary().length - 1)) && byteSource.get(endIndex - 2) == DASH
					&& byteSource.get(endIndex - 1) == DASH) {

				return true;

			}
		}

		return false;
	}

	/**
	 * While parsing a mime byte stream, parser transitions through various
	 * message parts and the parsing mechanism differs for each section.
	 * {@code ParseState} indicates the section of mime message which parser is
	 * parsing currently.
	 */
	enum ParserState {
		PARSING_PREAMBLE,

		PARSING_MESSAGE_HEADER,

		PARSING_BODY,

		END_OF_MESSAGE,

		ERROR;
	}

}