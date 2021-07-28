package abs.ixi.server.io.multipart;

import abs.ixi.server.common.ByteArray;
import abs.ixi.server.common.LineReader.LineBreak;
import abs.ixi.server.io.multipart.MultipartParser.ParseState;

/**
 * A container to hold parsing process state data. {@link MultipartParser} is
 * state-less; therefore parser always requires {@link MultipartParserContext}
 * instance to resume parsing process.
 */
public class MultipartParserContext {
	/**
	 * Current MIME message being parsed; bytes will be added to this message.
	 * Once parser reaches message boundary, this message will be marked
	 * complete and will be returned to invoker entity.
	 */
	private MultipartMessage multipartMessage;

	/**
	 * Parser state instance; helps parser to resume processing
	 */
	private ParseState parserState;

	/**
	 * Multipart which is being parsed
	 */
	private Multipart multipart;

	/**
	 * A flag to indicate when a message a ready; this is useful when parser is
	 * parsing a MIME stream which can potentially has multiple messages
	 */
	private boolean messageReady;

	/**
	 * Previous {@link LineBreak} instance
	 */
	private LineBreak previousLineBreak;

	public MultipartParserContext(byte[] boundary) {
		this.parserState = ParseState.PARSING_PREAMBLE;
		this.multipartMessage = new MultipartMessage(boundary);
	}

	public MultipartParserContext(MultipartMessage multipartMessage, byte[] boundary) {
		this.parserState = ParseState.PARSING_PREAMBLE;
		this.multipartMessage = multipartMessage;
	}

	public MultipartParserContext(MultipartMessage multipartMessage) {
		this.parserState = ParseState.PARSING_PREAMBLE;
		this.multipartMessage = multipartMessage;
	}

	public MultipartMessage getMultipartMessage() {
		return multipartMessage;
	}

	public ParseState getParserState() {
		return parserState;
	}

	public void setParserState(ParseState parserState) {
		this.parserState = parserState;
	}

	public byte[] getBoundary() {
		return multipartMessage.getBoundary();
	}

	public Multipart getMultipart() {
		return multipart;
	}

	public void addNewMultipart() {
		this.multipart = this.multipartMessage.newMultipart();
	}

	public void addMultipartHeader(String name, String value) {
		this.multipart.addHeader(name, value);
	}

	public boolean isMessageReady() {
		return messageReady;
	}

	public void setMessageReady(boolean messageReady) {
		this.messageReady = messageReady;
	}

	public LineBreak getPreviousLineBreak() {
		return previousLineBreak;
	}

	public void setPreviousLineBreak(LineBreak previousLineBreak) {
		this.previousLineBreak = previousLineBreak;
	}

	public void addMultipartContent(byte[] bytes) {
		this.multipart.addMultipartContent(bytes, 0, bytes.length);
	}

	public void addMultipartContent(ByteArray byteSource, int startIndex, int endIndex) {
		this.multipart.addMultipartContent(byteSource, startIndex, endIndex);
	}

	public void addNewBinaryContent() {
		this.multipart.addNewBinaryContent();
	}

}
