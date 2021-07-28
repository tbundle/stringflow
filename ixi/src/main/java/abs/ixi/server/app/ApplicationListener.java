package abs.ixi.server.app;

/**
 * A callback listener for developers which allows them to write hooks for
 * various stages of application life cycle within server.
 * 
 * <P>
 * There could be one listener implementation for each stage or one listener for
 * all stages. There are no recommended way.
 */
public interface ApplicationListener {

	/**
	 * The method is called right AFTER server finishes application
	 * initialization. As part of deployment, server scans through the 'deploy'
	 * directory and instantiate {@link Application} for each of the deployed
	 * application. The method is called when the initialization of the
	 * {@link Application} finishes.
	 * 
	 * @throws Exception
	 */
	default public void onInit() throws Exception{
		
	}

	/**
	 * The method is called right BEFORE the server calls the start method on
	 * {@link Application}. This is the last chance to execute a piece of code
	 * before the {@link Application} is ready to process its first request
	 * 
	 * @throws Exception
	 */
	default public void onStart() throws Exception{
		
	}

	/**
	 * The method is invoked right BEFORE server tries to off-load the
	 * {@link Application} instance from memory. This is will be the last piece
	 * of developer's code to get executed before the {@link Application} really
	 * shuts down. It could be a good place to execute cleanup tasks in an
	 * {@link Application}
	 * 
	 * @throws Exception
	 */
	default public void onDestroy() throws Exception{
		
	}
}
