package abs.ixi.notification.connector;

import abs.ixi.notification.payload.NotificationMessage;

/**
 * {@link HttpConnector} uses HTTP protocol to connect to external cloud
 * services.
 *
 * @param <T>
 */
public interface HttpConnector<T extends NotificationMessage, R extends Response> extends Connector<T, R> {

}
