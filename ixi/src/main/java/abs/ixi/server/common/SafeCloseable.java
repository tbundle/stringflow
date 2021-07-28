package abs.ixi.server.common;

import java.io.Closeable;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of {@link AutoCloseable} interface which provides default
 * implementation to close a resource; It conviniently swallows exception and
 * invokes {@link AutoCloseable#close()}
 */
public interface SafeCloseable extends AutoCloseable {
    public static final Logger LOGGER = LoggerFactory.getLogger(SafeCloseable.class);

    default boolean safeClose(Closeable resource) {
	try {
	    resource.close();
	    return true;
	} catch (IOException e) {
	    LOGGER.warn("Failed to close resource {}", resource);
	    return false;
	}
    }
}
