package abs.ixi.server.app.rs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates a resource operation parameter as URI segment parameter. The value
 * for such parameters are extracted from URI.
 */
@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface URISegmentParam {
	/**
	 * Binds name of the URI segment parameter
	 */
	String name();

}
