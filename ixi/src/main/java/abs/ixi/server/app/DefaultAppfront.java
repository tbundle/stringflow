package abs.ixi.server.app;

import java.util.List;

import abs.ixi.server.common.InitializationException;

/**
 * Default implementation of an {@link Appfront}. This offers basic features of
 * an {@link Appfront} such as routing a request to application layer, sending
 * response back to {@link Application} instance in sever. Although
 * implementation of these features is very basic in nature.
 */
public class DefaultAppfront extends BasicAppfront {

	public DefaultAppfront() {
	}

	public DefaultAppfront(String uriSegment, List<RequestReceiver> receivers) {
		super(uriSegment, receivers);
	}

	@Override
	public void init() throws InitializationException {
		this.initialized = true;
	}

	@Override
	public boolean isInitialized() {
		return this.initialized;
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}

	@Override
	public ApplicationResponse doProcess(XmppRequest request, RequestContext ctx) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
