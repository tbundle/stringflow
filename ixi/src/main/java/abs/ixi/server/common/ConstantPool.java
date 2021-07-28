package abs.ixi.server.common;

import java.util.Collection;

/**
 * A pool of contstants which are internalized
 * 
 * @author Yogi
 *
 */
public interface ConstantPool<T> {
	public Collection<T> values();
}
