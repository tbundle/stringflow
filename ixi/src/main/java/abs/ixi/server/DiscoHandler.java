package abs.ixi.server;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.CoreComponent.ServerFunction;
import abs.ixi.server.common.InstantiationException;
import abs.ixi.server.etc.conf.Configurations;
import abs.ixi.server.packet.JID;
import abs.ixi.server.packet.Packet;
import abs.ixi.server.packet.PresenceSubscription;
import abs.ixi.server.packet.Roster;
import abs.ixi.server.packet.Roster.RosterItem;
import abs.ixi.server.packet.XMPPNamespaces;
import abs.ixi.server.packet.xmpp.BareJID;
import abs.ixi.server.packet.xmpp.IQ;
import abs.ixi.server.packet.xmpp.IQ.IQType;
import abs.ixi.server.packet.xmpp.IQContent.IQContentType;
import abs.ixi.server.packet.xmpp.IQErrorContent;
import abs.ixi.server.packet.xmpp.IQErrorContent.IQError;
import abs.ixi.server.packet.xmpp.IQPushRegistration;
import abs.ixi.server.packet.xmpp.IQQuery;
import abs.ixi.server.packet.xmpp.IQVCardContent;
import abs.ixi.server.packet.xmpp.Presence;
import abs.ixi.server.packet.xmpp.Presence.PresenceType;
import abs.ixi.server.packet.xmpp.UserProfileData;
import abs.ixi.server.packet.xmpp.UserRegistrationData;
import abs.ixi.server.packet.xmpp.UserSearchData;
import abs.ixi.server.packet.xmpp.UserSearchData.Item;
import abs.ixi.server.router.Router;
import abs.ixi.server.session.SessionManager;
import abs.ixi.server.sys.monitor.DiscoHandlerJmxBean;
import abs.ixi.server.sys.monitor.JmxRegistrar;
import abs.ixi.util.CollectionUtils;
import abs.ixi.util.StringUtils;
import abs.ixi.util.UUIDGenerator;

public class DiscoHandler extends PacketProducerConsumer {
	private static final Logger LOGGER = LoggerFactory.getLogger(DiscoHandler.class);

	private static final String COMPONENT_NAME = "disco";

	private static final String FIRST = "first";
	private static final String LAST = "last";
	private static final String NICK = "nick";
	private static final String EMAIL = "email";

	private String ioControllerJid;

	public DiscoHandler(Configurations config, Router router) throws InstantiationException {
		super(COMPONENT_NAME, config, router);

		try {
			JmxRegistrar.registerBean("abs.ixi.server.jmx:type=DiscoHandlerJmxBean",
					new DiscoHandlerJmxBean(this.inboundQ, this));

		} catch (MalformedObjectNameException | InstanceAlreadyExistsException | MBeanRegistrationException
				| NotCompliantMBeanException e) {
			throw new InstantiationException(e);
		}
	}

	@Override
	public void start() throws Exception {
		super.start();
		this.ioControllerJid = Stringflow.runtime().getCoreComponentJid(ServerFunction.IO_CONTROL);
	}

	@Override
	public void process(PacketEnvelope<? extends Packet> envelope) {
		IQ iqPacket = (IQ) envelope.getPacket();

		LOGGER.debug("Processing IQPacket {}", iqPacket.xml());

		try {
			if (iqPacket.getContent().getType() == IQContentType.QUERY) {
				IQQuery query = (IQQuery) iqPacket.getContent();

				String xmlns = query.getXmlns();

				if (StringUtils.safeEquals(xmlns, XMPPNamespaces.ROSTER_NAMESPACE)) {
					processRosterPacket(iqPacket);

				} else if (StringUtils.safeEquals(xmlns, XMPPNamespaces.JABBER_SEARCH_NAMESPACE)) {
					processJabberSearchPacket(iqPacket);

				} else if (StringUtils.safeEquals(xmlns, XMPPNamespaces.USER_REGISTER_NAMESPACE)) {
					processRegisterationPacket(iqPacket);
				}

			} else if (iqPacket.getContent().getType() == IQContentType.PUSH_REGISTRATION) {
				processIqPush(iqPacket, (IQPushRegistration) iqPacket.getContent());

			} else if (iqPacket.getContent().getType() == IQContentType.JINGLE) {
				processIqJingle(iqPacket);

			} else if (iqPacket.getContent().getType() == IQContentType.VCARD) {
				processIQVCard(iqPacket);
			}

			LOGGER.debug("IQ Packet {} has processed", iqPacket);

		} catch (Exception e) {
			LOGGER.error("Error while processing iqPacket : {} ", iqPacket.xml(), e);
		}

	}

	public void getRoster(IQ packet) {
		LOGGER.debug("processing get roster ...");

		BareJID userJID = packet.getFrom().getBareJID();

		IQQuery query = (IQQuery) packet.getContent();

		int previousVersion = query.getRoster().getVersion();
		int currrentVersion = dbService.getRosterVersion(userJID);

		if (previousVersion == 0) {
			Roster fullRoster = dbService.getUserFullRoster(userJID);

			if (fullRoster != null) {
				fullRoster.setVersion(currrentVersion);

				sendRosterResponse(packet.getId(), packet.getFrom(), fullRoster);

				// if (!CollectionUtils.isNullOrEmpty(fullRoster.getItems())) {
				// for (RosterItem item : fullRoster.getItems()) {
				// sendPresenceOfRoster(item, packet.getFrom());
				// }
				// }
			}

		} else {

			if (currrentVersion > previousVersion) {
				Roster roster = dbService.getRoster(userJID, previousVersion);
				roster.setVersion(currrentVersion);

				if (CollectionUtils.isNullOrEmpty(roster.getItems())) {
					// No roster item available response
					sendRosterResponse(packet.getId(), packet.getFrom(), roster);

				} else {

					// Roster success response
					sendSuccessIQ(packet.getId(), packet.getFrom());

					for (RosterItem item : roster.getItems()) {
						// Roster push
						sendSetRosterItem(packet.getId(), packet.getFrom(), item);
					}
				}

			} else {
				// roster success response
				sendSuccessIQ(packet.getId(), packet.getFrom());
			}

		}

	}

	public void updateRoster(IQ iqPacket, Roster roster) {
		if (CollectionUtils.isNullOrEmpty(roster.getItems())) {
			return;
		}

		BareJID userJID = iqPacket.getFrom().getBareJID();
		RosterItem rosterItem = roster.getItems().get(0);

		if (rosterItem.getJid() == null || !dbService.isUserExist(rosterItem.getJid().getBareJID())) {
			LOGGER.warn("User with jid {} doesn't exist in system, So Roster not updated");
			// TODO: Send roster update failure response
			return;
		}

		if (rosterItem.getSubscription() == PresenceSubscription.REMOVE) {
			removeSubscriber(rosterItem.getJid().getBareJID(), iqPacket.getFrom().getBareJID());

			dbService.deleteRosterItem(iqPacket.getFrom().getBareJID(), rosterItem.getJid().getBareJID(),
					getRosterItemName(iqPacket.getFrom().getBareJID(), rosterItem));

			// Sending unsubscribed presence to contact
			sendUnSubscribedPresence(iqPacket.getFrom().getBareJID(), rosterItem.getJid().getBareJID());

		} else {
			if (rosterItem.getSubscription() == PresenceSubscription.BOTH) {
				addSubscriber(rosterItem.getJid().getBareJID(), iqPacket.getFrom().getBareJID());
				addSubscriber(iqPacket.getFrom().getBareJID(), rosterItem.getJid().getBareJID());

			} else if (rosterItem.getSubscription() == PresenceSubscription.FROM) {
				addSubscriber(iqPacket.getFrom().getBareJID(), rosterItem.getJid().getBareJID());

			} else if (rosterItem.getSubscription() == PresenceSubscription.TO) {
				addSubscriber(rosterItem.getJid().getBareJID(), iqPacket.getFrom().getBareJID());

			}

			dbService.updateRoster(iqPacket.getFrom().getBareJID(), rosterItem.getJid().getBareJID(),
					getRosterItemName(iqPacket.getFrom().getBareJID(), rosterItem));

		}

		// Sending roster push
		int currentVersion = dbService.getRosterVersion(userJID);
		rosterItem.setItemVersion(currentVersion);
		sendSetRosterItem(UUIDGenerator.uuid(), iqPacket.getFrom().getBareJID().toJID(), rosterItem);

		// Sending success responce
		sendSuccessIQ(iqPacket.getId(), iqPacket.getFrom());

	}

	private String getRosterItemName(BareJID userJID, RosterItem item) {
		String itemName = item.getName();

		if (StringUtils.isNullOrEmpty(itemName)) {
			itemName = dbService.getUserRosterItemName(userJID, item.getJid().getBareJID());
		}

		if (StringUtils.isNullOrEmpty(itemName)) {
			itemName = dbService.getUserName(item.getJid().getBareJID());
		}

		if (StringUtils.isNullOrEmpty(itemName)) {
			itemName = item.getJid().getNode();
		}

		return itemName;
	}

	private void processJabberSearchPacket(IQ iqPacket) {
		if (iqPacket.getType() == IQType.GET) {
			IQ responseIQ = new IQ(iqPacket.getId(), IQType.RESULT);
			responseIQ.setTo(iqPacket.getFrom());

			// TODO: set From JABBER search service jid

			IQQuery query = new IQQuery(XMPPNamespaces.JABBER_SEARCH_NAMESPACE);
			UserSearchData searchData = new UserSearchData();
			searchData.setSendSearchAttributes(true);

			query.setUserSearchData(searchData);

			responseIQ.setContent(query);

			this.routePacketTOIOController(responseIQ);

		} else if (iqPacket.getType() == IQType.SET) {
			IQQuery query = (IQQuery) iqPacket.getContent();
			UserSearchData userSearchData = query.getUserSearchData();

			Map<String, String> searchParam = userSearchData.getSearchRequestData();

			Set<Item> users = searchUsers(searchParam);

			IQ responseIQ = new IQ(iqPacket.getId(), IQType.RESULT);
			responseIQ.setTo(iqPacket.getFrom());

			// TODO: set From JABBER search service jid

			IQQuery responseQuery = new IQQuery(XMPPNamespaces.JABBER_SEARCH_NAMESPACE);
			UserSearchData searchData = new UserSearchData();
			searchData.setSearchedItems(users);

			responseQuery.setUserSearchData(searchData);

			responseIQ.setContent(responseQuery);

			this.routePacketTOIOController(responseIQ);
		}
	}

	private Set<Item> searchUsers(Map<String, String> searchParam) {
		Set<Item> items = new HashSet<>();

		if (searchParam != null) {

			for (Entry<String, String> entry : searchParam.entrySet()) {
				if (StringUtils.safeEquals(entry.getKey(), FIRST)) {
					List<Item> matchedUsers = dbService.serchUserByFirstName(entry.getValue());

					if (!CollectionUtils.isNullOrEmpty(matchedUsers)) {
						items.addAll(matchedUsers);
					}

				} else if (StringUtils.safeEquals(entry.getKey(), LAST)) {
					List<Item> matchedUsers = dbService.serchUserByLastName(entry.getValue());

					if (!CollectionUtils.isNullOrEmpty(matchedUsers)) {
						items.addAll(matchedUsers);
					}

				} else if (StringUtils.safeEquals(entry.getKey(), NICK)) {
					List<Item> matchedUsers = dbService.serchUserByNickName(entry.getValue());

					if (!CollectionUtils.isNullOrEmpty(matchedUsers)) {
						items.addAll(matchedUsers);
					}

				} else if (StringUtils.safeEquals(entry.getKey(), EMAIL)) {
					List<Item> matchedUsers = dbService.serchUserByEmail(entry.getValue());

					if (!CollectionUtils.isNullOrEmpty(matchedUsers)) {
						items.addAll(matchedUsers);
					}
				}
			}
		}

		return items;
	}

	private void processIQVCard(IQ iqPacket) {
		if (iqPacket.getType() == IQType.SET) {
			IQVCardContent vcard = (IQVCardContent) iqPacket.getContent();
			UserProfileData userProfile = vcard.getUserData();
			userProfile.setJabberId(iqPacket.getFrom().getBareJID());

			this.dbService.updateUserProfile(userProfile);

			sendSuccessIQ(iqPacket.getId(), iqPacket.getFrom());

		} else if (iqPacket.getType() == IQType.GET) {
			UserProfileData userProfileData;

			if (iqPacket.getTo() != null && !iqPacket.getTo().equals(Stringflow.runtime().jid())) {
				userProfileData = this.dbService.getUserProfile(iqPacket.getTo().getBareJID());

			} else {
				userProfileData = this.dbService.getUserProfile(iqPacket.getFrom().getBareJID());
			}

			if (userProfileData != null) {
				IQ iq = new IQ(iqPacket.getId(), IQType.RESULT);
				iq.setTo(iqPacket.getFrom());
				iq.setFrom(userProfileData.getJabberId().toJID());

				IQVCardContent vcard = new IQVCardContent();
				vcard.setUserData(userProfileData);

				iq.setContent(vcard);

				routePacketTOIOController(iq);

			} else {
				sendErrorIQ(iqPacket.getId(), IQError.SERVICE_UNAVAILABLE_ERROR);
			}
		}
	}

	private void sendErrorIQ(String iqId, IQError error) {
		IQ iq = new IQ(iqId, IQType.ERROR);
		iq.setFrom(Stringflow.runtime().jid());

		IQErrorContent errorContent = new IQErrorContent(error);
		iq.setContent(errorContent);

		routePacketTOIOController(iq);
	}

	private void processIqJingle(IQ iqPacket) {
		// IQJingle jingle = (IQJingle) iqPacket.getContent();
		// Do IF something needed
		routePacketTOIOController(iqPacket);
	}

	private void processIqPush(IQ packet, IQPushRegistration pushRegistration) {
		if (pushRegistration.isRemoved()) {
			dbService.discardDeviceToken(packet.getFrom().getBareJID(), pushRegistration.getDeviceToken());

		} else {
			dbService.updateDeviceTocken(packet.getFrom().getBareJID(), pushRegistration.getDeviceId(),
					pushRegistration.getDeviceToken(), pushRegistration.getService().name(),
					pushRegistration.getDeviceType().name());
		}

		sendSuccessIQ(packet.getId(), packet.getFrom());
	}

	private void processRegisterationPacket(IQ iqPacket) {
		LOGGER.debug("processing user registration iq packet ...");
		IQType type = iqPacket.getType();

		if (type == IQType.SET) {
			IQQuery query = (IQQuery) iqPacket.getContent();

			UserRegistrationData userRegistrationData = query.getUserRegistrationData();

			if (userRegistrationData.isRemove()) {
				unRegisterUser(iqPacket.getFrom().getBareJID());
				sendSuccessIQ(iqPacket.getId(), iqPacket.getFrom());

			} else if (!StringUtils.isNullOrEmpty(userRegistrationData.getPassword())) {
				changePassword(iqPacket.getFrom().getBareJID(), userRegistrationData.getPassword());
				sendSuccessIQ(iqPacket.getId(), iqPacket.getFrom());
			}

		} else if (type == IQType.GET) {
			UserRegistrationData userRegistrationData = getUserRegistrationInfo(iqPacket.getFrom().getBareJID());
			sendUserRegistrationInfoIQ(iqPacket.getId(), iqPacket.getFrom(), userRegistrationData);
		}
	}

	private void changePassword(BareJID bareJID, String password) {
		this.dbService.changeUserPassword(bareJID, password);
	}

	private void unRegisterUser(BareJID userJID) {
		this.dbService.unRegisterUser(userJID);
	}

	private UserRegistrationData getUserRegistrationInfo(BareJID userJID) {
		UserRegistrationData userRegistrationData = dbService.getUserRegistrationInfo(userJID);
		userRegistrationData.setJabberId(userJID);
		userRegistrationData.setUserName(userJID.getNode());

		return userRegistrationData;
	}

	private void processRosterPacket(IQ iqPacket) {
		LOGGER.debug("processing roster iq packet ...");

		IQQuery query = (IQQuery) iqPacket.getContent();
		Roster roster = query.getRoster();

		if (roster != null) {
			IQType type = iqPacket.getType();

			if (type == IQType.SET) {
				updateRoster(iqPacket, query.getRoster());

			} else if (type == IQType.GET) {
				getRoster(iqPacket);
			}
		}
	}

	private void sendRosterResponse(String iqId, JID toJID, Roster roster) {
		IQQuery query = new IQQuery(XMPPNamespaces.ROSTER_NAMESPACE);
		query.setRoster(roster);

		IQ iq = new IQ(iqId, IQType.RESULT, query);
		iq.setTo(toJID);
		iq.setFrom(Stringflow.runtime().jid());

		routePacketTOIOController(iq);
	}

	private void sendSuccessIQ(String iqId, JID toJID) {
		IQ iq = new IQ(iqId, IQType.RESULT);
		iq.setTo(toJID);
		iq.setFrom(Stringflow.runtime().jid());

		routePacketTOIOController(iq);
	}

	private void sendSetRosterItem(String iqId, JID to, RosterItem item) {
		IQ iq = new IQ(iqId, IQType.SET);
		iq.setTo(to);
		iq.setFrom(Stringflow.runtime().jid());

		Roster roster = new Roster();
		roster.setVersion(item.getItemVersion());
		roster.addItem(item);

		IQQuery query = new IQQuery(XMPPNamespaces.ROSTER_NAMESPACE);
		query.setRoster(roster);

		iq.setContent(query);

		routePacketTOIOController(iq);
	}

	private void sendUserRegistrationInfoIQ(String iqId, JID toJID, UserRegistrationData useData) {
		IQQuery query = new IQQuery(XMPPNamespaces.USER_REGISTER_NAMESPACE);
		query.setUserRegistrationData(useData);

		IQ iq = new IQ(iqId, IQType.RESULT, query);
		iq.setTo(toJID);
		iq.setFrom(Stringflow.runtime().jid());

		routePacketTOIOController(iq);
	}

	private void sendUnSubscribedPresence(BareJID fromJID, BareJID toJID) {
		Presence presence = new Presence();
		presence.setType(PresenceType.UNSUBSCRIBED);
		presence.setTo(toJID.toJID());
		presence.setFrom(fromJID.toJID());

		routePacketTOIOController(presence);
	}

	private void routePacketTOIOController(Packet packet) {
		PacketEnvelope<Packet> envelope = new PacketEnvelope<Packet>(packet, this.getName());
		envelope.setDestinationComponent(this.ioControllerJid);
		increaseOutboundCount();
		router.route(envelope);
	}

	public void addSubscriber(BareJID userJID, BareJID subscriberJID) {
		SessionManager.getInstance().addUserPresenceSubscriber(userJID, subscriberJID);
		dbService.addPresenceSubscriber(userJID, subscriberJID);
	}

	public void removeSubscriber(BareJID userJID, BareJID subscriberJID) {
		SessionManager.getInstance().removeUserPresenceSubscriber(userJID, subscriberJID);
		dbService.deletePresenceSubscription(userJID, subscriberJID);
	}

	@Override
	public void shutdown() throws Exception {
		LOGGER.info("Shutting down {} component", getName());
		super.shutdown();
	}

}
