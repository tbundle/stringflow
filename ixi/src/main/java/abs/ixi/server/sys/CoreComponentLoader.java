package abs.ixi.server.sys;

import static abs.ixi.server.etc.conf.Configurations.Bundle.PROCESS;
import static java.util.Objects.requireNonNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.httpclient.util.StringUtils;
import abs.ixi.server.BasicComponent;
import abs.ixi.server.DiscoHandler;
import abs.ixi.server.PacketConsumer;
import abs.ixi.server.ServerComponent;
import abs.ixi.server.common.InstantiationException;
import abs.ixi.server.etc.conf.Configurations;
import abs.ixi.server.etc.conf.ProcessConfigAware;
import abs.ixi.server.io.IOController;
import abs.ixi.server.io.net.NetworkTransport;
import abs.ixi.server.muc.MultiUserChatHandler;
import abs.ixi.server.router.PacketRouter;
import abs.ixi.server.router.Router;
import abs.ixi.server.session.PresenceManager;

/**
 * A loader class to load all the core components. It should not be confused
 * with a class loader program. It simply instantiates all the core server
 * components and loads them into memory.
 * 
 * @author Yogi
 *
 */
public class CoreComponentLoader implements ProcessConfigAware {
	private static final Logger LOGGER = LoggerFactory.getLogger(CoreComponentLoader.class);

	/**
	 * Immutable server configurations
	 */
	protected Configurations conf;

	/**
	 * List of core components that will be loaded
	 */
	private static final List<Class<?>> comps = new ArrayList<>();

	static {
		comps.add(PacketRouter.class);
		comps.add(NetworkTransport.class);
		comps.add(IOController.class);
		comps.add(DiscoHandler.class);
		comps.add(PresenceManager.class);
		comps.add(MultiUserChatHandler.class);
	}

	public CoreComponentLoader(Configurations conf) {
		requireNonNull(conf, "Configs can't be null");
		this.conf = conf;
	}

	/**
	 * Load core server components. A static list of core server components is
	 * maintained. The method fails-fast as soon as it fails to load first
	 * component.
	 * 
	 * @param configs
	 * @return
	 * @throws InstantiationException
	 * @throws ComponentLoadingException
	 */
	public Map<String, ServerComponent> load() throws ComponentLoadingException {
		Map<String, ServerComponent> map = new HashMap<>();

		String[] filter = getLoadFilter(this.conf);

		Router router = loadPacketRouter();

		if (router == null) {
			LOGGER.error("Packet router instance not found; quiting component loading");
			throw new ComponentLoadingException("Packet Router instance not available");
		} else {
			map.put(((BasicComponent) router).getName(), (BasicComponent) router);
		}

		for (Class<?> clz : comps) {
			if (!filtered(filter, clz.getName())) {

				if (Router.class.isAssignableFrom(clz)) {
					continue; // Router has already been loaded
				}

				try {

					Constructor<?> constructor = null;
					ServerComponent component = null;

					if (PacketConsumer.class.isAssignableFrom(clz)) {
						constructor = clz.getConstructor(Configurations.class, Router.class);
						component = (ServerComponent) constructor.newInstance(this.conf, router);

					} else {
						constructor = clz.getConstructor(Configurations.class);
						component = (ServerComponent) constructor.newInstance(this.conf);
					}

					map.put(component.getName(), component);

				} catch (NoSuchMethodException | SecurityException | IllegalAccessException
						| java.lang.InstantiationException | IllegalArgumentException | InvocationTargetException e) {
					LOGGER.error("Failed to load core server component");
					throw new ComponentLoadingException("Failed to load core server component", e);
				}
			}
		}

		return map;
	}

	private Router loadPacketRouter() throws ComponentLoadingException {
		for (Class<?> clz : comps) {
			if (Router.class.isAssignableFrom(clz)) {
				LOGGER.info("Loading packet router");
				try {
					Constructor<?> constructor = clz.getConstructor(Configurations.class);
					return (Router) constructor.newInstance(this.conf);

				} catch (NoSuchMethodException | SecurityException | IllegalAccessException
						| java.lang.InstantiationException | IllegalArgumentException | InvocationTargetException e) {
					LOGGER.error("Failed to load packet router");
					throw new ComponentLoadingException("Failed to load packet router", e);
				}
			}
		}

		return null;
	}

	/**
	 * Check if a component class is part of load filter
	 * 
	 * @param name
	 * @return
	 */
	private boolean filtered(String[] filter, String name) {
		if (filter != null && filter.length > 0) {
			for (String s : filter) {
				if (StringUtils.safeEquals(s, name)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Component load filter value array; returns null if no value exist.
	 */
	private String[] getLoadFilter(Configurations configs) {
		String filter = configs.get(_CORE_COMPONENT_LOAD_FILTER, PROCESS);
		return filter != null ? filter.split(",") : null;
	}

}
