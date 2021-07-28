package abs.ixi.server.io.multipart;

/**
 * A {@code MultipartContentHandler} is nothing but a wrapper around the content
 * bytes; it simplifies access interface to the content bytes.
 * <p>
 * The handler instances are expected to be stateful and NOT thread-safe
 * </p>
 */
public interface ContentHandler<T> {
	public T getContent();
}
