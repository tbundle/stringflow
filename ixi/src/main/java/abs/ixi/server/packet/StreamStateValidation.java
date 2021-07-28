package abs.ixi.server.packet;

import abs.ixi.server.ValidationError;
import abs.ixi.server.io.StreamContext;
import abs.ixi.server.io.StreamContext.StreamState;
import abs.ixi.server.packet.Packet.PacketXmlElement;
import abs.ixi.server.packet.xmpp.AckPacket;
import abs.ixi.server.packet.xmpp.AckRequestPacket;
import abs.ixi.server.packet.xmpp.BOSHBody;
import abs.ixi.server.packet.xmpp.IQ;
import abs.ixi.server.packet.xmpp.IQContent;
import abs.ixi.server.packet.xmpp.IQQuery;
import abs.ixi.server.packet.xmpp.Message;
import abs.ixi.server.packet.xmpp.Presence;
import abs.ixi.server.packet.xmpp.SASLAuthPacket;
import abs.ixi.server.packet.xmpp.SASLChallengeResponse;
import abs.ixi.server.packet.xmpp.SMEnablePacket;
import abs.ixi.server.packet.xmpp.SMResumePacket;
import abs.ixi.server.packet.xmpp.StartTlsPacket;
import abs.ixi.server.packet.xmpp.StreamHeader;
import abs.ixi.server.packet.xmpp.IQContent.IQContentType;
import abs.ixi.util.StringUtils;

public class StreamStateValidation extends XMPPValidation {

	@Override
	public void validate(Packet packet, String trigger, Object... context) throws ValidationError {

		if (packet != null) {

			StreamState streamState = StreamState.UNKNOWN;

			if (context != null) {
				for (Object obj : context) {
					if (obj.getClass().isAssignableFrom(StreamContext.class)) {
						StreamContext sc = (StreamContext) context[0];
						streamState = sc.getState();
					}
				}
			}

			if (streamState == StreamState.UNKNOWN) {
				throw new ValidationError(XMPPError.INTERNAL_SERVER);
			}

			if (packet.isBoshBodyPacket()) {

				BOSHBody boshBody = (BOSHBody) packet;
				validateBoshBody(boshBody, streamState);

			} else if (packet.getXmlElementName() == PacketXmlElement.STREAM_HEADER) {

				StreamHeader streamHeader = (StreamHeader) packet;
				validateStreamHeader(streamHeader, streamState);

			} else if (packet.getXmlElementName() == PacketXmlElement.START_TLS) {

				StartTlsPacket tls = (StartTlsPacket) packet;
				validateStartTLSPacket(tls, streamState);

			} else if (packet.getXmlElementName() == PacketXmlElement.SASL_AUTH) {

				SASLAuthPacket authPacket = (SASLAuthPacket) packet;
				validateSASLAuthPacket(authPacket, streamState);

			} else if (packet.getXmlElementName() == PacketXmlElement.SASL_CHALALNGE_RESPONSE) {

				SASLChallengeResponse saslResponse = (SASLChallengeResponse) packet;
				validateSASLChallangeResponsePacket(saslResponse, streamState);

			} else if (packet.getXmlElementName() == PacketXmlElement.IQ) {

				IQ iq = (IQ) packet;
				validateIQPacket(iq, streamState);

			} else if (packet.getXmlElementName() == PacketXmlElement.MESSAGE) {

				Message message = (Message) packet;
				validateMessagePacket(message, streamState);

			} else if (packet.getXmlElementName() == PacketXmlElement.PRESENCE) {

				Presence presence = (Presence) packet;
				validatePresencePacket(presence, streamState);

			} else if (packet.getXmlElementName() == PacketXmlElement.SM_ENABLE) {

				SMEnablePacket smEnablePacket = (SMEnablePacket) packet;
				validateSMEnablePacket(smEnablePacket, streamState);

			} else if (packet.getXmlElementName() == PacketXmlElement.SM_RESUME) {

				SMResumePacket smResumePacket = (SMResumePacket) packet;
				validateStreamResumePacket(smResumePacket, streamState);

			} else if (packet.getXmlElementName() == PacketXmlElement.ACK) {

				AckPacket ack = (AckPacket) packet;
				validateAckPacket(ack, streamState);

			} else if (packet.getXmlElementName() == PacketXmlElement.ACK_REQUEST) {

				AckRequestPacket ackRequestPacket = (AckRequestPacket) packet;
				validateAckRequestPacket(ackRequestPacket, streamState);

			} else {
				throw new ValidationError(XMPPError.NOT_AUTHORISED);
			}

		}

	}

	private void validateBoshBody(BOSHBody boshBody, StreamState streamState) {

		if (boshBody.isStreamStartRequest() && streamState != StreamState.INITIATED) {
			throw new ValidationError(XMPPError.STREAM_HEADER_NOT_EXPECTED);

		} else if (boshBody.isStreamRestart()) {
			if (streamState != StreamState.TLS_DONE && streamState != StreamState.SASL_DONE) {
				throw new ValidationError(XMPPError.STREAM_HEADER_NOT_EXPECTED);
			}
		}

	}

	private void validateStreamHeader(StreamHeader streamHeader, StreamState streamState) throws ValidationError {

		if (!streamHeader.isCloseStream()) {
			if (streamState != StreamState.INITIATED && streamState != StreamState.TLS_DONE
					&& streamState != StreamState.SASL_DONE) {

				throw new ValidationError(XMPPError.STREAM_HEADER_NOT_EXPECTED);
			}
		}
	}

	private void validateStartTLSPacket(StartTlsPacket tls, StreamState streamState) {
		if (streamState != StreamState.TLS_ADVERTISED) {
			throw new ValidationError(XMPPError.START_TLS_NOT_EXPECTED);
		}
	}

	private void validateSASLAuthPacket(SASLAuthPacket authPacket, StreamState streamState) throws ValidationError {

		if (streamState != StreamState.SASL_ADVERTISED && streamState != StreamState.SASL_FAILED)
			throw new ValidationError(XMPPError.AUTH_NOT_EXPECTED);
	}

	private void validateSASLChallangeResponsePacket(SASLChallengeResponse saslResponse, StreamState streamState) {

		if (streamState != StreamState.SASL_STARTED)
			throw new ValidationError(XMPPError.SASL_RESPONSE_NOT_EXPECTED);
	}

	private void validateAckPacket(AckPacket ack, StreamState streamState) {
		if (streamState != StreamState.OPEN) {
			throw new ValidationError(XMPPError.ACK_NOT_EXPECTED);
		}
	}

	private void validateAckRequestPacket(AckRequestPacket ackRequestPacket, StreamState streamState) {
		if (streamState != StreamState.OPEN) {
			throw new ValidationError(XMPPError.ACK_REQUEST_NOT_EXPECTED);
		}
	}

	private void validateStreamResumePacket(SMResumePacket smResumePacket, StreamState streamState) {
		if (streamState != StreamState.RESOURCE_BIND_AND_SM_ADVERTISED) {
			throw new ValidationError(XMPPError.STREAM_RESUME_NOT_EXPECTED);
		}
	}

	private void validateSMEnablePacket(SMEnablePacket smEnablePacket, StreamState streamState) {
		if (streamState != StreamState.OPEN) {
			throw new ValidationError(XMPPError.STREAM_MANAGEMENT_NOT_EXPECTED);
		}
	}

	private void validatePresencePacket(Presence presence, StreamState streamState) {

		if (streamState != StreamState.OPEN)
			throw new ValidationError(XMPPError.PRESENCE_NOT_EXPECTED);
	}

	private void validateMessagePacket(Message message, StreamState streamState) throws ValidationError {

		if (streamState != StreamState.OPEN)
			throw new ValidationError(XMPPError.MESSAGE_NOT_EXPECTED);

	}

	private void validateIQPacket(IQ iq, StreamState streamState) throws ValidationError {
		IQContent iqContent = iq.getContent();

		if (iqContent != null) {

			if (iqContent.getType() == IQContentType.REQUEST) {
				// MultiConnection IQ for existing stream
				if (streamState == StreamState.INITIATED && iq.getSid() != null) {
					if (iq.getFrom() != null) {
						// JID fullJID = new JID(iq.getFrom(), iq.getSid());

						// if
						// (SessionManager.getInstance().getIOService(fullJID)
						// == null) {
						// throw new ValidationError(XMPPError.IQ_NOT_EXPECTED);
						// }
					}

				} else {

					if (streamState != StreamState.OPEN) {
						throw new ValidationError(XMPPError.IQ_NOT_EXPECTED);
					}
				}

			} else if (iqContent.getType() == IQContentType.QUERY) {
				IQQuery query = (IQQuery) iq.getContent();

				if (StringUtils.safeEquals(query.getXmlns(), XMPPNamespaces.USER_REGISTER_NAMESPACE, false)) {

					if (!(streamState == StreamState.INITIATED || streamState == StreamState.OPEN))
						throw new ValidationError(XMPPError.IQ_NOT_EXPECTED);

				} else {
					if (streamState != StreamState.OPEN) {
						throw new ValidationError(XMPPError.IQ_NOT_EXPECTED);
					}
				}

			} else if (iqContent.getType() == IQContentType.BIND) {

				if (streamState != StreamState.RESOURCE_BIND_AND_SM_ADVERTISED
						&& streamState != StreamState.STREAM_RESUME_FAILED) {
					throw new ValidationError(XMPPError.RESOURCE_BIND_NOT_EXPECTED);
				}

			} else {
				if (streamState != StreamState.OPEN) {
					throw new ValidationError(XMPPError.IQ_NOT_EXPECTED);
				}
			}

		}

		// if (iq.getContent() != null &&
		// IQResourceBind.class.isAssignableFrom(iq.getContent().getClass())
		// && streamState != StreamState.RESOURCE_BIND_AND_SM_ADVERTISED ) {
		//
		// throw new ValidationError(XMPPError.RESOURCE_BIND_NOT_EXPECTED);
		// }
		//
		// if (streamState == StreamState.INITIATED) {
		// if (IQQuery.class.isAssignableFrom(iq.getContent().getClass())) {
		// IQQuery query = (IQQuery) iq.getContent();
		//
		// if (!StringUtils.safeEquals(query.getXmlns(),
		// XMPPNamespaces.USER_REGISTER_NAMESPACE, false)) {
		// throw new ValidationError(XMPPError.IQ_NOT_EXPECTED);
		// }
		//
		// } else if (iq.getSid() != null && iq.getContent().getType() ==
		// IQContentType.REQUEST) {
		// // MultiConnection IQ for existing stream
		// if (iq.getFrom() != null) {
		// JID fullJID = new JID(iq.getFrom(), iq.getSid());
		//
		// if (SessionManager.getInstance().getIOService(fullJID) == null) {
		// throw new ValidationError(XMPPError.IQ_NOT_EXPECTED);
		// }
		// }
		//
		// } else {
		// throw new ValidationError(XMPPError.IQ_NOT_EXPECTED);
		// }
		//
		// }
		//
		// if (IQPing.class.isAssignableFrom(iq.getContent().getClass()) &&
		// streamState != StreamState.OPEN) {
		// throw new ValidationError(XMPPError.NOT_AUTHORISED);
		// }

	}
}
