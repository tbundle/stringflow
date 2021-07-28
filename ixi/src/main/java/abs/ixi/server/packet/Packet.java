package abs.ixi.server.packet;

import java.io.Serializable;
import java.sql.Timestamp;

import abs.ixi.server.PacketEnvelope;
import abs.ixi.server.Writable;
import abs.ixi.server.packet.xmpp.BOSHBody;
import abs.ixi.server.packet.xmpp.IQ;
import abs.ixi.server.packet.xmpp.Stanza;
import abs.ixi.server.router.Routable;

/**
 * Root interface to represent all sorts of packets (user level, stream
 * management, control) within server. Regardless of the type or its usage
 * within server, each packet definition must implement {@code Packet}
 * interface.
 */
public interface Packet extends Writable, MimeAware, Routable, Serializable {
	/**
	 * @return Element name of this packet. It is xml element name. These names
	 *         top level xml element names. therefore, for all kind of
	 *         {@link IQ} packets, this method will return
	 *         {@link PacketXmlElement#IQ}
	 */
	public PacketXmlElement getXmlElementName();

	/**
	 * Check if this packet is a {@link Stanza} i.e.
	 * {@code Message, IQ, Presence}.
	 * 
	 * @return true if this packet is a {@link Stanza} otherwise false
	 */
	public boolean isStanza();

	/**
	 * Check if this packet is a {@link BOSHBody} i.e.
	 * {@code Message, IQ, Presence}.
	 * 
	 * @return true if this packet is a {@link BOSHBody} otherwise false
	 */
	default public boolean isBoshBodyPacket() {
		return false;
	}

	/**
	 * @return Returns {@link JID} of the destination for this packet. It s
	 *         important to note that destination is generally outside of the
	 *         server.
	 */
	public JID getDestination();

	/**
	 * Sets the destination for this packet.
	 * <p>
	 * A packets wrapped within a {@link PacketEnvelope} while flowing through
	 * the server to various server components. The destination held by the
	 * packets are the destination of end entity; however to make the routing
	 * possible within server, it is the {@link PacketEnvelope} which has source
	 * and destination {@link JID}s
	 * </p>
	 * 
	 * @param dest
	 */
	public void setDestination(JID dest);

	/**
	 * Sets the destination for this packet. The destination {@link JID} is
	 * created from the supplied string.
	 * <p>
	 * A packets wrapped within a {@link PacketEnvelope} while flowing through
	 * the server to various server components. The destination held by the
	 * packets are the destination of end entity; however to make the routing
	 * possible within server, it is the {@link PacketEnvelope} which has source
	 * and destination {@link JID}s
	 * </p>
	 * 
	 * @param dest
	 */
	public void setDestination(String dest) throws InvalidJabberId;

	/**
	 * Returns {@link JID} of the source for this packet
	 */
	public String getSourceId();

	/**
	 * 
	 * @return Packet is close stream header or not
	 */
	public boolean isCloseStream();

	/**
	 * @return timestamp when the packet was created within server
	 */
	public Timestamp getCreateTime();

	/**
	 * {@link PacketElement} enum captures xml element name of this packet i.e.
	 * Message, IQ, Presence, StreamHeader, auth etc. During packet processing,
	 * at various points, we are required to discern the packet type to make
	 * decision. The enum helps us get packet element name without blowing up
	 * the {@link Packet} interface with one method for each packet
	 * element/type.
	 * 
	 * @author Yogi
	 *
	 */
	public enum PacketXmlElement {
		MESSAGE("message"),

		IQ("iq"),

		PRESENCE("presence"),

		STREAM_HEADER("stream"),

		ACK("a"),

		ACK_REQUEST("r"),

		FAILURE("failure"),

		SASL_AUTH("auth"),

		SASL_CHALALNGE_RESPONSE("response"),

		SASL_CHALLANGE("challange"),

		SASL_SUCCESS("success"),

		SM_ENABLED("enabled"),

		SM_ENABLE("enable"),

		SM_RESUME("resume"),

		SM_RESUMED("resumed"),

		SM_FAILED("failed"),

		START_TLS("starttls"),

		STREAM_ERROR("error"),

		STREAM_FEATURE("features"),

		TLS_PROCEED("proceed"),

		BOSH_BODY("body");

		private String elmName;

		private PacketXmlElement(String elmName) {
			this.elmName = elmName;
		}

		public String elementNameString() {
			return this.elmName;
		}
	}

}
