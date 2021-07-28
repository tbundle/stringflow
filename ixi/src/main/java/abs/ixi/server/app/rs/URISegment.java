package abs.ixi.server.app.rs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@Code URISegment} annotation identifies URI segment for which a resource
 * class or method will serve request for.
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface URISegment {
    /**
     * Returns uri segment string associated with the annotated resource class
     * or method. The segment should specified should not include matrix
     * parameters.
     */
    public String value();
}
