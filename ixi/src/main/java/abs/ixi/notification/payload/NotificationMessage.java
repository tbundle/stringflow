package abs.ixi.notification.payload;

import abs.ixi.notification.NotificationService;

/**
 * Root interface for supported message types supported by various
 * {@link NotificationService}s
 */
public interface NotificationMessage {
    /**
     * Returns plain text payload for this {@link NotificationMessage}
     */
    public String stringify();

    /**
     * Returns XMPP payload format this {@link NotificationMessage}
     */
    public String xmppPayload();

}
