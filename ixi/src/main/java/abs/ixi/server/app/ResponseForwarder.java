package abs.ixi.server.app;

/**
 * Receives application {@link ApplicationResponse} from server entities which can not
 * route packets or may be these entities are unaware of XMPP protocol.
 */
public interface ResponseForwarder {
	/**
	 * Receives {@link ApplicationResponse} from an entity
	 * 
	 * @param responseContainer
	 * @throws Exception
	 */
	public void forward(ResponseContainer<ApplicationResponse> responseContainer) throws Exception;
}
