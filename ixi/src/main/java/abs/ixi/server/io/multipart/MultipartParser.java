package abs.ixi.server.io.multipart;

import java.nio.charset.StandardCharsets;

import abs.ixi.server.common.ByteArray;
import abs.ixi.server.common.LineReader;
import abs.ixi.server.common.SingletonLineReaderFactory;
import abs.ixi.server.common.LineReader.Line;
import abs.ixi.server.common.LineReader.LineBreak;
import abs.ixi.util.ArrayUtils;

/**
 * As the name suggests, {@code MultipartParser} parses a byte stream carrying a
 * multipart message. Multipart message definition supported in server is not
 * same as outlined in RFC 822. It is more simplified version of the multipart
 * message defined for mail protocol. It will fair to say that
 * {@code MultipartParser} can process multipart message used by HTTP protocol.
 * <p>
 * Parser implementation is state less; therefore it is also thread-safe.
 * </p>
 */
public class MultipartParser {
	private static final LineReader<Line> reader = SingletonLineReaderFactory.getLineReader();

	/**
	 * Character hyphen
	 */
	private static final byte DASH = '-';

	/**
	 * Colon Character
	 */
	private static final String COLON = ":";

	// Restricting instantiation
	MultipartParser() {
		// do-nothing constructor
	}

	/**
	 * Parses mime bytes from given {@link ByteArray} using the handler given.
	 * As the parser is state less, the parsing process state is maintained by
	 * {@link MultipartParserContextOld}
	 * 
	 * @param byteSource
	 * @param handler
	 * @throws MalformedMimeException
	 */
	public void parse(ByteArray byteSource, MultipartParserContext context) throws MalformedMimeException {
		while (byteSource.hasNext()) {
			ParseState state = context.getParserState();

			switch (state) {
			case PARSING_PREAMBLE:
				parsePreamble(byteSource, context);
				break;
			case PARSING_HEADER:
				parseHeaders(byteSource, context);
				break;
			case PARSING_BODY:
				parseBody(byteSource, context);
				break;
			case END:
				break;
			}
		}
	}

	/**
	 * Processes preamble section of multipart. Changes the parser state to
	 * {@link ParseState#PARSING_HEADER}
	 * 
	 * @param byteSource
	 * @param context
	 * @throws MalformedMimeException
	 */
	private void parsePreamble(ByteArray byteSource, MultipartParserContext context) throws MalformedMimeException {
		Line line = reader.readLine(byteSource, true);

		if (isStartBoundary(byteSource, line.start(), line.end(), context)) {
			context.addNewMultipart();
			context.setParserState(ParseState.PARSING_HEADER);

		} else {
			throw new MalformedMimeException("Could not find start of boundary");
		}

	}

	/**
	 * Checks if the range given in byte source is multipart boundary.The method
	 * MUST not change {@link ByteArray} markers such as position, limit.
	 * 
	 * @param byteSource
	 * @param startIndex
	 * @param endIndex
	 * @param context
	 * @return
	 */
	private boolean isStartBoundary(ByteArray byteSource, int startIndex, int endIndex,
			MultipartParserContext context) {
		if (byteSource.get(startIndex++) == DASH && byteSource.get(startIndex++) == DASH) {
			if (ArrayUtils.areEqual(byteSource.bytes(), startIndex, endIndex - 1, context.getBoundary(), 0,
					(context.getBoundary().length - 1))) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Parses header section of a multipart. At this point, we assume that
	 * boundary has already been parsed.
	 * 
	 * @param src
	 * @param context
	 */
	private void parseHeaders(ByteArray src, MultipartParserContext context) throws MalformedMimeException {
		Line line = reader.readLine(src, false);

		if (line.isBlankLine()) {
			context.addNewBinaryContent();
			context.setParserState(ParseState.PARSING_BODY);
			return;
		}

		String headerLine = new String(src.bytes(), line.start(), line.length(), StandardCharsets.US_ASCII);
		prepareHeader(headerLine, context);
	}

	/**
	 * Processes header line received in byte stream and generates header
	 * key-value pairs
	 * 
	 * @param headerLine header line received for a mime
	 * @param context parser context
	 * @throws MalformedMimeException
	 */
	private void prepareHeader(String headerLine, MultipartParserContext context) throws MalformedMimeException {
		String[] header = headerLine.split(COLON);

		if (header.length == 2) {
			context.addMultipartHeader(header[0].trim(), header[1].trim());

		} else {
			throw new MalformedMimeException("Could not preapre header");
		}
	}

	/**
	 * Parses mime body for a multipart in the incoming byte stream.
	 * 
	 * @param byteSource
	 * @param context
	 */
	private void parseBody(ByteArray byteSource, MultipartParserContext context) {
		Line line = reader.readLine(byteSource, false);
		int isBoundary = isBoundary(byteSource, line.start(), line.end(), context);

		switch (isBoundary) {
		case 0:
			if (context.getPreviousLineBreak() != null) {
				if (LineBreak.CRLF == context.getPreviousLineBreak()) {
					context.addMultipartContent(new byte[] { LineReader.CR, LineReader.LF });

				} else if (LineBreak.LF == context.getPreviousLineBreak()) {
					context.addMultipartContent(new byte[] { LineReader.LF });
				}
			}

			context.addMultipartContent(byteSource, line.start(), line.end());
			context.setPreviousLineBreak(line.getLineBreak());

			break;

		case 1:
			context.setParserState(ParseState.PARSING_HEADER);
			context.addNewMultipart();
			context.setPreviousLineBreak(null);

			break;

		case 2:
			context.setParserState(ParseState.END);
			context.setMessageReady(true);
			break;

		default:
			break;
		}

	}

	/**
	 * Checks if the {@link Line} is start boundary or end boundary
	 * 
	 * @param line
	 * @param byteSource
	 * @param context
	 * @return 0 if not a boundary, 1 if it is a start boundary and 2 if it is a
	 *         end boundary
	 */
	private int isBoundary(ByteArray byteSource, int startIndex, int endIndex, MultipartParserContext context) {

		if (byteSource.get(startIndex++) == DASH && byteSource.get(startIndex++) == DASH) {
			if (ArrayUtils.areEqual(byteSource.bytes(), startIndex, endIndex - 1, context.getBoundary(), 0,
					(context.getBoundary().length - 1))) {

				return 1;

			} else {
				if (ArrayUtils.areEqual(byteSource.bytes(), startIndex, endIndex - 3, context.getBoundary(), 0,
						(context.getBoundary().length - 1)) && byteSource.get(endIndex - 2) == DASH
						&& byteSource.get(endIndex - 1) == DASH) {

					return 2;

				}
			}
		}

		return 0;
	}

	/**
	 * While parsing a mime byte stream, parser transitions through various
	 * message parts and the parsing mechanism differs for each section.
	 * {@code ParseState} indicates the section of mime message which parser is
	 * parsing currently.
	 */
	enum ParseState {
		PARSING_PREAMBLE,

		PARSING_HEADER,

		PARSING_BODY,

		END;
	}

}
