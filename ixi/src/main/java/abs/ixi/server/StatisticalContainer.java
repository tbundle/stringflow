package abs.ixi.server;

import abs.ixi.server.router.PacketRouter;

/**
 * A contract for {@link ServerComponent} implementations to be aware of various
 * statistical data such inbound packet rate, outgoing packet rate, packet
 * processing rate, packet waiting time etc.
 * 
 * @author Yogi
 *
 */
public interface StatisticalContainer {
	/**
	 * @return average rate of packet processing by this component. The rate is
	 *         calculated based on total time taken in processing since the
	 *         component is up
	 */
	public double getPacketProcessingRate();

	/**
	 * @return rate at which packets are submitted to this component. The rate
	 *         is calculated based on total number of packets received in last
	 *         one hour
	 */
	public double getInboundPacketRate();

	/**
	 * @return rate at which packets are submitted to {@link PacketRouter} by
	 *         this component in last one hour.
	 */
	public double getOutboundPacketRate();

	/**
	 * @return average waiting time of packets within this component queue
	 */
	public double getPacketWaitingTime();

	/**
	 * @return max size of the inbound packet waiting queue
	 */
	public int getMaxWaitingQueueSize();
	
}
