package abs.ixi.server.rest;

import abs.ixi.server.BasicComponent;
import abs.ixi.server.common.InstantiationException;
import abs.ixi.server.etc.conf.Configurations;

/**
 * {@code RESTGateway} is an extension server component which allows to make
 * outbound calls to REST endpoints. These REST endpoints will always be hosted
 * on an external environment.
 * 
 * @author Yogi
 *
 */
public class RESTGateway extends BasicComponent {

	private static final String COMPONENT_NAME = "restGateway";

	/**
	 * Cache store for all the rest endpoints registered
	 */
	// TODO: Cache needs to loaded from database
	private RestEndpointCache endpointCache;

	public RESTGateway(Configurations config) throws InstantiationException {
		super(COMPONENT_NAME, config);
	}

}
