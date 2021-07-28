package abs.ixi.server.session;

import java.util.function.Consumer;
import java.util.function.Predicate;

import abs.ixi.server.ServerComponent;
import abs.ixi.server.packet.xmpp.BareJID;
import net.spy.memcached.MemcachedClient;

/**
 * A repository of {@link UserSession}s in Memcached. Its recommended to use
 * this store during clustering. {@code SessionStore} maintains a cache of
 * sessions and exposes interface to query user sessions. Only
 * {@link SessionManager} has "write" permission on {@link UserSession}s,
 * therefore write operations have default access(package access).
 * <p>
 * There can be just one instance of {@link DefaultSessionStore} with server.
 * Therefore, {@link DefaultSessionStore} must be a Singleton. At the same time,
 * it is also important to ensure thread-safety as SessionStore may be accessed
 * by various {@link ServerComponent}s.
 * </p>
 */
public class MemCachedSessionStore implements SessionStore<BareJID, UserSession> {
	private MemcachedClient client;

	private static MemCachedSessionStore instance;

	private MemCachedSessionStore() {
		//TODO: need to instantiate MemcachedClient
		// TODO Auto-generated constructor stub
	}

	public static MemCachedSessionStore getInstance() {
		if (instance == null) {
			synchronized (MemCachedSessionStore.class) {
				if (instance == null) {
					instance = new MemCachedSessionStore();
				}

			}
		}

		return instance;
	}

	@Override

	public UserSession get(BareJID bareJID) {
		return (UserSession) client.get(bareJID.toString());
	}

	@Override
	public UserSession store(BareJID bareJID, UserSession us) {
		client.set(bareJID.toString(), 0, us);
		return us;
	}

	@Override
	public boolean contains(BareJID bareJID) {
		Object obj = client.get(bareJID.toString());
		return obj != null;
	}

	@Override
	public UserSession remove(BareJID bareJID) {
		UserSession us = (UserSession) client.get(bareJID.toString());
		client.delete(bareJID.toString());
		return us;
	}

	@Override
	public void purge(Predicate<BareJID> p) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void purgeAll() {
		client.flush();

	}

	@Override
	public void purgeAll(Consumer<UserSession> consumer) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void forEach(Consumer<UserSession> consumer) {
		throw new UnsupportedOperationException();
	}

}
