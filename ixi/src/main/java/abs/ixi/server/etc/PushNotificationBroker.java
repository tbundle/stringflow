package abs.ixi.server.etc;

import static abs.ixi.server.etc.conf.Configurations.Bundle.SYSTEM;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.notification.ApnsConfiguration.Environment;
import abs.ixi.notification.ApnsService;
import abs.ixi.notification.FcmService;
import abs.ixi.notification.NotificationServiceFactory;
import abs.ixi.notification.ServiceInstantiationException;
import abs.ixi.notification.connector.FcmResponse;
import abs.ixi.notification.connector.ResponseWrapper;
import abs.ixi.notification.payload.ApnsMessage.ApnsMessageBuilder;
import abs.ixi.notification.payload.ApnsResponse;
import abs.ixi.notification.payload.FcmMessage.FcmMessageBuilder;
import abs.ixi.server.ConsumerComponent;
import abs.ixi.server.ExecutionException;
import abs.ixi.server.PacketEnvelope;
import abs.ixi.server.ServerComponent;
import abs.ixi.server.Stringflow;
import abs.ixi.server.common.InstantiationException;
import abs.ixi.server.common.Triplet;
import abs.ixi.server.couplet.Couplet;
import abs.ixi.server.couplet.ExternalNotification;
import abs.ixi.server.etc.conf.Configurations;
import abs.ixi.server.etc.conf.Configurations.Bundle;
import abs.ixi.server.etc.conf.SystemConfigAware;
import abs.ixi.server.muc.MultiUserChatHandler;
import abs.ixi.server.packet.Packet;
import abs.ixi.server.packet.xmpp.BareJID;
import abs.ixi.server.packet.xmpp.IQPushRegistration.DeviceType;
import abs.ixi.server.packet.xmpp.IQPushRegistration.PushNotificationService;
import abs.ixi.server.packet.xmpp.Message;
import abs.ixi.server.packet.xmpp.Message.MessageType;
import abs.ixi.server.packet.xmpp.MessageBody;
import abs.ixi.server.packet.xmpp.MessageContent;
import abs.ixi.server.packet.xmpp.MessageContent.MessageContentType;
import abs.ixi.server.packet.xmpp.MessageDelay;
import abs.ixi.server.packet.xmpp.MessageMedia;
import abs.ixi.server.packet.xmpp.MessageThread;
import abs.ixi.util.CollectionUtils;
import abs.ixi.util.StringUtils;

/**
 * {@link ServerComponent} which processes requests to send push notifications
 * to user. The component has no logic/understanding if when/who/why to send a
 * notification. Each of the
 * 
 * @author Yogi
 *
 */
public class PushNotificationBroker extends ConsumerComponent implements SystemConfigAware {
	private static final Logger LOGGER = LoggerFactory.getLogger(PushNotificationBroker.class);

	private static final String COMPONENT_NAME = "notification-broker";

	private static final String CONVERSATION_ID = "conversation_id";
	private static final String MESSAGE_ID = "message_id";
	private static final String FROM_JID = "from_jid";
	private static final String MESSAGE = "message";
	private static final String MEDIA_ID = "media_id";
	private static final String MEDIA_THUMB = "media_thumb";
	private static final String CONTENT_TYPE = "content_type";
	private static final String IS_MARKABLE_MESASGE = "is_markable_message";
	private static final String IS_MDR_REQUESTED = "is_mdr_requested";
	private static final String TIMESTAMP = "timeStamp";
	private static final String TRUE = "true";

	// TODO: This can not have mucController here. We need another way to
	// achieve this.
	/**
	 * Muc handle is only needed for building notification title of for IOS
	 * notifications.
	 */
	private MultiUserChatHandler mucHandler;

	public PushNotificationBroker(Configurations config) throws InstantiationException {
		super(COMPONENT_NAME, config);
	}

	@Override
	public void process(PacketEnvelope<? extends Packet> envelope) {
		try {
			if (envelope.getPacket() instanceof Message) {
				Message message = (Message) envelope.getPacket();

				if (message.isNotifyableMessage()) {
					LOGGER.debug("Sending push notification to user {} and messageId : {}", message.getTo(),
							message.getId());

					List<Triplet<String, String, String>> deviceTokens = this.dbService
							.getDeviceTokens(message.getTo().getBareJID());

					if (CollectionUtils.isNullOrEmpty(deviceTokens)) {
						LOGGER.info("No device token found for user {}", message.getTo());
						return;
					}

					LOGGER.debug("sending push notification to user {} and device token {} , message {}",
							message.getTo().getBareJID(), deviceTokens, message);

					boolean isNotificationSent = sendPushNotification(message, deviceTokens);

					if (!isNotificationSent) {
						LOGGER.debug("Failed to send push notification to user JID {} for message {}",
								message.getTo().getBareJID(), message.xml());
					}
				}
			}

		} catch (Exception e) {
			LOGGER.error("error while sending push notification", e);
		}
	}

	private boolean sendPushNotification(Message message, List<Triplet<String, String, String>> deviceTokens) {
		if (!CollectionUtils.isNullOrEmpty(deviceTokens)) {
			List<String> apnsTokens = new ArrayList<>();
			List<String> fcmAndroidTokens = new ArrayList<>();
			List<String> fcmIOsTokens = new ArrayList<>();
			List<String> gcmTokens = new ArrayList<>();

			for (Triplet<String, String, String> deviceToken : deviceTokens) {
				if (StringUtils.safeEquals(PushNotificationService.APNS.name(), deviceToken.getFirst(), false)) {
					apnsTokens.add(deviceToken.getSecond());

				} else if (StringUtils.safeEquals(PushNotificationService.FCM.name(), deviceToken.getFirst(), false)) {
					if (StringUtils.safeEquals(DeviceType.ANDROID.name(), deviceToken.getThird(), false)) {
						fcmAndroidTokens.add(deviceToken.getSecond());

					} else if (StringUtils.safeEquals(DeviceType.IOS.name(), deviceToken.getThird(), false)) {
						fcmIOsTokens.add(deviceToken.getSecond());
					}

				} else if (StringUtils.safeEquals(PushNotificationService.GCM.name(), deviceToken.getFirst(), false)) {
					gcmTokens.add(deviceToken.getSecond());
				}
			}

			boolean apnsSent = false;
			if (apnsTokens.size() > 0) {
				apnsSent = sendAPNSNotification(message, apnsTokens);
			}

			boolean fcmAndroidSent = false;
			if (fcmAndroidTokens.size() > 0) {
				fcmAndroidSent = sendFcmAndroidNotification(message, fcmAndroidTokens);
			}

			boolean fcmIOSSent = false;
			if (fcmIOsTokens.size() > 0) {
				fcmIOSSent = sendFcmIOsNotification(message, fcmIOsTokens);
			}

			return apnsSent || fcmAndroidSent || fcmIOSSent;
		}

		return false;
	}

	private boolean sendFcmAndroidNotification(Message message, List<String> fcmTokens) {
		LOGGER.debug("Sending FCM Android notifications for message {} and fcm tokens {}", message, fcmTokens);
		try {
			String serverKey = Stringflow.runtime().configurations().get(FCM_SERVER_KEY, Bundle.SYSTEM);
			FcmService fcmService = NotificationServiceFactory.newFCMService().withServerKey(serverKey)
					.buildFCMService();

			FcmMessageBuilder notificationBuilder = fcmService.getMessageBuilder().withData(MESSAGE_ID, message.getId())
					.withData(FROM_JID, message.getFrom().getFullJID()).withTarget(fcmTokens);

			if (message.getContents() != null) {

				for (MessageContent content : message.getContents()) {
					if (content.isContentType(MessageContentType.BODY)) {
						MessageBody body = (MessageBody) content;

						notificationBuilder.withData(MESSAGE, body.getContent()).withData(
								NotifiactionCode.SF_NOTIFICATION_CODE,
								Integer.toString(NotifiactionCode.TEXT_MESSAGE.val()));

					} else if (content.isContentType(MessageContentType.MEDIA)) {
						MessageMedia media = (MessageMedia) content;

						notificationBuilder.withData(MEDIA_ID, media.getMediaId())
								.withData(NotifiactionCode.SF_NOTIFICATION_CODE,
										Integer.toString(NotifiactionCode.MEDIA_MESSAGE.val()))
								.withData(MEDIA_THUMB, media.getThumb())
								.withData(CONTENT_TYPE, media.getContentType().getMimeType());

					} else if (content.isContentType(MessageContentType.CM_MARKABLE)) {
						notificationBuilder.withData(IS_MARKABLE_MESASGE, TRUE);

					} else if (content.isContentType(MessageContentType.MDR_REQUEST)) {
						notificationBuilder.withData(IS_MDR_REQUESTED, TRUE);

					} else if (content.isContentType(MessageContentType.DELAY)) {
						MessageDelay delay = (MessageDelay) content;
						notificationBuilder.withData(TIMESTAMP, delay.getStamp());

					} else if (content.isContentType(MessageContentType.THREAD)) {
						MessageThread thread = (MessageThread) content;
						notificationBuilder.withData(CONVERSATION_ID, thread.getThreadId());
					}
				}
			}

			FcmResponse response = fcmService.send(notificationBuilder.build()).getResponse();

			if (response.getResponseCode() == 200 && response.getSuccess() > 0) {
				return true;
			}

		} catch (ServiceInstantiationException e) {
			LOGGER.error("Failed to instantiate notification service", e);

		} catch (Exception e) {
			LOGGER.error("Failed to send fcm notification", e);
		}

		return false;
	}

	private boolean sendFcmIOsNotification(Message message, List<String> fcmTokens) {
		LOGGER.debug("Sending FCM IOS notifications for message {} and fcm tokens {}", message, fcmTokens);

		try {
			String serverKey = Stringflow.runtime().configurations().get(FCM_SERVER_KEY, SYSTEM);
			FcmService fcmService = NotificationServiceFactory.newFCMService().withServerKey(serverKey)
					.buildFCMService();

			FcmMessageBuilder notificationBuilder = fcmService.getMessageBuilder().withData(MESSAGE_ID, message.getId())
					.withData(FROM_JID, message.getFrom().getFullJID()).withTarget(fcmTokens);

			notificationBuilder.withNotificationTitle(generateNotificationTitle(message));

			if (message.getContents() != null) {
				for (MessageContent content : message.getContents()) {
					if (content.isContentType(MessageContentType.BODY)) {
						MessageBody body = (MessageBody) content;

						notificationBuilder.withData(MESSAGE, body.getContent()).withData(
								NotifiactionCode.SF_NOTIFICATION_CODE,
								Integer.toString(NotifiactionCode.TEXT_MESSAGE.val()));

					} else if (content.isContentType(MessageContentType.MEDIA)) {
						MessageMedia media = (MessageMedia) content;

						notificationBuilder.withData(MEDIA_ID, media.getMediaId())
								.withData(NotifiactionCode.SF_NOTIFICATION_CODE,
										Integer.toString(NotifiactionCode.MEDIA_MESSAGE.val()))
								.withData(MEDIA_THUMB, media.getThumb())
								.withData(CONTENT_TYPE, media.getContentType().getMimeType());

					} else if (content.isContentType(MessageContentType.CM_MARKABLE)) {
						notificationBuilder.withData(IS_MARKABLE_MESASGE, TRUE);

					} else if (content.isContentType(MessageContentType.MDR_REQUEST)) {
						notificationBuilder.withData(IS_MDR_REQUESTED, TRUE);

					} else if (content.isContentType(MessageContentType.DELAY)) {
						MessageDelay delay = (MessageDelay) content;
						notificationBuilder.withData(TIMESTAMP, delay.getStamp());

					} else if (content.isContentType(MessageContentType.THREAD)) {
						MessageThread thread = (MessageThread) content;
						notificationBuilder.withData(CONVERSATION_ID, thread.getThreadId());
					}
				}
			}

			FcmResponse response = fcmService.send(notificationBuilder.build()).getResponse();

			if (response.getResponseCode() == 200 && response.getSuccess() > 0) {
				return true;
			}

		} catch (ServiceInstantiationException e) {
			LOGGER.error("Failed to instantiate notification service", e);

		} catch (Exception e) {
			LOGGER.error("Failed to send fcm notification", e);
		}

		return false;
	}

	// TODO: Try to remove use of this method. this is very costly method for
	// server. client should handle title of notification like FCM.
	private String generateNotificationTitle(Message message) {

		if (message.getType() == MessageType.GROUP_CHAT) {
			String roomSubject = mucHandler.getChatRoomSubject(message.getFrom().getBareJID());

			if (StringUtils.isNullOrEmpty(roomSubject)) {
				roomSubject = message.getFrom().getNode();
			}

			BareJID senderJID = mucHandler.getChatRoomMemberJID(message.getFrom().getBareJID(),
					message.getFrom().getResource());

			String senderName = dbService.getUserRosterItemName(message.getTo().getBareJID(), senderJID);

			if (StringUtils.isNullOrEmpty(senderName)) {
				senderName = dbService.getUserName(senderJID);

				if (StringUtils.isNullOrEmpty(senderName)) {
					senderName = message.getFrom().getResource();
				}
			}

			return roomSubject + "(" + senderName + ")";

		} else {
			String contactName = dbService.getUserRosterItemName(message.getTo().getBareJID(),
					message.getFrom().getBareJID());

			if (StringUtils.isNullOrEmpty(contactName)) {
				contactName = dbService.getUserName(message.getFrom().getBareJID());

				if (StringUtils.isNullOrEmpty(contactName)) {
					contactName = message.getFrom().getNode();
				}
			}

			return contactName;
		}
	}

	private boolean sendAPNSNotification(Message message, List<String> apnsTokens) {
		try {
			ApnsService apnsService = NotificationServiceFactory.newAPNService().withEnvironment(getAPNSEnv())
					.withCertFilePath(Stringflow.runtime().configurations().get(APNS_CERT_FILE_PATH, SYSTEM))
					.withPassPhrase(Stringflow.runtime().configurations().get(APNS_CERT_PASSPHRASE, SYSTEM))

					.buildApnsService();

			ApnsMessageBuilder notificationBuilder = apnsService.getMessageBuilder()
					.withAlertTitle(generateNotificationTitle(message)).withCustomField(MESSAGE_ID, message.getId())
					.withCustomField(FROM_JID, message.getFrom().getFullJID()).withTarget(apnsTokens).withSilent(true);

			if (message.getContents() != null) {
				for (MessageContent content : message.getContents()) {
					if (content.isContentType(MessageContentType.BODY)) {
						MessageBody body = (MessageBody) content;

						notificationBuilder.withMessage(body.getContent()).withCustomField(
								NotifiactionCode.SF_NOTIFICATION_CODE,
								Integer.toString(NotifiactionCode.TEXT_MESSAGE.val()));

					} else if (content.isContentType(MessageContentType.MEDIA)) {
						MessageMedia media = (MessageMedia) content;

						notificationBuilder.withCustomField(MEDIA_ID, media.getMediaId())
								.withCustomField(NotifiactionCode.SF_NOTIFICATION_CODE,
										Integer.toString(NotifiactionCode.MEDIA_MESSAGE.val()))
								.withCustomField(MEDIA_THUMB, media.getThumb())
								.withCustomField(CONTENT_TYPE, media.getContentType().getMimeType());

					} else if (content.isContentType(MessageContentType.CM_MARKABLE)) {
						notificationBuilder.withCustomField(IS_MARKABLE_MESASGE, TRUE);

					} else if (content.isContentType(MessageContentType.MDR_REQUEST)) {
						notificationBuilder.withCustomField(IS_MDR_REQUESTED, TRUE);

					} else if (content.isContentType(MessageContentType.DELAY)) {
						MessageDelay delay = (MessageDelay) content;
						notificationBuilder.withCustomField(TIMESTAMP, delay.getStamp());

					} else if (content.isContentType(MessageContentType.THREAD)) {
						MessageThread thread = (MessageThread) content;
						notificationBuilder.withCustomField(CONVERSATION_ID, thread.getThreadId());
					}
				}
			}

			ResponseWrapper<ApnsResponse> response = apnsService.send(notificationBuilder.build());
			return true;

		} catch (ServiceInstantiationException e) {
			LOGGER.error("Failed to instantiate notification service", e);

		} catch (Exception e) {
			LOGGER.error("Failed to send apns notification", e);
		}

		return false;
	}

	private Environment getAPNSEnv() {
		String env = Stringflow.runtime().configurations().get(APNS_CERT_ENVIRONMENT, SYSTEM);
		return StringUtils.safeEquals(Environment.DEV.name(), env.trim(), false) ? Environment.DEV : Environment.PROD;
	}

	protected void execute(Couplet couplet) throws ExecutionException {
		try {
			LOGGER.trace("Sending push notification for {}", couplet);

			if (ExternalNotification.class.isAssignableFrom(couplet.getClass())) {
				ExternalNotification notification = (ExternalNotification) couplet;
				List<Triplet<String, String, String>> deviceTokens = this.dbService
						.getDeviceTokens(notification.getDestination());
			}

		} catch (Exception e) {
			LOGGER.info("Failed to process {}", couplet, e);
			throw new ExecutionException(this.getJID().toString(), e);
		}
	}

}
