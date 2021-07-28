package abs.ixi.server.app;

/**
 * {@code JidDelegator} is an implementation of {@link RequestDelegator} which
 * delegates incoming requests to application based on application JID. This is
 * also the default delegator used by server.
 */
public class JidDelegator implements RequestDelegator {

	@Override
	public void delegate(RequestContainer<? extends ApplicationRequest> request) {
		//TODO Have not been implemented
	}

}
