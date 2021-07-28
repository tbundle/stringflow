package abs.ixi.server.app;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.etc.conf.SystemConfigAware;
import abs.ixi.util.CollectionUtils;
import abs.ixi.util.DirectoryUtils;
import abs.ixi.util.StringUtils;
import abs.ixi.util.SystemPropertyUtils;

/**
 * {@link Deployer} is responsible to deploy all the applications in server.
 * Primary responsibilities of {@link Deployer} are to scan through the 'deploy'
 * directory of the server and deploy each application found.
 * 
 * As part of deployment, {@link Deployer} ensures that each application
 * conforms to the directory structure rules of the server for an application
 * and following to that, it reads the deployment configuration file for the
 * application and instantiate the {@link Application} instance with provided
 * configurations
 * 
 * Deployer is executed at server start.
 */
public class Deployer {
	private static final Logger LOGGER = LoggerFactory.getLogger(Deployer.class);

	private static final String FILE_DEPLOY_CONFIG = "deploy.xml";
	private static final String DIRECTORY_DEPLOY = "deploy";
	private static final String DIRECTORY_LIB = "libs";
	private static final String DIRECTORY_CONF = "conf";
	private static final String DIRECTORY_CLASSES = "classes";
	private static final String SLASH = File.separator;

	private AppDescriptorParser parser;

	public Deployer() throws ApplicationError {
		try {

			this.parser = new AppDescriptorParser();

		} catch (ParserConfigurationException e) {
			LOGGER.error("Failed to instantiate AppDescriptorParser", e);
			throw new ApplicationError("Failed to instantiate AppDescriptorParser", e);
		}
	}

	public List<Deployable> deploy() throws ApplicationError {
		LOGGER.info("Starting application deployment");

		String ixiHome = SystemPropertyUtils.get(SystemConfigAware.SF_HOME);

		if (StringUtils.isNullOrEmpty(ixiHome)) {
			LOGGER.error("ixi.home is not set; Shutting down server");
			throw new ApplicationError("IXI.HOME is not set");
		}

		Path deployPath = Paths.get(ixiHome + SLASH + DIRECTORY_DEPLOY);

		if (!Files.exists(deployPath)) {
			LOGGER.error("Deploy directory not found, Shutting down server ");
			throw new ApplicationError("Deploy directory not found");
		}

		File deployDir = new File(ixiHome + SLASH + DIRECTORY_DEPLOY);
		List<String> appDirs = DirectoryUtils.getChildDirs(deployDir);

		if (!CollectionUtils.isNullOrEmpty(appDirs)) {
			List<Deployable> applications = new ArrayList<>();

			// Each application is within a directory
			for (String appDir : appDirs) {
				Application application = getApplication(appDir);

				if (application != null) {
					applications.add(application);
					LOGGER.info("Application {} is deployed successfully", application.getName());

				} else {
					LOGGER.warn("Failed to deploy application in dir {}. Skipping...", appDir);
				}
			}

			return applications;

		} else {
			LOGGER.info("No Application found to deploy.");
			return null;
		}
	}

	private Application getApplication(String appDir) {
		LOGGER.info("Deploying application of dir {}", appDir);

		List<String> appFiles = DirectoryUtils.getChildrens(appDir);

		boolean isValid = verifyDirStructure(appFiles);

		if (isValid) {

			try {
				String deployXMLPath = new StringBuilder(appDir).append(SLASH).append(FILE_DEPLOY_CONFIG).toString();
				AppDeploymentDescriptor descriptor = parser.parse(deployXMLPath);

				ApplicationContext ctx = new ApplicationContext(descriptor);

				// Supply ResponseForwarder implementation below
				Application app = new Application(ctx, null);
				app.deploy();

				return app;

			} catch (Exception e) {

				LOGGER.error("Failed to deploy application : {}", appDir, e);
			}

		} else {
			LOGGER.error("Deploy directory for app : {} is not valid", appDir);
		}

		return null;
	}

	/**
	 * verifies the directory structure of the application. A correctly deployed
	 * application in the server will have classes, conf and lid directories
	 * along with a deployment descriptor file -deploy.config/deploy.xml
	 * 
	 * @param appDir dir list found in the application deployment dir
	 */
	private boolean verifyDirStructure(List<String> appDir) {
		boolean isValid = false;
		boolean libFound = false;
		boolean confFound = false;
		boolean classesFound = false;
		boolean deployConfFound = false;

		for (String folder : appDir) {
			File file = new File(folder);

			if (file.isDirectory()) {
				if (file.getName().equals(DIRECTORY_CONF)) {
					confFound = true;

				} else if (file.getName().equals(DIRECTORY_CLASSES)) {
					classesFound = true;

				} else if (file.getName().equals(DIRECTORY_LIB)) {
					libFound = true;
				}

			} else {
				// TODO : add check for deploy.xml
				if (file.getName().equals(FILE_DEPLOY_CONFIG)) {
					deployConfFound = true;
				}

			}

		}

		if (classesFound && confFound && libFound && deployConfFound) {
			isValid = true;
		}

		return isValid;
	}
}
