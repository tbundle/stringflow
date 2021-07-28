package abs.ixi.server.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@code TimeSlice} represents a duration bound bucket of objects. It stores
 * objects witin a map. The implementation is <b>not thread-safe</b>
 * 
 * @author Yogi
 *
 * @param <OBJECT>
 */
public class TimeSlice<OBJECT> {
	private long startTime;
	private Map<OBJECT, Integer> store;

	public TimeSlice() {
		this.startTime = System.currentTimeMillis();
		this.store = new ConcurrentHashMap<>();
	}

	public Integer addOrIncrement(OBJECT key) {
		return this.store.merge(key, 1, (v1, v2) -> v1 + v2);
	}

	/**
	 * Remove an object from this timeslice.
	 * 
	 * @param key
	 * @return
	 */
	public Integer remove(OBJECT key) {
		return this.store.remove(key);
	}

	/**
	 * @return count value associated with the key; returns 0 if the key does
	 *         not exist.
	 */
	public int count(OBJECT key) {
		Integer count = this.store.get(key);
		return count != null ? count : 0;
	}

	/**
	 * Start time of this timeslice.
	 * 
	 * @return
	 */
	public long getStartTime() {
		return startTime;
	}

	/**
	 * Check if a given object stored in this timeslice.
	 * 
	 * @param o
	 * @return
	 */
	public boolean contains(OBJECT o) {
		return this.store.containsKey(o);
	}

	/**
	 * Size of this timeSlice; its nothing but number of objects stored in this
	 * timeslice.
	 * 
	 * @return
	 */
	public int size() {
		return this.store.size();
	}

	/**
	 * Invoke a {@link DeathHandler} on all the values stored in this timeslice.
	 * 
	 * @param handler
	 */
	void invoke(DeathHandler<OBJECT> handler) {
		this.store.forEach((k, v) -> handler.onDeath(k, v));
	}

	@Override
	public String toString() {
		return "TimeSlice-" + this.startTime + "[" + this.store.size() + "]";
	}

}
