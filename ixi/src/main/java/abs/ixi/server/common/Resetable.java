package abs.ixi.server.common;

/**
 * Interface to enforce reset operation. Although the implementation mechanism
 * is dependent on the definition of the operation but the interface enforces
 * that reset operation on a instance will flush out data, reset counters,
 * position and other markers held by the object.
 * <p>
 * The implementation must ensure that it exposes mechanism to reload the
 * instance so that it can be reused; however the reload operation signatures
 * will vary implementations.
 * <p>
 */
public interface Resetable {
    /**
     * Flushes out data, repositions counters and markers held by this object
     */
    public void reset();
}
