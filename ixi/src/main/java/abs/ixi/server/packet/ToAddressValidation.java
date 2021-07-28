package abs.ixi.server.packet;

import abs.ixi.server.ValidationError;

public class ToAddressValidation extends XMPPValidation {

	@Override
	public void validate(Packet packet, String trigger, Object... context) throws ValidationError {
		// TODO Auto-generated method stub

	}

	// @Override
	// public void validate(XMPPPacket packet, String trigger, Object...context)
	// throws ValidationError {
	//
	// if(packet != null) {
	// JID to = packet.getTo();O
	//
	// if(to == null) {
	// throw new ValidationError(XMPPError.INVALID_TO_ADDRESS);
	//
	// } else {
	// //TODO : address validation
	// }
	//
	// }

	// }

}
