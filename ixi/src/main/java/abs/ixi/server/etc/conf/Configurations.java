package abs.ixi.server.etc.conf;

/**
 * Facade interface for server configurations. Any entity within server can get
 * {@link Configurations} at runtime and access the configurations.
 */
public interface Configurations {
	/**
	 * Get the value for a configuration property from a given property bundle.
	 * 
	 * @param key
	 * @return
	 */
	public String get(String key, Bundle bundle);

	/**
	 * Get the value for a configuration property from a given property bundle.
	 * If the value of the key is null or the key is missing from
	 * configurations, default value supplied is returned.
	 * 
	 * @param key
	 * @return
	 */
	public String getOrDefault(String key, String def, Bundle bundle);

	/**
	 * Get integer value for a configuration property for a given property
	 * bundle. It may throw a {@link NumberFormatException}
	 * 
	 * @param key
	 * @param def
	 * @param bundle
	 * @return
	 */
	public Integer getOrDefaultInteger(String key, int def, Bundle bundle);

	/**
	 * Get boolean value for a configuration property from a given property
	 * bundle. Parsing a string into boolean interprets to true if the string
	 * value is <i>true</i> otherwise false; even a null value will be parsed
	 * into null. Therefore supplying a default value will never be used. Thats
	 * why this method does not accept any default value.
	 * 
	 * @param key
	 * @param bundle
	 * @return
	 */
	public boolean getBoolean(String key, Bundle bundle);

	/**
	 * Get value for a system property. System properties are not Java system
	 * properties; they are the proeprties loaded from system.properties config
	 * file.
	 * 
	 * @param key Property key
	 * @return value if present otherwise null
	 */
	public String getSystemProperty(String key);

	/**
	 * Get value for a System property; if there is no such property or value is
	 * null the default value is returned.
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public String getOrDefaultSystemProperty(String key, String value);

	/**
	 * Server configurations are bundled into various categories i.e. Process
	 * bundle, system bundle etc.
	 * 
	 * @author Yogi
	 *
	 */
	public enum Bundle {
		/**
		 * System level properties
		 */
		SYSTEM,
		/**
		 * Property bundle created from sf-prcoess.properties
		 */
		PROCESS,

		/**
		 * Cluster properties
		 */
		CLUSTER;
	}

}
