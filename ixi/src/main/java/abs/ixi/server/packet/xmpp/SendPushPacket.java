package abs.ixi.server.packet.xmpp;

import abs.ixi.server.packet.JID;

public class SendPushPacket extends ControlPacket {
    private static final long serialVersionUID = 1L;

    private JID userJID;

    public SendPushPacket(JID userJID) {
	this.userJID = userJID;
    }

    public JID getUserJID() {
	return userJID;
    }

    public void setUserJID(JID userJID) {
	this.userJID = userJID;
    }

    @Override
    public String getSourceId() {
	return null;
    }

}
