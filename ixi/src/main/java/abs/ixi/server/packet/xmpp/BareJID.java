package abs.ixi.server.packet.xmpp;

import abs.ixi.server.packet.InvalidJabberId;
import abs.ixi.server.packet.JID;

/**
 * As stated in XMPP protocol specifications, user {@code BareJID} is the
 * combination of node and domain; essentially {@code BareJID} uniquely
 * identifies an user in XMPP service.
 * <p>
 * Similar to {@link JID}, {@code BareJID} should be treated as immutable within
 * server; and thats the reason there are no <i>setter</i> methods exposed in
 * this class.
 * </p>
 * 
 * @author Yogi
 *
 */
public final class BareJID {
	private static final String SEPERATOR = "@";

	/**
	 * String literal for node
	 */
	private String node;

	/**
	 * String literal for domain in which user has logged-in
	 */
	private String domain;

	public BareJID(String node, String domain) {
		this.node = node;
		this.domain = domain;
	}

	public BareJID(String bareJid) throws InvalidJabberId {
		if (bareJid != null) {
			if (bareJid.contains(SEPERATOR)) {
				String[] arr = bareJid.split(SEPERATOR);

				if (arr.length == 2) {
					node = arr[0];
					domain = arr[1];
				} else {
					throw new InvalidJabberId("bareJid " + bareJid + "is not valid");
				}
			} else {
				throw new InvalidJabberId("bareJid " + bareJid + "is not valid");
			}
		}
	}

	public String getNode() {
		return node;
	}

	public String getDomain() {
		return domain;
	}

	public JID toJID() {
		return new JID(node, domain);
	}

	@Override
	public int hashCode() {

		return this.toString().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}

		if (this == o) {
			return true;
		}

		if (o instanceof BareJID) {
			return this.node.equals(((BareJID) o).getNode()) && this.domain.equals(((BareJID) o).getDomain());
		}

		return false;
	}

	@Override
	public String toString() {
		return this.node + SEPERATOR + this.domain;
	}

}
