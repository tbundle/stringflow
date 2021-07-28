package abs.ixi.server.io.multipart;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.filesystem.DefaultFileSystem;
import abs.ixi.filesystem.FileSystemFactory;
import abs.ixi.server.common.ByteArray;
import abs.ixi.server.io.multipart.MimeParser.ParserState;
import abs.ixi.server.io.multipart.MimeStreamProcessor.MediaTransferResultHandler;
import abs.ixi.server.packet.JID;
import abs.ixi.server.session.SessionManager;
import abs.ixi.util.StringUtils;

public class MimeEventHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(MimeEventHandler.class);

	private static final String TO = "to";
	private static final String FROM = "from";
	private static final String STREAM_ID = "streamId";
	private static final String MEDIA_ID = "mediaId";
	private static final String BOUNDARY = "boundary";

	private static DefaultFileSystem fileSystem = FileSystemFactory.getDefaultFileSystemWithHashFileLocator();

	private Map<String, String> headers;
	private String mediaId;
	private byte[] boundary;
	private byte[] messageHeaderBytes;
	private ParserState parserState;
	private MediaTransferResultHandler resultHandler;
	private byte[] partialLineBytes;

	public MimeEventHandler(MediaTransferResultHandler resultHandler) {
		this.headers = new HashMap<>();
		this.parserState = ParserState.PARSING_PREAMBLE;
		this.messageHeaderBytes = new byte[0];
		this.resultHandler = resultHandler;
	}

	public void addHeader(String key, String value) {
		this.headers.put(key, value);

		if (StringUtils.safeEquals(key, MEDIA_ID, false)) {
			this.mediaId = value;

		} else if (StringUtils.safeEquals(key, BOUNDARY, false)) {
			this.boundary = value.getBytes();
		}
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public byte[] getBoundary() {
		return boundary;
	}

	public void setBoundary(byte[] boundary) {
		this.boundary = boundary;
	}

	public byte[] getMessageHeaderBytes() {
		return messageHeaderBytes;
	}

	public void setMessageHeaderBytes(byte[] messageHeaderBytes) {
		this.messageHeaderBytes = messageHeaderBytes;
	}

	public ParserState getParserState() {
		return parserState;
	}

	public void setParserState(ParserState parserState) {
		this.parserState = parserState;
	}

	public byte[] getPartialLineBytes() {
		return partialLineBytes;
	}

	public void setPartialLineBytes(byte[] partialLineBytes) {
		this.partialLineBytes = partialLineBytes;
	}

	/**
	 * Store mime message header bytes.
	 * 
	 * @param byteSource
	 * @param position
	 * @param length
	 * @param lineBreak
	 */
	public void addMessageHeaderBytes(ByteArray byteSource, int position, int length, byte[] lineBreak) {
		byte[] newMessageHeader = new byte[this.messageHeaderBytes.length + lineBreak.length + length];

		System.arraycopy(this.messageHeaderBytes, 0, newMessageHeader, 0, this.messageHeaderBytes.length);

		for (int destPos = (this.messageHeaderBytes.length); destPos < newMessageHeader.length
				- lineBreak.length; destPos++) {
			newMessageHeader[destPos] = byteSource.get(position++);
		}

		System.arraycopy(lineBreak, 0, newMessageHeader, messageHeaderBytes.length + length, lineBreak.length);

		this.messageHeaderBytes = newMessageHeader;
	}

	/**
	 * Checks mime message headers are proper or not. In future it will also
	 * check that for this mime negotiation is done or not on xmpp stream.
	 */
	public boolean verifyMimeHeader() {
		String from = headers.get(FROM);
		String to = headers.get(TO);
		String streamId = headers.get(STREAM_ID);

		try {
			if (this.mediaId != null && !StringUtils.isNullOrEmpty(from) && !StringUtils.isNullOrEmpty(to)) {
				JID fromJID = new JID(from);
				JID toJID = new JID(to);

				boolean exist = SessionManager.getInstance().isUserStreamExists(fromJID.getBareJID(), streamId);

				if (exist)
					SessionManager.getInstance().storeMedia(fromJID, toJID, mediaId);

				return exist;
			}

		} catch (Exception e) {
			// Swallow Exception;
		}

		return false;
	}

	public void messageHeaderParsed() throws MalformedMimeException, IOException {
		boolean isProperHeaders = this.verifyMimeHeader();

		if (isProperHeaders) {
			fileSystem.writeTo(mediaId, messageHeaderBytes);

		} else {
			throw new MalformedMimeException("Mime validation is failed");
		}
	}

	public void persistMessageBody(ByteArray byteSource, int startIndex, int length, byte[] lineBreak)
			throws IOException {
		byte[] bodyLine = new byte[length + lineBreak.length];
		System.arraycopy(byteSource.bytes(), startIndex, bodyLine, 0, length);
		System.arraycopy(lineBreak, 0, bodyLine, length, lineBreak.length);
		fileSystem.writeTo(mediaId, bodyLine);
	}

	public void endOfMimeMessage() {
		fileSystem.closeFileOutputStream(mediaId);
		resultHandler.mediaReceived(mediaId);
		LOGGER.info("Media with id {} is saved successfully", mediaId);
	}

	/**
	 * Resets this {@link MimeEventHandler} Object.
	 */
	public void reset() {
		this.headers = new HashMap<>();
		this.parserState = ParserState.PARSING_PREAMBLE;
		this.messageHeaderBytes = new byte[0];
		this.boundary = null;
		this.mediaId = null;
		this.partialLineBytes = null;
	}

}
