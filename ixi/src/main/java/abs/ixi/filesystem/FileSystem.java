package abs.ixi.filesystem;

/**
 * An interface to abstract underlying file system implementation. FileSystem
 * does not refer to the literal OS file system such as NTFS, EXT3 etc instead
 * it is the system which manages the way media files are stored and accessed.
 * <p>
 * {@code FileSystem} is the contract for any {@code FileSystem} implementation
 * which can be plugged-into Server. It is imperative that each of the file
 * system implementation will be targated to certain OS level file system to
 * harness its capabilities; therefore it will also have limitations imposed by
 * that file system.
 * </p>
 * <p>
 * Server has a default implementation of this interface:
 * {@link DefaultFileSystem}
 * </p>
 * 
 * @author Yogi
 *
 */
public interface FileSystem {
    
}
