package abs.ixi.server.app;

import static abs.ixi.server.Stringflow.runtime;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.ConsumerComponent;
import abs.ixi.server.BasicComponent;
import abs.ixi.server.CoreComponent.ServerFunction;
import abs.ixi.server.PacketConsumer;
import abs.ixi.server.PacketEnvelope;
import abs.ixi.server.common.InstantiationException;
import abs.ixi.server.etc.conf.Configurations;
import abs.ixi.server.packet.JID;
import abs.ixi.server.packet.Packet;
import abs.ixi.server.router.Router;
import abs.ixi.util.CollectionUtils;

/**
 * {@code ApplicationController} is a packet receiver server component (an
 * implementation of {@link ConsumerComponent}).
 * {@code ApplicationController} manages deployed applications within server at
 * runtime. It is the interface through which application instances interact
 * with other components within server.
 * <p>
 * Server loads {@code ApplicationController} in memory if it is NOT running in
 * {@link ServerMode#ROUTER} mode.
 * </p>
 * <p>
 * {@code ApplicationController} is expected to be singleton;meaning having just
 * one instance of this class in memory at any point.
 * </p>
 */
public final class ApplicationController extends BasicComponent implements PacketConsumer, ResponseForwarder {
	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationController.class);

	public static final String COMPONENT_NAME = "application-controller";

	private List<Deployable> applications;

	private RequestDelegator delegator;

	private Router router;

	public ApplicationController(Configurations config, Router router) throws InstantiationException {
		super(COMPONENT_NAME, config);
		this.router = router;
	}

	@Override
	public void start() throws Exception {
		LOGGER.info("Starting Application Controlller");

		LOGGER.info("Subscribing to router in order to receive packets");
		router.subscribe(this);

		this.applications = deployApps();
		this.delegator = new EndpointDelegator(this.applications);
	}

	private List<Deployable> deployApps() throws Exception {
		LOGGER.debug("Deploying applications...");

		Deployer deployer = new Deployer();
		List<Deployable> apps = deployer.deploy();

		if (!CollectionUtils.isNullOrEmpty(apps)) {
			LOGGER.info("Deployed Applications...");

			for (Deployable app : apps) {
				LOGGER.info(app.getName());
			}
		}

		return apps;
	}

	public List<Deployable> getApplications() {
		return applications;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public JID getJID() {
		return this.jid;
	}

	@Override
	public void shutdown() throws Exception {
		LOGGER.info("Shutting down {} component", getName());
		super.shutdown();
	}

	@Override
	public boolean submit(PacketEnvelope<? extends Packet> envelope) {
		// This can not be used for submitting packets to application
		// controller because application controller processes packets
		// synchronously
		return false;
	}

	@SuppressWarnings("unchecked")
	public void process(PacketEnvelope<? extends Packet> envelope) {
		Packet packet = envelope.getPacket();

		if (packet instanceof RequestContainer) {
			this.delegator.delegate((RequestContainer<ApplicationRequest>) packet);

		} else {
			// TODO Generate error for user; it may be a router error also.
		}

	}

	@Override
	public void forward(ResponseContainer<ApplicationResponse> responseContainer) throws Exception {
		PacketEnvelope<Packet> envelope = new PacketEnvelope<Packet>(responseContainer, this.getName());
		envelope.setDestinationComponent(runtime().getCoreComponentJid(ServerFunction.NETWORK_TRANSPORT));

		LOGGER.debug("Routing Response container : {}", responseContainer);
		router.route(envelope);
	}
}
