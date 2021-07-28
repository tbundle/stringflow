package abs.ixi.server.session;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.CoreComponent.ServerFunction;
import abs.ixi.server.PacketEnvelope;
import abs.ixi.server.PacketProducerConsumer;
import abs.ixi.server.Stringflow;
import abs.ixi.server.common.InstantiationException;
import abs.ixi.server.etc.conf.Configurations;
import abs.ixi.server.packet.JID;
import abs.ixi.server.packet.Packet;
import abs.ixi.server.packet.Roster;
import abs.ixi.server.packet.Roster.RosterItem;
import abs.ixi.server.packet.xmpp.BareJID;
import abs.ixi.server.packet.xmpp.Presence;
import abs.ixi.server.packet.xmpp.XMPPPacket;
import abs.ixi.server.packet.xmpp.Presence.PresenceType;
import abs.ixi.server.router.Router;
import abs.ixi.server.sys.monitor.JmxRegistrar;
import abs.ixi.server.sys.monitor.PresenceManagerJmxbean;
import abs.ixi.util.CollectionUtils;

public class PresenceManager extends PacketProducerConsumer {
	private static final Logger LOGGER = LoggerFactory.getLogger(PresenceManager.class);

	private static final String COMPONENT_NAME = "presence-manager";

	public PresenceManager(Configurations config, Router router) throws InstantiationException {
		super(COMPONENT_NAME, config, router);
	}

	@Override
	public void start() throws Exception {
		super.start();

		JmxRegistrar.registerBean("abs.ixi.server.presence:type=PresenceManagerJmxBean",
				new PresenceManagerJmxbean(this.inboundQ, this));

		LOGGER.debug("Presence Manager started");
	}

	@Override
	public void process(PacketEnvelope<? extends Packet> envelope) {
		try {
			XMPPPacket packet = (XMPPPacket) envelope.getPacket();

			LOGGER.debug("Processing packet {}", packet.xml());

			if (Presence.class.isAssignableFrom(packet.getClass())) {
				Presence presencePacket = (Presence) packet;

				if (presencePacket.isMuc()) {
					return;
				}

				if (presencePacket.isInitialPresence()) {
					SessionManager.getInstance().handleUserInitialPresence(presencePacket.getFrom());
					sendRosterPresence(presencePacket.getFrom());
				}

				processPresencePacket(presencePacket);
			}

		} catch (Exception e) {
			LOGGER.error("Failed to process envelope {}", envelope.getPacket(), e);
		}
	}

	private void sendRosterPresence(JID userJID) throws Exception {
		Roster fullRoster = dbService.getUserFullRoster(userJID.getBareJID());
		if (!CollectionUtils.isNullOrEmpty(fullRoster.getItems())) {
			for (RosterItem item : fullRoster.getItems()) {
				sendPresenceOfRoster(item, userJID);
			}
		}
	}

	private void sendPresenceOfRoster(RosterItem rosterItem, JID userJID) throws Exception {
		Presence presence = new Presence();
		presence.setFrom(rosterItem.getJid());
		presence.setTo(userJID);

		if (SessionManager.getInstance().isUserOnline(rosterItem.getJid())) {
			presence.setType(PresenceType.AVAILABLE);

		} else {
			presence.setType(PresenceType.UNAVAILABLE);
		}

		sendPresence(presence, userJID.getBareJID());
	}

	public void addSubscriber(BareJID userJID, BareJID subscriberJID) {
		SessionManager.getInstance().addUserPresenceSubscriber(userJID, subscriberJID);
		dbService.addPresenceSubscriber(userJID, subscriberJID);
	}

	public void removeSubscriber(BareJID userJID, BareJID subscriberJID) {
		SessionManager.getInstance().removeUserPresenceSubscriber(userJID, subscriberJID);
		dbService.deletePresenceSubscription(userJID, subscriberJID);
	}

	public List<BareJID> getSubscribers(BareJID userJID) {
		List<BareJID> subscribers = SessionManager.getInstance().getUserPresenceSubscribers(userJID);

		if (!CollectionUtils.isNullOrEmpty(subscribers)) {
			return subscribers;

		} else {
			return Collections.<BareJID>emptyList();
		}

	}

	private void processPresencePacket(Presence presence) throws Exception {
		if (PresenceType.SUBSCRIBE == presence.getType()) {
			// TODO: handle it

		} else if (PresenceType.UNSUBSCRIBE == presence.getType()) {
			// TODO: handle it

		} else if (PresenceType.SUBSCRIBED == presence.getType()) {
			// TODO: handle it

		} else if (PresenceType.UNSUBSCRIBED == presence.getType()) {
			// TODO: handle it

		} else {
			if (presence.getTo() == null) {
				sendPresenceToSubscribers(presence);

			} else {
				Presence userPresence = (Presence) presence.clone();
				sendPresence(userPresence, presence.getTo().getBareJID());
			}

			sendPresence(presence, presence.getFrom().getBareJID());
		}

	}

	public void sendPresenceToSubscribers(Presence presence) throws Exception {
		List<BareJID> subscribers = getSubscribers(presence.getFrom().getBareJID());

		if (!CollectionUtils.isNullOrEmpty(subscribers)) {

			for (BareJID subcriber : subscribers) {
				Presence userPresence = (Presence) presence.clone();
				sendPresence(userPresence, subcriber);
			}
		}
	}

	private void sendPresence(Presence presence, BareJID to) {
		presence.setTo(to.toJID());
		PacketEnvelope<Packet> envelope = new PacketEnvelope<Packet>(presence, this.getName());
		envelope.setDestinationComponent(Stringflow.runtime().getCoreComponentJid(ServerFunction.IO_CONTROL));

		router.route(envelope);
	}

	@Override
	public void shutdown() throws Exception {
		LOGGER.info("Shutting down {} component", getName());
		super.shutdown();
	}

}
