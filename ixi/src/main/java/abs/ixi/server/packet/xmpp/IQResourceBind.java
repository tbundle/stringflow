package abs.ixi.server.packet.xmpp;

import abs.ixi.server.packet.JID;
import abs.ixi.xml.Element;
import abs.ixi.xml.XMLUtils;

public class IQResourceBind extends AbstractIQContent {
	private static final long serialVersionUID = -556586666728940691L;

	public static final String XML_ELM_NAME = "bind".intern();

	private static final String BIND_OPEN_TAG = "<bind xmlns='urn:ietf:params:xml:ns:xmpp-bind'>".intern();
	private static final String BIND_CLOSE_TAG = "</bind>";
	private static final String JID_OPEN_TAG = "<jid>";
	private static final String JID_CLOSE_TAG = "</jid>";

	private static final String RESOURCE = "resource".intern();

	private String resource;

	private JID userJID;

	public IQResourceBind(String xmlns) {
		super(xmlns, IQContentType.BIND);
	}

	public IQResourceBind(Element elm) {
		this(elm.getAttribute(XMLUtils.XMLNS_ATTRIBUTE));

		if (elm.getChild(RESOURCE) != null) {
			this.setResource(elm.getChild(RESOURCE).val());
		}
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public JID getUserJID() {
		return userJID;
	}

	public void setUserJID(JID userJID) {
		this.userJID = userJID;
	}

	@Override
	public String xml() {
		StringBuilder sb = new StringBuilder(BIND_OPEN_TAG);

		if (this.userJID != null) {
			sb.append(JID_OPEN_TAG).append(userJID.getFullJID()).append(JID_CLOSE_TAG);
		}

		sb.append(BIND_CLOSE_TAG);

		return sb.toString();
	}

	@Override
	public StringBuilder appendXml(StringBuilder sb) {
		sb.append(BIND_OPEN_TAG);

		if (this.userJID != null) {
			sb.append(JID_OPEN_TAG).append(userJID.getFullJID()).append(JID_CLOSE_TAG);
		}

		sb.append(BIND_CLOSE_TAG);

		return sb;
	}

}
