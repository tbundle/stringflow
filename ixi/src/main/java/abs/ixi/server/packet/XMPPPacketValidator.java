package abs.ixi.server.packet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.RequestValidation;
import abs.ixi.server.ValidationError;
import abs.ixi.server.packet.xmpp.AckPacket;
import abs.ixi.server.packet.xmpp.AckRequestPacket;
import abs.ixi.server.packet.xmpp.IQ;
import abs.ixi.server.packet.xmpp.Message;
import abs.ixi.server.packet.xmpp.Presence;
import abs.ixi.server.packet.xmpp.SASLAuthPacket;
import abs.ixi.server.packet.xmpp.SASLChallengeResponse;
import abs.ixi.server.packet.xmpp.SMEnablePacket;
import abs.ixi.server.packet.xmpp.SMResumePacket;
import abs.ixi.server.packet.xmpp.StartTlsPacket;
import abs.ixi.server.packet.xmpp.StreamHeader;
import abs.ixi.server.packet.xmpp.XMPPPacket;
import abs.ixi.util.CollectionUtils;

public class XMPPPacketValidator implements PacketValidator<XMPPPacket> {
	protected static final Logger LOGGER = LoggerFactory.getLogger(XMPPPacketValidator.class);

	protected final Map<String, List<RequestValidation>> validations = new HashMap<>();

	private static XMPPPacketValidator instance;

	public static XMPPPacketValidator getInstance() {
		if (instance == null) {
			synchronized (XMPPPacketValidator.class) {
				instance = new XMPPPacketValidator();
			}
		}

		return instance;
	}

	private XMPPPacketValidator() {
		addStreamHeaderValidations();
		addStartTLSValidations();
		addSASLAuthValidations();
		addSASLChallangeResponseValidations();
		addIQValidations();
		addPresenceValidations();
		addMessageValidations();
		addSMEnableValidations();
		addSMResumeValidations();
		addAckValidations();
		addAckRequestValidations();
	}

	@Override
	public void validate(XMPPPacket packet, String trigger, Object... args) throws ValidationError {
		if (!CollectionUtils.isNullOrEmpty(validations.get(packet.getClass().getName()))) {

			for (RequestValidation validation : validations.get(packet.getClass().getName())) {
				validation.validate(packet, trigger, args);
			}
		}
	}

	private void addAckRequestValidations() {
		LOGGER.debug("Adding Ack Request validetors ");

		List<RequestValidation> validators = new ArrayList<>();
		validators.add(new StreamStateValidation());

		validations.put(AckRequestPacket.class.getName(), validators);
	}

	private void addAckValidations() {
		LOGGER.debug("Adding Ack validetors ");

		List<RequestValidation> validators = new ArrayList<>();
		validators.add(new StreamStateValidation());

		validations.put(AckPacket.class.getName(), validators);
	}

	private void addSMResumeValidations() {
		LOGGER.debug("Adding SM resume validetors ");

		List<RequestValidation> validators = new ArrayList<>();
		validators.add(new StreamStateValidation());

		validations.put(SMResumePacket.class.getName(), validators);
	}

	private void addSMEnableValidations() {
		LOGGER.debug("Adding start tls packet validetors ");

		List<RequestValidation> validators = new ArrayList<>();
		validators.add(new StreamStateValidation());

		validations.put(SMEnablePacket.class.getName(), validators);

	}

	private void addStartTLSValidations() {
		LOGGER.debug("Adding start tls packet validetors ");

		List<RequestValidation> validators = new ArrayList<>();
		validators.add(new StreamStateValidation());

		validations.put(StartTlsPacket.class.getName(), validators);
	}

	private void addMessageValidations() {
		LOGGER.debug("Adding Message packet validetors ");

		List<RequestValidation> validators = new ArrayList<>();
		validators.add(new StreamStateValidation());

		validations.put(Message.class.getName(), validators);
	}

	private void addPresenceValidations() {
		LOGGER.debug("Adding Presence packet validetors ");

		List<RequestValidation> validators = new ArrayList<>();
		validators.add(new StreamStateValidation());

		validations.put(Presence.class.getName(), validators);
	}

	private void addSASLAuthValidations() {
		LOGGER.debug("Adding SASL Auth packet validations ");

		List<RequestValidation> validators = new ArrayList<>();
		validators.add(new StreamStateValidation());

		validations.put(SASLAuthPacket.class.getName(), validators);

	}

	private void addSASLChallangeResponseValidations() {
		LOGGER.debug("Adding SASL Response packet validations ");

		List<RequestValidation> validators = new ArrayList<>();
		validators.add(new StreamStateValidation());

		validations.put(SASLChallengeResponse.class.getName(), validators);

	}

	private void addIQValidations() {
		LOGGER.debug("Adding IQ validations ");

		List<RequestValidation> validators = new ArrayList<>();
		validators.add(new StreamStateValidation());

		validations.put(IQ.class.getName(), validators);
	}

	private void addStreamHeaderValidations() {
		LOGGER.debug("Adding StreamHeader validations ");

		List<RequestValidation> validators = new ArrayList<>();
		validators.add(new StreamStateValidation());
		validators.add(new ToAddressValidation());
		validators.add(new FromAddressValidation());

		validations.put(StreamHeader.class.getName(), validators);

	}

}
