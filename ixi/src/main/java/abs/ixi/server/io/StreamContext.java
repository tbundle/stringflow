package abs.ixi.server.io;

import abs.ixi.server.packet.JID;
import abs.ixi.util.StringUtils;

/**
 * Represents stream context. Encapsulates details such as stream state,
 * negotiated features, security, id, language, charset and version
 */
public class StreamContext {
	public static final String DEFAULT_STREAM_LANG = "en";
	public static final String DEFAULT_STREAM_VERSION = "1.0";
	public static final String DEFAULT_STREAM_ENCODING = "UTF-8";

	private String streamId;

	private JID from;

	private StreamState state;

	private boolean isSecured;
	private String saslMechanism;

	private String encoding;
	private String version;
	private String lang;

	private int authRetryCount = 0;

	public StreamContext(String streamId) {
		this(streamId, null, StreamState.INITIATED, DEFAULT_STREAM_ENCODING, DEFAULT_STREAM_VERSION,
				DEFAULT_STREAM_LANG);
	}

	public StreamContext(String id, JID from, StreamState state, String encoding, String version, String lang) {
		this.streamId = id;
		this.from = from;
		this.state = state;
		this.encoding = encoding;
		this.version = version;
		this.lang = lang;
	}

	public String getStreamId() {
		return streamId;
	}

	public void setStreamId(String streamId) {
		this.streamId = streamId;
	}

	public StreamState getState() {
		return state;
	}

	public void setState(StreamState state) {
		this.state = state;
	}

	public boolean isSecured() {
		return isSecured;
	}

	public void setSecured(boolean isSecured) {
		this.isSecured = isSecured;
	}

	public String getSaslMechanism() {
		return saslMechanism;
	}

	public void setSaslMechanism(String saslMechanism) {
		this.saslMechanism = saslMechanism;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public JID getFrom() {
		return from;
	}

	public void setFrom(JID from) {
		this.from = from;
	}

	public boolean isResourceBound() {
		return !StringUtils.isNullOrEmpty(this.from.getResource());
	}

	public void setUserResourceId(String resourceId) {
		this.from.setResource(resourceId);
	}

	public String getUserResourceId() {
		return this.from.getResource();
	}

	public void saslDone(boolean result, JID userJID) {
		if (result) {
			this.setFrom(userJID);
			this.setState(StreamState.SASL_DONE);

		} else {
			this.setState(StreamState.SASL_FAILED);

			authRetryCount++;

			if (authRetryCount >= 3) {
				this.setState(StreamState.UNKNOWN);
			}
		}
	}

	// public boolean isStanzaAckReceived(long writtenStanzaCount) {
	// return writtenStanzaCount <= this.getAckReceivedCount();
	// }

	public enum StreamState {
		INITIATED,

		TLS_ADVERTISED,

		TLS_STARTED,

		TLS_DONE,

		SASL_ADVERTISED,

		SASL_STARTED,

		SASL_DONE,

		SASL_FAILED,

		RESOURCE_BIND_AND_SM_ADVERTISED,

		STREAM_RESUME_FAILED,

		OPEN,

		CLOSED,

		UNKNOWN;
	}

}
