package abs.ixi.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.notification.ApnsConfiguration.Environment;

/**
 * This is a common builder class to build all the {@link NotificationService}s
 * ({@link FcmService} and {@link ApnsService}). Although its just one builder
 * which can build all the notification services, it has multiple facades; one
 * for each service type.
 */
public class NotificationServiceBuilder implements FcmServiceBuilder, ApnsServiceBuilder {
	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServiceBuilder.class);

	private ExternalService service;
	private ServiceConfiguration config;

	public NotificationServiceBuilder(ExternalService service) throws ServiceInstantiationException {
		this.service = service;
		this.config = getConfigInstance(service);
	}

	private ServiceConfiguration getConfigInstance(ExternalService service) throws ServiceInstantiationException {
		switch (service) {
		case FCM:
			return new FcmConfiguration();
		case APNS:
			return new ApnsConfiguration();
		default:
			throw new ServiceInstantiationException("Unsupported service type");
		}

	}

	public NotificationServiceBuilder withConfig(ApnsConfiguration config) throws BadConfigException {
		if (this.service == ExternalService.APNS) {
			this.config = config;
			return this;

		}

		LOGGER.error("Failed to instantiate notification service due to bad configrations {}", config);
		throw new BadConfigException("Unexpected service configurations found");
	}

	public NotificationServiceBuilder withConfig(FcmConfiguration config) throws BadConfigException {
		if (this.service == ExternalService.APNS) {
			this.config = config;
			return this;
		}

		LOGGER.error("Failed to instantiate notification service due to bad configrations {}", config);
		throw new BadConfigException("Unexpected service configurations found");
	}

	@Override
	public FcmServiceBuilder withServerKey(String serverKey) {
		FcmConfiguration fcmConfig = (FcmConfiguration) config;
		fcmConfig.setServerKey(serverKey);

		return this;
	}

	@Override
	public FcmServiceBuilder withSenderId(String senderId) {
		FcmConfiguration fcmConfig = (FcmConfiguration) config;
		fcmConfig.setSenderId(senderId);

		return this;
	}

	@Override
	public FcmService buildFCMService() throws ServiceInstantiationException {
		if (this.config != null && this.service == ExternalService.FCM) {
			return new FcmService((FcmConfiguration) this.config);
		}

		throw new ServiceInstantiationException("Unexpected service configurations found");
	}

	@Override
	public ApnsServiceBuilder withCertFilePath(String certFilePath) {
		ApnsConfiguration apnsConfig = (ApnsConfiguration) config;
		apnsConfig.setCertFilePath(certFilePath);

		return this;
	}

	@Override
	public ApnsServiceBuilder withPassPhrase(String passPhrase) {
		ApnsConfiguration apnsConfig = (ApnsConfiguration) config;
		apnsConfig.setPassPhrase(passPhrase);

		return this;
	}

	@Override
	public ApnsServiceBuilder withEnvironment(Environment env) {
		ApnsConfiguration apnsConfig = (ApnsConfiguration) config;
		apnsConfig.setEnv(env);

		return this;
	}

	@Override
	public ApnsService buildApnsService() throws ServiceInstantiationException {
		if (this.config != null && this.service == ExternalService.APNS) {
			return new ApnsService((ApnsConfiguration) this.config);

		}

		throw new ServiceInstantiationException("Unexpected service configurations found");
	}

}
