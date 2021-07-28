package abs.ixi.filesystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.util.UUIDGenerator;

/**
 * XMPP recommends peer to peer file transfer in which server acts as relay
 * however this requires both parties (peers) to be online at the same time for
 * file to get transferred. Therefore, Stringflow supports 2-step file transfer
 * mechanims in addition to peer to peer mechanims.
 * <p>
 * In 2-step file transfer, first the file gets transmitted to server from the
 * sender. In second step, server sends the file to receiver when receiver
 * requests for it. In this process, server stores files from users on the disk
 * and {@code FileSystem} is the entity which governs file storage mechanism on
 * the disk.
 * </p>
 * 
 * @author Yogi
 *
 */
public class DefaultFileSystem implements FileSystem {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultFileSystem.class);

	/**
	 * {@link FileLocator} instance which encapsulates algorithm to locate a
	 * file on storage based on its name
	 */
	private FileLocator fileLocator;

	/**
	 * Cache for opened {@link FileOutputStream} for file names.
	 */
	private Map<String, FileOutputStream> openOutputStreams = new ConcurrentHashMap<>();

	protected DefaultFileSystem(FileLocator fileLocator) {
		this.fileLocator = fileLocator;
	}

	/**
	 * Create a file in the file system.
	 * 
	 * @return file-name which is uuid generated for this file.
	 */
	public String createFile() {
		String fileName = UUIDGenerator.uuid();
		return fileName;
	}

	/**
	 * Write bytes to file of given name. It find file location using
	 * {@link FileLocator}. If file not found create a new file at that location
	 * and open a {@link FileOutputStream} and cache that stream to
	 * {@link DefaultFileSystem#openOutputStreams} for further writing operation
	 * till timeout.
	 * 
	 * @param fileName
	 * @param data
	 * @throws IOException
	 */
	public void writeTo(String fileName, byte[] data) throws IOException {
		FileOutputStream fos = null;

		try {
			fos = openOutputStreams.get(fileName);

			if (fos == null) {
				File file = getFile(fileName);
				fos = new FileOutputStream(file, true);
				this.openOutputStreams.put(fileName, fos);
			}

			writeOutputStream(fos, data);

		} catch (IOException e) {
			LOGGER.error("Failed to write on file {} due to {}", fileName, e);
			deleteFile(fileName);
			throw e;
		}
	}

	/**
	 * Give {@link FileInputStream} for given file name.
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public FileInputStream getInputStreamFrom(String fileName) throws IOException {
		try {

			return new FileInputStream(getFile(fileName));

		} catch (IOException e) {
			LOGGER.error("Failed to get InputStream for file {} due to {}", fileName, e);
			throw e;
		}
	}

	/**
	 * If any {@link OutputStream} is opened for this fileName than removes it
	 * from {@link DefaultFileSystem#openOutputStreams} Open stream cache and
	 * closes that stream. delete that file from storage location using
	 * {@link FileLocator}.
	 * 
	 * @param fileName
	 */
	public void deleteFile(String fileName) {
		try {
			closeFileOutputStream(fileName);

			File file = getFile(fileName);

			if (file != null) {
				file.delete();
			}

		} catch (IOException e) {
			LOGGER.error("Failed to delete file {}", fileName, e);
		}
	}

	/**
	 * It remove file Stream from {@link DefaultFileSystem#openOutputStreams}
	 * and close it.
	 * 
	 * @param fileName
	 */
	public void closeFileOutputStream(String fileName) {
		OutputStream os = openOutputStreams.remove(fileName);

		if (os != null) {
			closeOutputStream(os);
		}
	}

	/**
	 * Safe Closes {@link OutputStream}
	 * 
	 * @param os
	 */
	private void closeOutputStream(OutputStream os) {
		try {
			if (os != null) {
				os.close();
			}
		} catch (IOException e) {
			LOGGER.error("Failed to close output stream", e);
		}
	}

	/**
	 * return {@link File} object for given file name.
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public File getFile(String fileName) throws IOException {
		return new File(getFileAbsolutePath(fileName));
	}

	/**
	 * return file's absolute path for given file name using
	 * {@link FileLocator}.
	 * 
	 * @param fileName
	 * @return
	 */
	private String getFileAbsolutePath(String fileName) {
		String fileLocation = this.fileLocator.fileLocation(fileName);

		File directory = new File(fileLocation);

		if (!directory.exists()) {
			directory.mkdirs();
		}

		return fileLocation + File.separator + fileName;
	}

	/**
	 * Write bytes on given output stream.
	 * 
	 * @param os
	 * @param data
	 * @throws IOException
	 */
	private void writeOutputStream(OutputStream os, byte[] data) throws IOException {
		os.write(data);
	}

	@Override
	public String toString() {
		return "FileSystem(" + this.fileLocator + ")";
	}

}
