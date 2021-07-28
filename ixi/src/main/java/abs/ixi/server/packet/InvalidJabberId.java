package abs.ixi.server.packet;

/**
 * Exception to indicate invalid Jabber Id. JabberId format has been defined in
 * XMPP protocol specifications.
 * 
 * @author XBBNNW0
 *
 */
public class InvalidJabberId extends XMPPException {
    private static final long serialVersionUID = 1L;

    public InvalidJabberId(String msg) {
	super(msg);
    }
}
