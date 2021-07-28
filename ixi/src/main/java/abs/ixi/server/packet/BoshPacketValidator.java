package abs.ixi.server.packet;

import java.util.ArrayList;
import java.util.List;

import abs.ixi.server.RequestValidation;
import abs.ixi.server.ValidationError;
import abs.ixi.server.packet.xmpp.BOSHBody;
import abs.ixi.server.packet.xmpp.XMPPPacket;
import abs.ixi.util.CollectionUtils;

public class BoshPacketValidator implements PacketValidator<BOSHBody> {
	private List<RequestValidation> validations;
	private XMPPPacketValidator xmppPacketValidator;

	private static BoshPacketValidator instance;

	private BoshPacketValidator() {
		addBoshBodyValidations();
		xmppPacketValidator = PacketValidatorFactory.getXmppPacketValidator();
	}

	private void addBoshBodyValidations() {
		validations = new ArrayList<>();
		validations.add(new StreamStateValidation());
		validations.add(new ToAddressValidation());
		validations.add(new FromAddressValidation());
	}

	public static BoshPacketValidator getInstance() {
		if (instance == null) {
			synchronized (instance) {
				instance = new BoshPacketValidator();
			}
		}

		return instance;
	}

	@Override
	public void validate(BOSHBody boshBody, String trigger, Object... args) throws ValidationError {
		if (!CollectionUtils.isNullOrEmpty(validations)) {

			for (RequestValidation validation : validations) {
				validation.validate(boshBody, trigger, args);
			}
		}

		if (!CollectionUtils.isNullOrEmpty(boshBody.getXmppPackets())) {

			for (XMPPPacket xmppPacket : boshBody.getXmppPackets()) {
				xmppPacketValidator.validate(xmppPacket, null, new Object[] { args });
			}
		}
	}

}
