package abs.ixi.notification;

import abs.ixi.notification.connector.FcmConnector;
import abs.ixi.notification.connector.FcmResponse;
import abs.ixi.notification.payload.FcmMessage;
import abs.ixi.notification.payload.FcmMessage.FcmMessageBuilder;

public class FcmService extends AbstractNotificationService<FcmMessage, FcmResponse, FcmConfiguration, FcmConnector> {
    public FcmService(FcmConfiguration config) {
	super(config, new FcmConnector(config));
    }

    @Override
    public FcmMessageBuilder getMessageBuilder() {
	return new FcmMessage.FcmMessageBuilder();
    }

}
