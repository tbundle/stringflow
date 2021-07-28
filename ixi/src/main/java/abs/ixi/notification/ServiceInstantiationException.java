package abs.ixi.notification;

/**
 * {@link ServiceInstantiationException} is thrown when system fails to
 * instantiate a service
 */
public class ServiceInstantiationException extends Exception {
    private static final long serialVersionUID = 1L;

    private String service;

    public ServiceInstantiationException(String service) {
	super("Failed to instantiate " + service);
	this.service = service;
    }

    public ServiceInstantiationException(String service, String msg) {
	super(msg);
	this.service = service;
    }

    public String getServiceName() {
	return this.service;
    }

}
