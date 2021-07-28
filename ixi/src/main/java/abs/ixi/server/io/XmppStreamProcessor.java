package abs.ixi.server.io;

import static abs.ixi.util.StringUtils.safeEquals;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.common.CharArray;
import abs.ixi.server.common.ParserException;
import abs.ixi.server.packet.XMPPError;
import abs.ixi.server.packet.XMPPUtil;
import abs.ixi.server.packet.XmppPacketFactory;
import abs.ixi.server.packet.xmpp.StreamError;
import abs.ixi.server.packet.xmpp.StreamHeader;
import abs.ixi.server.packet.xmpp.XMPPPacket;
import abs.ixi.xml.DomEventHandler;
import abs.ixi.xml.Element;
import abs.ixi.xml.ParseEventHandler.ParseEventCallback;
import abs.ixi.xml.ParserState.Error;

/**
 * {@code XmppStreamProcessor} is an implementation of
 * {@link InputStreamProcessor} capable of processing XMPP stream. Typically
 * Stringflow processes a multiplexed stream (extended XMPP stream) which should
 * be processed using {@link MuxStreamProcessor}
 * <p>
 * It is important to note that processing os multiplexed stream is little more
 * performance intensive than a pure XMPP stream.
 * </p>
 */
public class XmppStreamProcessor extends CharStreamProcessor<XMPPPacket> {
	private static final Logger LOGGER = LoggerFactory.getLogger(XmppStreamProcessor.class);

	public XmppStreamProcessor(PacketCollector<XMPPPacket> collector) {
		super(collector);
		this.xmlParserHandler = new DomEventHandler(new DomEventCallback());
	}

	/**
	 * Parse xml characters stored in {@link CharArray} object. Parsed elements
	 * generate {@link XMPPPacket} and these packets are handled in Callback
	 * invocation. Each time character source exhausts or parser quits
	 * processing, we check if there is an error in parser. For parser errors,
	 * {@link ParserException} is thrown.
	 * 
	 * @throws Exception
	 */
	@Override
	public boolean processCharStream() throws Exception {
		if (this.packetProcessingError) {
			return false;
		}

		xmlParser.parse(this.xmlParserHandler, charSource);

		if (this.xmlParserHandler.getParserState().isError()) {
			handleStreamError();

			return false;
		}

		return true;
	}

	/**
	 * Handle parser error states here
	 * 
	 * @throws Exception
	 */
	protected void handleStreamError() throws Exception {
		try {
			Error error = this.xmlParserHandler.getParserState().getErrorType();

			if (error == Error.CLOSE_ELEMENT_BEFORE_OPEN
					&& this.xmlParserHandler.getParserState().getElementName() != null && safeEquals(
							this.xmlParserHandler.getParserState().getElementName().toString(), "stream:stream")) {

				StreamHeader header = new StreamHeader();
				header.setCloseStream(true);
				sendPacket(header);

			} else {
				LOGGER.info("XMLParser is in Error state. Stopping stream processing");
				XMPPError xmppError = getXmppError(error);
				StreamError errorPacket = new StreamError(XMPPUtil.getXMPPErrorResponse(xmppError));

				this.sendPacket(errorPacket);
			}
		} catch (Exception e) {
			LOGGER.error("Error processing stream error condition", e);
		} finally {
			stopProcessing();
		}
	}

	/**
	 * Packets are handed-over to {@link PacketCollector} for processing.
	 * 
	 * @param xmppPacket
	 * @throws Exception
	 */
	protected boolean sendPacket(XMPPPacket xmppPacket) throws Exception {
		this.collector.collect(xmppPacket);

		return true;
	}

	/**
	 * Callback event handler for {@link DomEventHandler}.
	 * {@link DomEventHandler} invokes the callback whenever it has a new
	 * element or there is a parsing error.
	 */
	class DomEventCallback implements ParseEventCallback {
		@Override
		public boolean onParsedElement(Element elm) {
			try {
				Element element = xmlParserHandler.getParsedElement();
				XMPPPacket xmppPacket = XmppPacketFactory.createPacket(element);

				return sendPacket(xmppPacket);

			} catch (Exception e1) {
				LOGGER.error("failed to handle packet", e1);
				// swallowing it for now.
			}

			return false;
		}
	}

}
