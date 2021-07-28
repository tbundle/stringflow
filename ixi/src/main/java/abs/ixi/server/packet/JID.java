package abs.ixi.server.packet;

import java.io.Serializable;

import abs.ixi.server.packet.xmpp.BareJID;
import abs.ixi.util.StringUtils;

/**
 * A domain object for XMPP Jabber id. XMPP Jabber ID is consists of three
 * components:
 * <ul>
 * <li>Node</li>
 * <li>Domain</li>
 * <li>ResourceId</li>
 * </ul>
 * In string format, {@code JID} is formatted as
 * <i>node<b>@</b>domain<b>/</b>resourceId</i>
 * <p>
 * <b>Node</b>: Node is the user identifier on XMPP service. A node is unique
 * for an user;however an user may own multiple nodes on the XMPP Service. Each
 * node is treated as a user on the server.<br>
 * <b>Domain</b>: Domain of the XMPP Service domain; an identifier for the XMPP
 * Service. Domain id is the realm in which user node exists.<br>
 * <b>ResourceId</b>: An user can login from multiple devices with same node;
 * resourceId identifieseach session; therefore in this case, {@code JID} will
 * have a different resourceid for the user. ResourceId can be also seen as user
 * device identifier.
 * </p>
 * <p>
 * {@code JID} must be treated as immuatable within server even when the
 * constructs and design of this class permits mutations on the instance.
 * </p>
 * 
 * @author Yogi
 *
 */
public class JID implements Serializable {
	private static final long serialVersionUID = 1303943957888525472L;

	private static final String SEPARATOR_AT = "@";
	private static final String SEPARATOR_SLASH = "/";

	private String node;
	private String domain;
	private String resource;

	/**
	 * Parses the supplied JabberId for node, domain and resource fields
	 * 
	 * @param jid
	 * @throws InvalidJabberId if the jid supplied is malformed
	 */
	public JID(String jid) throws InvalidJabberId {
		if (!StringUtils.isNullOrEmpty(jid)) {
			String[] arr = jid.split(SEPARATOR_AT);

			if (arr.length == 1) {
				this.domain = jid;

			} else if (arr.length == 2) {
				this.node = arr[0];

				String s = arr[1];
				String[] d = s.split(SEPARATOR_SLASH);

				if (d.length == 1) {
					this.domain = d[0];
				} else if (d.length == 2) {
					this.domain = d[0];
					this.resource = d[1];
				}
			}
		} else {
			throw new InvalidJabberId("JID supplied is invalid :" + jid);
		}
	}

	public JID(String node, String domain) {
		this.node = node;
		this.domain = domain;
	}

	public JID(String node, String domain, String resource) {
		this.node = node;
		this.domain = domain;
		this.resource = resource;
	}

	public JID(BareJID bareJID, String resource) {
		this.node = bareJID.getNode();
		this.domain = bareJID.getDomain();
		this.resource = resource;

	}

	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public BareJID getBareJID() {
		return new BareJID(node, domain);
	}

	public String getFullJID() {
		StringBuilder sb = new StringBuilder();

		if (!StringUtils.isNullOrEmpty(node)) {
			sb.append(node).append(SEPARATOR_AT);
		}

		sb.append(domain);

		if (!StringUtils.isNullOrEmpty(resource)) {
			sb.append(SEPARATOR_SLASH).append(resource);
		}

		return sb.toString();
	}

	public boolean isFullJId() {
		return StringUtils.isNullOrEmpty(this.resource) ? false : true;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}

		if (this == o) {
			return true;
		}

		if (o instanceof JID) {
			JID jid = (JID) o;

			boolean isThisFullJID = false;
			boolean isJidFullJID = false;
			boolean result = false;

			if ((isThisFullJID = isFullJId()) && (isJidFullJID = jid.isFullJId())) {
				if (node.equals(jid.node) && domain.equals(jid.domain) && resource.equals(jid.resource)) {
					result = true;
				}
			} else if (!isThisFullJID && !isJidFullJID) {
				if (node.equals(jid.node) && domain.equals(jid.domain)) {
					result = true;
				}
			}
			return result;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}

	@Override
	public String toString() {
		return new StringBuilder().append("JID {").append(node).append(SEPARATOR_AT).append(domain)
				.append(SEPARATOR_SLASH).append(resource).append("}").toString();
	}

}
