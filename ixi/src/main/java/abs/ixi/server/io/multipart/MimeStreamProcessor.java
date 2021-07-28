package abs.ixi.server.io.multipart;

import static abs.ixi.server.io.multipart.MimeParser.parse;

import abs.ixi.server.common.ByteArray;
import abs.ixi.server.io.InputStreamProcessor;
import abs.ixi.server.io.PacketCollector;

/**
 * As the name suggests, {@code MimeSerializer} serialzes mime bytes.
 * 
 * @author Yogi
 *
 */
public class MimeStreamProcessor implements InputStreamProcessor<MimePacket> {
	private MimeEventHandler eventHandler;

	private ByteArray networkData;

	private PacketCollector<MimePacket> collector;

	public MimeStreamProcessor(PacketCollector<MimePacket> collector) {
		this.collector = collector;
		this.networkData = new ByteArray(new byte[0]);
		this.collector = collector;
		this.eventHandler = new MimeEventHandler(new MediaTransferResultHandler());
	}

	@Override
	public boolean process(byte[] data) throws Exception {
		return this.process(data, 0, data.length);
	}

	@Override
	public boolean process(byte[] data, int offset, int length) throws Exception {
		this.networkData.reload(data, offset, (offset + length));

		while (this.networkData.hasNext()) {
			parse(networkData, eventHandler);
		}

		return true;
	}

	@Override
	public boolean hasUnprocessedBytes() {
		return this.eventHandler.getPartialLineBytes() != null && this.eventHandler.getPartialLineBytes().length > 0;
	}

	@Override
	public byte[] getUnprocessedBytes() {
		return this.eventHandler.getPartialLineBytes();
	}

	@Override
	public void flushUnprocessedBytes() {
		this.eventHandler.setPartialLineBytes(null);
	}

	class MediaTransferResultHandler {

		public void mediaReceived(String mediaId) {
			MimePacket mimePacket = new MimePacket(mediaId, true);
			collector.collectMimePacket(mimePacket);
		}

		public void mediaTransmissionFailed() {

		}

		public void mediaTransmissionFailed(String media) {

		}
	}
}
