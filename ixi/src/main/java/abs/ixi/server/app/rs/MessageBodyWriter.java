package abs.ixi.server.app.rs;

/**
 * Interface to inject object serialization (not necessarily Java Serialization)
 * mechanism which can be used by server runtime to generate network bytes out
 * of the entity (Object) returned in response.
 * <p>
 * Server runtime does not offer any assistance in terms of creating network
 * bytes out of response object; however for ease of use, server offers hooks to
 * plug-in JAX-RS providers for message reading and writing.
 * </p>
 * <p>
 * Application developer can provide two types of implementations for both
 * message writer and readers (also known as request reader and response
 * writer): 1)implementation of JAX-RS reader and writer interfaces
 * ({@link javax.ws.rs.ext.MessageBodyWriter} & {@link MessageBodyReader})
 * </p>
 */
public interface MessageBodyWriter {

}
