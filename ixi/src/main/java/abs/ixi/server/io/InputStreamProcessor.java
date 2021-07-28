package abs.ixi.server.io;

import abs.ixi.server.common.ChannelStream;
import abs.ixi.server.packet.Packet;

/**
 * {@code InputStreamProcessor} parses input byte stream received from user and
 * generates {@link Packet} objects.
 * 
 * <p>
 * {@link InputStreamProcessor} may not be thread-safe. It is up the
 * implementation.
 * </p>
 */
public interface InputStreamProcessor<PACKET extends Packet> {
	/**
	 * Processes byte stream and generates {@link Packet}s. Stream processors
	 * are stateful objects. While processing bytes, it may happen that an error
	 * is observed; it may happen due to various reasons such as malformed
	 * stream, corrupted stream, bad request, unexpected encoding or some
	 * unexpecetd condition in the stream processor code. The processor returns
	 * true as long as it can process further byte stream (it does not imply
	 * that it was able to generate packets from all the bytes; stream processor
	 * may ignore a bad request while continue to process). A return value false
	 * indicates the processor can not process bytes further.
	 * 
	 * @param data
	 * @throws Exception
	 */
	public boolean process(byte[] data) throws Exception;

	/**
	 * Processes byte stream and generates {@link Packet}s. Stream processors
	 * are stateful objects. While processing bytes, it may happen that an error
	 * is observed; it may happen due to various reasons such as malformed
	 * stream, corrupted stream, bad request, unexpected encoding or some
	 * unexpecetd condition in the stream processor code. The processor returns
	 * true as long as it can process further byte stream (it does not imply
	 * that it was able to generate packets from all the bytes; stream processor
	 * may ignore a bad request while continue to process). A return value false
	 * indicates the processor can not process bytes further.
	 * 
	 * @param data
	 * @param offset
	 * @param length
	 * @throws Exception
	 */
	public boolean process(byte[] data, int offset, int length) throws Exception;

	/**
	 * Returns true if this stream processor has unprocessed bytes. Generally
	 * these bytes are appended before the incoming bytes before processing.
	 * 
	 * <p>
	 * Partial bytes are stored by stream processor and {@link IOService} takes
	 * these bytes and hands them over to {@link ChannelStream} which appends
	 * these bytes before incoming bytes.We can possibly do this append
	 * operation in stream processor where it belongs naturally, but it would
	 * involve a massive array copy operation.
	 * </p>
	 */
	public boolean hasUnprocessedBytes();

	/**
	 * Returns bytes which were left unprocessed in previous run. These bytes
	 * could not be decoded into a character because they did not complete a
	 * char encoding; in other words, these bytes were part of all the bytes
	 * required to represent a character.
	 */
	public byte[] getUnprocessedBytes();

	/**
	 * Flushes partial bytes maintained by this stream processor
	 */
	public void flushUnprocessedBytes();

}
