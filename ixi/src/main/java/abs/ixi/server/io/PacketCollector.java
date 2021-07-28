package abs.ixi.server.io;

import abs.ixi.server.io.multipart.MimePacket;
import abs.ixi.server.packet.xmpp.IQ;
import abs.ixi.server.packet.xmpp.SASLSuccess;

/**
 * A {@link PacketCollector} collects pakcet from a packet generator. It does
 * not operate with them at all.
 * 
 * @author Yogi
 *
 */
public interface PacketCollector<T> {
    /**
     * Collects given packet; does not do any processing.
     * 
     * @param packet
     */
    public void collect(T packet);

    /**
     * Collect the packet which is inbound and most likely will be subjected to
     * further processing.
     * 
     * @param packet
     */
    public void collectInbound(T packet);

    /**
     * Collect packet will follow oubound path
     * 
     * @param packet
     */
    public void collectOutbound(T packet);

    /**
     * Collect SASL Success packet.
     * 
     * @param saslSuccess
     */
    public void collectSASLSuccess(SASLSuccess saslSuccess);

    /**
     * Collect pong IQ packet.
     * 
     * @param iq
     */
    public void collectPongIQ(IQ iq);

    /**
     * collect mime packet
     * 
     * @param mimePacket
     */
    public void collectMimePacket(MimePacket mimePacket);
}
