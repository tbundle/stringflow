package abs.ixi.server.app;

/**
 * Root interface for application requests
 */
public interface ApplicationRequest {
    /**
     * @return request end point URI. This uri uniquely maps a request to an
     *         appfront within an application
     */
    public String getEndpoint();

    /**
     * 
     * @return requestId
     */
    public String getId();
}
