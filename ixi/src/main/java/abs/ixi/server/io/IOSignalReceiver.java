package abs.ixi.server.io;

import java.nio.channels.Selector;

/**
 * An interface to allow channel state changes to bubble up in the server.
 * Entities connected to channels can always change the state of the underlying
 * channel for example closing them; however channel state change may also be
 * triggered from network layer (a socket receiveing FIN from client). Therefore
 * in such cases, lower level network classes can bubble up such state changes
 * using this interface.
 * <p>
 * All these methods are signals generated by a network channel.
 * </p>
 * 
 * @author Yogi
 *
 */
public interface IOSignalReceiver {
    /**
     * A Signal generated when new bytes have been read from the channel. These
     * bytes will be stored/copied into some byte stream which, by assumption,
     * observer will be aware of.
     */
    public void bytesRead(int bytesRead);

    /**
     * A signal generated when network channel is disconnected.
     */
    public void channelDisconnected();

    /**
     * A signal generated when network layers marks a network channel dead. It
     * could be triggered in various situations; typically the host on other
     * side chose to close the connection.
     */
    public void channelDead();

    /**
     * A Signal is generated when network layer marked that Socket channel is
     * registered with {@link Selector}. On it Signal receiver can choose to do
     * something.
     */
    public void channelRegistered();

}
