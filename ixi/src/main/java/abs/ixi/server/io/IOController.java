package abs.ixi.server.io;

import static abs.ixi.server.etc.conf.Configurations.Bundle.PROCESS;
import static abs.ixi.server.io.IOServiceFactory.newBasicIOService;
import static abs.ixi.server.io.IOServiceFactory.newLongPollingIOService;
import static abs.ixi.server.protocol.ProtocolFactory.newMimeProtocol;
import static abs.ixi.server.protocol.ProtocolFactory.newXMPPProtocol;
import static abs.ixi.server.protocol.ProtocolFactory.newtBoshProtocol;

import java.io.IOException;
import java.nio.channels.Selector;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.ComponentNotFoundException;
import abs.ixi.server.CoreComponent;
import abs.ixi.server.PacketEnvelope;
import abs.ixi.server.PacketProducerConsumer;
import abs.ixi.server.Stringflow;
import abs.ixi.server.common.ChannelStream;
import abs.ixi.server.common.InstantiationException;
import abs.ixi.server.etc.conf.Configurations;
import abs.ixi.server.io.multipart.MimePacket;
import abs.ixi.server.io.net.ByteStream;
import abs.ixi.server.io.net.ConnectionManager;
import abs.ixi.server.io.net.IOPortConnector;
import abs.ixi.server.packet.Packet;
import abs.ixi.server.packet.Packet.PacketXmlElement;
import abs.ixi.server.packet.XMPPNamespaces;
import abs.ixi.server.packet.xmpp.BOSHBody;
import abs.ixi.server.packet.xmpp.IQ;
import abs.ixi.server.packet.xmpp.IQContent.IQContentType;
import abs.ixi.server.packet.xmpp.IQQuery;
import abs.ixi.server.packet.xmpp.Message;
import abs.ixi.server.packet.xmpp.Message.MessageType;
import abs.ixi.server.packet.xmpp.Presence;
import abs.ixi.server.packet.xmpp.Stanza;
import abs.ixi.server.packet.xmpp.XMPPPacket;
import abs.ixi.server.protocol.BOSHProtocol;
import abs.ixi.server.protocol.MimeProtocol;
import abs.ixi.server.protocol.XMPPProtocol;
import abs.ixi.server.router.Router;
import abs.ixi.server.session.SessionManager;
import abs.ixi.server.session.UserSession;
import abs.ixi.server.sys.monitor.IxiJmxTaskRunnerBean;
import abs.ixi.server.sys.monitor.JmxRegistrar;
import abs.ixi.util.StringUtils;

/**
 * {@code IOController} is the bridge between byte-level processing and packet
 * processing components in server. {@code IOController}'s primary
 * responsibilities include-
 * <ul>
 * <li>Executing {@link IOService}s using its Executer service once it receives
 * signal of fresh bytes in {@link ChannelStream}</li>
 * <li>Pre-processing in-bound packets, updating user activity ina
 * {@link UserSession}, forwarding stream management packets to
 * {@link SessionManager} for handling and forwarding stanza packets to
 * {@link Router} for processing in server</li>
 * <li>Similarly, {@code IOController} receives all the out-bound packets from
 * {@link Router} which it delegates to {@link IOService} for processing
 * (writing bytes on to sockets)</li>
 * <li>Registering {@link IOService} with itself and maintain them in a cache.
 * This cache is scanned by connection watchdog for broken/dead connections</li>
 * </ul>
 */
public final class IOController extends PacketProducerConsumer implements InputHandler, CoreComponent {
	private static final Logger LOGGER = LoggerFactory.getLogger(IOController.class);

	/**
	 * Name of {@link IOController} server component
	 */
	private static final String COMPONENT_NAME = "io-controller";

	public static final String MUC_SERVICE_DOMAIN = "chat.alterbasics.com";

	/**
	 * IOController Executor service core threadpool size
	 */
	private static final int CORE_POOL_SIZE = 8;

	/**
	 * IOController Executor service max threadpool size
	 */
	private static final int MAX_POOL_SIZE = 15;

	/**
	 * IOController executor service keep alive time in minutes
	 */
	private static final int KEEP_ALLIVE_MINUTS = 2;

	// TODO: Xmpp IOS cache removed. Think about these cache later
	private static final Map<String, IOService<BOSHProtocol, BOSHBody>> BOSH_IOS_CACHE;

	private static final Map<String, IOService<MimeProtocol, MimePacket>> MIME_IOS_CACHE;

	/**
	 * Executer Service for {@link IOService} execution
	 */
	private ExecutorService executor;

	static {
		BOSH_IOS_CACHE = new ConcurrentHashMap<>();
		MIME_IOS_CACHE = new ConcurrentHashMap<>();
	}

	public IOController(Configurations conf, Router router) throws InstantiationException {
		super(COMPONENT_NAME, conf, router);

		int cps = conf.getOrDefaultInteger(_IO_CONTROLLER_THREDAPOOL_CORE_SIZE, CORE_POOL_SIZE, PROCESS);
		if (cps < 0) {
			LOGGER.warn("Invalid core pool size;defaulting to {}", CORE_POOL_SIZE);
			cps = CORE_POOL_SIZE;
		}

		int mps = conf.getOrDefaultInteger(_IO_CONTROLLER_THREADPOOL_MAX_SIZE, MAX_POOL_SIZE, PROCESS);
		if (mps < 0) {
			LOGGER.warn("Invalid max pool size;defaulting to {}", MAX_POOL_SIZE);
			mps = MAX_POOL_SIZE;
		}

		int keepAlive = conf.getOrDefaultInteger(_IO_CONTROLLER_THREADPOOL_KEEPALIVE, KEEP_ALLIVE_MINUTS, PROCESS);
		if (keepAlive < 0) {
			LOGGER.info("Invalid keep alive time;defaulting to {} minutes", KEEP_ALLIVE_MINUTS);
			keepAlive = KEEP_ALLIVE_MINUTS;
		}

		this.executor = new ThreadPoolExecutor(cps, mps, keepAlive, TimeUnit.MINUTES, new LinkedBlockingQueue<>());

		try {
			JmxRegistrar.registerBean("abs.ixi.server.jmx:type=IxiJmxThreadPoolBean",
					new IxiJmxTaskRunnerBean((ThreadPoolExecutor) this.executor));

		} catch (MalformedObjectNameException | InstanceAlreadyExistsException | MBeanRegistrationException
				| NotCompliantMBeanException e) {
			LOGGER.warn("Failed to register jmx bean for IOController", e);
		}

	}

	public static void addToBoshIosCache(IOService<BOSHProtocol, BOSHBody> ios) {
		BOSH_IOS_CACHE.put(ios.getResourceID(), ios);
	}

	public static void removeFromBoshIosCache(IOService<BOSHProtocol, BOSHBody> ios) {
		BOSH_IOS_CACHE.remove(ios.getResourceID());
	}

	public static void addToMimeIosCache(IOService<MimeProtocol, MimePacket> ios) {
		MIME_IOS_CACHE.put(ios.getResourceID(), ios);
	}

	public static void removeFromMimeIosCache(IOService<?, ?> ios) {
		MIME_IOS_CACHE.remove(ios.getResourceID());
	}

	private void sendPushNotification(final Message message) throws ComponentNotFoundException {
		PacketEnvelope<Packet> envelope = new PacketEnvelope<Packet>(message, this.getName());
		envelope.setDestinationComponent(Stringflow.runtime().getCoreComponentJid(ServerFunction.PUSH_NOTIFICATION));
		this.router.route(envelope);
	}

	/**
	 * Submits {@link IOService} instance to the thread-pool.
	 * 
	 * @param service service to be executed
	 */
	@Override
	public void execute(IOService<?, ?> service) {
		this.executor.submit(service);
	}

	@Override
	public void handle(Packet packet) throws Exception {
		handleXmppPacket((XMPPPacket) packet);
	}

	private void handleXmppPacket(XMPPPacket xmppPacket) throws Exception {
		if (xmppPacket != null && xmppPacket.isRoutable()) {
			LOGGER.debug("Handaling stanza {}", xmppPacket.xml());

			if (xmppPacket.getXmlElementName() == PacketXmlElement.MESSAGE) {
				Message message = (Message) xmppPacket;
				handleMessageRouting(message);

			} else if (xmppPacket.getXmlElementName() == PacketXmlElement.IQ) {
				IQ iq = (IQ) xmppPacket;
				handleIQRouting(iq);

			} else if (xmppPacket.getXmlElementName() == PacketXmlElement.PRESENCE) {
				Presence presence = (Presence) xmppPacket;
				handlePresenceRouting(presence);
			}

		} else {
			LOGGER.warn("Packet {} is unprocessable. So escape it.");
		}
	}

	private void handlePresenceRouting(Presence presence) throws ComponentNotFoundException {
		PacketEnvelope<Packet> packetEnvelope = new PacketEnvelope<Packet>(presence, this.getName());

		if (presence.isMuc()) {
			packetEnvelope
					.setDestinationComponent(Stringflow.runtime().getCoreComponentJid(ServerFunction.MUC_HANDLING));

		} else if (presence.getTo() != null
				&& StringUtils.safeEquals(presence.getTo().getDomain(), MUC_SERVICE_DOMAIN)) {
			packetEnvelope
					.setDestinationComponent(Stringflow.runtime().getCoreComponentJid(ServerFunction.MUC_HANDLING));

		} else {
			packetEnvelope.setDestinationComponent(
					Stringflow.runtime().getCoreComponentJid(ServerFunction.PRESENCE_MANAGEMENT));
		}

		this.router.route(packetEnvelope);
	}

	private void handleIQRouting(IQ iq) throws ComponentNotFoundException {
		PacketEnvelope<Packet> packetEnvelope = new PacketEnvelope<Packet>(iq, this.getName());

		if (iq.getContent().getType() == IQContentType.QUERY) {
			IQQuery query = (IQQuery) iq.getContent();

			if (StringUtils.safeEquals(query.getXmlns(), XMPPNamespaces.DISCO_ITEM_NAMESPACE)
					|| StringUtils.safeEquals(query.getXmlns(), XMPPNamespaces.DISCO_INFO_NAMESPACE)
					|| StringUtils.safeEquals(query.getXmlns(), XMPPNamespaces.MUC_ADMIN_NAMESPACE)
					|| StringUtils.safeEquals(query.getXmlns(), XMPPNamespaces.MUC_OWNER_NAMESPACE)) {

				packetEnvelope
						.setDestinationComponent(Stringflow.runtime().getCoreComponentJid(ServerFunction.MUC_HANDLING));

			} else if (StringUtils.safeEquals(query.getXmlns(), XMPPNamespaces.STRINGFLOW_MEDIA_NAMESPACE)) {
				packetEnvelope.setDestinationComponent(
						Stringflow.runtime().getCoreComponentJid(ServerFunction.MEDIA_STORE_MANAGEMENT));
			} else {
				packetEnvelope.setDestinationComponent(
						Stringflow.runtime().getCoreComponentJid(ServerFunction.DISCO_HANDLING));
			}

			this.router.route(packetEnvelope);

		} else if (iq.getContent().getType() == IQContentType.DATA || iq.getContent().getType() == IQContentType.CLOSE
				|| iq.getContent().getType() == IQContentType.OPEN
				|| iq.getContent().getType() == IQContentType.PUSH_REGISTRATION
				|| iq.getContent().getType() == IQContentType.JINGLE
				|| iq.getContent().getType() == IQContentType.VCARD) {

			packetEnvelope
					.setDestinationComponent(Stringflow.runtime().getCoreComponentJid(ServerFunction.DISCO_HANDLING));

			this.router.route(packetEnvelope);
		}
	}

	private void handleMessageRouting(Message message) throws ComponentNotFoundException {
		PacketEnvelope<Packet> packetEnvelope = new PacketEnvelope<Packet>(message, this.getName());

		if (message.getType() == MessageType.GROUP_CHAT) {
			packetEnvelope
					.setDestinationComponent(Stringflow.runtime().getCoreComponentJid(ServerFunction.MUC_HANDLING));
			this.router.route(packetEnvelope);

		} else {
			this.process(packetEnvelope);
		}
	}

	@Override
	public void process(PacketEnvelope<? extends Packet> envelope) {
		try {
			LOGGER.trace("Envelope {} with packet {} received", envelope, envelope.getPacket().getXmlElementName());

			if (envelope.getPacket().isStanza()) {
				Stanza stanza = (Stanza) envelope.getPacket();

				boolean success = SessionManager.getInstance().write(stanza);
				
				if (success) {
					LOGGER.info("Packet is written successfully : " + stanza.xml());
				} else {
					LOGGER.info("Failed to write packet : " + stanza.xml());
					if (envelope.getPacket().getXmlElementName() == PacketXmlElement.MESSAGE) {
						sendPushNotification((Message) envelope.getPacket());
					}
				}

			} else if (envelope.getPacket() instanceof MimePacket) {
				writeMimePacket((MimePacket) envelope.getPacket());

			} else {
				LOGGER.warn("Unexpected packet received for processing. Ignoring...", envelope.getPacket());
			}

		} catch (Exception e) {
			LOGGER.error("Error while processing packet {}", envelope.getPacket(), e);
		}
	}

	private void writeMimePacket(MimePacket packet) {
		IOService<?, ?> mimeIOService = MIME_IOS_CACHE.get(packet.getDestination().getResource());
		mimeIOService.writePacket(packet);
	}

	/**
	 * A static utility method to be used by {@link ConnectionManager}s when a
	 * new connection is accepted and registered with {@link Selector}. The
	 * method instantiate a {@link IOService} for this {@link IOPortConnector}.
	 * It is the stream type inside {@link IOPortConnector} which helps
	 * determine which {@link IOService} implementation to be instantiated
	 * 
	 * @param connector
	 * @throws IOException
	 */
	public static void instantiateIOService(IOPortConnector connector) throws IOException {
		if (connector.getByteStream() == ByteStream.XMPP) {
			IOService<XMPPProtocol, XMPPPacket> ios = newBasicIOService(connector, newXMPPProtocol());
			LOGGER.info("Instantiated Xmpp io service : " + ios.getId());

		} else if (connector.getByteStream() == ByteStream.BOSH) {
			IOService<BOSHProtocol, BOSHBody> ios = newLongPollingIOService(connector, newtBoshProtocol());
			addToBoshIosCache(ios);
			LOGGER.info("Instantiated Bosh io service : " + ios.getId());

		} else if (connector.getByteStream() == ByteStream.MIME) {
			IOService<MimeProtocol, MimePacket> ios = newBasicIOService(connector, newMimeProtocol());
			addToMimeIosCache(ios);
			LOGGER.info("Instantiated Mime io service : " + ios.getId());
		}
	}

	/**
	 * Verify that {@link MimeProtocol} io-service is available or not
	 * 
	 * @param sid
	 * @return
	 */
	public boolean isMimeStreamAvailable(String sid) {
		return MIME_IOS_CACHE.get(sid) != null;
	}

	@Override
	public ServerFunction getServerFunction() {
		return ServerFunction.IO_CONTROL;
	}

}
