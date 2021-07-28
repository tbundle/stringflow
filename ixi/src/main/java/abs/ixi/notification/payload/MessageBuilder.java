package abs.ixi.notification.payload;

public interface MessageBuilder<T extends NotificationMessage> {
    public PushNotification<T> build();
}
