package abs.ixi.notification.connector;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Sender;

import abs.ixi.httpclient.HttpContent;
import abs.ixi.httpclient.HttpMethod;
import abs.ixi.httpclient.HttpRequest;
import abs.ixi.httpclient.HttpResponse;
import abs.ixi.httpclient.HttpTransport;
import abs.ixi.httpclient.HttpUrl;
import abs.ixi.httpclient.HttpUrlBuilder;
import abs.ixi.notification.FcmConfiguration;
import abs.ixi.notification.payload.FcmMessage;
import abs.ixi.notification.payload.PushNotification;

public class FcmConnector extends AbstractHttpConnector<FcmMessage, FcmResponse, FcmConfiguration> {
	private static final Logger LOGGER = LoggerFactory.getLogger(FcmConnector.class);

	// FCM service endpoint details
	private static final String PROTOCOL = "https";
	private static final String HOST = "fcm.googleapis.com";
	private static final String PATH_PARAM1 = "fcm";
	private static final String PATH_PARAM2 = "send";
	private static final String KEY = "key";
	private static final String EQUALS = "=";

	// FCM request headers
	private static final String CONTENT_TYPE = "application/json";
	private static final String AUTHORIZATION_KEY = "Authorization";

	public FcmConnector(FcmConfiguration config) {
		super(config, new HttpTransport());
	}

	@Override
	public ResponseWrapper<FcmResponse> send(PushNotification<FcmMessage> notification) throws Exception {
		LOGGER.debug("Sending fcm notification >>>>>>>>>>>> for tokens {}", notification.getTargets());
		// sendGcm(notification);
		//
		// FcmResponse response = new FcmResponse();
		// response.setResponseCode(200);
		// response.setSuccess(1);
		// LOGGER.debug("Fcm notification sent >>>>>>> ");
		// return new ResponseWrapper<FcmResponse>(response);

		if (!isValid(notification)) {
			return null;
		}

		String message = notification.getPayload().stringify();
		String payload = preparePayload(message, notification.getTargets());

		LOGGER.debug("Sending payload {} to targets {}", payload, notification.getTargets());
		FcmResponse response = sendFcmRequest(payload, notification.getTargets());

		return new ResponseWrapper<FcmResponse>(response);
	}

	public void sendGcm(PushNotification<FcmMessage> notification) throws IOException {
		Message payload = preparePayload(notification);
		LOGGER.debug("SENDING GCM NOTIFICATION for payload {} and targets {}", payload, notification.getTargets());
		MulticastResult result = new Sender(config.getServerKey().toString()).send(payload, notification.getTargets(),
				1);

		if (result.getResults() != null) {
			int canonicalRegId = result.getCanonicalIds();

			LOGGER.info("Sucess count : {} and failed message count {}", result.getSuccess(), result.getFailure());

			if (canonicalRegId != 0) {

			}

		} else {
			int error = result.getFailure();
			LOGGER.error("Failed to Multicast messages " + error);
		}

	}

	private Message preparePayload(PushNotification<FcmMessage> notification) {

		return new Message.Builder().setData(notification.getPayload().getData().getDataMap()).build();
	}

	private FcmResponse sendFcmRequest(String payload, List<String> targets) {
		try {

			HttpUrl url = new HttpUrlBuilder().withProtocol(PROTOCOL).withHostname(HOST).withPathParameter(PATH_PARAM1)
					.withPathParameter(PATH_PARAM2).build();

			HttpContent content = new HttpContent(payload);

			HttpRequest httpRequest = new HttpTransport().requestBuilder().withMethod(HttpMethod.POST).withUrl(url)
					.withContent(content).withContentType(CONTENT_TYPE)
					.withHeader(AUTHORIZATION_KEY,
							new StringBuilder().append(KEY).append(EQUALS).append(config.getServerKey()).toString())
					.build();

			HttpResponse httpResponse = transport.sendRequest(httpRequest);
			String responseContent = httpResponse.getContent();

			LOGGER.debug("FCM HTTP Response code :>>>>- {}", httpResponse.getResponseCode());
			LOGGER.debug("FCM response content : >>>> - {}", responseContent);

			LOGGER.debug("SCM response headers : >>>>> ");
			for (Entry<String, String> entry : httpResponse.getHeaders().entrySet()) {
				LOGGER.debug("header key : {},  Value : {}", entry.getKey(), entry.getValue());
			}

			if (httpResponse.getResponseCode() == HttpURLConnection.HTTP_OK) {
				FcmResponse fcmResponse = parseToFcmResponse(responseContent);
				fcmResponse.setResponseCode(HttpURLConnection.HTTP_OK);
				return fcmResponse;

			} else {

				FcmResponse fcmResponse = new FcmResponse();
				fcmResponse.setResponseCode(httpResponse.getResponseCode());
				return fcmResponse;
			}

		} catch (MalformedURLException e) {
			LOGGER.error("Http url for FCM is malformed. Unable to send request", e);
		} catch (ParseException e) {
			LOGGER.error("Failed to parse FCM Response", e);
		} catch (IOException e) {
			LOGGER.error("Failed to send request to FCM server", e);
		}

		return new FcmResponse();
	}

	private FcmResponse parseToFcmResponse(String responseData) throws ParseException {
		try {
			ObjectMapper mapper = new ObjectMapper();
			FcmResponse fcmResponse = mapper.readValue(responseData, FcmResponse.class);

			return fcmResponse;

		} catch (Exception e) {
			LOGGER.error("Failed to parse Fcm response data {} ", responseData, e);
			throw new ParseException();
		}

	}

	@Override
	public List<ResponseWrapper<FcmResponse>> send(List<PushNotification<FcmMessage>> notifications) throws Exception {

		List<ResponseWrapper<FcmResponse>> results = new ArrayList<>();
		ResponseWrapper<FcmResponse> responseWrapper = null;
		for (PushNotification<FcmMessage> notification : notifications)

			responseWrapper = this.send(notification);
		if (responseWrapper != null) {

		}
		results.add(responseWrapper);

		return results;
	}

	private String preparePayload(String message, List<String> targets) {
		StringBuilder sb = new StringBuilder("{");

		if (targets.size() == 1) {
			sb.append("\"");
			sb.append("to");
			sb.append("\"");
			sb.append(" : ");
			sb.append("\"");
			sb.append(targets.get(0));
			sb.append("\"");
			sb.append(",");

		} else {

			sb.append("\"");
			sb.append("registration_ids");
			sb.append("\"");
			sb.append(" :[");

			for (String target : targets) {
				sb.append("\"");
				sb.append(target);
				sb.append("\"");
				sb.append(",");
			}

			sb = new StringBuilder(sb.substring(0, sb.lastIndexOf(",")));

			sb.append("]");
			sb.append(",");
		}

		sb.append(message).append("}");

		return sb.toString();
	}

}
