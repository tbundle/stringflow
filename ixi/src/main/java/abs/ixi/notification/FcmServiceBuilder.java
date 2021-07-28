package abs.ixi.notification;

/**
 * A facade to {@link NotificationServiceBuilder} which allows to build FCM
 * notification service
 */
public interface FcmServiceBuilder {
    /**
     * A builder method to supply server key to the {@link FcmService} being
     * built.
     * 
     * @param serverKey
     * @return
     */
    public FcmServiceBuilder withServerKey(String serverKey);

    /**
     * Builder method to set sender id for the {@link FcmService} being built
     * 
     * @param senderId
     * @return
     */
    public FcmServiceBuilder withSenderId(String senderId);

    /**
     * Builder method to build {@link FcmService} service with the given
     * configurations
     * 
     * @return
     * @throws ServiceInstantiationException
     */
    public FcmService buildFCMService() throws ServiceInstantiationException;
}
