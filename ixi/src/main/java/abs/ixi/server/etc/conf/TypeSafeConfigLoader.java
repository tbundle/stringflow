package abs.ixi.server.etc.conf;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * {@link ConfigLoader} implementation to load HOCON format.
 * 
 * @author Yogi
 *
 */
public class TypeSafeConfigLoader implements ConfigLoader<Config> {
	@Override
	public Config loadConfig(String file) throws IOException {
		requireNonNull(file, "config file name can't be null");
		return ConfigFactory.parseResources(file);
	}

	/**
	 * Load server component configurations
	 */
	@Override
	public Config loadConfig(File file) {
		requireNonNull(file, "config file can not be null");
		return ConfigFactory.parseFile(file);
	}

}
