package abs.ixi.server.app;

/**
 * {@code RequestReceiver} receives the request from {@link Appfront}. This is
 * useful only if application chooses to use {@link DefaultAppfront} in which
 * case, application can directly inject a {@code RequestReceiver} instance and
 * {@link DefaultAppfront} will delegate requests to {@code RequestReceiver}
 */
public interface RequestReceiver {

}
