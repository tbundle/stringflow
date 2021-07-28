package abs.ixi.notification;

import abs.ixi.notification.connector.ApnsConnector;
import abs.ixi.notification.payload.ApnsMessage;
import abs.ixi.notification.payload.ApnsResponse;
import abs.ixi.notification.payload.ApnsMessage.ApnsMessageBuilder;

public class ApnsService
	extends AbstractNotificationService<ApnsMessage, ApnsResponse, ApnsConfiguration, ApnsConnector> {

    public ApnsService(ApnsConfiguration config) {
	super(config, new ApnsConnector(config));
    }

    @Override
    public ApnsMessageBuilder getMessageBuilder() {
	return new ApnsMessage.ApnsMessageBuilder();
    }

}
