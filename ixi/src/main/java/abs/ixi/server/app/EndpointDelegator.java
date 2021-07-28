package abs.ixi.server.app;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.util.CollectionUtils;

/**
 * {@code EndpointDelegator} is an implementation of {@link RequestDelegator}
 * which delegates incoming requests to application based on the request
 * endpoints. This is also the default delegator used by server.
 */
public class EndpointDelegator implements RequestDelegator {
    private static final Logger LOGGER = LoggerFactory.getLogger(EndpointDelegator.class);

    private static final String SLASH = "/";

    private Map<Pattern, Deployable> applications;

    public EndpointDelegator(List<Deployable> appList) {
	this.applications = new HashMap<>();

	if (CollectionUtils.isNullOrEmpty(appList)) {
	    LOGGER.info("No application deployed ...");
	    return;
	}

	for (Deployable application : appList) {
	    try {
		Pattern p = Pattern.compile(application.getPublishAddress());
		this.applications.put(p, application);

	    } catch (PatternSyntaxException e) {
		LOGGER.error("Invalid urisegment pattern {}", e.getMessage());
	    }
	}

    }

    @Override
    public void delegate(RequestContainer<? extends ApplicationRequest> container) {
	try {
	    /**
	     * Below line has been commented as part of re-factoring. Containers
	     * do not expose source JID currently; therefore
	     */
	    // RequestContext ctx = new RequestContext(container.getSource(),
	    // container.getRequest().getEndpoint());
	    RequestContext ctx = new RequestContext(null, container.getRequest().getEndpoint());

	    Deployable application = getApplication(ctx.getUri().getPath().split(SLASH)[0]);

	    if (application != null) {
		application.process(container, ctx);

	    } else {
		LOGGER.info("No application found to delegate");
		// TODO: NO application found for give URL send XMPPResponse
	    }
	} catch (URISyntaxException e) {
	    // TODO Build the failure response
	}

    }

    private Deployable getApplication(String publishAddress) {
	if (applications != null && applications.size() != 0) {

	    for (Entry<Pattern, Deployable> entry : applications.entrySet()) {

		if (entry.getKey().matcher(publishAddress).matches()) {
		    return entry.getValue();
		}
	    }
	}

	return null;
    }

}
