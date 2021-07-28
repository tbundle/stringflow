package abs.ixi.server.io.multipart;

/**
 * Root interface to enforce a contract on all type of contents. In all probable
 * cases, a {@link Content} implementation will have a {@link ContentSource}
 * inside it.
 */
public interface Content {
    /**
     * @return content type
     */
    public String getContentType();

    /**
     * @return number of bytes required to capture the content in in-memory.
     *         Returns -1 if the length can not be determined.
     */
    public long getContentLength();

    /**
     * Returns {@link ContentSource} for this {@link Content}
     * 
     * @return
     */
    public ContentSource getContentSource();

}
