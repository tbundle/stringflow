package abs.ixi.server.app;

/**
 * {@code RequestDelegator} maps requests to applications within server. There
 * could be many strategies to map a request to application; and for each
 * strategy, there will be an implementation.
 */
public interface RequestDelegator {
    /**
     * delegates this request to application
     * 
     * @param request
     */
    public void delegate(RequestContainer<? extends ApplicationRequest> container);
}
