package abs.ixi.server.session;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.ServerComponent;
import abs.ixi.server.packet.xmpp.BareJID;

/**
 * A repository of {@link UserSession}s within server. {@code SessionStore}
 * maintains a cache of sessions and exposes interface to query user sessions.
 * Only {@link SessionManager} has "write" permission on {@link UserSession}s,
 * therefore write operations have default access(package access).
 * <p>
 * There can be just one instance of {@link DefaultSessionStore} with server.
 * Therefore, {@link DefaultSessionStore} must be a Singleton. At the same time,
 * it is also important to ensure thread-safety as SessionStore may be accessed
 * by various {@link ServerComponent}s.
 * </p>
 */
class DefaultSessionStore implements SessionStore<BareJID, UserSession> {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSessionStore.class.getName());

	/**
	 * Initial capacity of the session cache
	 */
	private static final int CACHE_INITIAL_CAPACITY = 10000;

	/**
	 * Session Cache load factor; this will control resizing of the bins.
	 */
	private static final float CACHE_LOAD_FACTOR = 0.75F;

	/**
	 * Session cache is updated by multiple threads at the same time. This needs
	 * to be adjusted as per the number of threads which will be updating the
	 * cache concurrently. Too high number will waste space and time.
	 */
	private static final int CACHE_CONCURRENCY_LEVEL = 4;

	private static final Map<BareJID, UserSession> sessionCache;

	static {
		sessionCache = new ConcurrentHashMap<>(CACHE_INITIAL_CAPACITY, CACHE_LOAD_FACTOR, CACHE_CONCURRENCY_LEVEL);
	}

	/**
	 * Get the {@link UserSession} object associated with given bare JID
	 * 
	 * @param bareJid
	 * @return {@link UserSession} for given {@link BareJID}; if not found,
	 *         returns null
	 */
	@Override
	public UserSession get(BareJID bareJid) {
		return sessionCache.get(bareJid);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserSession store(BareJID key, UserSession userSession) {
		return sessionCache.put(key, userSession);
	}

	/**
	 * Convinience method to store a {@link UserSession} object into
	 * {@link DefaultSessionStore} cache. If there is already a
	 * {@link UserSession} stored with the same bare JID, the session object
	 * will be replaced.
	 * 
	 * @param session
	 */
	UserSession store(UserSession session) {
		return sessionCache.put(session.getBareJID(), session);
	}

	/**
	 * Check if the session cache contains a {@link UserSession} object for
	 * given bare JID.
	 * 
	 * @param bareJID
	 * @return
	 */
	@Override
	public boolean contains(BareJID bareJID) {
		return sessionCache.containsKey(bareJID);
	}

	/**
	 * Remove {@link UserSession} object associated with the given bareJID from
	 * session cache
	 * 
	 * @param bareJID
	 */
	@Override
	public UserSession remove(BareJID bareJID) {
		return sessionCache.remove(bareJID);
	}

	@Override
	public void purge(Predicate<BareJID> p) {
		LOGGER.info("Purging Session Store");
		Iterator<Entry<BareJID, UserSession>> itr = sessionCache.entrySet().iterator();

		while (itr.hasNext()) {
			Entry<BareJID, UserSession> entry = itr.next();
			if (p.test(entry.getKey())) {
				itr.remove();
			}
		}
	}

	/**
	 * Purge this {@link DefaultSessionStore}. This will result in dropping all
	 * the {@link UserSession}s from session cache.
	 */
	@Override
	public void purgeAll() {
		LOGGER.info("Purging session store cache");
		sessionCache.clear();
	}

	@Override
	public void purgeAll(Consumer<UserSession> consumer) {
		LOGGER.info("Purging Session Store");

		Iterator<Entry<BareJID, UserSession>> itr = sessionCache.entrySet().iterator();

		while (itr.hasNext()) {
			Entry<BareJID, UserSession> entry = itr.next();
			consumer.accept(entry.getValue());
			itr.remove();
		}
	}

	/**
	 * Perform a given action on each of the values({@code UserSession}) in
	 * session cache
	 * 
	 * @param consumer
	 */
	@Override
	public void forEach(Consumer<UserSession> consumer) {
		sessionCache.values().forEach(consumer);
	}

}
