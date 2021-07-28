package abs.ixi.notification.connector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.notnoop.apns.ApnsService;
import com.notnoop.apns.PayloadBuilder;
import com.notnoop.exceptions.InvalidSSLConfig;

import abs.ixi.httpclient.HttpTransport;
import abs.ixi.notification.ApnsConfiguration;
import abs.ixi.notification.payload.ApnsMessage;
import abs.ixi.notification.payload.ApnsResponse;
import abs.ixi.notification.payload.PushNotification;

public class ApnsConnector extends AbstractHttpConnector<ApnsMessage, ApnsResponse, ApnsConfiguration> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ApnsConnector.class);

	// private static final String PROTOCOL = "https";
	// private static final String HOST = "gateway.sandbox.push.apple.com";
	// private static final String PATH_PARAM1 = "apns";
	// private static final String PATH_PARAM2 = "send";
	// private static final String CONTENT_TYPE = "application/json";
	// private static final String APNS_CERT_EXTENSION = ".p12";

	public ApnsConnector(ApnsConfiguration config) {
		super(config, new HttpTransport());
	}

	@Override
	public ResponseWrapper<ApnsResponse> send(PushNotification<ApnsMessage> notification) throws Exception {
		if (!isValid(notification)) {
			return null;
		}

		LOGGER.info("Preparing to notify targets {} message {}", notification.getTargets());

		try {
			String payload = prepareNotificationPayload(notification);

			ApnsService apnsService = setupService();
			apnsService.push(notification.getTargets(), payload);

			Map<String, Date> inactiveDevices = apnsService.getInactiveDevices();
			for (String deviceToken : inactiveDevices.keySet()) {
				Date inactiveAsOf = inactiveDevices.get(deviceToken);
				LOGGER.info("Apns inactive device token :{}, date {}", deviceToken, inactiveAsOf);
			}

			LOGGER.debug("notifications send");
		} catch (IOException e) {
			LOGGER.error("Failed to send notifications. Network connectivity could not be established", e);
		}

		return null;
	}

	@Override
	public List<ResponseWrapper<ApnsResponse>> send(List<PushNotification<ApnsMessage>> notifications)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	private String prepareNotificationPayload(PushNotification<ApnsMessage> n) {
		Map<String, String> data = n.getPayload().getCustomFields();
		data.put("message", n.getPayload().getMessage());

		PayloadBuilder builder = com.notnoop.apns.APNS.newPayload().customFields(data);

		if (!n.getPayload().isSilent()) {
			builder.alertBody(n.getPayload().getMessage());
			builder.sound("default");
			builder.alertTitle(n.getPayload().getAlertTitle());
		} else {
			builder.instantDeliveryOrSilentNotification();
		}

		return builder.build();
	}

	private ApnsService setupService() throws InvalidSSLConfig, FileNotFoundException {
		return com.notnoop.apns.APNS.newService()
				.withCert(readCertificate(config.getCertFilePath()), config.getPassPhrase())
				.withAppleDestination(config.isProd()).build();
	}

	private InputStream readCertificate(String certFilePath) throws FileNotFoundException {
		LOGGER.info("Using certificate {}", certFilePath);

		return new FileInputStream(new File(certFilePath));
	}

	// @Override
	// public ResponseWrapper<ApnsResponse> send(PushNotification<ApnsMessage>
	// notification) throws Exception {
	// if (!isValid(notification)) {
	// return null;
	// }
	//
	// String payload = notification.getPayload().stringify();
	// LOGGER.debug("Preparing to send {} to targets {}", payload,
	// notification.getTargets());
	//
	// ApnsResponse response = sendApnsRequest(payload,
	// notification.getTargets());
	// return new ResponseWrapper<ApnsResponse>(response);
	// }
	//
	// @Override
	// public List<ResponseWrapper<ApnsResponse>>
	// send(List<PushNotification<ApnsMessage>> notifications)
	// throws Exception {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// private ApnsResponse sendApnsRequest(String payload, List<String>
	// targets) {
	// try {
	// String requestBody = getApnsRequestBody(payload, targets);
	//
	// HttpUrl url = new
	// HttpUrlBuilder().withProtocol(PROTOCOL).withHostname(HOST).build();
	//
	// HttpContent content = new HttpContent(requestBody);
	//
	// // TODO : Set HTTP headers for APNS
	// HttpRequest httpRequest =
	// transport.requestBuilder().withMethod(HttpMethod.POST).withUrl(url)
	// .withContent(content).withContentType(CONTENT_TYPE).build();
	//
	// HttpResponse httpResponse = transport.sendRequest(httpRequest);
	//
	// if (httpResponse.getResponseCode() == HttpURLConnection.HTTP_OK) {
	// String responseData = httpResponse.getContent();
	// ApnsResponse apnsResponse = parseToApnsResponse(responseData);
	// apnsResponse.setResponseCode(httpResponse.getResponseCode());
	//
	// return apnsResponse;
	// }
	//
	// } catch (MalformedURLException e) {
	// LOGGER.error("Http url for APNS is malformed. Unable to send request");
	// } catch (ParseException e) {
	// LOGGER.error("Failed to parse APNS Response");
	// } catch (IOException e) {
	// LOGGER.error("Failed to send request to APNS server");
	// }
	//
	// return new ApnsResponse();
	// }
	//
	// private String getApnsRequestBody(String payload, List<String> targets) {
	// return null;
	// }
	//
	// private ApnsResponse parseToApnsResponse(String responseData) throws
	// ParseException {
	// return null;
	// }
	//
	// /**
	// * Sends notification to APNS using external library. The method will be
	// * removed once we develop our own code.
	// */
	// private void sendUsingLibrary(PushNotification<ApnsMessage> notification)
	// {
	// String payload = notification.getPayload().stringify();
	// LOGGER.info("Preparing to notify targets {} message {}",
	// notification.getTargets(), payload);
	//
	// try {
	// setupService().push(notification.getTargets(),
	// prepareNotificationPayload(notification));
	// } catch (Exception e) {
	// LOGGER.error("Failed to send notifications. Network connectivity could
	// not be established", e);
	// }
	// }
	//
	// private ApnsService setupService() {
	// return
	// com.notnoop.apns.APNS.newService().withCert(this.config.getCertFilePath(),
	// this.config.getPassPhrase())
	// .withAppleDestination(this.config.isProd()).build();
	// }
	//
	// private String prepareNotificationPayload(PushNotification<ApnsMessage>
	// n) {
	// Map<String, String> data = n.getPayload().getCustomFields();
	// data.put("message", n.getPayload().getMsg());
	//
	// PayloadBuilder builder =
	// com.notnoop.apns.APNS.newPayload().customFields(data);
	//
	// builder.instantDeliveryOrSilentNotification();
	// builder.sound("");
	// builder.alertBody(n.getPayload().getMsg());
	// builder.sound("default");
	//
	// return builder.build();
	// }

}
