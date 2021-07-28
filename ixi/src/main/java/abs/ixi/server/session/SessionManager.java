package abs.ixi.server.session;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.ServerStartupException;
import abs.ixi.server.Stringflow;
import abs.ixi.server.common.InitializationException;
import abs.ixi.server.common.InstantiationException;
import abs.ixi.server.etc.PersistenceService;
import abs.ixi.server.packet.JID;
import abs.ixi.server.packet.Packet;
import abs.ixi.server.packet.XMPPNamespaces;
import abs.ixi.server.packet.Packet.PacketXmlElement;
import abs.ixi.server.packet.xmpp.BareJID;
import abs.ixi.server.packet.xmpp.Message;
import abs.ixi.server.packet.xmpp.MessageDelay;
import abs.ixi.server.packet.xmpp.Presence;
import abs.ixi.server.packet.xmpp.Stanza;
import abs.ixi.server.packet.xmpp.Presence.PresenceType;
import abs.ixi.util.CollectionUtils;

/**
 * {@code SessionManager} is a custodian class for user sessions within server.
 * It manages complete life-cycle of {@link UserSession}s.
 * <p>
 * {@code SessionManager} is a singleton.
 * </p>
 * 
 * @author Yogi
 *
 */
public final class SessionManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(SessionManager.class);

	/**
	 * An in-memory data structure to store {@link UserSession}s instances
	 */
	private final DefaultSessionStore sessionStore;

	/**
	 * {@link DbService} instance
	 */
	private final PersistenceService dbService;

	/**
	 * Singleton instance of the {@code SessionManager}
	 */
	private static SessionManager instance;

	/**
	 * {@link AckTimerWheel} instance to track ACK timeouts from clients
	 */
	private AckTimerWheel ackTimerWheel;

	/**
	 * {@link SessionWatchdog} instance to manage sessions.
	 */
	private SessionWatchdog sessionWatchdog;

	SessionManager() throws InstantiationException {
		this.dbService = PersistenceService.getInstance();
		this.sessionStore = new DefaultSessionStore();
		this.ackTimerWheel = new AckTimerWheel();
		startSessionWatchDog();
	}

	private void startSessionWatchDog() {
		try {
			this.sessionWatchdog = new SessionWatchdog();
			this.sessionWatchdog.init();
			this.sessionWatchdog.start();
		} catch (InitializationException e) {
			LOGGER.error("Failed to initilize session watchdog.", e);
		} catch (ServerStartupException e) {
			LOGGER.error("Failed to start session watchdog.", e);
		}

	}

	public static SessionManager getInstance() {
		if (instance == null) {
			synchronized (SessionManager.class) {
				if (instance == null) {
					try {
						instance = new SessionManager();
					} catch (InstantiationException e) {
						// TODO throw a RuntimeException
						LOGGER.error("Failed to instantiate Session Manager", e);
					}
				}
			}
		}

		return instance;
	}

	/**
	 * 
	 * Written {@link AckTimerWheel} for ack waiting stanzas
	 * 
	 * @return
	 */
	public AckTimerWheel getAckTimerWheel() {
		return this.ackTimerWheel;
	}

	/**
	 * It authenticate user session with the help up {@link DbService}.
	 * 
	 * @param name
	 * @param passwd
	 * @return
	 */
	public boolean authenticate(String name, String passwd) {
		return dbService.authenticate(new BareJID(name, Stringflow.runtime().domain()), passwd);
	}

	/**
	 * Stream resumption requires copying various counters from one
	 * {@link LocalSession} to another {@link LocalSession}. If stream resumed
	 * successfully it also cache new {@link LocalSession} in
	 * {@link DefaultSessionStore} and also log this session in db.
	 * 
	 * @param userJID
	 * @param newSession
	 * @param prevStreamId
	 * @return
	 */
	public boolean resumeSession(LocalSession newSession, String prevStreamId, long prevStreamAckCount) {
		LOGGER.debug("Resuming stream for user {} with previous streamId {}", newSession.getUserJID(), prevStreamId);

		UserSession us = sessionStore.get(newSession.getUserJID());

		if (us != null) {
			boolean resumed = us.resumeSession(newSession, prevStreamId, prevStreamAckCount);

			if (resumed) {
				logSessionToDB(newSession);
			}

			return resumed;
		}

		LOGGER.info("No user session found for user jid {}", newSession.getUserJID());

		return false;
	}

	/**
	 * Binds New {@link LocalSession}. In binding process it will cache this new
	 * {@link LocalSession} in {@link DefaultSessionStore} And log this session
	 * in {@link Database#}
	 * 
	 * @param ls
	 * @return
	 */
	public boolean bindSession(LocalSession ls) {
		LOGGER.debug("Binds the local session for user {} and sessionId {} and streamId {}", ls.getUserJID(),
				ls.getSessionId(), ls.getSessionStreamId());

		UserSession us = this.getUserSession(ls.getUserJID());
		us.bindSession(ls);
		logSessionToDB(ls);
		return true;
	}

	/**
	 * Returns {@link UserSession} for given user. If there is already a
	 * {@link UserSession} instance in session cache for the bareJID then given
	 * {@link BareJID} otherwise a new {@link UserSession} instance will be
	 * created. Thats why this method is kept Synchronised.
	 * 
	 * @param userJID user {@link BareJID}
	 * @return {@link UserSession}
	 */
	private synchronized UserSession getUserSession(BareJID userJID) {
		UserSession us = sessionStore.get(userJID);

		if (us == null) {
			us = new UserSession(userJID, dbService.getUndeliveredStanzas(userJID),
					new UserPresence(dbService.getPresenceSubscribers(userJID)));

			sessionStore.store(us);
		}

		return us;
	}

	/**
	 * Log session to database using {@link DbService}.
	 * 
	 * @param userJID
	 * @param resourceId
	 * @param remoteAddress
	 */
	private void logSessionToDB(LocalSession ls) {
		dbService.persistUserSession(ls.getUserJID(), ls.getSessionId());
	}

	/**
	 * Log session to database using {@link DbService}.
	 * 
	 * @param userJID
	 * @param resourceId
	 * @param remoteAddress
	 */
	private void logoutSessionFromDB(LocalSession ls) {
		this.logoutSessionFromDB(ls.getUserJID(), ls.getSessionId());
	}

	/**
	 * Log session to database using {@link DbService}.
	 * 
	 * @param userJID
	 * @param resourceId
	 * @param remoteAddress
	 */
	private void logoutSessionFromDB(BareJID bareJID, String resource) {
		dbService.sessionLogout(bareJID, resource);
	}

	/**
	 * Destroy {@link UserSession} for the given bare JID. The action will
	 * remove {@link UserSession} instance from session cache, invoke
	 * {@link UserSession#destroy()} which intern will call a
	 * {@link LocalSession#destroy()}.
	 * <p>
	 * Additionally, the method will change user session status in persistence
	 * layer (database)and will broadcast user presence to its listeners.
	 * </p>
	 * 
	 * @param us {@link UserSession} to be destroyed
	 */
	private void destroyUserSession(BareJID userBareJID) {
		LOGGER.info("Destroying user session for {}", userBareJID);
		UserSession us = sessionStore.remove(userBareJID);

		us.destroy((v) -> {
			try {

				this.logoutSessionFromDB(v);

			} catch (Exception e) {
				LOGGER.warn("Exception caught while removing local session from db ", e);
			}
		});

		LOGGER.info("Persisting undelivered stanzas for {} ", userBareJID);
		us.persistUndeliveredStanzasToDb(this.dbService);
	}

	// TODO: offline presence sending code consider it later.
	private void sendOfflinePresence(BareJID userBareJID) {
		LOGGER.info("Sending offline presence for {} to subscribers", userBareJID);

		List<BareJID> subscribers = getUserPresenceSubscribers(userBareJID);

		if (!CollectionUtils.isNullOrEmpty(subscribers)) {
			Presence presence = createUnavailablePresence(userBareJID);

			subscribers.forEach((v) -> {
				UserSession us = sessionStore.get(v);

				if (us != null) {
					// TODO: manage it later
					// us.sendPresence(presence);
				}

			});
		}
	}

	private Presence createUnavailablePresence(BareJID userBareJID) {
		Presence presence = new Presence(PresenceType.UNAVAILABLE);
		presence.setFrom(userBareJID.toJID());
		return presence;
	}

	/**
	 * Clear All {@link UserSession} from session store.
	 */
	private void clearAllSesssion() {
		LOGGER.info("Shutting down Session Manager");

		sessionStore.purgeAll(new Consumer<UserSession>() {

			@Override
			public void accept(UserSession us) {
				destroyUserSession(us.getBareJID());
			}
		});

		LOGGER.info("Session Manager has been shutdown");
	}

	// TODO Implement it correctly; read the comments.
	public void shutdown() {
		LOGGER.info("Shutting down Session Manager");
		// TODO Shutting down session manager is tricky because we need to send
		// offline presence to all the clients; however, to do that we need
		// sessions alive. To implement it correctly, I think server shutdown
		// hook must stop inbound traffic first in ServerIO and then we can just
		// send offline presence to all the active session. post which we can
		// simply purge all the sessions after db update
		clearAllSesssion();
		LOGGER.info("Session Manager has been shutdown");
	}

	/**
	 * Check user session available or not.
	 * 
	 * @param jid
	 * @return
	 */
	public boolean isUserOnline(JID jid) {
		UserSession us = sessionStore.get(jid.getBareJID());

		if (us != null)
			return us.hasActiveLocalSession();

		return false;
	}

	/**
	 * Add presence subscribers to user session cache.
	 * 
	 * @param userJID
	 * @param subscriberJID
	 */
	public void addUserPresenceSubscriber(BareJID userJID, BareJID subscriberJID) {
		UserSession session = sessionStore.get(userJID);

		if (session != null) {
			session.addPresenceSubscriber(subscriberJID);
		}
	}

	/**
	 * Remove user presence subscribers from user session cache.
	 * 
	 * @param userJID
	 * @param subscriberJID
	 */
	public void removeUserPresenceSubscriber(BareJID userJID, BareJID subscriberJID) {
		UserSession session = sessionStore.get(userJID);

		if (session != null) {
			session.removePresenceSubscriber(subscriberJID);
		}
	}

	/**
	 * Returns Presence Subscribers list from user session cache.
	 * 
	 * @param userBareJID
	 * @return
	 */
	public List<BareJID> getUserPresenceSubscribers(BareJID userBareJID) {
		UserSession userSession = sessionStore.get(userBareJID);

		if (userSession != null) {
			return userSession.getPresenceSubscribers();
		}

		return null;
	}

	/**
	 * To update user initial presence status.
	 * 
	 * @param userJID
	 */
	public void handleUserInitialPresence(JID userJID) {
		UserSession us = this.sessionStore.get(userJID.getBareJID());

		if (us != null) {
			us.handleUserInitialPresence(userJID.getResource());
		}
	}

	/**
	 * Check user Session exists or not for given user jid and stream id
	 * 
	 * @param bareJID
	 * @param streamId
	 * @return
	 */
	public boolean isUserStreamExists(BareJID bareJID, String streamId) {
		UserSession us = sessionStore.get(bareJID);

		if (us != null)
			return us.isStreamExists(streamId);

		return false;
	}

	// TODO: Calling should directly use dbService to store media. need to
	// remove method from here.
	/**
	 * Storing media to db
	 * 
	 * @param senderJID
	 * @param receiverJID
	 * @param mediaName
	 */
	public void storeMedia(JID senderJID, JID receiverJID, String mediaName) {
		this.dbService.storeMedia(mediaName, senderJID.getBareJID(), receiverJID.getBareJID());
	}

	/**
	 * Write a {@link Packet} on to a {@link UserSession}. This is an outgoing
	 * packet which needs to be written on to socket. The packet is handed-over
	 * to the intended {@link UserSession}. It is further forwarded to
	 * appropriate {@link LocalSession} instances.
	 * 
	 * @param stanza outgoing stanza
	 * 
	 * @return true if a {@link UserSession} was found otherwise false
	 */
	public boolean write(Stanza stanza) {
		UserSession us = this.sessionStore.get(stanza.getDestination().getBareJID());

		if (us != null) {
			return us.write(stanza);

		} else {
			LOGGER.debug("No user session was found for stanza {}", stanza.xml());
			if (stanza.isInsurancedDeliveryRequired()) {
				addDelayContent(stanza);
				this.dbService.persistUndeliverdStanza(stanza);
			}
		}

		return false;
	}

	private void addDelayContent(Stanza stanza) {
		if (stanza.getXmlElementName() == PacketXmlElement.MESSAGE) {
			Message message = (Message) stanza;

			if (message.isNotifyableMessage() && !message.haveDelayContent()) {
				MessageDelay delay = new MessageDelay(XMPPNamespaces.DELAY_NAMESPACE);
				delay.setFrom(message.getFrom().getFullJID());
				message.addContent(delay);
			}
		}
	}

	/**
	 * Verify {@link LocalSession}s which have been inactive for if they are
	 * still active.
	 * <p>
	 * This method is used by {@link SessionWatchdog}. Use of this method for
	 * any other purpose is prohibited
	 * </p>
	 * 
	 * @param inactivityThreshold
	 */
	public void runSessionCleanup(long inactivityThreshold) {
		long sysTime = System.currentTimeMillis();

		this.sessionStore.forEach(new Consumer<UserSession>() {

			@Override
			public void accept(final UserSession us) {
				LOGGER.info("Verifying for user session : {} ", us.getBareJID());

				us.forEachLocalSession(new BiConsumer<UserSession, LocalSession>() {

					@Override
					public void accept(UserSession us, LocalSession ls) {
						LOGGER.info("Verifying for LocalSession : {} for user {}", ls, us.getBareJID());

						if (ls.isInactive()) {
							if (ls.isSessionStreamResumable()) {
								LOGGER.info("Local Session : {} is inactive, and resumable so, archiving it.", ls);
								ls.archive();

							} else {
								LOGGER.info("Local Session : {} is inactive, and not resumable so, removing it.", ls);
								us.removeLocalSession(ls);
								logoutSessionFromDB(ls);

							}

						} else if ((sysTime - ls.getLastActivityTime()) > inactivityThreshold) {
							LOGGER.info("LocalSession {} ideal activity time is expired, so triggering varification...",
									ls);
							ls.triggerVerification();
						}

						if (us.getLocalSessions().size() == 0) {
							LOGGER.info("No LocalSession Available for userSession of {}, So destroying it",
									us.getBareJID());
							destroyUserSession(us.getBareJID());
						}
					}

				});
			}
		});

	}

}
