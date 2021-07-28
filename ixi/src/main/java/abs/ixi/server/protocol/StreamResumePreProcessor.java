package abs.ixi.server.protocol;

import abs.ixi.server.io.PacketCollector;
import abs.ixi.server.io.StreamContext;
import abs.ixi.server.packet.xmpp.SMFailed;
import abs.ixi.server.packet.xmpp.SMResumePacket;
import abs.ixi.server.packet.xmpp.SMResumedPacket;
import abs.ixi.server.packet.xmpp.XMPPPacket;
import abs.ixi.server.session.LocalSession;
import abs.ixi.server.session.SessionManager;

/**
 * A {@link PacketPreProcessor} which handles Stream Resumption.
 * 
 * @author Yogi
 *
 */
public class StreamResumePreProcessor implements PacketPreProcessor<SMResumePacket, XMPPPacket> {
	@Override
	public void preProcess(SMResumePacket smResume, LocalSession ls, StreamContext context,
			PacketCollector<XMPPPacket> collector) throws Exception {

		boolean streamResumed = SessionManager.getInstance().resumeSession(ls, smResume.getPrevId(),
				smResume.getPrevHandledPacketCount());

		if (streamResumed) {
			context.setStreamId(ls.getSessionStreamId());
			context.setUserResourceId(ls.getSessionId());

			SMResumedPacket resumedPacket = new SMResumedPacket(smResume.getPrevId());
			resumedPacket.setPrevHandledPacketCount(ls.getHandledStanzaCount());
			resumedPacket.setFrom(context.getFrom());

			collector.collectOutbound(resumedPacket);

		} else {
			SMFailed failed = new SMFailed();
			failed.setSourceId(context.getStreamId());
			collector.collectOutbound(failed);
		}
	}

}
