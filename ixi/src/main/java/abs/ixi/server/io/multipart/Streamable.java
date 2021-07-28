package abs.ixi.server.io.multipart;

/**
 * A contract for any class to be {@link Streamable}. An {@link Streamable}
 * class exposes interface which allows bytes to be available in chunks.
 * 
 * @author Yogi
 *
 */
public interface Streamable {
    /**
     * Get a chunk of bytes from this Streamable object; bytes will be filled
     * into the array supplied starting index zero. Number of bytes returned
     * will always be at most n where n is the given count.
     * 
     * @param dest
     *            destination array in which bytes will be copied
     * @param count
     *            number of bytes to be copied
     * @return number of actual bytes copied into destination array; returns 0
     *         if there are no more bytes left
     */
    public int getByteChunk(byte[] dest, int count);

    /**
     * Get a chunk of bytes from this Streamable object; bytes will be filled
     * into the array supplied starting index zero. Number of bytes returned
     * will always be at most n where n is the given count.
     * 
     * @param dest
     *            destination array in which bytes will be copied
     * @param count
     *            number of bytes to be copied
     * @return number of actual bytes copied into destination array; returns 0
     *         if there are no more bytes left
     * @param offset
     *            offset of the first byte which will be copied
     */
    public int getByteChunk(byte[] dest, long offset, int count);

}
