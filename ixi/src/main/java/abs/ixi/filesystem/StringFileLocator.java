package abs.ixi.filesystem;

import static abs.ixi.server.etc.conf.Configurations.Bundle.PROCESS;

import java.io.File;

import abs.ixi.server.Stringflow;
import abs.ixi.server.etc.conf.Configurations;
import abs.ixi.server.etc.conf.ProcessConfigAware;

/**
 * {@link FileLocator} implementation to calculate file path for given file name
 * by extracting substrings from its name. First three characters of the
 * file-name will be the first level directory and subsequent three characters
 * will be second level directory name. For example, if the file name is
 * <i>v07splnjls-b09sh-mght9-c89nsjsomq22ms8wnw7am0a</i>, the file location will
 * be returned as <i>/v07/spl/</i>.
 * <p>
 * Currently we store files in 2-level deep directory structure. We can not make
 * too deep directory structure else it will result fast i-node burn. With
 * {@code StringFileLocator} with three-character directory generation, possible
 * number of first level directories will be 46656 ((26+10)*(26+10)*(26+10)).
 * Similarly second level directory names will also have same (46656)
 * possiblities. Therefore, together this will generate 2,176,782,336
 * directories.
 * </p>
 * <p>
 * Few points to remember-
 * <ul>
 * <li>Generated file locations must be naturally distributed for better
 * performance</li>
 * <li>EXT3 seem to have a limit of 31988 directories. Need to verify
 * though</li>
 * </ul>
 * </p>
 */
public final class StringFileLocator implements FileLocator, ProcessConfigAware {
	/**
	 * Default directory length
	 */
	private static final int DIR_LENGTH = 3;

	/**
	 * Default directory level
	 */
	private static final int DIR_LEVEL = 3;

	/**
	 * Number of characters in directory name
	 */
	private int dirLength;

	/**
	 * Number of directory levels in file location
	 */
	private int dirLevel;

	/**
	 * Base directory
	 */
	private String baseDir;

	public StringFileLocator(String baseDir) {
		Configurations conf = Stringflow.runtime().configurations();
		this.dirLength = conf.getOrDefaultInteger(_FILE_SYSTEM_DIR_LENGTH, DIR_LENGTH, PROCESS);
		this.dirLevel = conf.getOrDefaultInteger(_FILE_SYSTEM_DIR_LEVEL, DIR_LEVEL, PROCESS);
		this.baseDir = baseDir;
	}

	/**
	 * Get the location of the file represented by the given file name. The
	 * location is calculated by extracting n characters from its name which
	 * makes first level of directory name. Similarly subsequent n characters
	 * will make the second level directory name and so on. Number of directory
	 * levels and number of characters in each of the directory name is
	 * dependent on the configurations of this {@code StringFileLocator}
	 * instance.
	 */
	@Override
	public String fileLocation(String fileName) {
		StringBuilder fileLocation = new StringBuilder(this.baseDir).append(File.separator);
		int index = 0;

		for (int i = 0; i < this.dirLevel; i++) {
			fileLocation = fileLocation.append(STORE_DIR).append(File.separator)
					.append(fileName.substring(index, index + this.dirLength)).append(File.separator)
					.append(File.separator);
		}

		return fileLocation.toString();
	}

	public int getDirLength() {
		return dirLength;
	}

	public void setDirLength(int dirLength) {
		this.dirLength = dirLength;
	}

	public int getDirLevel() {
		return dirLevel;
	}

	public void setDirLevel(int dirLevel) {
		this.dirLevel = dirLevel;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}

}
