package abs.ixi.server.packet;

public interface PacketValidation<T> {
	public boolean validate(T packet);

}
