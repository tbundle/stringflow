package abs.ixi.server.couplet;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;

import abs.ixi.util.UUIDGenerator;

/**
 * Base class of all the data packet representations within server. A couplet is
 * BSON structure with Stringflow specific sementics.
 * 
 * @author Yogi
 *
 */
public abstract class Couplet implements Serializable {
	private static final long serialVersionUID = -4959909801368344260L;

	/**
	 * Unique id of this couplet; Can't be null. Every Couplet is uniquely
	 * identifiable in server.
	 */
	protected final String id;

	/**
	 * Epoch milliseconds when packet was created in server. Can't be null.
	 */
	protected long createTime;

	/**
	 * Constructor to instantiate a couplet with a randomly generated id and
	 * current instant as create time.
	 */
	public Couplet() {
		this.id = UUIDGenerator.uuid();
		this.createTime = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}

	/**
	 * Every {@link Couplet} has an equivalent BSON representation
	 * 
	 * @author Yogi
	 *
	 */
	public enum Type {
		EXTERNAL_NOTIFICATION("exn");

		private String root;

		private Type(String root) {
			this.root = root;
		}

		public String getRoot() {
			return root;
		}

	}
}
