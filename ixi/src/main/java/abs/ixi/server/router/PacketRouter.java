package abs.ixi.server.router;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.ConsumerComponent;
import abs.ixi.server.PacketConsumer;
import abs.ixi.server.PacketEnvelope;
import abs.ixi.server.PacketProducer;
import abs.ixi.server.common.InstantiationException;
import abs.ixi.server.etc.conf.Configurations;
import abs.ixi.server.packet.JID;
import abs.ixi.server.packet.Packet;
import abs.ixi.server.sys.monitor.JmxRegistrar;
import abs.ixi.server.sys.monitor.PacketRouterJmxBean;

/**
 * {@code PacketRouter} is a core component which is responsible for routing
 * packets to destination components. All the {@link PacketConsumer}
 * implementations must subscribe to {@code PacketRouter}.
 * 
 * @author Yogi
 *
 */
public class PacketRouter extends ConsumerComponent implements PacketProducer, Router {
	private static final Logger LOGGER = LoggerFactory.getLogger(PacketRouter.class);

	/**
	 * Name of the component
	 */
	public static final String COMPONENT_NAME = "router";

	/**
	 * A map of {@link JID} and {@link PacketConsumer} componenets
	 */
	private Map<String, PacketConsumer> components;

	public PacketRouter(Configurations config) throws InstantiationException {
		super(COMPONENT_NAME, config);
		this.components = new ConcurrentHashMap<>();
	}

	@Override
	public void start() throws Exception {
		super.start();
		JmxRegistrar.registerBean("abs.ixi.server.jmx:type=PacketRouterJmxBean",
				new PacketRouterJmxBean(this.inboundQ, this.components, this));
	}

	@Override
	public <P extends Packet> boolean route(PacketEnvelope<P> envelope) {
		return this.submit(envelope);
	}

	@Override
	public void process(PacketEnvelope<? extends Packet> envelope) {
		try {
			if (envelope.getDestinationComponent() != null) {
				PacketConsumer receiver = this.components.get(envelope.getDestinationComponent());

				if (receiver != null) {
					receiver.submit(envelope);

				} else {
					LOGGER.warn("Receiver {} not found", envelope.getDestinationComponent());
				}

			} else {
				LOGGER.warn("Could not route packet envelope due to destination component is null", envelope);
			}

		} catch (Exception e) {
			LOGGER.error("Exception caught during routing envelope {}", envelope, e);
		}

	}

	@Override
	public void subscribe(PacketConsumer component) {
		this.components.put(component.getName(), component);
	}

	@Override
	public void shutdown() throws Exception {
		LOGGER.info("Shutting down {} component", getName());
		super.shutdown();
	}

}
