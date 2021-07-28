package abs.ixi.server.sys;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;

import abs.ixi.util.SystemPropertyUtils;

/**
 * Represents underlying platform on which JVM is running.
 * 
 * @author Yogi
 *
 */
public class Platform {
	public static final String KEY_PROCESSOR_IDENTIFIER = "PROCESSOR_IDENTIFIER";

	public static final String KEY_PROCESSOR_ARCHITECTURE = "PROCESSOR_ARCHITECTURE";

	public static final String KEY_OS_NAME = "os.name";

	public static final String KEY_OS_ARCHITECTURE = "os.arch";

	public static final String KEY_OS_VERSION = "os.version";

	public static final String processorIdentifier() {
		return SystemPropertyUtils.get(KEY_PROCESSOR_IDENTIFIER);
	}

	public static final String processorArchitecture() {
		return SystemPropertyUtils.get(KEY_PROCESSOR_IDENTIFIER);
	}

	public static final String osName() {
		return SystemPropertyUtils.get(KEY_OS_NAME);
	}

	public static final String osArchitecture() {
		return SystemPropertyUtils.get(KEY_OS_ARCHITECTURE);
	}

	public static final String osVersion() {
		return SystemPropertyUtils.get(KEY_OS_VERSION);
	}

	public static final int availableProcessors() {
		return Runtime.getRuntime().availableProcessors();
	}

	public static final long totalMemory() {
		return Runtime.getRuntime().totalMemory();
	}

	public static final long freeMemory() {
		return Runtime.getRuntime().freeMemory();
	}

	public static final long maxMemory() {
		return Runtime.getRuntime().maxMemory();
	}

	public static final FileSystem fileSystemType() {
		return FileSystems.getDefault();
	}

}
