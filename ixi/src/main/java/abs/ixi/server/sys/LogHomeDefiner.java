package abs.ixi.server.sys;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.security.AccessController;
import java.security.PrivilegedAction;

import abs.ixi.util.StringUtils;
import abs.ixi.util.SystemPropertyUtils;
import ch.qos.logback.core.PropertyDefinerBase;

/**
 * Logback property definer. This class should not have a Logger instance as the
 * log directory location is still not defined.
 * 
 * @author Yogi
 *
 */
public class LogHomeDefiner extends PropertyDefinerBase {

	@Override
	public String getPropertyValue() {
		try {
			if (System.getSecurityManager() == null) {
				return getServerHome() + File.separator + "logs" + File.separator;
			} else {
				return AccessController.doPrivileged(new PrivilegedAction<String>() {
					@Override
					public String run() {
						return getServerHome() + File.separator + "logs" + File.separator;
					}
				});
			}
		} catch (SecurityException e) {
			System.err.println("Unable to determine stringflow home");
			e.printStackTrace();
		}

		return getServerHome();
	}

	private String getServerHome() {
		if (StringUtils.isNullOrEmpty(SystemPropertyUtils.get("ixi.home"))) {
			Path path = FileSystems.getDefault().getPath(".").toAbsolutePath().normalize();
			System.out.println(
					"Environment variable ixi.home is not set; picking the current dir as home:" + path.toString());

			return path.toString();
		} else {
			return SystemPropertyUtils.get("ixi.home");
		}
	}
}
