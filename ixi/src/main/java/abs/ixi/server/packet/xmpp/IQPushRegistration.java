package abs.ixi.server.packet.xmpp;

import abs.ixi.server.app.ApplicationRequest;
import abs.ixi.xml.Element;
import abs.ixi.xml.XMLUtils;

/**
 * This is an extention packet used to send device push notification
 * registration id to server. It may not be required once we integrate
 * {@link ApplicationRequest} into SDK and on server.
 */
public class IQPushRegistration extends AbstractIQContent {
	private static final long serialVersionUID = 1698006026833868281L;

	public static final String XML_ELM_NAME = "push";

	private static final String DEVICE_ID_TAG = "device-id";
	private static final String DEVICE_TOKEN_TAG = "device-token";
	private static final String NOTIFICATION_SERVICE_TAG = "notification-service";
	private static final String DEVICE_TYPE_TAG = "device-type";
	private static final String REMOVE_TAG = "remove";

	private String deviceId;
	private String deviceToken;
	private PushNotificationService service;
	private DeviceType deviceType;
	private boolean removed;

	public IQPushRegistration(String xmlns) {
		super(xmlns, IQContentType.PUSH_REGISTRATION);
	}

	public IQPushRegistration(Element elm) {
		this(elm.getAttribute(XMLUtils.XMLNS_ATTRIBUTE));

		for (Element child : elm.getChildren()) {
			switch (child.getName()) {

			case DEVICE_ID_TAG:
				this.setDeviceId(child.val());
				break;

			case DEVICE_TOKEN_TAG:
				this.setDeviceToken(child.val());
				break;

			case NOTIFICATION_SERVICE_TAG:
				this.setService(PushNotificationService.valueOf(child.val()));
				break;

			case DEVICE_TYPE_TAG:
				this.setDeviceType(DeviceType.valueOf(child.val()));
				break;

			case REMOVE_TAG:
				this.setRemoved(true);
				break;

			default:
				break;

			}

		}
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceToken() {
		return deviceToken;
	}

	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}

	public PushNotificationService getService() {
		return service;
	}

	public void setService(PushNotificationService service) {
		this.service = service;
	}

	public DeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	public boolean isRemoved() {
		return removed;
	}

	public void setRemoved(boolean removed) {
		this.removed = removed;
	}

	/**
	 * String literals to indicate external Push Notification Service. Currently
	 * Stringflow can route notifications through FCM and APNS.
	 */
	public enum PushNotificationService {
		FCM,

		GCM,

		APNS,

		WPNS;

	}

	/**
	 * String literals to indicate Device Type On witch Stringflow is running.
	 */
	public enum DeviceType {
		ANDROID,

		IOS,

		WINDOWS
	}

	@Override
	public String xml() {
		throw new UnsupportedOperationException();
	}

	@Override
	public StringBuilder appendXml(StringBuilder sb) {
		return sb;
	}

}
