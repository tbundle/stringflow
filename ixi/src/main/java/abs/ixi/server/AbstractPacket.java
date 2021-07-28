package abs.ixi.server;

import java.sql.Timestamp;

import abs.ixi.server.packet.InvalidJabberId;
import abs.ixi.server.packet.JID;
import abs.ixi.server.packet.Packet;
import abs.ixi.server.packet.xmpp.Stanza;
import abs.ixi.server.router.Routable;
import abs.ixi.util.DateUtils;

/**
 * Abstract implementation of {@link Packet} interface. This class helps bring
 * application requests and xmpp core packets under one class hierarchy.
 */
public abstract class AbstractPacket implements Packet, Cloneable {
	private static final long serialVersionUID = 8062851296240552016L;

	private JID destination;

	private Timestamp createTime;

	public AbstractPacket() {
		this.createTime = DateUtils.currentTimestamp();
	}

	@Override
	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	/**
	 * Default implementation with value false indicating that the packet is not
	 * a {@link Stanza}. It must be overridden by {@link Stanza}.
	 */
	@Override
	public boolean isStanza() {
		return false;
	}

	@Override
	public JID getDestination() {
		return this.destination;
	}

	@Override
	public void setDestination(JID dest) {
		this.destination = dest;
	}

	@Override
	public void setDestination(String dest) throws InvalidJabberId {
		this.destination = new JID(dest);
	}

	/**
	 * This is the Default implementation of {@link Routable#isRoutable()}; it
	 * returns false for all kinds of packets.
	 */
	@Override
	public boolean isRoutable() {
		return false;
	}

	@Override
	public boolean isCloseStream() {
		return false;
	}

	@Override
	public boolean isBoshBodyPacket() {
		return false;
	}

	/**
	 * Make sure that we perform deep-copy of the object web; so that the
	 * changes made in cloned object are not reflected back in the original
	 * object
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public PacketXmlElement getXmlElementName() {
		return null;
	}
}
