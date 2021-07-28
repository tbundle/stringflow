package abs.ixi.server.etc.conf;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.Stringflow;
import abs.ixi.util.StringUtils;

/**
 * {@code ServerConfigurations} loads all kind of server configurations and
 * keeps them in memory. Configuration are loaded as part of server bootstrap
 * process and kept in the memory as long as {@link Stringflow} instance is
 * alive which means that any changes to configuration files while server is
 * running will not impact the server runtime behaviour.
 */
public class ServerConfigurations implements Configurations {
	private static final Logger LOGGER = LoggerFactory.getLogger(ServerConfigurations.class);

	/**
	 * Server configuration property file. These are server core properties.
	 */
	private static final String SYSTEM_CONFIG_FILE = "system.properties";

	/**
	 * Stringflow process config file
	 */
	private static final String SF_CONFIG_FILE = "process.properties";

	/**
	 * Stringflow process properties
	 */
	private Properties sfProperties;

	/**
	 * Server properties
	 */
	private Properties systemProperties;

	/**
	 * Load all the configuration files into memory.
	 * 
	 * @throws ConfigurationException
	 */
	public void loadServerConfig() throws ConfigurationException {
		try {
			LOGGER.info("Loading configurations from file: {}", SYSTEM_CONFIG_FILE);
			PropertyConfigLoader propLoader = new PropertyConfigLoader();
			this.systemProperties = propLoader.loadConfig(SYSTEM_CONFIG_FILE);

			LOGGER.info("Loading config file {}", SF_CONFIG_FILE);
			this.sfProperties = propLoader.loadConfig(SF_CONFIG_FILE);

		} catch (IOException e) {
			throw new ConfigurationException("Failed to load server configuration", e);
		}

	}

	/**
	 * Get value for a key in system properties.
	 * 
	 * @return value for the key otherwise null
	 */
	@Override
	public String getSystemProperty(String key) {
		return this.systemProperties.getProperty(key);
	}

	/**
	 * Get value for a key in system properties.
	 * 
	 * @return value for the key otherwise null
	 */
	@Override
	public String getOrDefaultSystemProperty(String key, String value) {
		requireNonNull(key, "key can't be null");
		return this.systemProperties.containsKey(key) ? this.systemProperties.getProperty(key) : value;
	}

	// TODO: this needs to be removed
	@Override
	public String get(String key, Bundle bundle) {
		String val = get0(key, bundle);
		LOGGER.debug("****key {} has value {}", key, val);
		return val;
	}

	public String get0(String key, Bundle bundle) {
		requireNonNull(bundle, "property bundle is null");

		switch (bundle) {
		case PROCESS:
			return this.sfProperties.getProperty(key);
		case SYSTEM:
			return this.systemProperties.getProperty(key);
		default:
			return null;
		}
	}

	@Override
	public String getOrDefault(String key, String def, Bundle bundle) {
		String val = get(key, bundle);
		return val == null ? def : val;
	}

	@Override
	public Integer getOrDefaultInteger(String key, int def, Bundle bundle) {
		String val = get(key, bundle);
		return val == null ? def : parseInt(val);
	}

	@Override
	public boolean getBoolean(String key, Bundle bundle) {
		return parseBoolean(get(key, bundle));
	}

	/**
	 * Print all the configurations on console.
	 */
	public void printConfigs() {
		LOGGER.info("====================== System Properties =========================");

		for (Entry<Object, Object> prop : this.systemProperties.entrySet()) {
			LOGGER.info("{}:{}", prop.getKey().toString(), prop.getValue().toString());
		}

		LOGGER.info("==================== Stringflow Process Configrations ======================");

		for (Entry<Object, Object> prop : this.sfProperties.entrySet()) {
			LOGGER.info("{}:{}", prop.getKey(), prop.getValue());
		}

		LOGGER.info("Platform native byte order {} \n", ByteOrder.nativeOrder());
	}

}
