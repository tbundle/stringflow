package abs.ixi.server.common;

/**
 * A contract for anyhting to act as a byte source; it could be a file, memory
 * bytes or an entity.
 * 
 * @author Yogi
 *
 */
public interface ByteSource {
    /**
     * Copy bytes to a {@link ByteSink} instance. Returns number of bytes copied
     * to the sink; The method tries to copy as many bytes as possible to the
     * sink.
     * 
     * @param sink
     * @return
     * @throws Exception
     */
    public int copyBytes(ByteSink sink) throws Exception;

    /**
     * Copy bytes to a {@link ByteSink} instance. Returns number of bytes copied
     * to the sink; The method tries to copy as many bytes as possible to the
     * sink.
     * 
     * @param sink
     * @param count
     * @return
     * @throws Exception
     */
    public int copyBytes(ByteSink sink, int count) throws Exception;

    /**
     * Copy bytes to a {@link ByteSink} instance. Returns number of bytes copied
     * to the sink; The method tries to copy as many bytes as possible to the
     * sink.
     * 
     * @param sink
     * @return
     * @throws Exception
     */
    public int moveBytes(ByteSink sink) throws Exception;

    /**
     * Copy bytes to a {@link ByteSink} instance. Returns number of bytes copied
     * to the sink; The method tries to copy as many bytes as possible to the
     * sink.
     * 
     * @param sink
     * @param count
     * @return
     * @throws Exception
     */
    public int moveBytes(ByteSink sink, int count) throws Exception;

    /**
     * Check if the {@code ByteSource} has bytes
     * 
     * @return
     */
    public boolean hasBytes();

}
