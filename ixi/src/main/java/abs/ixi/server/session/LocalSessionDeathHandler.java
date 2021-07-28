package abs.ixi.server.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.ComponentNotFoundException;
import abs.ixi.server.CoreComponent.ServerFunction;
import abs.ixi.server.PacketEnvelope;
import abs.ixi.server.Stringflow;
import abs.ixi.server.common.AgeQueue;
import abs.ixi.server.common.DeathHandler;
import abs.ixi.server.common.Pair;
import abs.ixi.server.packet.Packet;
import abs.ixi.server.packet.Packet.PacketXmlElement;
import abs.ixi.server.packet.xmpp.Message;
import abs.ixi.server.packet.xmpp.Stanza;

/**
 * AgeQueue {@link DeathHandler} implementation for {@link LocalSession}
 * instances which dropped from {@link AgeQueue}.
 * 
 * @author Yogi
 *
 */
public class LocalSessionDeathHandler implements DeathHandler<Pair<LocalSession, Stanza>> {
	private static final Logger LOGGER = LoggerFactory.getLogger(LocalSessionDeathHandler.class);

	@Override
	public void onDeath(Pair<LocalSession, Stanza> sessionStanzaPair, Integer val) {
		try {
			Stanza stanza = sessionStanzaPair.getSecond();
			LocalSession ls = sessionStanzaPair.getFirst();

			if (!stanza.isDelivered()) {
				// TODO: Think about trigger white space keep alive verification
				// here.
				LOGGER.info(
						"Ack not received for stanza :{}, on LocalSession : {} of user : {},  So Triggrting verifcation....",
						stanza.xml(), ls, ls.getUserJID().toString());

				boolean triggered = ls.triggerVerification();

				if (!triggered) {
					ls.archive();
				}

				// TODO: For proper working of push notification it is necessary
				// to send push notification for here
				// or If not possible here then send push notification at the
				// time
				// of UserSession destruction. But it will generate bug of
				// duplicacy of push notification for some messages.

				// For now doing it. fix it later
				if (stanza.getXmlElementName() == PacketXmlElement.MESSAGE) {
					sendPushNotification((Message) stanza);
				}
			}
		} catch (ComponentNotFoundException e) {
			// TODO swallowing exception for now; need better handling
		}
	}

	private void sendPushNotification(final Message message) throws ComponentNotFoundException {
		LOGGER.info("Sending push notification for message {}", message.xml());
		PacketEnvelope<Packet> envelope = new PacketEnvelope<Packet>(message, "IOController");

		envelope.setDestinationComponent(Stringflow.runtime().getCoreComponentJid(ServerFunction.PUSH_NOTIFICATION));
		Stringflow.runtime().getRouter().route(envelope);
	}
}
