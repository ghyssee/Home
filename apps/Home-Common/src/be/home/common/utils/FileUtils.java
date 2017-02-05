package be.home.common.utils;

import be.home.common.exceptions.ApplicationException;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileUtils {

	private static final Logger log = Logger.getLogger(FileUtils.class);
    public static final boolean REMOVE_EMPTY_LINES = true;

	public static boolean isJPGFile(File fileName) {
		if (fileName != null
				&& fileName.getName().toLowerCase().endsWith(".jpg")) {
			return true;
		} else {
			return false;
		}
	}

	public static String stripFilenameFromUrl(String path) {
		String fileName = null;

		String separator = "/";

		int pos = path.lastIndexOf(separator);

		if (pos > -1) {
			fileName = path.substring(pos + 1);
		}

		return fileName;
	}

	public static String stripIllegalCharsFromFileName(String filename) {

		String newFileName = null;
		try {
			newFileName = URLEncoder.encode(filename, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

		return newFileName;

	}

    public static void copyFileUsingFileChannels(File source, File dest, boolean appendIfExist) throws IOException {
		FileChannel inputChannel = null;
		FileChannel outputChannel = null;
		try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        }
		catch (Exception e){
                e.printStackTrace();
        } finally {
			inputChannel.close();
			outputChannel.close();
		}
	}

	public static void copy(File src, File dst, boolean appendIfExist)
			throws IOException {

		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		try {
			in = new BufferedInputStream(new FileInputStream(src));
			out = new BufferedOutputStream(new FileOutputStream(dst,
					appendIfExist));
			// --- actual copy
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
		} finally {
			try {
				if (in != null)
					in.close();
			} finally {
				if (out != null)
					out.flush();
					out.close();
					out = null;
					System.gc();
			}
		}

	}

	public static void merge(File src1, File src2, File dst) throws IOException {
		copy(src1, dst, false);
		copy(src2, dst, true);
	}

    public static List<String> getContents(File aFile) throws IOException {
        return getContents(aFile, StandardCharsets.UTF_16);
    }
	public static List<String> getContents(File aFile, Charset charSet) throws IOException {
        return getContents(aFile, charSet.name(), false);
	}

    public static List<String> getContents(File aFile, Charset charSet, boolean removeEmptyLines) throws IOException {
        return getContents(aFile, charSet.name(), removeEmptyLines);
    }

    public static List<String> getContents(File aFile, String charSet, boolean removeEmptyLines) throws IOException {
		// ...checks on aFile are elided
		List<String> lines = new ArrayList();

			// use buffering, reading one line at a time
			// FileReader always assumes default encoding is OK!
			Charset charset = Charset.forName(charSet);
			BufferedReader input = new BufferedReader(new InputStreamReader(
					new FileInputStream(aFile), charset));
			try {
				String line = null; // not declared within while loop
				/*
				 * readLine is a bit quirky : it returns the content of a line
				 * MINUS the newline. it returns null only for the END of the
				 * stream. it returns an empty String if two newlines appear in
				 * a row.
				 */
				while ((line = input.readLine()) != null) {
					if (!removeEmptyLines || (removeEmptyLines && StringUtils.isNotBlank(line))) {
						lines.add(line);
					}
				}
			} finally {
				input.close();
			}

		return lines;
	}

	public static void deleteFile(File fileToDelete) {
		// Make sure the file or directory exists and isn't write protected
		if (!fileToDelete.exists())
			throw new IllegalArgumentException(
					"Delete: no such file or directory: " + fileToDelete);

		if (!fileToDelete.canWrite())
			throw new IllegalArgumentException("Delete: write protected: "
					+ fileToDelete);

		// If it is a directory, make sure it is empty
		if (fileToDelete.isDirectory()) {
			String[] files = fileToDelete.list();
			if (files.length > 0)
				throw new IllegalArgumentException(
						"Delete: directory not empty: " + fileToDelete);
		}

		// Attempt to delete it
		boolean success = fileToDelete.delete();

		if (!success)
			throw new IllegalArgumentException("Delete: deletion failed");
	}


    public static String encodeFilename(String filename){
        if (filename != null){
            return filename.replaceAll("''", "\"");
        }
        return filename;
    }

    public static int getLevel(File folder) {
        int level = 0;
        File parent = folder;
        do {
            if (parent.isDirectory()){
                level++;
            }
            parent = new File(parent.getParent());
        }
        while (parent.getParent() != null);
        return level;
    }

    public static String[] getParentList(File folder) {
        int level = 0;
        File parent = folder;
        List <String> folders = new ArrayList <String> ();
        //folders.
        do {
            if (parent.isDirectory()){
                folders.add(parent.getName());
            }
            parent = new File(parent.getParent());
        }
        while (parent.getParent() != null);
        Collections.reverse(folders);
        //String[] tmp = (String []) folders.toArray();
        return folders.toArray(new String[folders.size()]);
    }

	public static String joinPathAndFile(String path, String file){
		if (path == null) return file;
		return path.endsWith("\\") || path.endsWith("/") ? path + file : path + File.separator + file;
	}

	/**
	 * Creates a reader capable of handling BOMs.
	 */
	public static InputStreamReader newReader(final InputStream inputStream) {
		return new InputStreamReader(new BOMInputStream(inputStream), StandardCharsets.UTF_8);
	}

	public static void checkDirectory(String directory) throws IOException {
		Path pathToFile = Paths.get(directory);
		if (!Files.exists(pathToFile)) {
			try {
				Files.createDirectories(pathToFile);
				log.info("Creating directory " + directory);
			} catch (IOException e) {
				throw new ApplicationException("There was a problem creating the directory " + directory);
			}
		}
	}

	public static boolean renameFile(String oldFile, String newFile)  {
		File oldF = new File(oldFile);
		File newF = new File(newFile);
		return oldF.renameTo(newF);
	}


	public static Path relativize(Path pathBase, Path pathAbsolute) {
		Path pathRelative = pathBase.relativize(pathAbsolute);
		return pathRelative;
	}

}
