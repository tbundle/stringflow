package abs.ixi.server.common;

/**
 * A contract for an entity to be uniquely identifiable.
 * 
 * @author Yogi
 *
 * @param <T>
 */
public interface Identifiable<T> {
	/**
	 * Get unique id of an {@link Identifiable} entity
	 * 
	 * @return unique id of this entity
	 */
	public T getId();
}
