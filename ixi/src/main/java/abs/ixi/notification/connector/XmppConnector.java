package abs.ixi.notification.connector;

import abs.ixi.notification.NotificationServiceFactory;
import abs.ixi.notification.payload.NotificationMessage;

/**
 * Root interface for all the Xmpp Connectors maintained by
 * {@link NotificationServiceFactory}
 * 
 * @param <R>
 *            return type of the response
 * @param <CONFIG>
 *            connector configurations
 */
public interface XmppConnector<T extends NotificationMessage, R extends Response> extends Connector<T, R> {

}
