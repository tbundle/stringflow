package abs.ixi.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.packet.InvalidJabberId;
import abs.ixi.server.packet.JID;
import abs.ixi.server.packet.Packet;
import abs.ixi.server.router.Router;

/**
 * A null implementation of {@link ServerComponent}.
 */
public class NullComponent implements ServerComponent, PacketConsumer, Router {
	private static final Logger LOGGER = LoggerFactory.getLogger(NullComponent.class);

	private static final String NAME = "null-component";

	private JID jid;

	public NullComponent() {
		try {
			this.jid = new JID(NAME + "@" + Stringflow.runtime().domain());
		} catch (InvalidJabberId e) {
			// Ignore it
		}
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public JID getJID() {
		return this.jid;
	}

	@Override
	public void init() {
		// blank implementation.
	}

	@Override
	public void start() throws Exception {
	}

	@Override
	public void shutdown() throws Exception {
	}

	@Override
	public <P extends Packet> boolean route(PacketEnvelope<P> envelope) {
		LOGGER.debug("Routing through NullComponent");
		return true;
	}

	@Override
	public void subscribe(PacketConsumer component) {
		// do-nothing
	}

	@Override
	public boolean submit(PacketEnvelope<? extends Packet> envelope) {
		return false;
	}

}
