package abs.ixi.filesystem;

/**
 * Server {@link FileSystem} dynamically calculates the location of a file on
 * the storage based on file name. There is no metadata maintained to locate a
 * file. The file name is used as seed to calculate its location while storing
 * it as well as reading/accessing it.
 * <p>
 * At any given point, there could be many algorithms to calculate file path.
 * {@code FilePathCalculator} is the contract for all the implementation of an
 * algorithm to calculate file path on the storage.
 * </p>
 * 
 * @author Yogi
 *
 */
public interface FileLocator {
	public String STORE_DIR = "store";

	/**
	 * Get the file location on storage based on its name. The location returned
	 * is calculated based on its name; this may not map to an absolute location
	 * on the {@link FileSystem}. For example a fileName
	 * <i>a0kknj-3no8-8nsj-a09bnlaxt63ndowm90nhbd</i> may be mapped to
	 * <i>/a0k/knj/</i> but this path may be relative to some root path.
	 * 
	 * @param fileName
	 * @return file location
	 */

	public String fileLocation(String fileName);
}
