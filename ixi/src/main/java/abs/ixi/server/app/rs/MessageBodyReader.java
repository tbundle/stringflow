package abs.ixi.server.app.rs;

/**
 * {@code MessageBodyReader} outlines contract for a reader which server runtime
 * will use to read request body and will create Java objects.
 * <p>
 * Application layer in the server borrows concepts and ideas from JAX-RS
 * implementation; in fact, JAX-RS can easily be tweaked to work with Stringflow
 * server. Stringlfow application layer supports both {@code MessageBodyReader}
 * defined within Stringflow and also {@link javax.ws.rs.ext.MessageBodyReader}.
 * Application developer can hook an implementation of any of these two as a
 * provider to stringflow server runtime.
 * </p>
 */
public interface MessageBodyReader {

}
