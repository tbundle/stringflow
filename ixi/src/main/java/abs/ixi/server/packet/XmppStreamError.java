package abs.ixi.server.packet;

import abs.ixi.server.packet.xmpp.XMPPPacket;

/**
 * Exception to capture xmpp stream error. The exception must be used to
 * represent only and only stream error mentioned in
 * <a href="https://tools.ietf.org/html/rfc6120">rfc6120</a>
 */
public class XmppStreamError extends XMPPException {
	private static final long serialVersionUID = 1L;

	private XMPPPacket src;
	private StreamError error;

	public XmppStreamError(StreamError error) {
		this(null, error);
	}

	public XmppStreamError(XMPPPacket src, StreamError error) {
		super("");

		this.src = src;
		this.error = error;
	}

	public XMPPPacket getSource() {
		return src;
	}

	public void setSrc(XMPPPacket src) {
		this.src = src;
	}

	public StreamError getError() {
		return error;
	}

	/**
	 * Enum to represent XMPP stream errors as specified in rfc6120
	 */
	public enum StreamError {

	}

}
