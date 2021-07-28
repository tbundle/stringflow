package abs.ixi.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.common.ConcurrentQueue;
import abs.ixi.server.common.InstantiationException;
import abs.ixi.server.etc.PersistenceService;
import abs.ixi.server.etc.conf.Configurations;
import abs.ixi.server.packet.Packet;

/**
 * This is the bare minimum {@link ServerComponent}. It can receive user level
 * {@link Packet} traffic and process it and finally can redirect it to some
 * other {@link ServerComponent}
 */
public abstract class ConsumerComponent extends BasicComponent implements PacketConsumer {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerComponent.class);

	/**
	 * Queue to hold inbound packets for processingby this component
	 */
	protected ConcurrentQueue<PacketEnvelope<? extends Packet>> inboundQ;

	private long inboundPacketCount;

	private long outboundPacketCount;

	/**
	 * Thread to process inbound packets
	 */
	private Thread inPacketProcessor;

	/**
	 * database service instance
	 */
	protected PersistenceService dbService;

	public ConsumerComponent(String name, Configurations conf) throws InstantiationException {
		super(name, conf);
		this.inboundQ = new ConcurrentQueue<>();
	}

	@Override
	public void start() throws Exception {
		super.start();

		this.dbService = PersistenceService.getInstance();

		this.inPacketProcessor = new Thread(new Runnable() {

			@Override
			public void run() {
				while (!Thread.currentThread().isInterrupted() && !stopping && !stopped) {
					try {
						process(inboundQ.take());

					} catch (InterruptedException e) {
						LOGGER.warn("Inbound packet processor thread in component {} has been interrupted", name);
						Thread.currentThread().interrupt();

					} catch (Exception e) {
						LOGGER.warn("Exception caught in Inbound packet processor thread in component {}", name, e);
					}
				}
			}
		});

		LOGGER.info("Starting inbound packet processor thread in component {}", this.name);
		this.inPacketProcessor.start();
	}

	@Override
	public boolean submit(PacketEnvelope<? extends Packet> envelope) {
		return this.inboundQ.offer(envelope);
	}

	@Override
	public void shutdown() throws Exception {
		super.shutdown();
	}

	/**
	 * Abstract method for packet processing
	 */
	protected abstract void process(PacketEnvelope<? extends Packet> envelope);

	// /**
	// * An abstract method which will have processing logic.
	// *
	// * @param couplet Incoming {@link Couplet} instance
	// */
	// protected abstract void execute(Couplet couplet) throws
	// ExecutionException;

	public long getInboundPacketCount() {
		return inboundPacketCount;
	}

	public void setInboundPacketCount(long inboundPacketCount) {
		this.inboundPacketCount = inboundPacketCount;
	}

	public long getOutboundPacketCount() {
		return outboundPacketCount;
	}

	public void setOutboundPacketCount(long outboundPacketCount) {
		this.outboundPacketCount = outboundPacketCount;
	}

	public void increaseOutboundCount() {
		setOutboundPacketCount(++outboundPacketCount);
	}

	public void increaseInboundCount() {
		setInboundPacketCount(++inboundPacketCount);
	}

}
