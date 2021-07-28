package abs.ixi.server.app;

import java.util.List;

/**
 * Holds Application context data such as deployment information,
 * {@link Appfront} configurations, Callbacks and other information
 * <p>
 * {@link ApplicationContext} is immutable object.The interface MUST NOT expose
 * anything which can mutate the state of the instance.
 * </p>
 */
public final class ApplicationContext {
	private AppDeploymentDescriptor descriptor;
	private List<Appfront> appFronts;

	public ApplicationContext() {
	}

	public ApplicationContext(AppDeploymentDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	public AppDeploymentDescriptor getDescriptor() {
		return descriptor;
	}

	public List<Appfront> getAppFronts() {
		return appFronts;
	}

}
