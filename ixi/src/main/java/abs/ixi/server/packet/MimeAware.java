package abs.ixi.server.packet;

/**
 * Interface to induce MIME awareness in a {@link Packet}
 */
public interface MimeAware {
	default public boolean hasMime() {
		return false;
	}
}
