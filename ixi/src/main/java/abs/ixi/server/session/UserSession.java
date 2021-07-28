package abs.ixi.server.session;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.connection.Connection;

import abs.ixi.server.common.SimpleQueue;
import abs.ixi.server.etc.PersistenceService;
import abs.ixi.server.packet.XMPPNamespaces;
import abs.ixi.server.packet.Packet.PacketXmlElement;
import abs.ixi.server.packet.xmpp.BareJID;
import abs.ixi.server.packet.xmpp.Message;
import abs.ixi.server.packet.xmpp.MessageDelay;
import abs.ixi.server.packet.xmpp.Presence;
import abs.ixi.server.packet.xmpp.Stanza;
import abs.ixi.util.StringUtils;

/**
 * A user can login from multiple devices at the same time; each login will
 * create a {@link LocalSession} in server. {@code UserSession} is a container
 * for all these local sessions for a single user.
 * <p>
 * Apart from managing user sessions, {@code UserSession} also manages message
 * delivery offsets and user presence.
 * </p>
 */
public class UserSession implements Serializable {
	private static final long serialVersionUID = 2388208530972905718L;

	private static final Logger LOGGER = LoggerFactory.getLogger(UserSession.class);

	/**
	 * String value of user {@link BareJID}. There could be multiple resources
	 * associate with same user barejid. ResourceId is maintained at
	 * {@link Connection} level.
	 */
	private BareJID bareJID;

	/**
	 * Most recent {@link UserPresence} instance for this Use
	 */
	private UserPresence presence;

	/**
	 * List of {@link LocalSession}s for this user. Each time a user logs-in
	 * from a different device, it generates a {@link LocalSession} in server
	 * and gets attached to existing {@link UserSession}
	 */
	private CopyOnWriteArrayList<LocalSession> localSessions;

	/**
	 * Any packet which is not bound destined to a {@link LocalSession} but to a
	 * {@link BareJID} is queued up here. These are also the packets which needs
	 * to be re-delivered if required.
	 */
	private transient SimpleQueue<Stanza> outboundPacketQ;

	public UserSession(BareJID bareJID, List<Stanza> undeliveredStanzas, UserPresence presence) {
		this.bareJID = bareJID;
		this.presence = presence;
		this.localSessions = new CopyOnWriteArrayList<>();
		this.outboundPacketQ = new SimpleQueue<>(undeliveredStanzas);
	}

	public UserSession(BareJID bareJID, SimpleQueue<Stanza> undeliveredStanzas, UserPresence presence) {
		this.bareJID = bareJID;
		this.presence = presence;
		this.localSessions = new CopyOnWriteArrayList<>();
		this.outboundPacketQ = undeliveredStanzas == null ? new SimpleQueue<>() : undeliveredStanzas;
	}

	public BareJID getBareJID() {
		return this.bareJID;
	}

	public UserPresence getUserPresence() {
		return this.presence;
	}

	public void setPresence(UserPresence presence) {
		this.presence = presence;
	}

	/**
	 * Returns user {@link LocalSession} belonging to the given resourceId
	 */
	public LocalSession getLocalSessionBySessionId(String sessionId) {
		for (LocalSession session : localSessions) {
			if (StringUtils.safeEquals(session.getSessionId(), sessionId)) {
				return session;
			}
		}

		return null;
	}

	/**
	 * Returns user {@link LocalSession} belonging to the given streamId
	 */
	public LocalSession getLocalSessionByStreamId(String streamId) {
		for (LocalSession session : localSessions) {
			if (StringUtils.safeEquals(session.getSessionStreamId(), streamId)) {
				return session;
			}
		}

		return null;
	}

	public List<LocalSession> getLocalSessions() {
		return this.localSessions.subList(0, localSessions.size());
	}

	/**
	 * Removing {@link LocalSession} from cache
	 * 
	 * @param ls
	 * @return
	 */
	public boolean removeLocalSession(LocalSession ls) {
		return localSessions.remove(ls);
	}

	/**
	 * Remove {@link LocalSession} instance from this {@code UserSession}.
	 * Please note, removing a {@link LocalSession} does not mean destroying it.
	 * However, this {@code UserSession} does updates the Presence for the user
	 * assuming that removed {@link LocalSession} will be destroyed by the
	 * caller.
	 * 
	 * @param resourceId of the {@link LocalSession}
	 * @return removed {@link LocalSession} instance
	 */
	public LocalSession removeLocalSesion(String sessionId) {
		for (LocalSession ls : this.localSessions) {
			if (StringUtils.safeEquals(sessionId, ls.getSessionId())) {
				this.localSessions.remove(ls);
				return ls;
			}
		}

		return null;
	}

	/**
	 * @return number of {@link LocalSession} instances held by this
	 *         {@code UserSession}
	 */
	public int localSessionCount() {
		return this.localSessions.size();
	}

	/**
	 * @return true if there is no {@link LocalSession} instance held by this
	 *         {@link UserSession}; Ideally such {@link UserSession}s must be
	 *         destroyed.
	 */
	public boolean isEmpty() {
		return this.getLocalSessions().size() == 0 ? true : false;
	}

	/**
	 * Perform given action on each of the {@link LocalSession}s held by this
	 * {@code UserSession}
	 * 
	 * @param consumer
	 */
	public void forEach(Consumer<LocalSession> consumer) {
		this.localSessions.forEach(consumer);
	}

	/**
	 * Perform given action on each of the {@link LocalSession}s held by this
	 * {@code UserSession}
	 * 
	 * @param consumer
	 */
	public void forEachLocalSession(BiConsumer<UserSession, LocalSession> consumer) {
		for (LocalSession ls : this.localSessions) {
			consumer.accept(this, ls);
		}
	}

	public void addPresenceSubscriber(BareJID subscriberJID) {
		if (this.presence != null) {
			presence.addPresenceSubscriber(subscriberJID);
		}
	}

	public void removePresenceSubscriber(BareJID subscriberJID) {
		if (this.presence != null) {
			presence.removePresenceSubscriber(subscriberJID);
		}
	}

	public List<BareJID> getPresenceSubscribers() {
		if (this.presence != null) {
			return this.presence.getSubscribers();
		}

		return null;
	}

	/**
	 * On Destroying {@link UserSession}. {@link SessionManager} sends offline
	 * presence to it's presence subscribers by this method.
	 * 
	 * @param presence
	 */
	private void sendPresence(Presence presence) {
		this.localSessions.forEach((v) -> v.write(presence));
	}

	/**
	 * @return true if there is any active {@link LocalSession} instance held by
	 *         this {@code UserSession} otherwise false
	 */
	public boolean hasActiveLocalSession() {
		if (this.localSessions.size() > 0) {

			for (LocalSession ls : this.localSessions) {
				if (!ls.isDead())
					return true;
			}

		}

		return false;
	}

	/**
	 * It update user online status and mark user {@link LocalSession} ready on
	 * initial presence receive.
	 * 
	 * @param sessionId
	 */
	public void handleUserInitialPresence(String sessionId) {
		this.presence.setOnline(true);

		LocalSession ls = this.getLocalSessionBySessionId(sessionId);

		if (ls != null) {
			ls.markSessionReady();
		}
	}

	/**
	 * Add new {@link LocalSession} with this {@link UserSession}. And copy un
	 * delivered stanzas to new {@link LocalSession}
	 * 
	 * @param newSession
	 */
	public void bindSession(LocalSession newSession) {
		synchronized (this.outboundPacketQ) {
			newSession.addToOutboundStanzaQueue(this.outboundPacketQ);
			newSession.setParentSession(this);
			this.localSessions.add(newSession);
		}
	}

	/**
	 * Stream resumption requires copying various counters from one
	 * {@link LocalSession} to another {@link LocalSession}
	 * 
	 * @param newSession
	 * @param prevStreamId
	 * @return
	 */
	public boolean resumeSession(LocalSession newSession, String prevStreamId, long prevStreamAckCount) {
		LocalSession previousSession = getLocalSessionByStreamId(prevStreamId);

		if (previousSession != null) {
			synchronized (this.outboundPacketQ) {
				boolean resumed = previousSession.resumeSession(newSession, prevStreamAckCount);
				previousSession.archive();

				if (resumed) {
					newSession.setParentSession(this);
					this.localSessions.add(newSession);
				}

				return resumed;
			}

		}

		LOGGER.debug("No Local sesion found for user {} and previous streamId {}", this.getBareJID(), prevStreamId);

		return false;
	}

	/**
	 * Check local session exists with streamId or not
	 * 
	 * @param streamId
	 * @return
	 */
	public boolean isStreamExists(String streamId) {
		LocalSession ls = getLocalSessionByStreamId(streamId);
		return ls != null;
	}

	/**
	 * Write an outgoing {@link Stanza} packet on to the {@link LocalSession}s
	 * held be this {@link UserSession} instance. If the destination of the
	 * stanza is a {@link BareJID}, the stanza will be written on to all the
	 * {@link LocalSession}s; and if the stanza destination is a full JID, the
	 * packet will be written on to the {@link LocalSession} associated with the
	 * full JID.
	 * 
	 * @param stanza stanza instance to be written on to {@link LocalSession}s
	 * @return true if there was at least one {@link LocalSession} found at
	 *         which the stanza was written
	 */
	boolean write(Stanza stanza) {
		synchronized (this.outboundPacketQ) {
			boolean written = false;

			if (stanza.getDestination().isFullJId()) {
				LocalSession ls = this.getLocalSessionBySessionId(stanza.getDestination().getResource());
				written = ls != null ? ls.write(stanza) : false;

			} else {
				for (LocalSession ls : this.localSessions) {
					LOGGER.debug("Writing stanza {} on session {}", stanza.xml(), ls.getSessionId());
					written = written || ls.write(stanza);
				}

			}

			if (stanza.isInsurancedDeliveryRequired()) {
				addToOutboundPacketQ(stanza);
			}

			return written;
		}
	}

	private void addToOutboundPacketQ(Stanza stanza) {
		synchronized (this.outboundPacketQ) {
			addDelayContent(stanza);
			this.outboundPacketQ.add(stanza);
		}
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
	 * It removes acknowledged stanza from {@link #outboundPacketQ}.
	 */

	public void dropAcknowledgedStanza(Stanza acknowledgedStanza) {
		this.outboundPacketQ.drop(new Predicate<Stanza>() {

			@Override
			public boolean test(Stanza stanza) {
				return acknowledgedStanza == stanza;
			}
		});
	}

	/**
	 * Destroy this {@code UserSession}; the action will result in destruction
	 * of all the {@link LocalSession}s held by this {@link UserSession}. While
	 * destrying the {@link LocalSession}, the presence of the user needs to
	 * updated and also broadcasted to its listener users.
	 * <p>
	 * It is unlikely that server will ever destroy an active user session
	 * unless it's executing shutdown hook. In all other scenarios, session is
	 * destryed only if the server deems session inactive.
	 * </p>
	 * <p>
	 * We do not remove {@link LocalSession}s from the list after invoking
	 * {@link LocalSession#destroy()}. The whole {@link UserSession} object will
	 * be garbage collected.
	 * </p>
	 * 
	 * @param consumer
	 */
	public void destroy(Consumer<LocalSession> consumer) {
		LOGGER.debug("Destroying UserSession " + this);

		this.localSessions.forEach(consumer.andThen((v) -> v.destroy()));
	}

	/**
	 * Destroy this {@code UserSession}; the action will result in destruction
	 * of all the {@link LocalSession}s held by this {@link UserSession}. While
	 * destrying the {@link LocalSession}, the presence of the user needs to
	 * updated and also broadcasted to its listener users.
	 * <p>
	 * It is unlikely that server will ever destroy an active user session
	 * unless it's executing shutdown hook. In all other scenarios, session is
	 * destryed only if the server deems session inactive.
	 * </p>
	 */
	public void destroy() {
		LOGGER.debug("Destroying UserSession " + this);
		this.localSessions.forEach((v) -> v.destroy());
	}

	/**
	 * firstly it will remove Delivered Stanzas from {@link #outboundPacketQ}
	 * then persist {@link #outboundPacketQ} to db.
	 * 
	 * @param dbService
	 */
	public void persistUndeliveredStanzasToDb(PersistenceService dbService) {
		dbService.persistUndeliverdStanzas(this.outboundPacketQ);
	}

	@Override
	public String toString() {
		return "UserSession[" + this.bareJID + ", LocalSession(" + localSessionCount() + ")]";
	}
}
