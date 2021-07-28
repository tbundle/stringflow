package abs.ixi.notification;

/**
 * Factory class to instantiate and configure {@link NotificationService}
 */
public class NotificationServiceFactory {
	/**
	 * Returns {@link FcmServiceBuilder} which can be used to configure
	 * {@link FcmService}. Ideally, an application should have just one instance
	 * of the service
	 * 
	 * @throws ServiceInstantiationException
	 */
	public static FcmServiceBuilder newFCMService() throws ServiceInstantiationException {
		return new NotificationServiceBuilder(ExternalService.FCM);
	}

	/**
	 * Returns {@link ApnsServiceBuilder} which can be used to instantiate and
	 * configure {@link ApnsService}. Ideally, an application should have just
	 * one instance of the service
	 * 
	 * @throws ServiceInstantiationException
	 */
	public static ApnsServiceBuilder newAPNService() throws ServiceInstantiationException {
		return new NotificationServiceBuilder(ExternalService.APNS);
	}

}
