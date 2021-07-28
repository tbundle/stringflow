package abs.ixi.server.app;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;

import abs.ixi.server.packet.JID;

/**
 * This is READ-ONLY object.
 */
public final class RequestContext {
	private JID source;
	private URI uri;
	private Calendar createTime;

	protected RequestContext(JID source, String uri) throws URISyntaxException {
		this.source = source;
		this.uri = new URI(uri);
		this.createTime = Calendar.getInstance();
	}

	public JID getSource() {
		return source;
	}

	public URI getUri() {
		return this.uri;
	}

	public Calendar getCreateTime() {
		return createTime;
	}

}
