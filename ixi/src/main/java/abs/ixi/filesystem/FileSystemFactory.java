package abs.ixi.filesystem;

import abs.ixi.server.etc.conf.SystemConfigAware;
import abs.ixi.util.SystemPropertyUtils;

/**
 * Factory to get {@link FileLocator} instances
 * 
 * @author Yogi
 *
 */
public class FileSystemFactory implements SystemConfigAware {
	private static DefaultFileSystem defaultFileSystemWithHashFileLocator;
	private static DefaultFileSystem defaultFileSystemWithStringFileLocator;

	public static DefaultFileSystem getDefaultFileSystemWithHashFileLocator() {
		if (defaultFileSystemWithHashFileLocator == null) {
			synchronized (FileSystemFactory.class) {
				defaultFileSystemWithHashFileLocator = new DefaultFileSystem(new HashFileLocator(getHomeDirectory()));
			}
		}

		return defaultFileSystemWithHashFileLocator;
	}

	public static DefaultFileSystem getDefaultFileSystemWithStringFileLocator() {
		if (defaultFileSystemWithStringFileLocator == null) {
			synchronized (FileSystemFactory.class) {
				defaultFileSystemWithStringFileLocator = new DefaultFileSystem(
						new StringFileLocator(getHomeDirectory()));
			}
		}

		return defaultFileSystemWithStringFileLocator;
	}

	private static String getHomeDirectory() {
		return SystemPropertyUtils.get(SF_HOME);
	}

}
