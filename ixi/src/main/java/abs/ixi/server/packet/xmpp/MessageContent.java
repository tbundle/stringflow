package abs.ixi.server.packet.xmpp;

import java.io.Serializable;

import abs.ixi.server.XMLConvertible;

public interface MessageContent extends XMLConvertible, Serializable {

	public boolean isContentType(MessageContentType type);

	public enum MessageContentType {
		THREAD, BODY,

		SUBJECT,

		MEDIA,

		MDR_REQUEST,

		MDR_RECEIVED,

		CM_MARKABLE,

		CM_RECEIVED,

		CM_DISPLAYED,

		CM_ACKNOWLEDGED,

		DELAY,

		CSN_ACTIVE,

		CSN_INACTIVE,

		CSN_COMPOSING,

		CSN_PAUSED,

		CSN_GONE
	}
}
