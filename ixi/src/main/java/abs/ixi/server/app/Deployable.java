package abs.ixi.server.app;

import abs.ixi.server.common.NameAware;

/**
 * This is a facade interface implemented by {@link Application}. It enforces
 * implementation to be name aware and deployable.
 */
public interface Deployable extends NameAware<String> {
	/**
	 * Returns the string name of this {@code Deployable}
	 */
	public String getName();

	/**
	 * Triggers initialization sequence for this deployable. The method may
	 * throw Exception for various reasons.
	 * 
	 * @throws Exception
	 */
	public void init() throws Exception;

	/**
	 * Starts this deployable for request processing. The method may throw
	 * exceptions for various reasons.
	 * 
	 * @throws Exception
	 */
	public void start() throws Exception;

	/**
	 * The method submits an envelope with {@link XmppRequest} instance for
	 * processing.
	 * <p>
	 * {@link Deployable} process requests in a asynchronous way; the method
	 * does not return the response as part of call, rather the response
	 * generated is written on the socket directly as and when it is ready.
	 * </p>
	 * 
	 * @param envelope
	 */
	public void process(RequestContainer<? extends ApplicationRequest> container, RequestContext ctx);

	/**
	 * Returns the address at which application is published within server
	 */
	public String getPublishAddress();

	/**
	 * Deploys this {@code Deployable} in server. If the deployment fails which
	 * can happen due to various reason, the method throws
	 * {@link ApplicationError}
	 * 
	 * @throws ApplicationError
	 */
	public void deploy() throws ApplicationError;

	/**
	 * Shutsdown this {@code Deployable}. The operation can not be reverted.
	 * 
	 * @throws Exception
	 */
	public void shutdown() throws Exception;
}
