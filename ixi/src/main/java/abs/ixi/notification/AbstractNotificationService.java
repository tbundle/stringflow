package abs.ixi.notification;

import java.util.List;

import abs.ixi.notification.connector.Connector;
import abs.ixi.notification.connector.Response;
import abs.ixi.notification.connector.ResponseWrapper;
import abs.ixi.notification.payload.NotificationMessage;
import abs.ixi.notification.payload.PushNotification;

public abstract class AbstractNotificationService<T extends NotificationMessage, R extends Response, CONF extends ServiceConfiguration, CONNECT extends Connector<T, R>>
	implements NotificationService<T, R> {
    protected CONF config;
    protected CONNECT connector;

    public AbstractNotificationService(CONF config, CONNECT connector) {
	this.config = config;
	this.connector = connector;
    }

    @Override
    public ResponseWrapper<R> send(PushNotification<T> notification) throws Exception {
	return connector.send(notification);
    }

    @Override
    public List<ResponseWrapper<R>> send(List<PushNotification<T>> notifications) throws Exception {
	return this.connector.send(notifications);
    }
}
