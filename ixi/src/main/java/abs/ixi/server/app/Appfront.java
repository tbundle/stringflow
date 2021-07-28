package abs.ixi.server.app;

import abs.ixi.server.common.Initializable;

/**
 * An {@link Appfront} is the entry point for a deployed {@link Application} in
 * server. There must be at least one {@link Appfront} for each
 * {@link Application} deployed within server.
 * 
 * For an incoming request, server hands-over the packet to the
 * {@link Application} instance and the {@link Application} is responsible to
 * find out the right {@link Appfront} which will process the request.
 * 
 * Each {@link Appfront} has certain configuration parameters specified in
 * deploy.config/deploy.xml in an Application. Based on these configurations,
 * {@link Application} hands-over the request to one of these {@link Appfront}s
 * 
 * For each request, there must be just one {@link Appfront} which can process
 * it. If the configurations specified are not right, {@link Application} may
 * get multiple {@link Appfront} instances eligible for the same request. In
 * this case {@link Application} will forward request to FIRST {@link Appfront}
 * in the list. Ideally, this situation will not arise if the configurations are
 * correct.
 */
public interface Appfront extends Initializable {
	/**
	 * Returns uri segment for this {@link Appfront}
	 */
	public String getUriSegment();

	/**
	 * Server hands-over the request to this method along with
	 * {@link RequestContext}. This is a synchronous call and returned
	 * {@link ApplicationResponse} will be sent to the client
	 */
	public ApplicationResponse doProcess(XmppRequest request, RequestContext ctx) throws Exception;

	/**
	 * This method is called by server to indicate that server is removing this
	 * {@link Appfront} from memory
	 */
	public void destroy();

}
