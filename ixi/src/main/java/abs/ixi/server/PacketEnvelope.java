package abs.ixi.server;

import abs.ixi.server.packet.Packet;

/**
 * {@code PacketEnvelope} is a wrapper around user level packets. It contains
 * info required for routing and processing by a {@code ServerComponent} The
 * {@link PacketEnvelope} also contains processing errors for the {@link Packet}
 * 
 * @param <PACKET>
 *            the {@link Packet} type which is held by this
 *            {@link PacketEnvelope}
 */
public class PacketEnvelope<PACKET extends Packet> {
	private String sourceComponent;
	private String destinationComponent;

	private PACKET packet;
	private Error error;

	public PacketEnvelope(PACKET packet, String sourceComponent) {
		this.packet = packet;
		this.sourceComponent = sourceComponent;
	}

	public String getSourceComponent() {
		return sourceComponent;
	}

	public String getDestinationComponent() {
		return destinationComponent;
	}

	public void setDestinationComponent(String destinationComponent) {
		this.destinationComponent = destinationComponent;
	}

	public void setPacket(PACKET packet) {
		this.packet = packet;
	}

	public PACKET getPacket() {
		return packet;
	}

	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
	}

	@Override
	public String toString() {
		return "PacketEnvelope[" + envelopeContentType() + "]-src[" + this.sourceComponent + "]-dest["
				+ this.destinationComponent + "]";
	}

	private String envelopeContentType() {
		return error == null ? packet.getClass().getName() : error.name();
	}
}
