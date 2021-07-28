package abs.ixi.server.io.multipart;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

import abs.ixi.server.AbstractPacket;
import abs.ixi.server.common.ChannelStream;

/**
 * This packet holds MIME data.
 */
public class MimePacket extends AbstractPacket {
	private static final long serialVersionUID = 7167918052060152480L;

	private static String MEDIA_ACK_RESPONSE = "MIME-Version: 1.0 \n" + "Content-Type: text/plain \n" + "mediaId: %s \n"
			+ "\n\n";

	private static String SID_RESPONSE = "MIME-Version: 1.0 \n" + "Content-Type: text/plain \n" + "sid: %s \n" + "\n\n";

	private Content mediaContent;
	private String sid;
	private boolean sidResponse;
	private boolean mediaAckResponse;
	private String mediaId;

	public MimePacket(Content mediaContent) {
		this.mediaContent = mediaContent;
	}

	public MimePacket(String sid) {
		this.sid = sid;
		this.sidResponse = true;
	}

	public MimePacket(String mediaId, boolean mediaAckResponse) {
		this.mediaId = mediaId;
		this.mediaAckResponse = true;
	}

	@Override
	public long writeTo(Socket socket) throws IOException {
		if (mediaAckResponse) {
			socket.getOutputStream().write(getMediaAckResponse().getBytes(StandardCharsets.US_ASCII));

		} else if (sidResponse) {
			socket.getOutputStream().write(getSidResponse().getBytes(StandardCharsets.US_ASCII));

		} else if (mediaContent == null)
			return 0L;

		return mediaContent.getContentSource().writeTo(socket);
	}

	@Override
	public long writeTo(SocketChannel socketChannel) throws IOException {
		if (mediaContent == null)
			return 0L;

		return mediaContent.getContentSource().writeTo(socketChannel);
	}

	@Override
	public long writeTo(OutputStream os) throws IOException {
		if (mediaAckResponse) {
			os.write(getMediaAckResponse().getBytes(StandardCharsets.US_ASCII));

		} else if (sidResponse) {
			os.write(getSidResponse().getBytes(StandardCharsets.US_ASCII));

		} else if (mediaContent == null)
			return 0L;

		return mediaContent.getContentSource().writeTo(os);
	}

	@Override
	public long writeTo(ChannelStream cs) throws IOException {
		if (mediaContent == null)
			return 0L;

		return mediaContent.getContentSource().writeTo(cs);
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getMediaId() {
		return mediaId;
	}

	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}

	public boolean isSidResponse() {
		return sidResponse;
	}

	public void setSidResponse(boolean sidResponse) {
		this.sidResponse = sidResponse;
	}

	public boolean isMediaAckResponse() {
		return mediaAckResponse;
	}

	public void setMediaAckResponse(boolean mediaAckResponse) {
		this.mediaAckResponse = mediaAckResponse;
	}

	@Override
	public boolean hasMime() {
		return true;
	}

	@Override
	public boolean isRoutable() {
		return true;
	}

	@Override
	public String getSourceId() {
		return null;
	}

	public String getSidResponse() {
		return String.format(SID_RESPONSE, this.sid);
	}

	public String getMediaAckResponse() {
		return String.format(MEDIA_ACK_RESPONSE, this.mediaId);
	}

}
