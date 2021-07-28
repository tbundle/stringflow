package abs.ixi.server.couplet;

import abs.ixi.server.etc.PushNotificationBroker;
import abs.ixi.server.packet.xmpp.BareJID;

/**
 * Stringflow custom packet to request push notification. These packets are
 * processed by {@link PushNotificationBroker} server component.
 * 
 * @author Yogi
 *
 */
public class ExternalNotification extends Couplet {
	private static final long serialVersionUID = -828744657306472541L;

	/**
	 * Trigger {@link Couplet}; the assumption is that a push notification is
	 * triggered by another couplet in the server. Can be null.
	 */
	private Couplet trigger;

	/**
	 * {@link BareJID} of the recipient. This is a string represenatation of
	 * {@link BareJID}. Can't be null.
	 */
	private String destination;

	/**
	 * Push Notificaion message (content); Can't be null.
	 */
	private String msg;

	public ExternalNotification(String destination, String msg) {
		this(null, destination, msg);
	}

	public ExternalNotification(Couplet trigger, String destination, String msg) {
		this.trigger = trigger;
		this.destination = destination;
		this.msg = msg;
	}

	public String getDestination() {
		return destination;
	}

	public String getNotificationMessage() {
		return this.msg;
	}

	public Couplet getTrigger() {
		return trigger;
	}
}
