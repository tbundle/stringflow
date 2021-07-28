package abs.ixi.server.protocol;

/**
 * A factory implementation to instantiate various implementations of
 * {@link Protocol} interface. {@link ProtocolFactory} also decides if the same
 * instance of protocol should be returned or new one. If the protocol is
 * stateless, one instance is shared across all calls for that protocol. however
 * for protocols which are stateful, a new instance is returned for each call.
 * <p>
 * {@link XMPPProtocol} and {@link BOSHProtocol} are stateful;
 * {@link MimeProtocol} is stateless.
 * </p>
 * 
 * @author Yogi
 *
 */
public final class ProtocolFactory {
	/**
	 * {@link MimeProtocol} instance; this is the only instance which is
	 * returned on each invocation of {@link ProtocolFactory#getMimeProtocol()}
	 */
	private static MimeProtocol mimeProtocol;

	/**
	 * @return a new instance of {@link XMPPProtocol}
	 */
	public static final XMPPProtocol newXMPPProtocol() {
		return new XMPPProtocol();
	}

	/**
	 * @return a new instance of {@link BOSHProtocol}
	 */
	public static final BOSHProtocol newtBoshProtocol() {
		return new BOSHProtocol();
	}

	/**
	 * @return {@link MimeProtocol} instance; it's a singleton instance held by
	 *         this factory
	 */
	public static final MimeProtocol newMimeProtocol() {
		if (mimeProtocol == null) {
			synchronized (ProtocolFactory.class) {
				if (mimeProtocol == null) {
					mimeProtocol = new MimeProtocol();
				}
			}
		}

		return mimeProtocol;
	}

}
