package abs.ixi.notification.connector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.httpclient.HttpTransport;
import abs.ixi.notification.ServiceConfiguration;
import abs.ixi.notification.payload.NotificationMessage;
import abs.ixi.notification.payload.PushNotification;
import abs.ixi.util.CollectionUtils;

public abstract class AbstractHttpConnector<T extends NotificationMessage, R extends Response, C extends ServiceConfiguration>
		implements HttpConnector<T, R> {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractHttpConnector.class);

	protected C config;
	protected HttpTransport transport;

	public AbstractHttpConnector(C config, HttpTransport transport) {
		this.config = config;
		this.transport = transport;
	}

	/**
	 * Validates push notification if it is OK to process it. If there are no
	 * targets or the message is empty, the method will return false indicating
	 * that the notification is not OK to process further.
	 */
	protected boolean isValid(PushNotification<T> notification) {
		if (CollectionUtils.isNullOrEmpty(notification.getTargets())) {
			LOGGER.info("No targets found. Skipping...");
			return false;
		}

		if (notification.getPayload() == null) {
			LOGGER.info("No message found. Skipping...");
			return false;
		}

		return true;
	}

}
