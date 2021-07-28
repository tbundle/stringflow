package abs.ixi.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DirectoryUtils {
    private static final String DIR_SEPERATOR = File.separator;

    public static List<String> getChildDirs(String path) {
	File file = new File(path);

	return getChildDirs(file);
    }

    public static List<String> getChildDirs(File file) {
	if (file.isDirectory()) {
	    String[] directories = file.list(new FilenameFilter() {
		@Override
		public boolean accept(File current, String name) {
		    return new File(current, name).isDirectory();
		}
	    });

	    if (directories.length != 0) {
		List<String> childDirectories = new ArrayList<>();

		Arrays.stream(directories).forEach((child) -> {
		    childDirectories.add(file.getAbsolutePath() + DIR_SEPERATOR + child);
		});

		return childDirectories;
	    }

	}

	return null;
    }

    public static List<String> getChildFiles(String path) {
	File file = new File(path);

	return getChildFiles(file);
    }

    public static List<String> getChildFiles(File file) {
	if (file.isDirectory()) {
	    String[] files = file.list(new FilenameFilter() {
		@Override
		public boolean accept(File current, String name) {
		    return new File(current, name).isFile();
		}
	    });

	    if (files.length != 0) {
		List<String> childFiles = new ArrayList<>();

		Arrays.stream(files).forEach((child) -> {
		    childFiles.add(file.getAbsolutePath() + DIR_SEPERATOR + child);
		});

		return childFiles;
	    }

	}

	return null;
    }

    public static List<String> getChildrens(String path) {
	File file = new File(path);

	return getChildrens(file);
    }

    public static List<String> getChildrens(File file) {
	if (file.isDirectory()) {
	    String[] files = file.list();

	    if (files.length != 0) {
		List<String> childs = new ArrayList<>();

		Arrays.stream(files).forEach((child) -> {
		    childs.add(file.getAbsolutePath() + DIR_SEPERATOR + child);
		});

		return childs;
	    }

	}

	return null;
    }

}
