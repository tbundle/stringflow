package abs.ixi.server;

import abs.ixi.server.common.Identifiable;

/**
 * A contract for any entity within server to be able to emit its state. State
 * of an entity includes following-
 * <ul>
 * <li>Identification (if the entity is {@link Identifiable})
 * <li>Execution state (Running/Suspended if applicable)
 * <li>Size of collections held
 * <li>Any other information which has memory or CPU impact
 * </ul>
 * 
 * @author Yogi
 *
 */
public interface StateEmitter {
    /**
     * @return Emit the state of this entity
     */
    public EntityState emit();
}
