package abs.ixi.server;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.filesystem.DefaultFileSystem;
import abs.ixi.filesystem.FileSystemFactory;
import abs.ixi.server.CoreComponent.ServerFunction;
import abs.ixi.server.common.InstantiationException;
import abs.ixi.server.etc.conf.Configurations;
import abs.ixi.server.io.IOController;
import abs.ixi.server.io.multipart.BinaryContent;
import abs.ixi.server.io.multipart.Content;
import abs.ixi.server.io.multipart.FileContentSource;
import abs.ixi.server.io.multipart.MimePacket;
import abs.ixi.server.muc.MultiUserChatHandler;
import abs.ixi.server.packet.JID;
import abs.ixi.server.packet.Packet;
import abs.ixi.server.packet.xmpp.BareJID;
import abs.ixi.server.packet.xmpp.IQ;
import abs.ixi.server.packet.xmpp.IQQuery;
import abs.ixi.server.packet.xmpp.IQ.IQType;
import abs.ixi.server.router.Router;

/**
 * {@code MediaStoreManager} is the guardian component for media that server
 * processes. It could be receiving the media files, sending them or storing
 * them on hard drive.
 * <p>
 * Stringflow server uses proprietary content model i.e <i>SF Content Model</i>
 * to transfer/process media requests. The model is designed to benefit modern
 * needs to conversational systems. Along with propriety model, server supports
 * multiple other file transfer protocols such as SOCKS5, SI-File
 * Transfer(XEP-0096), Jingle and HTTP file upload/download.
 * </p>
 * 
 * @author Yogi
 *
 */
public class MediaStoreManager extends PacketProducerConsumer {
	private static final Logger LOGGER = LoggerFactory.getLogger(MediaStoreManager.class);

	/**
	 * Name of the componet
	 */
	private static final String COMPONENT_NAME = "mediaStoreManager";

	private static DefaultFileSystem fileSystem = FileSystemFactory.getDefaultFileSystemWithHashFileLocator();

	private IOController ioController;

	private MultiUserChatHandler mucHandler;

	private String ioControllerJid;

	public MediaStoreManager(Configurations config, Router router) throws InstantiationException {
		super(COMPONENT_NAME, config, router);
	}

	@Override
	public void start() throws Exception {
		LOGGER.info("Starting Media Store Manager");
		super.start();
		this.ioControllerJid = Stringflow.runtime().getCoreComponentJid(ServerFunction.IO_CONTROL);

		/**
		 * Current design does not allow components to refer other components
		 * within server. That's why we have commented out below. We may,
		 * infact, have to re-design media transfer protocol
		 */

		// this.ioController = (IOController)
		// Stringflow.runtime().configurations()
		// .getServerComponent(PropNames.IO_CONTROLLER);
		//
		// this.mucHandler = (MultiUserChatHandler)
		// Stringflow.runtime().getServerConfig()
		// .getServerComponent(PropNames.MUC_HANDLER);
	}

	@Override
	public void process(PacketEnvelope<? extends Packet> envelope) {
		IQ iq = (IQ) envelope.getPacket();

		LOGGER.debug("processing packet {}", iq.xml());

		if (iq.isMediaRequestIQ()) {
			IQQuery query = (IQQuery) iq.getContent();
			String mediaId = query.getMediaId();
			String sid = query.getSid();

			boolean isValid = verifyUserMediaRequest(iq.getFrom(), mediaId, sid);

			if (isValid) {
				IQ responseIQ = new IQ(iq.getId(), IQType.RESULT);
				responseIQ.setDestination(iq.getFrom());
				responseIQ.setContent(iq.getContent());
				routeIQPacket(responseIQ);
				sendMedia(mediaId, iq.getFrom().getBareJID(), sid);

			} else {
				IQ responseIQ = new IQ(iq.getId(), IQType.ERROR);
				responseIQ.setDestination(iq.getFrom());
				responseIQ.setContent(iq.getContent());

				routeIQPacket(responseIQ);
			}
		}

	}

	private void sendMedia(String mediaId, BareJID userBareJID, String sid) {
		try {
			Content content = new BinaryContent(new FileContentSource(fileSystem.getFile(mediaId)));
			MimePacket mimePacket = new MimePacket(content);

			mimePacket.setDestination(new JID(userBareJID, sid));

			PacketEnvelope<Packet> packetEnvelope = new PacketEnvelope<Packet>(mimePacket, this.getName());
			packetEnvelope.setDestinationComponent(this.ioControllerJid);

			this.router.route(packetEnvelope);

		} catch (IOException e) {
			LOGGER.error("Failed to send media : {} to : {} on sid : {}", mediaId, userBareJID, sid, e);
		}

	}

	private void routeIQPacket(IQ iq) {
		PacketEnvelope<Packet> packetEnvelope = new PacketEnvelope<Packet>(iq, this.getName());
		packetEnvelope.setDestinationComponent(this.ioControllerJid);

		this.router.route(packetEnvelope);
	}

	private boolean verifyUserMediaRequest(JID senderJID, String mediaId, String sid) {
		BareJID receiverJID = dbService.getMediaReceiverJID(mediaId);

		if (receiverJID == null)
			return false;

		if (receiverJID.equals(senderJID.getBareJID())
				|| mucHandler.isChatRoomMember(receiverJID, senderJID.getBareJID())) {
			return ioController.isMimeStreamAvailable(sid);
		}

		return false;
	}

	@Override
	public void shutdown() throws Exception {
		LOGGER.info("Shutting down {} component", getName());
		super.shutdown();
	}

}
