package abs.ixi.server.session;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Contract for a datasource to act as Session Store for Stringflow cluster.
 * 
 * @author Yogi
 *
 */
public interface SessionStore<K, V> {
	/**
	 * Retrieve a value from the store
	 * 
	 * @param key
	 * @return
	 */
	public V get(K key);

	/**
	 * Strore a value into {@code SessionStore}
	 * 
	 * @param key
	 * @param val
	 * @return
	 */
	public V store(K key, V val);

	/**
	 * Check if a value exists in Session Store for a key
	 * 
	 * @param key
	 * @return
	 */
	public boolean contains(K key);

	/**
	 * Remove a value from session store associated with given key
	 * 
	 * @param key
	 * @return
	 */
	public V remove(K key);

	/**
	 * Purge all entries which matches the predictate
	 * 
	 * @param p
	 */
	public void purge(Predicate<K> p);

	/**
	 * Purge all entries stored in this session store
	 */
	public void purgeAll();

	/**
	 * Purge all the entries in the session store. Additionally, each value will
	 * be given to a consumer before being removed from the sessions store.
	 * 
	 * @param consumer
	 */
	public void purgeAll(Consumer<V> consumer);

	/**
	 * Execute a consumer for each value stored in session store
	 * 
	 * @param consumer
	 */
	public void forEach(Consumer<V> consumer);
	
}
