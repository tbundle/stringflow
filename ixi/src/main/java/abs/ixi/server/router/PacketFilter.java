package abs.ixi.server.router;

public interface PacketFilter<T> {

    public boolean accept(T packet);

}
