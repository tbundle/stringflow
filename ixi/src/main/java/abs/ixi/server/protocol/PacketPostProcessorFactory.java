package abs.ixi.server.protocol;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.packet.Packet;
import abs.ixi.server.packet.xmpp.FailurePacket;
import abs.ixi.server.packet.xmpp.IQ;
import abs.ixi.server.packet.xmpp.SASLChallenge;
import abs.ixi.server.packet.xmpp.SASLSuccess;
import abs.ixi.server.packet.xmpp.SMResumedPacket;
import abs.ixi.server.packet.xmpp.StreamFeaturePacket;
import abs.ixi.server.packet.xmpp.StreamHeader;
import abs.ixi.server.packet.xmpp.TLSProceed;

public class PacketPostProcessorFactory {
	private static final Logger LOGGER = LoggerFactory.getLogger(PacketPostProcessorFactory.class);

	private static final Map<String, PacketPostProcessor> processors;

	static {
		processors = new HashMap<>();

		LOGGER.info("Loading Negotiation Packet Processors");
		processors.put(StreamHeader.class.getCanonicalName(), new StreamHeaderPostProcessor());

		processors.put(StreamFeaturePacket.class.getCanonicalName(), new StreamFeaturePostProcessor());

		processors.put(TLSProceed.class.getCanonicalName(), new TLSProceedPostProcessor());

		processors.put(FailurePacket.class.getCanonicalName(), new FailurePostProcessor());

		processors.put(SASLSuccess.class.getCanonicalName(), new SASLSuccessPostProcessor());

		processors.put(SASLChallenge.class.getCanonicalName(), new SASLChallangePostProcessor());

		processors.put(IQ.class.getCanonicalName(), new IQPostProcessor());

		processors.put(SMResumedPacket.class.getCanonicalName(), new StreamResumedPostProcessor());

	}

	public static PacketPostProcessor getPostProcessor(Packet packet) {
		return processors.get(packet.getClass().getCanonicalName());
	}
}
