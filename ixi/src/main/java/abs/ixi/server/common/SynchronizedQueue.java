package abs.ixi.server.common;

/**
 * Interface for queue implementations used within the server. All the
 * implementations have to be thread-safe. Essentially, these implementations
 * will be either customizations made over java provided queues or fresh new
 * implementations
 */
public interface SynchronizedQueue<E> {

}
