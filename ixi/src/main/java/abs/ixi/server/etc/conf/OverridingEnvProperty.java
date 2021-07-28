package abs.ixi.server.etc.conf;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation which is used on class fields to capture the environment
 * property name whose value, if present, will ovveride then value of this
 * property.
 * 
 * @author Yogi
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface OverridingEnvProperty {
	String value();
}
