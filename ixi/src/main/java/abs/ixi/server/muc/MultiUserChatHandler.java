package abs.ixi.server.muc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.CoreComponent.ServerFunction;
import abs.ixi.server.PacketEnvelope;
import abs.ixi.server.PacketProducerConsumer;
import abs.ixi.server.Stringflow;
import abs.ixi.server.common.InstantiationException;
import abs.ixi.server.etc.conf.Configurations;
import abs.ixi.server.muc.ChatRoom.AccessMode;
import abs.ixi.server.muc.ChatRoom.Affiliation;
import abs.ixi.server.muc.ChatRoom.ChatRoomMember;
import abs.ixi.server.muc.ChatRoom.Role;
import abs.ixi.server.muc.MucXmppUtil.ErrorType;
import abs.ixi.server.packet.JID;
import abs.ixi.server.packet.Packet;
import abs.ixi.server.packet.XMPPNamespaces;
import abs.ixi.server.packet.xmpp.BareJID;
import abs.ixi.server.packet.xmpp.IQ;
import abs.ixi.server.packet.xmpp.IQErrorContent;
import abs.ixi.server.packet.xmpp.IQQuery;
import abs.ixi.server.packet.xmpp.Message;
import abs.ixi.server.packet.xmpp.MessageContent;
import abs.ixi.server.packet.xmpp.MessageSubject;
import abs.ixi.server.packet.xmpp.Presence;
import abs.ixi.server.packet.xmpp.XMPPPacket;
import abs.ixi.server.packet.xmpp.IQ.IQType;
import abs.ixi.server.packet.xmpp.IQContent.IQContentType;
import abs.ixi.server.packet.xmpp.IQErrorContent.IQError;
import abs.ixi.server.packet.xmpp.Message.MessageType;
import abs.ixi.server.packet.xmpp.MessageContent.MessageContentType;
import abs.ixi.server.packet.xmpp.Presence.PresenceType;
import abs.ixi.server.router.Router;
import abs.ixi.util.CollectionUtils;
import abs.ixi.util.ObjectUtils;
import abs.ixi.util.StringUtils;

public class MultiUserChatHandler extends PacketProducerConsumer {
	private static final Logger LOGGER = LoggerFactory.getLogger(MultiUserChatHandler.class);

	private static final String COMPONENT_NAME = "MUC";

	private static final String ERROR_XML = "<error by='%s' type='%s'>%s</error>";
	private static final String CONFLICT_ERROR_XML = "<conflict xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>";
	private static final String NOT_ALLOWED_ERROR_XML = "<not-allowed xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>";

	private Map<BareJID, ChatRoom> chatRooms;

	public MultiUserChatHandler(Configurations config, Router router) throws InstantiationException {
		super(COMPONENT_NAME, config, router);
	}

	@Override
	public void start() throws Exception {
		super.start();

		this.chatRooms = dbService.getChatRooms();

		LOGGER.debug("MUC Handler Started");
	}

	@Override
	public void shutdown() throws Exception {
		LOGGER.info("Shutting down {} component", getName());
		super.shutdown();
	}

	@Override
	public void process(PacketEnvelope<? extends Packet> envelope) {
		try {
			XMPPPacket packet = (XMPPPacket) envelope.getPacket();

			LOGGER.debug("Processing muc  packet : {}", packet.xml());

			/*
			 * 1. Add User a) Add the user in the member set and persist in DB
			 * b) Send New Member's presence to existing members. c) Send
			 * Existing members's presence to new member
			 */
			if (packet instanceof Presence) {
				Presence presencePacket = (Presence) packet;
				processPresencePacket(presencePacket);

			} else if (packet instanceof Message) {
				Message msgPacket = (Message) packet;
				processMessagePacket(msgPacket);

			} else if (packet instanceof IQ) {
				IQ iq = (IQ) packet;
				processIQPacket(iq);
			}

		} catch (Exception e) {
			LOGGER.error("Failed to process packet {}", envelope.getPacket(), e);
		}
	}

	private void processIQPacket(IQ iq) throws Exception {
		if (iq.getContent().getType() == IQContentType.QUERY) {

			IQQuery query = (IQQuery) iq.getContent();

			if (iq.getType() == IQType.GET) {
				if (StringUtils.safeEquals(query.getXmlns(), XMPPNamespaces.DISCO_ITEM_NAMESPACE, false)) {

					sendChatRoomOrMemberList(iq);

				} else if (StringUtils.safeEquals(query.getXmlns(), XMPPNamespaces.DISCO_INFO_NAMESPACE, false)) {
					sendChatRoomInfo(iq);

				} else if (StringUtils.safeEquals(query.getXmlns(), XMPPNamespaces.MUC_OWNER_NAMESPACE, false)) {
					sendRoomConfigForm(iq);

				}

			} else if (iq.getType() == IQType.SET && iq.getContent() != null) {
				if (StringUtils.safeEquals(query.getXmlns(), XMPPNamespaces.MUC_ADMIN_NAMESPACE, false)) {
					if (!CollectionUtils.isNullOrEmpty(query.getRooms())) {
						ChatRoom queryRoom = query.getRooms().get(0);

						if (isRoomExists(queryRoom.getRoomJID())) {

							ChatRoom room = this.chatRooms.get(queryRoom.getRoomJID());

							if (room.isRoomOwner(iq.getFrom().getBareJID())
									|| room.isRoomAdmin(iq.getFrom().getBareJID())) {

								for (ChatRoomMember member : queryRoom.getMembers()) {

									if (member.getAffiliation() == Affiliation.NONE) {
										removeMemberFromRoom(member, room);

									} else {
										addorUpdateRoomMember(room, member);

										exchangingPresence(room, member);
										sendChatRoomSubject(room, member);
									}

									sendSuccessIQ(iq);
								}

							} else {
								LOGGER.debug("requester {} is not owner or admin for group {}", iq.getFrom(),
										room.getRoomJID());
								sendIQErrorResponse(iq, IQError.NOT_ALLOWED_ERROR);
							}

						} else {
							LOGGER.debug("group {} does not exist", queryRoom.getRoomJID());
							sendIQErrorResponse(iq, IQError.NOT_ALLOWED_ERROR);
						}

					}

				} else if (StringUtils.safeEquals(query.getXmlns(), XMPPNamespaces.MUC_OWNER_NAMESPACE, false)) {
					if (!CollectionUtils.isNullOrEmpty(query.getRooms())) {
						ChatRoom queryRoom = query.getRooms().get(0);

						if (isRoomExists(queryRoom.getRoomJID())) {
							ChatRoom room = this.chatRooms.get(queryRoom.getRoomJID());

							if (room.isRoomOwner(iq.getFrom().getBareJID())) {
								AccessMode accessMode = queryRoom.getAccessMode();

								if (accessMode != null) {
									room.setAccessMode(accessMode);
									dbService.updateRoomAccessMode(room);
								}

								sendSuccessIQ(iq);

							} else {
								sendIQErrorResponse(iq, IQError.FORBIDDEN_ERROR);
							}

						} else {
							sendIQErrorResponse(iq, IQError.FORBIDDEN_ERROR);
						}

					} else if (query.isDestroyRoom()) {

						if (isRoomExists(iq.getTo().getBareJID())) {
							ChatRoom room = this.chatRooms.get(iq.getTo().getBareJID());

							if (room.isRoomOwner(iq.getFrom().getBareJID())) {
								destroyRoom(room, query.getReason());
								sendSuccessIQ(iq);

							} else {
								sendIQErrorResponse(iq, IQError.FORBIDDEN_ERROR);
							}

						} else {
							sendIQErrorResponse(iq, IQError.FORBIDDEN_ERROR);
						}

					}

				}

			}
		}
	}

	private void destroyRoom(ChatRoom chatRoom, String reason) {
		if (!CollectionUtils.isNullOrEmpty(chatRoom.getMembers())) {

			for (ChatRoomMember member : chatRoom.getMembers()) {
				dbService.removeChatRoomMember(chatRoom.getRoomJID(), member.getUserJID());

				member.setAffiliation(Affiliation.NONE);
				member.setRole(Role.NONE);

				sendUnavailablePresence(member, member);
			}
		}

		this.chatRooms.remove(chatRoom.getRoomJID());

		dbService.deleteChatRoom(chatRoom.getRoomJID());
	}

	private void addorUpdateRoomMember(ChatRoom room, ChatRoomMember member) {
		if (room.isRoomMember(member.getUserJID())) {
			updateRoomMember(room, member);

		} else {
			if (member.getAffiliation() == null) {
				member.setAffiliation(Affiliation.MEMBER);
			}

			if (member.getRole() == null) {
				member.setRole(member.getAffiliation() == Affiliation.OWNER ? Role.MODERATOR : Role.PARTICIPANT);
			}

			addRoomMember(room, member);
		}
	}

	private void sendRoomConfigForm(IQ iq) {
		if (isRoomExists(iq.getTo().getBareJID())) {
			ChatRoom room = this.chatRooms.get(iq.getTo().getBareJID());

			if (room.isRoomOwner(iq.getFrom().getBareJID())) {
				IQ responseIQ = new IQ(iq.getId(), IQType.RESULT);
				responseIQ.setFrom(iq.getTo());
				responseIQ.setTo(iq.getFrom());

				IQQuery responseQuery = new IQQuery(XMPPNamespaces.MUC_OWNER_NAMESPACE);
				responseIQ.setContent(responseQuery);

				responseQuery.setRoomConfigFormResponse(true);

				routeToIOController(responseIQ);

				return;
			}
		}

		sendIQErrorResponse(iq, IQError.FORBIDDEN_ERROR);
	}

	public void sendSuccessIQ(IQ requestIQ) {
		IQ responseIQ = new IQ(requestIQ.getId(), IQType.RESULT);
		responseIQ.setFrom(requestIQ.getTo());
		responseIQ.setTo(requestIQ.getFrom());

		routeToIOController(responseIQ);
	}

	public void sendIQErrorResponse(IQ requestIQ, IQError iqError) {
		IQ responseIQ = new IQ(requestIQ.getId(), IQType.ERROR);
		responseIQ.setFrom(requestIQ.getTo());
		responseIQ.setTo(requestIQ.getFrom());

		IQErrorContent error = new IQErrorContent(iqError.getErrorXMl());
		responseIQ.setContent(error);

		routeToIOController(responseIQ);
	}

	private void sendChatRoomInfo(IQ iq) {
		ChatRoom room = this.chatRooms.get(iq.getTo().getBareJID());

		if (room != null) {
			IQ responseIQ = new IQ(iq.getId(), IQType.RESULT);
			responseIQ.setFrom(iq.getTo());
			responseIQ.setTo(iq.getFrom());

			IQQuery responseQuery = new IQQuery(XMPPNamespaces.DISCO_INFO_NAMESPACE);
			responseIQ.setContent(responseQuery);
			responseQuery.setRooms(Arrays.asList(room));

			routeToIOController(responseIQ);
		}

	}

	private void sendChatRoomOrMemberList(IQ iq) throws Exception {
		IQ responseIQ = new IQ(iq.getId(), IQType.RESULT);

		if (StringUtils.isNullOrEmpty(iq.getTo().getNode())) {
			// chat rooms listing
			List<ChatRoom> chatRooms = new ArrayList<>();

			for (ChatRoom room : this.chatRooms.values()) {
				if (room.getMember(iq.getFrom().getBareJID()) != null) {
					chatRooms.add(room);
				}
			}

			responseIQ.setFrom(new JID(iq.getTo().getDomain()));
			responseIQ.setTo(iq.getFrom());

			IQQuery responseQuery = new IQQuery(XMPPNamespaces.DISCO_ITEM_NAMESPACE);
			responseQuery.setRooms(chatRooms);
			responseQuery.setRoomListResponse(true);

			responseIQ.setContent(responseQuery);

		} else {
			// Chat room members listing
			ChatRoom room = this.chatRooms.get(iq.getTo().getBareJID());

			responseIQ.setFrom(iq.getTo());
			responseIQ.setTo(iq.getFrom());

			IQQuery responseQuery = new IQQuery(XMPPNamespaces.DISCO_ITEM_NAMESPACE);
			responseQuery.setRooms(Arrays.asList(room));
			responseQuery.setRoomItemListResponse(true);

			responseIQ.setContent(responseQuery);
		}

		routeToIOController(responseIQ);
	}

	private void processMessagePacket(Message msgPacket) {

		if (this.chatRooms.containsKey(msgPacket.getTo().getBareJID())) {

			if (msgPacket.getContents() != null) {

				for (MessageContent content : msgPacket.getContents()) {

					if (content.isContentType(MessageContentType.SUBJECT)) {

						updateRoomSubject(msgPacket.getTo().getBareJID(), (MessageSubject) content);

						return;
					}

				}
			}

			broadcastMsgToChatRoomMembers(msgPacket.getTo().getBareJID(), msgPacket);

		} else {

			LOGGER.info("Room {} not available. so message not broadcasted {}",
					msgPacket.getTo().getBareJID().toString(), msgPacket.xml());
		}
	}

	private void updateRoomSubject(BareJID roomJID, MessageSubject subject) {
		ChatRoom room = this.chatRooms.get(roomJID);
		dbService.updateRoomSubject(room.getRoomJID(), subject.getContent());
		room.setSubject(subject.getContent());

		Set<ChatRoomMember> members = room.getMembers();

		if (!CollectionUtils.isNullOrEmpty(members)) {
			for (ChatRoomMember member : members) {
				sendChatRoomSubject(room, member);
			}
		}
	}

	private void processPresencePacket(Presence presence) {
		if (presence.getType() == PresenceType.UNAVAILABLE) {
			leaveChatRoom(presence);

		} else if (presence.getType() == PresenceType.AVAILABLE) {

			updateChatRoom(presence);
		}
	}

	private void leaveChatRoom(Presence presence) {
		ChatRoom chatRoom = this.chatRooms.get(presence.getTo().getBareJID());

		if (chatRoom != null) {
			ChatRoomMember member = chatRoom.getMember(presence.getFrom().getBareJID());

			if (member != null) {
				removeMemberFromRoom(member, chatRoom);

			} else {
				LOGGER.info("Leave chat room request failed because user {} does not exists in Room {} does",
						presence.getFrom().getFullJID(), presence.getTo().getFullJID());

				sendNotAllowedPresenceErrorResponse(presence);
			}

		} else {
			LOGGER.info("Leave chat room request failed for user {} beecause Room {} does not exist",
					presence.getFrom().getFullJID(), presence.getTo().getFullJID());

			sendNotAllowedPresenceErrorResponse(presence);
		}
	}

	private void removeMemberFromRoom(ChatRoomMember member, ChatRoom chatRoom) {
		dbService.removeChatRoomMember(chatRoom.getRoomJID(), member.getUserJID());
		chatRoom.delMember(member.getUserJID());

		member.setRole(Role.NONE);

		sendUnavailablePresence(chatRoom, member);

		LOGGER.info("Member {} is removed from Chat Room {}", member.getUserJID(), chatRoom.getRoomJID());
	}

	private void updateChatRoom(Presence presence) {
		if (isRoomExists(presence.getTo().getBareJID())) {

			ChatRoom room = this.chatRooms.get(presence.getTo().getBareJID());

			if (room.isRoomMember(presence.getFrom().getBareJID())) {
				updateRoomMemberNickName(presence);

			} else {

				joinRoomMember(presence);

			}

		} else {

			createRoom(presence);

		}
	}

	private void joinRoomMember(Presence presence) {
		ChatRoom room = this.chatRooms.get(presence.getTo().getBareJID());

		if (room.getAccessMode() == AccessMode.PRIVATE) {

			LOGGER.info("User {} is not allowed to join group {} because group access mode {}",
					presence.getFrom().getFullJID(), presence.getTo().getFullJID(), room.getAccessMode());

			sendNotAllowedPresenceErrorResponse(presence);

		} else {

			ChatRoomMember member = room.new ChatRoomMember(presence.getFrom(), presence.getTo().getResource(),
					Affiliation.MEMBER, Role.PARTICIPANT);

			if (room.doesNickNameConflict(member)) {
				LOGGER.error("Nickname : {}, conflicts with one of the existing members", member.getNickName());

				String errorResponse = String.format(ERROR_XML, member.getUserJID(), ErrorType.CANCEL.getValue(),
						CONFLICT_ERROR_XML);

				sendPresenceErrorResponse(errorResponse, room.getRoomJID().toJID(), member.getUserJID().toJID());

			} else {
				addRoomMember(room, member);

				exchangingPresence(room, member);

				sendChatRoomSubject(room, member);
			}

		}

	}

	private void sendNotAllowedPresenceErrorResponse(Presence presence) {
		String errorResponse = String.format(ERROR_XML, presence.getTo().getFullJID(), ErrorType.CANCEL.getValue(),
				NOT_ALLOWED_ERROR_XML);

		sendPresenceErrorResponse(errorResponse, presence.getTo(), presence.getFrom());
	}

	private void updateRoomMemberNickName(Presence presence) {
		ChatRoom room = this.chatRooms.get(presence.getTo().getBareJID());

		ChatRoomMember member = room.getMember(presence.getFrom().getBareJID());

		if (!StringUtils.isNullOrEmpty(presence.getTo().getResource())
				&& room.getMemberByNickName(presence.getTo().getResource()) == null) {

			member.setNickName(presence.getTo().getResource());

			dbService.updateNickName(room.getRoomJID(), member.getUserJID(), member.getNickName());

			// Send unavailable presence
			sendUnavailablePresence(room, member);

			// Exchanging presence with new nickName
			exchangingPresence(room, member);

		} else {
			LOGGER.error(
					"User {} is not allowed to join group {} because Nickname : {}, conflicts with one of the existing members",
					presence.getFrom().getFullJID(), presence.getTo().getFullJID(), member.getNickName());

			String errorResponse = String.format(ERROR_XML, member.getUserJID(), ErrorType.CANCEL.getValue(),
					CONFLICT_ERROR_XML);

			sendPresenceErrorResponse(errorResponse, presence.getTo(), presence.getFrom());
		}
	}

	private void createRoom(Presence presence) {
		ChatRoom room = new ChatRoom(presence.getTo().getBareJID(), presence.getTo().getNode(),
				presence.getTo().getNode(), AccessMode.PRIVATE, true);

		addChatRoom(room);

		ChatRoomMember member = room.new ChatRoomMember(presence.getFrom(), presence.getTo().getResource(),
				Affiliation.OWNER, Role.MODERATOR);

		addRoomMember(room, member);

		exchangingPresence(room, member);

		sendChatRoomSubject(room, member);

		LOGGER.debug("Created chat room : {} ", presence.getTo().getNode());
	}

	private void sendUnavailablePresence(ChatRoom room, ChatRoomMember from) {
		for (ChatRoomMember to : room.getMembers()) {
			sendUnavailablePresence(from, to);
		}

		// self
		sendUnavailablePresence(from, from);
	}

	private void sendPresence(ChatRoomMember from, ChatRoomMember to) {
		JID fromJID = from.getRoomJID().toJID();
		fromJID.setResource(from.getNickName());

		Presence presence = new Presence();
		presence.setMuc(true);
		presence.setFrom(fromJID);
		presence.setTo(to.getUserJID().toJID());
		presence.setRoomMember(from);

		routeToIOController(presence);
	}

	private void sendUnavailablePresence(ChatRoomMember from, ChatRoomMember to) {
		JID fromJID = from.getRoomJID().toJID();
		fromJID.setResource(from.getNickName());

		Presence presence = new Presence();
		presence.setMuc(true);
		presence.setType(PresenceType.UNAVAILABLE);
		presence.setFrom(fromJID);
		presence.setTo(to.getUserJID().toJID());
		presence.setRoomMember(from);

		routeToIOController(presence);
	}

	private void sendChatRoomSubject(ChatRoom room, ChatRoomMember member) {
		Message message = new Message();
		message.setType(MessageType.GROUP_CHAT);
		message.setFrom(room.getRoomJID().toJID());
		message.setTo(member.getUserJID().toJID());
		message.addContent(new MessageSubject(room.getSubject()));

		routeToIOController(message);
	}

	private void exchangingPresence(ChatRoom room, ChatRoomMember newMember) {
		for (ChatRoomMember roomMamber : room.getMembers()) {
			sendPresence(newMember, roomMamber);

			if (!roomMamber.getUserJID().equals(newMember.getUserJID()))
				sendPresence(roomMamber, newMember);
		}
	}

	private void broadcastMsgToChatRoomMembers(BareJID roomJID, Message msg) {
		ChatRoom room = this.chatRooms.get(roomJID);

		if (room.isRoomMember(msg.getFrom().getBareJID())) {

			ChatRoomMember sender = room.getMember(msg.getFrom().getBareJID());

			final JID fromJID = new JID(room.getRoomJID(), sender.getNickName());

			for (ChatRoomMember member : room.getMembers()) {

				if (!member.getUserJID().equals(sender.getUserJID())) {

					Message message = ObjectUtils.cloneObject(msg);
					message.setFrom(fromJID);
					message.setTo(member.getUserJID().toJID());

					routeToIOController(message);
				}
			}

		} else {
			LOGGER.info("Sender {} is not member of room {}, So failed to broadcast message {}", msg.getFrom(), roomJID,
					msg.xml());
		}
	}

	public void addChatRoom(ChatRoom chatRoom) {
		dbService.addChatRoom(chatRoom);
		this.chatRooms.put(chatRoom.getRoomJID(), chatRoom);
	}

	private void updateRoomMember(ChatRoom room, ChatRoomMember memberDetails) {
		ChatRoomMember roomMember = room.getMember(memberDetails.getUserJID());

		if (memberDetails.getAffiliation() != null) {
			roomMember.setAffiliation(memberDetails.getAffiliation());
		}

		if (memberDetails.getRole() != null) {
			roomMember.setRole(memberDetails.getRole());
		}

		dbService.updateRoomMemberDetails(room.getRoomJID(), roomMember.getUserJID(), roomMember.getAffiliation(),
				roomMember.getRole());
	}

	private void addRoomMember(ChatRoom room, ChatRoomMember member) {
		room.addMember(member);

		dbService.addChatRoomMember(room.getRoomJID(), member.getUserJID(), member.getNickName(),
				member.getAffiliation(), member.getRole());

	}

	public void sendPresenceErrorResponse(String errorResponseXMl, JID roomJID, JID to) {
		Presence presence = new Presence();
		presence.setMuc(true);
		presence.setType(PresenceType.ERROR);

		presence.setFrom(roomJID);
		presence.setTo(to);

		presence.setError(true);
		presence.setErrorXml(errorResponseXMl);

		routeToIOController(presence);
	}

	public void routeToIOController(Packet packet) {
		PacketEnvelope<Packet> envelope = new PacketEnvelope<Packet>(packet, this.getName());
		envelope.setDestinationComponent(Stringflow.runtime().getCoreComponentJid(ServerFunction.IO_CONTROL));
		router.route(envelope);
	}

	public boolean isChatRoomMember(BareJID roomJID, BareJID memberJID) {
		ChatRoom room = chatRooms.get(roomJID);

		if (room != null) {
			return room.isRoomMember(memberJID);
		}

		return false;
	}

	public boolean isRoomExists(BareJID roomJID) {
		return this.chatRooms.containsKey(roomJID);
	}

	public BareJID getChatRoomMemberJID(BareJID roomJID, String memberNickName) {
		ChatRoom room = this.chatRooms.get(roomJID);

		if (room != null) {
			ChatRoomMember member = room.getMemberByNickName(memberNickName);

			if (member != null) {
				return member.getUserJID();
			}
		}

		return null;
	}

	public String getChatRoomSubject(BareJID roomJID) {
		ChatRoom room = this.chatRooms.get(roomJID);
		return room != null ? room.getSubject() : null;
	}
}
