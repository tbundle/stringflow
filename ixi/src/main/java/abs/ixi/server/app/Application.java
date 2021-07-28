package abs.ixi.server.app;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.app.AppDeploymentDescriptor.AppfrontInfo;
import abs.ixi.server.common.TaskRunner;
import abs.ixi.util.CollectionUtils;
import abs.ixi.util.StringUtils;

/**
 * Represents deployed application within server. There will be just one
 * instance of this class created for each deployed application.
 * 
 * Application publish address is the address which marks start of the URI
 * end-points served by this application.
 */
public final class Application implements Deployable {
	private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

	private static final String AFTER_INIT = "after-init";
	private static final String BEFORE_START = "before-start";
	private static final String BEFORE_SHUTDOWN = "before-shutdown";

	private String name;
	private String publishAddress;
	private Map<String, Appfront> appFronts;
	private Map<LifeCycleEvent, ApplicationListener> listeners;
	private AppfrontLocator locator;
	private ApplicationContext ctx;

	private ResponseForwarder responseForwarder;

	public Application(ApplicationContext ctx, ResponseForwarder forwarder) throws ApplicationError {
		this.ctx = ctx;
		String appName = ctx.getDescriptor().getName();
		String publishAddress = ctx.getDescriptor().getPublishAddress();

		if (StringUtils.isNullOrEmpty(publishAddress)) {
			LOGGER.warn("Application publish address is missing");
			throw new ApplicationError("Application publish address is missing");
		}

		if (!StringUtils.isNullOrEmpty(appName)) {
			this.name = appName;
		}

		this.publishAddress = publishAddress;

		this.listeners = new HashMap<>();
		this.appFronts = new HashMap<>();

		this.responseForwarder = forwarder;
	}

	@Override
	public String getPublishAddress() {
		return publishAddress;
	}

	public Map<String, Appfront> getAppFronts() {
		return appFronts;
	}

	public Map<LifeCycleEvent, ApplicationListener> getListeners() {
		return listeners;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void deploy() throws ApplicationError {
		try {
			this.init();
		} catch (Exception e) {
			LOGGER.error("Failed to initialize application {}", this.name, e);
			throw new ApplicationInitializationError(e);
		}

		try {
			this.start();
		} catch (Exception e) {
			LOGGER.error("Failed to start application {}", this.name, e);
			throw new ApplicationStartupError(e);
		}

	}

	@Override
	public void init() throws Exception {
		addAppFronts();

		this.locator = new AppfrontLocator(appFronts);

		addListeners();

		// Listener callback is invoked after server finishes application
		// initialization So any code need to written above the call

		if (listeners.get(LifeCycleEvent.INIT) != null) {
			LOGGER.info("Executing onInit callback for application {}", name);
			listeners.get(LifeCycleEvent.INIT).onInit();
		}

	}

	@Override
	public void start() throws Exception {
		if (listeners.get(LifeCycleEvent.START) != null) {
			LOGGER.info("Executing onStart callback for application {}", name);
			listeners.get(LifeCycleEvent.START).onStart();
		}

		// Listener callback is executed before the start sequence. So rest of
		// the code goes here

		// initializing appfronts
		for (Appfront appfront : appFronts.values()) {
			appfront.init();
		}
	}

	@Override
	public void shutdown() throws Exception {
		if (listeners.get(LifeCycleEvent.DESTROY) != null) {
			LOGGER.info("Executing onDestroy callback for application {}", name);

			listeners.get(LifeCycleEvent.DESTROY).onDestroy();
		}

		// Listener callback is invoked before servers initiates application
		// destruction so any server side code must be written below the call
	}

	@Override
	public void process(RequestContainer<? extends ApplicationRequest> container, RequestContext ctx) {
		LOGGER.info("Application request >> : {}", container);

		// TODO Fill in more information into RequestContext if required.

		Appfront front = this.locator.locate(ctx);

		LOGGER.info("Application front >> : {}", front);

		if (front != null) {
			XmppRequest request = (XmppRequest) container.getRequest();

			TaskRunner.getInstance().submit(new ExecutionContainer(request, front, ctx));

			// TODO We are not doing anything with Future right now
			// We may need it in future though

		} else {
			// TODO build error response and return it to the user
		}
	}

	private void addAppFronts() throws ApplicationInitializationError {
		List<AppfrontInfo> appfrontInfoList = this.ctx.getDescriptor().getAppfronts();

		if (!CollectionUtils.isNullOrEmpty(appfrontInfoList)) {

			for (AppfrontInfo appfrontInfo : appfrontInfoList) {
				try {
					LOGGER.info("Loading appfront for class name {}", appfrontInfo.getAppfrontClassName());
					Class<?> clz = Class.forName(appfrontInfo.getAppfrontClassName());

					if (Appfront.class.isAssignableFrom(clz)) {
						String uriSegment = appfrontInfo.getUriSegement();

						LOGGER.info("Appfront's uri segement {}", uriSegment);

						if (!StringUtils.isNullOrEmpty(uriSegment)) {
							BasicAppfront appfront = (BasicAppfront) clz.newInstance();
							appfront.setUriSegment(uriSegment);
							appfront.setUriSegment(uriSegment);

							// if
							// (!CollectionUtils.isNullOrEmpty(appfrontInfo.getRequestReceivers()))
							// {
							// List<RequestReceiver> requestReceivers = new
							// ArrayList<>();
							//
							// for (String requestReceiverName :
							// appfrontInfo.getRequestReceivers()) {
							// LOGGER.info("Loading request receiver for class
							// name {}", requestReceiverName);
							// Class<?> receiverClz =
							// Class.forName(requestReceiverName);
							//
							// if
							// (RequestReceiver.class.isAssignableFrom(receiverClz))
							// {
							// RequestReceiver requestReceiver =
							// (RequestReceiver) receiverClz.newInstance();
							// requestReceivers.add(requestReceiver);
							//
							// } else {
							// LOGGER.warn(
							// "Supplied Appfront request receiver {} is not a
							// valid RequestReceiver. Skipping",
							// receiverClz.getCanonicalName());
							// throw new ApplicationInitializationError(
							// "Supplied RequestReceiver is not a valid Appfront
							// RequestReceiver");
							// }
							// }
							//
							// appfront.setReceivers(requestReceivers);
							//
							// } else {
							// LOGGER.info("No request receiver configured for
							// appfront {}",
							// appfrontInfo.getAppfrontClassName());
							// }

							appFronts.put(uriSegment, appfront);

						} else {
							LOGGER.warn("Failed to load Appfront {}. URI segment is missing", this.getName());
							throw new ApplicationInitializationError("URI segment is missing for appfront");
						}

					} else {
						LOGGER.warn("Supplied Appfront {} is not a valid Appront. Skipping", clz.getCanonicalName());
						throw new ApplicationInitializationError("Supplied Appfront is not a valid Appfront");
					}

				} catch (Exception e) {
					LOGGER.error("Failed to add Appfront {}", appfrontInfo.getAppfrontClassName(), e);
					throw new ApplicationInitializationError("Failed to add Appfront");
				}
			}

		} else {
			LOGGER.info("No Appfront is configured for application {}", this.getName());
			throw new ApplicationInitializationError("No appfront found to load");
		}
	}

	private void addListeners() throws ApplicationInitializationError {
		Map<String, String> map = ctx.getDescriptor().getCallbacks();

		if (map != null && map.size() > 0) {

			for (Entry<String, String> entry : map.entrySet()) {
				try {

					if (StringUtils.safeEquals(entry.getKey(), AFTER_INIT)) {
						addApplicationCallback(entry.getValue(), LifeCycleEvent.INIT);

					} else if (StringUtils.safeEquals(entry.getKey(), BEFORE_START)) {
						addApplicationCallback(entry.getValue(), LifeCycleEvent.START);

					} else if (StringUtils.safeEquals(entry.getKey(), BEFORE_SHUTDOWN)) {
						addApplicationCallback(entry.getValue(), LifeCycleEvent.DESTROY);
					}

				} catch (Exception e) {
					LOGGER.error("Failed to add callback listener {}", entry.getKey(), e);
					throw new ApplicationInitializationError("Failed to add Appfront");
				}
			}

		} else {
			LOGGER.info("No callback listener is configured for application {}", this.getName());
		}
	}

	private void addApplicationCallback(String callback, LifeCycleEvent event) throws ApplicationInitializationError {

		if (!StringUtils.isNullOrEmpty(callback)) {
			LOGGER.info("Adding {} callback to Application {}", callback, this.getName());

			try {
				Class<?> clz = Class.forName(callback);

				if (ApplicationListener.class.isAssignableFrom(clz)) {
					ApplicationListener listener = (ApplicationListener) clz.newInstance();
					listeners.put(event, listener);

					LOGGER.info("Added callaback listener {} to {}", clz.getCanonicalName(), this.getName());

				} else {
					LOGGER.warn("Invalid Callback listener {}. Skipping...", clz.getCanonicalName());
					throw new ApplicationInitializationError("Supplied callback listener is not a valid");
				}

			} catch (Exception e) {
				LOGGER.error("Failed to add callback listner {} to application {}", callback, this.getName(), e);
				throw new ApplicationInitializationError("Failed to add callback listener");
			}

		} else {
			LOGGER.info("{} has no callback listener configured for lifecycle event {}", this.getName(), event);
		}
	}

	/**
	 * Receives a {@link XmppResponse} from {@link ExecutionContainer} and
	 * prepares object instances which can be sent back to
	 * {@link ApplicationController} to write back on the socket
	 * 
	 * @param response
	 */
	void doReturnResponse(ApplicationResponse response, RequestContext ctx) {
		try {
			ResponseContainer<ApplicationResponse> responseContainer = new ResponseContainer<>(response);
			responseContainer.setDestination(ctx.getSource());

			this.responseForwarder.forward(responseContainer);

		} catch (Exception e) {
			// TODO Process the exception correctly
			// Send the error response to receiver yet again
			// or may be Response receiver will automatically
			// send the error response
		}
	}

	public void setResponseReceiver(ResponseForwarder responseReceiver) {
		this.responseForwarder = responseReceiver;
	}

	/**
	 * {@code ExecutionContainer} is an execution container for a request. The
	 * task is initiated by {@link Application} and submitted to a thread pool
	 * for execution.
	 */
	class ExecutionContainer implements Callable<ApplicationResponse> {
		private XmppRequest request;
		private Appfront appFront;
		private RequestContext ctx;

		public ExecutionContainer(XmppRequest request, Appfront appFront, RequestContext ctx) {
			this.request = request;
			this.appFront = appFront;
			this.ctx = ctx;
		}

		@Override
		public ApplicationResponse call() throws Exception {
			ApplicationResponse response = this.appFront.doProcess(this.request, this.ctx);
			doReturnResponse(response, this.ctx);

			return response;
		}
	}

}
