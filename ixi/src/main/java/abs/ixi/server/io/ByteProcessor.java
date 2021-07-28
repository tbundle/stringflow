package abs.ixi.server.io;

import abs.ixi.server.common.ParserException;
import abs.ixi.server.packet.Packet;

/**
 * Low-level byte stream processor which can process a certain type of byte
 * stream i.e MIME byte stream, XMPP stream. The processors require governance
 * by a controlling entity which can facilitate switch-over mechanism and can
 * provide all the required dependencies and processing context. In scenarios in
 * which a stream is multiplexed, multiple ByteProcessor will be required to
 * work in tandem and during stream processing, ByteProcessor will switch.
 */
public interface ByteProcessor {
	/**
	 * Resume processing a byte stream. It could be that previous ByteProcessor
	 * was building a packet which is not yet complete. This half-built packet
	 * is passed onto to this ByteProcessor.
	 * 
	 * @param switchOver
	 *            half-built packet from previous processor
	 */
	public void resume(Packet switchOver);

	/**
	 * Process byte stream; processing a byte stream typically involves
	 * encoding, parsing and building a packet. Therefore, the method may throw
	 * {@link ParserException} or {@link MalformedStreamException}
	 * 
	 * @throws ParserException
	 * @throws MalformedStreamException
	 */
	public void processBytes() throws Exception;

}
