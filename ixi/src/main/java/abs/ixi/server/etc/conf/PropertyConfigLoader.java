package abs.ixi.server.etc.conf;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link ConfigLoader} implementation to read and load java property files.
 */
public class PropertyConfigLoader implements ConfigLoader<Properties> {
	private static final Logger LOGGER = LoggerFactory.getLogger(PropertyConfigLoader.class);

	/**
	 * Read a java property file adn loads key/value pairs found into
	 * {@link Properties} objects. The file specified must be in classpath.
	 * 
	 * @param file configuration file to be read
	 * @return instance of {@link Properties} object
	 * @throws IOException if any IO error occurs during file read
	 */
	@Override
	public Properties loadConfig(String file) throws IOException {
		requireNonNull(file, "config file name can't be null");

		try (InputStream is = PropertyConfigLoader.class.getClassLoader().getResourceAsStream(file)) {
			if (is != null) {
				Properties props = new Properties();
				props.load(is);

				return props;
			}

			throw new FileNotFoundException("Unable to load file");

		} catch (IOException e) {
			LOGGER.error("Failed to load config from file {}", file);
			throw e;
		}
	}

	/**
	 * Reads configuration file specified and loads key/value pairs found into
	 * {@link Properties} objects.
	 * 
	 * @param file configuration file to be read
	 * @return instance of {@link Properties} object
	 * @throws IOException if any IO error occurs during file read
	 */
	@Override
	public Properties loadConfig(File file) throws IOException {
		try (InputStream is = new FileInputStream(file)) {
			Properties props = new Properties();
			props.load(is);

			return props;
		} catch (IOException e) {
			LOGGER.error("Failed to config from file {}", file);
			throw e;
		}
	}

}
