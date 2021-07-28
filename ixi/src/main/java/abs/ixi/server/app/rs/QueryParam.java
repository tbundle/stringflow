package abs.ixi.server.app.rs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates a parameter in query string of the URI
 */
@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryParam {
	/**
	 * Binds name of the query parameter
	 */
	String name();
}
