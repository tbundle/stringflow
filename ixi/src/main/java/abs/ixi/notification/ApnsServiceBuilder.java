package abs.ixi.notification;

import abs.ixi.notification.ApnsConfiguration.Environment;

/**
 * A facade to {@link NotificationServiceBuilder} which allows to build APNS
 * notification service
 */
public interface ApnsServiceBuilder {
    /**
     * A builder method to supply certification file path to the
     * {@link ApnsService} being built.
     * 
     * @param certFilePath
     * @return
     */
    public ApnsServiceBuilder withCertFilePath(String certFilePath);

    /**
     * Builder method to set passPhrase for the {@link ApnsService} being built
     * 
     * @param passPhrase
     * @return
     */
    public ApnsServiceBuilder withPassPhrase(String passPhrase);

    /**
     * Builder method to set environment for the {@link ApnsService} being built
     * 
     * @param env
     * @return
     */
    public ApnsServiceBuilder withEnvironment(Environment env);

    /**
     * Builder method to build {@link ApnsService} service with the given
     * configurations
     * 
     * @return
     * @throws ServiceInstantiationException
     */
    public ApnsService buildApnsService() throws ServiceInstantiationException;
}
