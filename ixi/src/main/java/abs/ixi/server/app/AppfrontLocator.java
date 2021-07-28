package abs.ixi.server.app;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A service locator pattern to locate applicable {@link Appfront} instance for
 * a request. Appfront uri segments are regular expressions therefore
 * {@code AppfrontLocator} matches them as patterns against the
 * {@link XmppRequest} endpoint
 */
public class AppfrontLocator {
	private static final Logger LOGGER = LoggerFactory.getLogger(AppfrontLocator.class.getName());
	private static final String SLASH = "/";
	
	private Map<Pattern, Appfront> afmap;

	public AppfrontLocator(Map<String, Appfront> afmap) {
		if (afmap == null || afmap.size() == 0) {
			throw new ApplicationInitializationError("apfront mapping patterns are null");
		}

		this.afmap = new HashMap<>();

		for (Entry<String, Appfront> entry : afmap.entrySet()) {
			try {
				Pattern p = Pattern.compile(entry.getKey());

				this.afmap.put(p, entry.getValue());

			} catch (PatternSyntaxException e) {
				LOGGER.error("Invalid urisegment pattern {}", e.getMessage());
			}
		}
	}

	public Appfront locate(RequestContext ctx) {
		for (Entry<Pattern, Appfront> entry : afmap.entrySet()) {

			if (entry.getKey().matcher(ctx.getUri().getPath().split(SLASH)[1]).matches()) {
				return entry.getValue();
			}
		}

		return null;
	}
}
