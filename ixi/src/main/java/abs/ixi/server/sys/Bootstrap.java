package abs.ixi.server.sys;

import static abs.ixi.server.etc.conf.Configurations.Bundle.PROCESS;
import static abs.ixi.server.sys.Platform.availableProcessors;
import static abs.ixi.server.sys.Platform.freeMemory;
import static abs.ixi.server.sys.Platform.maxMemory;
import static abs.ixi.server.sys.Platform.osArchitecture;
import static abs.ixi.server.sys.Platform.osName;
import static abs.ixi.server.sys.Platform.osVersion;
import static abs.ixi.server.sys.Platform.totalMemory;
import static abs.ixi.util.SystemPropertyUtils.set;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.ServerComponent;
import abs.ixi.server.etc.conf.Configurations;
import abs.ixi.server.etc.conf.ProcessConfigAware;

/**
 * Server bootstrap process. During bootstrap, server runtime is loaded into
 * memeory; it involves bringing core components are associated entities into
 * memory. When bootstrap is in progress, server runtime loading is in progress,
 * therefore, during bootstrap, server runtime access is blocked for entities.
 * 
 * @author Yogi
 *
 */
public class Bootstrap extends CoreComponentLoader {
	private static final Logger LOGGER = LoggerFactory.getLogger(Bootstrap.class);

	/**
	 * XMPP String literals which are frequently used. These Strings are
	 * internalized.
	 */
	public static List<String> xmppLiterals = new ArrayList<>();

	static {
		xmppLiterals.addAll(Arrays.asList("stream", "auth", "success", "features", "mechanism", "iq", "resource",
				"bind", "session", "ver", "presence", "message", "body", "jid", "pubsub", "entities", "entity", "item",
				"group", "name", "retract", "open", "error", "close", "data"));

		xmppLiterals.addAll(
				Arrays.asList("id", "to", "from", "xmlns", "version", "xml:lang", "encoding", "type", "hash", "node",
						"sid", "rid", "seq", "block-size", "hold", "wait", "route", "ack", "polling", "inactivity"));
	}

	public Bootstrap(Configurations conf) {
		super(conf);
	}

	/**
	 * Internalize String literals.
	 */
	private void internalizeXmppLiterals() {
		LOGGER.debug("Internalizing XMPP Strings");

		for (String literal : xmppLiterals) {
			literal.intern();
		}
	}

	/**
	 * Initiate bootstrap process
	 * 
	 * @throws Exception
	 */
	public Map<String, ServerComponent> initiate() throws Exception {
		LOGGER.info("internalizing string literals");

		internalizeXmppLiterals();
		importParserConfigsIntoJavaSystem();
		printUnderlyingPlatformProperties();

		Map<String, ServerComponent> components = loadServerComponents();

		return components;
	}

	/**
	 * Load server components into memory
	 * 
	 * @param configs
	 * @throws Exception
	 */
	private Map<String, ServerComponent> loadServerComponents() throws ComponentLoadingException {
		try {
			return load();
		} catch (ComponentLoadingException e) {
			throw e;
		} catch (Exception e) {
			throw new ComponentLoadingException(e);
		}
	}

	/**
	 * Setting parser properties as system variables
	 */
	private void importParserConfigsIntoJavaSystem() {
		LOGGER.info("Setting swift parser properties");

		set(MAX_ATTR_COUNT_LIMIT, this.conf.get(ProcessConfigAware.MAX_ATTR_COUNT_LIMIT, PROCESS));
		set(PARSER_MAX_ATTRIBUTE_NUMBER, this.conf.get(ProcessConfigAware.PARSER_MAX_ATTRIBUTE_NUMBER, PROCESS));
		set(PARSER_MAX_ELEMENT_NAME_SIZE, this.conf.get(ProcessConfigAware.PARSER_MAX_ELEMENT_NAME_SIZE, PROCESS));
		set(PARSER_MAX_ATTRIBUTE_NAME_SIZE, this.conf.get(ProcessConfigAware.PARSER_MAX_ATTRIBUTE_NAME_SIZE, PROCESS));
		set(PARSER_MAX_ATTRIBUTE_VALUE_SIZE,
				this.conf.get(ProcessConfigAware.PARSER_MAX_ATTRIBUTE_VALUE_SIZE, PROCESS));
		set(PARSER_MAX_CDATA_SIZE, this.conf.get(ProcessConfigAware.PARSER_MAX_CDATA_SIZE, PROCESS));
	}

	/**
	 * Print underlying system proeprties
	 */
	private void printUnderlyingPlatformProperties() {
		try {
			// Below two are platform dependent and lot of time value for
			// these properties are null in Java system. And we don't came use
			// of them as of now. Therefore have commented out them.
			// LOGGER.debug("Processor Identifier: {}", processorIdentifier());
			// LOGGER.debug("Processor Architecture: {}",
			// processorArchitecture());
			LOGGER.info("OS Name: {}", osName());
			LOGGER.debug("OS Architecture: {}", osArchitecture());
			LOGGER.debug("OS Version: {}", osVersion());
			LOGGER.info("Available Processors: {}", availableProcessors());
			LOGGER.info("Free Memory: {}", freeMemory());
			LOGGER.debug("Max Memory: {}", maxMemory());
			LOGGER.info("Total Memory: {}", totalMemory());
			LOGGER.debug("FileSystem: {}", Platform.fileSystemType());

		} catch (Exception e) {
			LOGGER.warn("Execption caught while reading underlying system proerpties", e);
		}
	}

}
