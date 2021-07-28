package abs.ixi.server.packet;

import abs.ixi.server.ValidationError;

public class FromAddressValidation extends XMPPValidation {

	@Override
	public void validate(Packet packet, String trigger, Object... context) throws ValidationError {

	}

	// @Override
	// public void validate(XMPPPacket packet, String trigger, Object...
	// context) throws ValidationError {
	// StreamContext ctx = null;
	//
	// if (context != null) {
	// for (Object obj : context) {
	// if (obj.getClass().isAssignableFrom(StreamContext.class)) {
	// ctx = (StreamContext) context[0];
	// }
	// }
	// }

	// if (packet.getFrom() != null && ctx.getFrom() != null
	// && !StringUtils.safeEquals(packet.getFrom().getBareJID(),
	// ctx.getFrom().getBareJID())) {
	//
	// throw new ValidationError(XMPPError.INVALID_FROM_ADDRESS);
	//
	// }

	// }

}
