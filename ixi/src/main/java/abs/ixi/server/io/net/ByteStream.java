package abs.ixi.server.io.net;

/**
 * An enumeration to indicate the type of byte stream; which is another name for
 * a protocol.
 * 
 * @author Yogi
 *
 */
public enum ByteStream {
    /**
     * As the name suggests, MIME is exchanged on when layer-7 protocol is MIME
     */
    MIME,

    /**
     * XMPP Stream corresponds to XMPP protocol
     */
    XMPP,

    /**
     * BOSH corresponds to BOSH specification defined as an extension of XMPP
     * protocol
     */
    BOSH;

}
