package abs.ixi.server.session;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.connection.Stream;

import abs.ixi.server.common.SimpleQueue;
import abs.ixi.server.io.IOService;
import abs.ixi.server.packet.xmpp.BareJID;
import abs.ixi.server.packet.xmpp.Message;
import abs.ixi.server.packet.xmpp.Stanza;
import abs.ixi.util.StringUtils;
import abs.ixi.util.UUIDGenerator;

/**
 * A valid user can login from multiple devices at the same time (Although two
 * sessions from one device are not permitted; we don't have a solid mechanism
 * to avoid that though). Therefore, One {@link UserSession} instance can have
 * multiple {@code DeviceSession} instances. {@code DeviceSession} represents a
 * user session from one device.
 */
public class LocalSession {
	private static final Logger LOGGER = LoggerFactory.getLogger(LocalSession.class);
	/**
	 * Parent {@link UserSession} for this instance.
	 */
	private UserSession parentSession;
	/**
	 * Unique id for this session; to simplify the working, session id is same
	 * as resourceId of the user.
	 */
	private String sessionId;

	/**
	 * Session streamId like(for xmpp stream id or mime stream id)
	 */
	private String sessionStreamId;

	/**
	 * {@link BareJID} of user
	 */
	private BareJID userJID;

	/**
	 * {@link IOService} for this session.
	 */
	private IOService<?, ?> ioService;

	/**
	 * Before writing {@link Stanza}s on this session. Add them to this queue.
	 */
	private SimpleQueue<Stanza> outboundStanzaQ;

	/**
	 * Incoming {@link Stanza} count for this stream. Currently this is updated
	 * only {@link Message} stanza. In future we have to do it for all
	 * {@link Stanza}'s.
	 */
	private long handledStanzaCount;

	/**
	 * Outgoing {@link Stanza} count for this stream. Currently this is updated
	 * only {@link Message} stanza. In future we have to do it for all
	 * {@link Stanza}'s.
	 */
	private long sentStanzaCount;

	/**
	 * when Outgoing stanzas has received by client. Client sends
	 * acknowledgement for received stanza's count send to server. That time
	 * this counter is updated.
	 */
	private long acknowledgedStanzaCount;

	/**
	 * Time of most recent activity on this stream; any inbound/outbound packet
	 * transmission is an activity on the stream
	 */
	private long lastActivityTime;

	/**
	 * After inactivity of stream Stream can be resumed till this time.
	 */
	private long maxResumptionTimeInSec;

	/**
	 * flag for Stream Management enabling status
	 */
	private boolean isStreamResumable;

	/**
	 * Is StreamManagement Enabled. If Stream management enabled on this session
	 * then Stanza level acknowledgement will work on this session.
	 */
	private boolean isStreamManagementEnabled;

	/**
	 * This flag indicate that this session is ready to write stanzas. Is flag
	 * is on after initial presence received from client. if stanzas come for
	 * write before flag on, those stanzas stored in outboundStanza Q to write
	 * later.
	 */
	private boolean isReady;

	public LocalSession(IOService<?, ?> ioService) {
		this(UUIDGenerator.secureId(), ioService);
	}

	public LocalSession(String sessionId, IOService<?, ?> ioService) {
		this.sessionId = sessionId;
		this.ioService = ioService;
		this.outboundStanzaQ = new SimpleQueue<>();
	}

	public UserSession getParentSession() {
		return parentSession;
	}

	public void setParentSession(UserSession parentSession) {
		this.parentSession = parentSession;
	}

	public String getSessionId() {
		return sessionId;
	}

	public long getHandledStanzaCount() {
		return handledStanzaCount;
	}

	public long increaseHandledStanzaCount() {
		return ++this.handledStanzaCount;
	}

	public void setHandledStanzaCount(long handledPacketCount) {
		this.handledStanzaCount = handledPacketCount;
	}

	public long getSentStanzaCount() {
		return sentStanzaCount;
	}

	public long increaseSentStanzaCount() {
		return ++this.sentStanzaCount;
	}

	public void setSentStanzaCount(long sentPacketCount) {
		this.sentStanzaCount = sentPacketCount;
	}

	public void setAcknowledgedStanzaCount(long ackReceivedCount) {
		LOGGER.debug("Ack received count {} on localSession {}", ackReceivedCount, this);
		LOGGER.debug("Before acking Acknowldege count {} on localSession {}", this.acknowledgedStanzaCount, this);
		LOGGER.debug("Befor marking ack outboundStanzaQ aize : {} on localSession {}", outboundStanzaQ.size(), this);
		if (isStreamManagementEnabled && ackReceivedCount > this.acknowledgedStanzaCount) {
			this.onAckReceived(ackReceivedCount - this.acknowledgedStanzaCount);
			this.acknowledgedStanzaCount = ackReceivedCount;
		}
	}

	public long getLastActivityTime() {
		return this.lastActivityTime;
	}

	public void setLastActivityTime(long lastActivityTime) {
		this.lastActivityTime = lastActivityTime;
	}

	public void setUserJID(BareJID userJID) {
		this.userJID = userJID;
	}

	public BareJID getUserJID() {
		return this.userJID;
	}

	public String getSessionStreamId() {
		return sessionStreamId;
	}

	public void setSessionStreamId(String sessionStreamId) {
		this.sessionStreamId = sessionStreamId;
	}

	public void setMaxResumptionTimeInSec(long maxResumptionTimeInSec) {
		this.maxResumptionTimeInSec = maxResumptionTimeInSec;
	}

	public void setStreamResumable(boolean isStreamResumable) {
		this.isStreamResumable = isStreamResumable;
	}

	public boolean isSessionStreamResumable() {
		return this.isStreamResumable
				&& (System.currentTimeMillis() - this.getLastActivityTime() < this.maxResumptionTimeInSec * 1000);
	}

	public boolean isStreamManagementEnabled() {
		return isStreamManagementEnabled;
	}

	public void enableStreamManagement() {
		this.isStreamManagementEnabled = true;
	}

	private void onAckReceived(long ackCount) {
		while (ackCount > 0) {
			Stanza acknowledgedStanza = this.outboundStanzaQ.drop();

			if (acknowledgedStanza != null) {
				LOGGER.debug("Ackt received for stanza {} on loaclsession {}", acknowledgedStanza.xml(), this);
				LOGGER.debug("After Ack marking ouboundStanzaQ size :{}  on local session {}" + outboundStanzaQ.size(),
						this);
				acknowledgedStanza.setDelivered(true);

				this.parentSession.dropAcknowledgedStanza(acknowledgedStanza);
			} else {

				LOGGER.warn(
						"Acknowledeged stazaa is >>>>>>>>>>>>> null, Ack is coming even if Ack waiting Q is empty, Its major flow , uncretain Ack is coming,"
								+ " Need to close session {}");
			}

			ackCount--;
		}
	}

	/**
	 * Resuming New session with this session
	 */
	public boolean resumeSession(LocalSession newSession, long prevStreamAckCount) {
		this.setAcknowledgedStanzaCount(prevStreamAckCount);

		if (isSessionStreamResumable()) {
			newSession.setHandledStanzaCount(this.handledStanzaCount);
			newSession.setAcknowledgedStanzaCount(this.acknowledgedStanzaCount);

			// New stream sent stanza count will be updated with acknowledged
			// stanza count which is actual successfully delivered stanza count
			// on previous stream
			newSession.setSentStanzaCount(this.acknowledgedStanzaCount);
			newSession.setSessionStreamId(this.sessionStreamId);
			newSession.setMaxResumptionTimeInSec(this.maxResumptionTimeInSec);
			newSession.setStreamResumable(this.isStreamResumable);

			if (this.isStreamManagementEnabled) {
				newSession.enableStreamManagement();
			}

			newSession.setOutboundStanzaQueue(this.outboundStanzaQ);

			return true;
		}

		return false;
	}

	public void markSessionReady() {
		synchronized (this.outboundStanzaQ) {
			this.isReady = true;

			if (isStreamManagementEnabled) {
				this.sendAllUndeliveredStanzas();

			} else {
				this.outboundStanzaQ.clear();
			}

		}

	}

	/**
	 * It will write all packets of {@link #outboundStanzaQ}. This method should
	 * call only just after Resource bind or {@link Stream} resumption.
	 * Otherwise packet order will be disturbed.
	 */
	private void sendAllUndeliveredStanzas() {
		synchronized (this.outboundStanzaQ) {
			if (!(this.isDead() || this.outboundStanzaQ.isEmpty())) {

				try {
					@SuppressWarnings("unchecked")
					SimpleQueue<Stanza> undeliveredStanzaQ = (SimpleQueue<Stanza>) this.outboundStanzaQ.clone();

					this.outboundStanzaQ.clear();

					undeliveredStanzaQ.forEach(new Consumer<Stanza>() {

						@Override
						public void accept(Stanza stanza) {
							write(stanza);
						}

					});

				} catch (CloneNotSupportedException e) {
					// Swallow it
				}

			}

		}
	}

	/**
	 * If stream management is enabled on this session then it will put that
	 * stanza in {@link #outboundStanzaQ} and increase {@link #sentStanzaCount}.
	 * then it will hand-over to corresponding {@link IOService} to handle that
	 * outbound packet.
	 * 
	 * @param stanza
	 * @return true if the stanza was written successfully otherwise false
	 */
	public boolean write(Stanza stanza) {
		synchronized (this.outboundStanzaQ) {
			if (this.isDead()) {
				LOGGER.debug("LoaclSession {} is Dead. So adding stanza {} to outboundStanzaQ", stanza.xml());
				this.outboundStanzaQ.add(stanza);

			} else if (!isReady) {
				LOGGER.debug(
						"LoaclSession {} is not ready (initial presence not received till now) so adding stanza {} to outboundStanzaQ {}",
						stanza.xml());
				this.outboundStanzaQ.add(stanza);

			} else {

				boolean handled = this.ioService.handleOutboundPacket(stanza);

				if (handled && this.isStreamManagementEnabled) {
					this.outboundStanzaQ.add(stanza);
					this.increaseSentStanzaCount();
					SessionManager.getInstance().getAckTimerWheel().add(this, stanza);
				}

				if (!handled) {
					this.archive();
				}

				return handled;
			}
		}

		return false;
	}

	/**
	 * Set outbound stanza queue for this local session
	 * 
	 * @param queue
	 */
	public void setOutboundStanzaQueue(SimpleQueue<Stanza> queue) {
		this.outboundStanzaQ = queue;
	}

	/**
	 * Copy a queue to the outbound queue of this LocalSession. The stanza
	 * packets held are not cloned instead they are just added to the
	 * {@link SimpleQueue} instance.
	 * 
	 * @param queue
	 */
	public void addToOutboundStanzaQueue(SimpleQueue<Stanza> queue) {

		queue.forEach(new Consumer<Stanza>() {
			@Override
			public void accept(Stanza t) {
				outboundStanzaQ.add(t);
			}
		});
	}

	/**
	 * Verify if this session is still active. The method triggers PING semetics
	 * on underlying {@link IOService} instance which client on other side of
	 * the network must respond.
	 */
	boolean triggerVerification() {
		if (this.ioService == null || this.ioService.hasPingTimeout()) {
			return false;

		} else {
			return this.ioService.sendPing();
		}
	}

	/**
	 * Check if the local session has timed out the ping
	 * 
	 * @return
	 */
	boolean isInactive() {
		return this.ioService != null && this.ioService.hasPingTimeout();
	}

	/**
	 * Check if this local session is dead.
	 * 
	 * @return true if the underlying {@link IOService} instance has been
	 *         destroyed
	 */
	boolean isDead() {
		return this.ioService == null;
	}

	/**
	 * If the session is found inactive, it can be archived. LocalSession can
	 * not be removed from memory until stream resumption time has not expired
	 * therefore an inactive session must be archived until then.
	 * <p>
	 * Archiving session destroys the undlerying {@link IOService} instance and
	 * removes its reference from this session
	 * </p>
	 */
	public void archive() {
		if (ioService != null) {
			this.ioService.destroy();
			this.ioService = null;
		}
	}

	/**
	 * Destroy this {@link LocalSession}. This will result in associated
	 * {@link IOService} shutdown(which will internally send stream close stanza
	 * to the client)
	 */
	public void destroy() {
		if (ioService != null) {
			this.ioService.destroy();
			this.ioService = null;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		// Possibility of ClassCastException; caller must ensure that obj is
		// always a LocalSession instance.
		return this.userJID.equals(((LocalSession) obj).getUserJID())
				&& StringUtils.safeEquals(this.getSessionId(), ((LocalSession) obj).getSessionId());
	}

	@Override
	public String toString() {
		return "LocalSession[" + this.getUserJID() + "," + this.getSessionId() + "]";
	}

}
