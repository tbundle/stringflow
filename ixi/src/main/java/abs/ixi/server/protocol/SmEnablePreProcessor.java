package abs.ixi.server.protocol;

import static abs.ixi.server.etc.conf.Configurations.Bundle.PROCESS;

import abs.ixi.httpclient.util.ObjectUtils;
import abs.ixi.server.Stringflow;
import abs.ixi.server.io.PacketCollector;
import abs.ixi.server.io.StreamContext;
import abs.ixi.server.packet.xmpp.SMEnablePacket;
import abs.ixi.server.packet.xmpp.SMEnabledPacket;
import abs.ixi.server.packet.xmpp.XMPPPacket;
import abs.ixi.server.session.LocalSession;

public class SmEnablePreProcessor implements PacketPreProcessor<SMEnablePacket, XMPPPacket> {
	@Override
	public void preProcess(SMEnablePacket smEnable, LocalSession ls, StreamContext context,
			PacketCollector<XMPPPacket> collector) throws Exception {

		ls.enableStreamManagement();

		SMEnabledPacket smEnabledPacket = new SMEnabledPacket();

		if (smEnable.isResumable()) {
			String servceMaxResumption = Stringflow.runtime().configurations().get(_STREAM_MAX_RESUMPTION_TIME,
					PROCESS);

			long serverMaxResumptionTime = ObjectUtils.parseToLong(servceMaxResumption);

			long clientMaxResumptionTime = smEnable.getMaxResumptionTime();

			long preferedMaxResumptionTime = clientMaxResumptionTime > 0
					? Math.min(clientMaxResumptionTime, serverMaxResumptionTime) : serverMaxResumptionTime;

			ls.setStreamResumable(true);
			ls.setMaxResumptionTimeInSec(preferedMaxResumptionTime);

			smEnabledPacket.setResume(true);
			smEnabledPacket.setMaxResumptionTime(preferedMaxResumptionTime);
		}

		smEnabledPacket.setSmId(context.getFrom().getFullJID());

		collector.collectOutbound(smEnabledPacket);

	}

}
