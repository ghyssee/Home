package be.home.picmgt.model;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import be.home.common.constants.BooleanConstants;
import be.home.common.constants.InitConstants;
import be.home.common.exceptions.ApplicationException;
import be.home.common.utils.DateUtils;
import be.home.common.utils.FileComparator;
import be.home.common.utils.FileUtils;

public class FileMonitor extends BasePicMgt {

	private static final Logger log = Logger.getLogger(FileMonitor.class);

	public FileMonitor() {
	}
	
	public void start() {
		final String batchJob = "FileMonitor";
		log.info("Batchjob " + batchJob + " started on " + new Date());
		log4GE.start(batchJob);
		String sourceFolder = (String) map.fetch("sourceFolder");
		List<File> filesToMove = readRootFolders(sourceFolder);
		if (filesToMove == null) {
			log4GE
					.error("Job not executed properly. See LOG-file for more info");
		} else {
			// sort the files
			log.info("now sorting " + filesToMove.size() + " files");
			log4GE.info("Files found: " + filesToMove.size());
			Collections.sort(filesToMove, new FileComparator());
			File destFolder = checkDestFolder();
			for (Iterator i = filesToMove.iterator(); i.hasNext();) {
				File fileToMove = (File) i.next();
				moveFile(fileToMove, destFolder);
			}
			// move them to the destination, watch out for duplicate filenames

		}
		log.info("Batchjob " + batchJob + " ended on " + new Date());
		log4GE.end();

	}

	private List<File> readRootFolders(String source) {

		File sourceFile = new File(source);
		List<File> filesToMove = null;
		if (!sourceFile.exists()) {
			log.error("Source folder not found");
			return null;
		}
		File[] sourceFolders = sourceFile.listFiles();
		for (int i = 0; i < sourceFolders.length; i++) {
			File sourceFolder = sourceFolders[i];
			log.info("Reading files of " + sourceFolder);
			List<File> files = readSubFolder(sourceFolder);
			if (files != null) {
				int nrOfFiles = files.size();
				if (filesToMove == null) {
					filesToMove = new ArrayList<File>(nrOfFiles);
				}
				log.info(nrOfFiles + " files found");
				filesToMove.addAll(files);
			}
		}

		return filesToMove;
	}

	private List<File> readSubFolder(File subFolder) {

		List<File> listedFiles = null;
		if (subFolder != null) {
			File[] files = subFolder.listFiles();
			if (files != null) {
				listedFiles = new ArrayList<File>(Arrays.asList(files));
			}
		}
		return listedFiles;
	}

	private File checkDestFolder() {

		String destination = map.fetch(InitConstants.FILEMONITOR_DESTFOLDER);
		Boolean createNewFolder = BooleanConstants.decode(map
				.fetch(InitConstants.FILEMONITOR_CREATENEWFOLDER));
		if (StringUtils.isEmpty(destination)) {
			throw new ApplicationException("Invalid destination directory");
		}
		File destMap = new File(destination);
		// check if destination map is valid
		if (destMap == null || !destMap.isDirectory()) {
			throw new ApplicationException("Directory " + destMap
					+ " is not valid");
		}
		// append current date to destination map
		String date = DateUtils.formatYYYYMMDD(new Date());
		destination += destination.endsWith("/") || destination.endsWith("\\") ? ""
				: "/";
		destination += date;
		// check new destination map
		destMap = new File(destination);
		if (destMap.isDirectory()) {
			// map already exist
			if (createNewFolder.booleanValue()) {
				// create a new unique map
				int i = 0;
				DecimalFormat df = new DecimalFormat("00");
				do {
					destMap = new File(destination + "." + df.format(++i));
				} while (destMap.exists());
			} else {
				return destMap;
			}
		}
		// now make the map
		if (destMap.mkdir()) {
			String infoLine = "Destination Map " + destMap + " created";
			log.info(infoLine);
			log4GE.info(infoLine);
		} else {
			throw new ApplicationException("Error creating map " + destMap);
		}

		return destMap;

	}

	private void moveFile(File fileToMove, File destFolder) {
		if (fileToMove != null) {
			if (FileUtils.isJPGFile(fileToMove)) {
				File destFileName = new File(destFolder + "/"
						+ fileToMove.getName());
				int counter = 0;
				while (destFileName.exists()) {
					// get a unique filename
					File parent = fileToMove.getParentFile();
					String alias = map.fetch(parent.getName());
					if (alias == null) {
						alias = "(" + String.valueOf(++counter) + ")";
					} else {
						// append (teller) to the alias
						alias += counter == 0 ? "" : "("
								+ String.valueOf(++counter) + ")";
					}
					alias += " ";
					destFileName = new File(destFolder + "/" + alias
							+ fileToMove.getName());
				}
				// move the file
				if (fileToMove.renameTo(destFileName)) {
					log
							.info("File " + fileToMove + " moved to "
									+ destFileName);
				} else {
					String errorMessage = "File " + fileToMove
							+ " could not be moved";
					log.error(errorMessage);
					log4GE.error(errorMessage);
				}
			}
		}
	}
}
