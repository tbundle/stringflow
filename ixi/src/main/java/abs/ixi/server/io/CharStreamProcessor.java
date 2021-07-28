package abs.ixi.server.io;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.common.CharArray;
import abs.ixi.server.packet.Packet;
import abs.ixi.server.packet.XMPPError;
import abs.ixi.xml.ParseEventHandler;
import abs.ixi.xml.ParserState.Error;
import abs.ixi.xml.XmlParser;
import abs.ixi.xml.XmlParserFactory;

/**
 * Abstract implementation of {@link InputStreamProcessor} interface. The
 * instance of this class offers packet generation fromaework from stream bytes
 * read from network socket.
 * 
 * @author Yogi
 *
 * @param <PACKET>
 */
public abstract class CharStreamProcessor<PACKET extends Packet> implements InputStreamProcessor<PACKET> {
	private static final Logger LOGGER = LoggerFactory.getLogger(CharStreamProcessor.class);

	protected static XmlParser xmlParser = XmlParserFactory.getParser();
	protected ParseEventHandler xmlParserHandler;

	protected PacketCollector<PACKET> collector;

	protected byte[] partialBytes;
	private CharsetDecoder decoder;
	protected CharArray charSource;
	protected boolean packetProcessingError;

	public CharStreamProcessor(PacketCollector<PACKET> collector) {
		this.collector = collector;
		this.decoder = StandardCharsets.UTF_8.newDecoder();
		this.charSource = new CharArray(new char[0]);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean process(byte[] data) throws Exception {
		return this.process(data, 0, data.length);
	}

	/**
	 * {@inheritDoc}
	 */
	// TODO This is bogus; Abstract implementation for char processing. It means
	// it has been written for XMPP only which is not true.
	@Override
	public boolean process(byte[] data, int offset, int length) throws Exception {
		// partial bytes are appended to input data so lets flush it
		flushUnprocessedBytes();

		CharBuffer chars = readChar(data, offset, length);
		charSource.reload(chars.array(), 0, chars.position());

		while (this.charSource.hasNext() && !this.packetProcessingError) {
			processCharStream();
		}

		return this.packetProcessingError;
	}

	protected CharBuffer readChar(byte[] b) throws MalformedStreamException {
		return readChar(ByteBuffer.wrap(b));
	}

	protected CharBuffer readChar(byte[] b, int offset, int length) throws MalformedStreamException {
		return readChar(ByteBuffer.wrap(b, offset, length));
	}

	protected CharBuffer readChar(ByteBuffer buf) throws MalformedStreamException {
		CharBuffer cb = CharBuffer.allocate(buf.remaining());

		CoderResult cr = this.decoder.decode(buf, cb, false);

		if (cr.isError()) {
			throw new MalformedStreamException();
		}

		if (cr.isUnderflow() && buf.remaining() > 0) {
			LOGGER.debug("cr underflow encountered");

			this.partialBytes = new byte[buf.remaining()];
			buf.get(this.partialBytes);
		}

		if (cr.isOverflow()) {
			LOGGER.debug("Not sure how can we reach here");
		}

		return cb;
	}

	/**
	 * Stops processing while some error occurs during packet processing.
	 */
	protected void stopProcessing() {
		this.packetProcessingError = true;
	}

	/**
	 * Process {@link CharArray} which is reloaded with decoded character of
	 * input bytes. Returns true as long as processor is in state to process
	 * further bytes; a return value false indicates that processor has gone
	 * into bad state and cant process bytes further. This may also be the
	 * result of protocol policy; for example XMPP does not allow any byte
	 * processing in case there is an error on the stream.
	 */
	public abstract boolean processCharStream() throws Exception;

	protected XMPPError getXmppError(Error error) {
		if (error == Error.ATTR_COUNT_LIMIT_EXCEEDED) {
			return XMPPError.ATTR_COUNT_LIMIT_EXCEEDED;

		} else if (error == abs.ixi.xml.ParserState.Error.ATTR_NAME_LENGTH_LIMIT_EXCEEDED) {
			return XMPPError.ATTR_NAME_LENGTH_LIMIT_EXCEEDED;

		} else if (error == Error.ATTR_VALUE_LENGTH_LIMIT_EXCEEDED) {
			return XMPPError.ATTR_VALUE_LENGTH_LIMIT_EXCEEDED;

		} else if (error == Error.ELEMENT_NAME_SIZE_LIMIT_EXCEEDED) {
			return XMPPError.ATTR_NAME_LENGTH_LIMIT_EXCEEDED;

		} else if (error == Error.ELEMENT_VALUE_SIZE_LIMIT_EXCEEDED) {
			return XMPPError.STANZA_TOO_BIG;

		} else {
			return XMPPError.INVALID_XML;
		}
	}

	@Override
	public boolean hasUnprocessedBytes() {
		return this.partialBytes != null && this.partialBytes.length > 0;
	}

	@Override
	public byte[] getUnprocessedBytes() {
		return this.partialBytes;
	}

	@Override
	public void flushUnprocessedBytes() {
		this.partialBytes = null;
	}

}
