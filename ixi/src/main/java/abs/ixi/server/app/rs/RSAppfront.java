package abs.ixi.server.app.rs;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.app.Appfront;
import abs.ixi.server.app.Application;
import abs.ixi.server.app.ApplicationResponse;
import abs.ixi.server.app.BasicAppfront;
import abs.ixi.server.app.RequestContext;
import abs.ixi.server.app.RequestReceiver;
import abs.ixi.server.app.XmppRequest;
import abs.ixi.server.common.InitializationException;

/**
 * Default implementation of an {@link Appfront}. This offers basic features of
 * an {@link Appfront} such as routing a request to application layer, sending
 * response back to {@link Application} instance in sever. Although
 * implementation of these features is very basic in nature.
 */
public class RSAppfront extends BasicAppfront {
	private static final Logger LOGGER = LoggerFactory.getLogger(RSAppfront.class);

	public RSAppfront() {
	}

	public RSAppfront(String uriSegment, List<RequestReceiver> receivers) {
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
		Endpoint ep = new Endpoint(ctx.getUri().getPath(), ctx.getUri().getQuery(), 2);

		ClassResource<RequestReceiver> cr = this.receivers.get(ep.unmatchedSegment());

		if (cr != null) {
			ep.advance();
			return cr.invokeOperation(request, ctx, ep);

		} else {
			LOGGER.error("Not matching request receiver found {}, {}", request, ctx);
			// TODO Construct failure response
			return null;
		}

	}

}
