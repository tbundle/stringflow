package abs.ixi.notification;

import java.util.List;

import abs.ixi.notification.connector.Response;
import abs.ixi.notification.connector.ResponseWrapper;
import abs.ixi.notification.payload.FcmMessage;
import abs.ixi.notification.payload.MessageBuilder;
import abs.ixi.notification.payload.NotificationMessage;
import abs.ixi.notification.payload.PushNotification;

/**
 * Common interface across {@link NotificationService}s
 */
public interface NotificationService<T extends NotificationMessage, R extends Response> {
    /**
     * sends the push notification to device. The method does not offer compile
     * time type safety; the supplied notification must hold the appropriate
     * {@link NotificationMessage} for example FCM service can send only
     * {@link FcmMessage} payload
     */
    public ResponseWrapper<R> send(PushNotification<T> notification) throws Exception;

    /**
     * sends all the notifications in the list. The method does not offer
     * compile time type safety; the supplied notification must hold the
     * appropriate {@link NotificationMessage} for example FCM service can send
     * only {@link FcmMessage} payload
     */
    public List<ResponseWrapper<R>> send(List<PushNotification<T>> notifications) throws Exception;

    public MessageBuilder<T> getMessageBuilder();
}
