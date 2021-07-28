package abs.ixi.filesystem;

import java.io.File;
import java.util.UUID;

public class HashFileLocator implements FileLocator {
	/**
	 * Number of bits which needs to be shifted to get relevant bits to prepare
	 * directory path for a file
	 */
	private static final int FIRST_SHIFT = 16;

	/**
	 * Number of bits shift for second level of directory
	 */
	private static final int SECOND_SHIFT = 8;

	/**
	 * MASK for masking three MSB bytes in an integer
	 */
	private static final int MASK = 0x000F;

	/**
	 * Base directory
	 */
	private String baseDir;

	public HashFileLocator(String baseDir) {
		this.baseDir = baseDir;
	}

	/**
	 * Calculate file path for the given file name; Here we apply some lousy
	 * hashing mechanism to prepare first and second level of directory names.
	 * Currently we store files in 2-level deep directory structure. We can not
	 * make too deep directory structure else it will result fast i-node burn.
	 * <p>
	 * With the mechanism below, we generate numeric directory names; and
	 * possible number of directories will be 32385 (127 * 255). [Explanation:
	 * as integer is a signed data-type and hascode for String will always be
	 * +ve, the hascode will always have signed bit as zero. Therefore the MSB
	 * can have max 127 value however second MSB byte can have value upto 255].
	 * </p>
	 * <p>
	 * Although {@link UUID} values are naturally distributed, the hashcode
	 * generated may not be. We must keep a watch on this. If hascodes are
	 * naturally distributed and we plan to store 1000 files in a directory,
	 * with this mechanism we can store upto 32385 * 1000 = 32,385,000 (32
	 * million) files.
	 * </p>
	 * <p>
	 * EXT3 seem to have a limit of 31988 directories. Need to verify though
	 * </p>
	 * <p>
	 * We need a hashing fucntion which generates file paths with natural
	 * distributiiion. It's a MUST.
	 * </p>
	 * 
	 * @param fileName
	 * @return
	 */
	@Override
	public String fileLocation(String fileName) {
		int hash = fileName.hashCode();
		int rel = hash >>> FIRST_SHIFT;

		int first = rel & MASK;
		int second = rel >>> SECOND_SHIFT;

		return new StringBuilder(this.baseDir).append(File.separator).append(STORE_DIR).append(File.separator)
				.append(first).append(File.separator).append(second).append(File.separator).append(File.separator)
				.toString();
	}

}
