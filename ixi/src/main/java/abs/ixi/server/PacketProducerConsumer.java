package abs.ixi.server;

import abs.ixi.server.common.InstantiationException;
import abs.ixi.server.etc.conf.Configurations;
import abs.ixi.server.router.PacketRouter;
import abs.ixi.server.router.Router;

public abstract class PacketProducerConsumer extends ConsumerComponent implements PacketProducer {
	/**
	 * {@link PacketRouter} instannce running inside same node
	 */
	protected Router router;

	public PacketProducerConsumer(String name, Configurations conf, Router router) throws InstantiationException {
		super(name, conf);
		this.router = router;
	}

}
