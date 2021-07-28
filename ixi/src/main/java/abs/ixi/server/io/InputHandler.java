package abs.ixi.server.io;

import abs.ixi.server.ServerComponent;
import abs.ixi.server.packet.Packet;

/**
 * A facade interface which can take incoming packets and submit for further
 * processing within server. Generally, {@code InputHandlerFacade} is the first
 * component to process the packet (last in {@link ServerIO} realm to process).
 * <p>
 * A {@code InputHandlerFacade} is expected to pre-process packets before they
 * are forwarded to {@link ServerComponent}s for processing; pre-processing a
 * packet involves enriching it and sometime even transforming it so that it can
 * be processed, tracked, and an entry is made in various ledgers in server.
 * </p>
 */
public interface InputHandler {
	/**
	 * Execute this {@link IOService}
	 * 
	 * @param ios
	 */
	// TODO this method does not belong here; an IOService can not be executed
	// by a input handler; it is the IOController which has ability to execute
	// IOService; InputHandler is only to process inbound packets; Refactor and
	// remove this method.
	public void execute(IOService<?, ?> ios);

	/**
	 * Takes incoming packet and may process it and forward it for further
	 * processing
	 * 
	 * @param packet
	 */
	public void handle(Packet packet) throws Exception;

}
