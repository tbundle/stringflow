package abs.ixi.server.etc.conf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Root interface for all the config loaders.
 * 
 * @author Yogi
 *
 * @param <T>
 */
public interface ConfigLoader<T> {
	/**
	 * Load configurations from a file. the file name supplied should contain
	 * absolute path . As the file name is supplied, the method throws
	 * {@link FileNotFoundException} if file is not found on the classpath.
	 * 
	 * @param file file name
	 * @return configurations
	 */
	public T loadConfig(String file) throws IOException;

	/**
	 * Load configurations from a file.
	 * 
	 * @param file configuration file
	 * @return configurations
	 */
	public T loadConfig(File file) throws IOException;
}
