package abs.ixi.server.packet.xmpp;

import java.io.Serializable;

import abs.ixi.server.XMLConvertible;

/**
 * Root interface for all the supported content inside an {@link IQ} packet.
 * Apart from core XMPP supported content, there are quite a few extension in
 * Stringflow.
 */
public interface IQContent extends XMLConvertible, Serializable {
	/**
	 * @return {@link IQContentType} of the this {@link IQContent}
	 */
	public IQContentType getType();

	/**
	 * Enum to indicate content type implementation
	 */
	public enum IQContentType {
		BIND,

		QUERY,

		VCARD,

		PING,

		JINGLE,

		OPEN,

		DATA,

		CLOSE,

		REQUEST,

		RESPOSNE,

		SESSION,

		PUSH_REGISTRATION,

		ERROR;
	}

}
