package abs.ixi.server.packet;

import abs.ixi.util.StringUtils;

/**
 * Represents XML Namespace
 */
public class Namespace {
	public static final String DEFAULT_NAMESPACE_PREFIX = "default";
	
	private String prefix;
	private String uri;

	public Namespace(String prefix, String uri) {
		this.prefix = StringUtils.isNullOrEmpty(prefix) ? DEFAULT_NAMESPACE_PREFIX : prefix;
		this.uri = uri;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	@Override
	public String toString() {
		return prefix + ":" + uri;
	}
}
