package abs.ixi.server.protocol;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.packet.Packet;
import abs.ixi.server.packet.xmpp.AckPacket;
import abs.ixi.server.packet.xmpp.AckRequestPacket;
import abs.ixi.server.packet.xmpp.BOSHBody;
import abs.ixi.server.packet.xmpp.IQ;
import abs.ixi.server.packet.xmpp.Message;
import abs.ixi.server.packet.xmpp.Presence;
import abs.ixi.server.packet.xmpp.SASLAuthPacket;
import abs.ixi.server.packet.xmpp.SASLChallengeResponse;
import abs.ixi.server.packet.xmpp.SMEnablePacket;
import abs.ixi.server.packet.xmpp.SMResumePacket;
import abs.ixi.server.packet.xmpp.StartTlsPacket;
import abs.ixi.server.packet.xmpp.StreamHeader;

/**
 * A factory for {@link PacketPreProcessor}s.
 * 
 * @author Yogi
 *
 */
public class PacketPreProcessorFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(PacketPreProcessorFactory.class);

    private static final Map<String, PacketPreProcessor<? extends Packet, ? extends Packet>> processors;

    static {
	processors = new HashMap<>();

	LOGGER.info("Loading Negotiation Packet Processors");
	processors.put(StreamHeader.class.getCanonicalName(), new StreamHeaderPreProcessor());

	processors.put(StartTlsPacket.class.getCanonicalName(), new StartTLSPreProcessor());

	processors.put(SASLAuthPacket.class.getCanonicalName(), new SASLAuthPreProcessor());

	processors.put(SASLChallengeResponse.class.getCanonicalName(), new SASLChallengeResponsePreProcessor());

	processors.put(SMEnablePacket.class.getCanonicalName(), new SmEnablePreProcessor());

	processors.put(SMResumePacket.class.getCanonicalName(), new StreamResumePreProcessor());

	processors.put(IQ.class.getCanonicalName(), new IQPreProcessor());

	processors.put(Message.class.getCanonicalName(), new MessagePreProcessor());

	processors.put(Presence.class.getCanonicalName(), new PresencePreProcessor());

	processors.put(AckPacket.class.getCanonicalName(), new AckPreProcessor());

	processors.put(AckRequestPacket.class.getCanonicalName(), new AckRequestPreProcessor());

	processors.put(BOSHBody.class.getCanonicalName(), new BoshBodyPreProcessor());

    }

    public static PacketPreProcessor<? extends Packet, ? extends Packet> getPreProcessor(Packet packet) {
	return processors.get(packet.getClass().getCanonicalName());
    }

}
