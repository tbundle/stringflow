package abs.ixi.server.session;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.common.AgeQueue;
import abs.ixi.server.common.Pair;
import abs.ixi.server.packet.xmpp.Stanza;

public final class AckTimerWheel {
	private static final Logger LOGGER = LoggerFactory.getLogger(AckTimerWheel.class);

	/**
	 * When a packet is written on to a {@link LocalSession}, an ACK from client
	 * is expected within stipulated time; failing to that the
	 * {@link LocalSession} instance is deemed as dead and marked for cleanup.
	 * {@link AgeQueue} helps track if the server has received ACK for outgoing
	 * packets.
	 */
	private AgeQueue<Pair<LocalSession, Stanza>> ackWaitingQ;

	public AckTimerWheel() {
		// TODO Below time settings must be available in configurations
		this.ackWaitingQ = new AgeQueue<>(Duration.ofSeconds(25), Duration.ofSeconds(2),
				new LocalSessionDeathHandler());
		startWheel();
	}

	private void startWheel() {
		this.ackWaitingQ.start();
		LOGGER.info("AckTimerWheel started");
	}

	/**
	 * Add a {@link LocalSession} for a {@link Stanza} to the timer wheel for
	 * ACK timeout
	 * 
	 * @param ls
	 * @param s
	 */
	public void add(LocalSession ls, Stanza s) {
		this.ackWaitingQ.add(new Pair<LocalSession, Stanza>(ls, s));
	}

}
