package abs.ixi.server.packet;

public class PacketValidatorFactory {
	public static XMPPPacketValidator getXmppPacketValidator() {
		return XMPPPacketValidator.getInstance();
	}

	public static BoshPacketValidator getBoshPacketValidator() {
		return BoshPacketValidator.getInstance();
	}
}
