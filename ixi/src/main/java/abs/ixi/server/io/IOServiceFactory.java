package abs.ixi.server.io;

import java.io.IOException;

import abs.ixi.server.io.net.IOPortConnector;
import abs.ixi.server.packet.Packet;
import abs.ixi.server.protocol.Protocol;

/**
 * A singleton factory to instantiate {@link IOService}. There are multiple
 * implementations of {@link IOService} in server and this factory is the only
 * place where it can be instantiated.
 */
public final class IOServiceFactory {

	/**
	 * Instantiate {@link BasicIOService} with given protocol and connector
	 * 
	 * @param connection
	 * @param protocol
	 * @return
	 * @throws IOException
	 */
	public static final <P extends Protocol<T>, T extends Packet> IOService<P, T> newBasicIOService(
			IOPortConnector connector, P protocol) throws IOException {
		return new BasicIOService<>(connector, protocol);
	}

	/**
	 * Instantiate {@link LongPollingIOService} with given protocol and
	 * connector
	 * 
	 * @param connection
	 * @param protocol
	 * @return
	 */
	public static final <P extends Protocol<T>, T extends Packet> IOService<P, T> newLongPollingIOService(
			IOPortConnector connector, P protocol) {
		return new LongPollingIOService<>(connector, protocol);
	}
}
