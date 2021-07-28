package abs.ixi.notification.connector;

import java.util.List;

import abs.ixi.notification.NotificationService;
import abs.ixi.notification.payload.NotificationMessage;
import abs.ixi.notification.payload.PushNotification;

/**
 * Root interface for connector definitions used by {@link NotificationService}
 */
public interface Connector<T extends NotificationMessage, R extends Response> {
    /**
     * sends request to the destination and returns {@link ResponseWrapper}
     * 
     * @param notification
     * @return
     * @throws Exception
     */
    public ResponseWrapper<R> send(PushNotification<T> notification) throws Exception;

    /**
     * Send a list of notifications
     * 
     * @param notifications
     * @return
     * @throws Exception
     */
    public List<ResponseWrapper<R>> send(List<PushNotification<T>> notifications) throws Exception;

}
