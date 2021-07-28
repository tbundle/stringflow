package abs.ixi.server.packet.xmpp;

import abs.ixi.server.packet.InvalidJabberId;
import abs.ixi.server.packet.JID;
import abs.ixi.xml.Element;

public abstract class Stanza extends XMPPPacket {
	private static final long serialVersionUID = 3292705425251421492L;

	public static final String ID_ATTRIBUTE = "id".intern();
	public static final String FROM_ATTRIBUTE = "from".intern();
	public static final String TO_ATTRIBUTE = "to".intern();

	protected String id;
	protected JID from;
	protected JID to;
	private boolean delivered;

	public Stanza() {
		super();
	}

	public Stanza(Element element) throws InvalidJabberId {
		super(element);

		if (this.element.getAttribute(ID_ATTRIBUTE) != null) {
			this.id = this.element.getAttribute(ID_ATTRIBUTE);
		}

		if (this.element.getAttribute(FROM_ATTRIBUTE) != null) {
			this.from = new JID(this.element.getAttribute(FROM_ATTRIBUTE));
		}

		if (this.element.getAttribute(TO_ATTRIBUTE) != null) {
			this.to = new JID(this.element.getAttribute(TO_ATTRIBUTE));
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public JID getFrom() {
		return from;
	}

	public void setFrom(JID from) {
		this.from = from;
	}

	public JID getTo() {
		return to;
	}

	public void setTo(JID to) {
		this.to = to;
	}

	public boolean isDelivered() {
		return delivered;
	}

	public void setDelivered(boolean delivered) {
		this.delivered = delivered;
	}

	@Override
	public JID getDestination() {
		return to;
	}

	@Override
	public void setDestination(String dest) throws InvalidJabberId {
		this.to = new JID(dest);
	}

	@Override
	public void setDestination(JID dest) {
		this.to = dest;
	}

	@Override
	public String getSourceId() {
		if (this.from == null) {
			return null;
		}

		return this.from.getFullJID();
	}

	@Override
	public final boolean isStanza() {
		return true;
	}

	@Override
	public boolean isRoutable() {
		return true;
	}

	public abstract boolean isInsurancedDeliveryRequired();
}
