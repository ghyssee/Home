package be.home.picmgt.model;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import be.home.common.constants.InitConstants;
import be.home.common.constants.StatusConstants;
import be.home.common.utils.FileComparator;
import be.home.common.utils.FileUtils;
import be.home.picmgt.model.to.ErrorFilesTO;

public class DVDMaker extends BasePicMgt {

	private static final Logger log = Logger.getLogger(DVDMaker.class);

	private static final boolean CONTINUE = true;
	private static final boolean STOP = false;

	public DVDMaker() {
	}

	public void start() {

		final String batchJob = "DVDMaker";
		log.info("Batchjob " + batchJob + " started on " + new Date());
		log4GE.start(batchJob);
		String sourceFolder = (String) map.fetch("sourceFolder");
		int status = readRootFolders(sourceFolder);
		if (status == StatusConstants.FAILED) {
			log4GE
					.error("Job not executed properly. See LOG-file for more info");
		} else {
			map.put(InitConstants.DVDMAKER_SIZE, "0");
			map.put(InitConstants.DVDMAKER_STARTCOUNTER, "1");
		}
		log.info("Batchjob " + batchJob + " ended on " + new Date());
		log4GE.end();

	}

	public int readRootFolders(String source) {

		File sourceFile = new File(source);
		if (!sourceFile.exists()) {
			log.error("Source folder not found");
			return StatusConstants.FAILED;
		}
		File[] inputFolders = sourceFile.listFiles();
		List<ErrorFilesTO> errorFiles = new ArrayList<ErrorFilesTO>();
		for (int i = 0; i < inputFolders.length; i++) {
			File sourceFolder = inputFolders[i];

			File destFolder = validateDestinationFolder();
			if (moveFiles(sourceFolder, destFolder, errorFiles) == STOP) {
				break;
			}
			removeEmptySubFolder(sourceFolder);
		}
		// show the list of files that could not be moved
		if (errorFiles != null && errorFiles.size() > 0) {
			log4GE.error("Following files could not be moved:");
			log4GE.startTable();
			log4GE.addColumn("Reason", 30);
			log4GE.addColumn("Info", 50);
			log4GE.printHeaders();
			for (Iterator it = errorFiles.iterator(); it.hasNext();) {
				ErrorFilesTO errorFile = (ErrorFilesTO) it.next();
				log4GE.printRow(new String[] { errorFile.getErrorMessage(),
						errorFile.getFile().getName() });
			}
		}
		log4GE.endTable();
		return StatusConstants.OK;
	}

	private boolean moveFiles(File sourceFolder, File destFolder,
			List<ErrorFilesTO> errorFiles) {
		final long MAXSIZE = Long.parseLong(map
				.fetch(InitConstants.DVDMAKER_CAPACITY));
		final int MAXFILESDIR = Integer.parseInt(map
				.fetch(InitConstants.DVDMAKER_MAXFILES_DIR));
		long currentSize = Long.parseLong(map
				.fetch(InitConstants.DVDMAKER_SIZE));

		long currentFileSize = 0;

		File[] sourceFiles = sourceFolder.listFiles();
		int nrOfFiles = getNumberOfFiles(destFolder);

		log.info("Foldername             : " + sourceFolder);
		log.info("Number of files to move: "
				+ new Integer(sourceFiles == null ? 0 : sourceFiles.length));
		// sorting the files
		Arrays.sort(sourceFiles, new FileComparator());
		for (int i = 0; i < sourceFiles.length; i++) {

			if (nrOfFiles >= MAXFILESDIR) {
				// save info to ini-file
				int startCounter = Integer.parseInt(map
						.fetch(InitConstants.DVDMAKER_STARTCOUNTER));
				updateIniFile(currentSize, ++startCounter);
				destFolder = validateDestinationFolder();
				nrOfFiles = getNumberOfFiles(destFolder);
			}
			File fileToMove = sourceFiles[i];

			// move the file
			if (FileUtils.isJPGFile(fileToMove)) {
				currentFileSize = fileToMove.length();
				boolean success = fileToMove.renameTo(new File(destFolder,
						fileToMove.getName()));
				if (!success) {
					// File was not successfully moved
					log.warn("Problem moving file " + fileToMove);
					errorFiles.add(new ErrorFilesTO(fileToMove,
							"already exists"));
				} else {
					// File successfully moved
					log.info("Move file: " + fileToMove + " - " + destFolder);
					nrOfFiles++;
					currentSize += currentFileSize;
					if (currentSize >= MAXSIZE) {
						return STOP;
					}
				}

			} else {
				// File is not a JPG file
				log.warn("File " + fileToMove + " has invalid file extension ");
				errorFiles.add(new ErrorFilesTO(fileToMove,
						"invalid file extension"));
			}

		}
		// save info to ini-file
		updateIniFile(currentSize, Integer.parseInt(map
				.fetch(InitConstants.DVDMAKER_STARTCOUNTER)));
		return CONTINUE;

	}

	private File validateDestinationFolder() {

		File newDestFolder = null;
		int startCounter = Integer.parseInt(map
				.fetch(InitConstants.DVDMAKER_STARTCOUNTER));
		String dest = constructNewDestFolder(startCounter);
		int maxFilesDir = Integer.parseInt(map
				.fetch(InitConstants.DVDMAKER_MAXFILES_DIR));
		int nrOfFiles = 0;
		do {
			newDestFolder = new File(dest);
			nrOfFiles = 0;
			if (newDestFolder.exists()) {
				nrOfFiles = newDestFolder.listFiles().length;
				dest = constructNewDestFolder(++startCounter);
			} else {
				// create the directory
				log.info("Creating folder " + newDestFolder);
				if (!newDestFolder.mkdir()) {
					String errorMessage = "Error creating directory "
							+ newDestFolder;
					log.error(errorMessage);
					throw new RuntimeException(errorMessage);
				}
			}
		} while (nrOfFiles >= maxFilesDir);

		return newDestFolder;
	}

	private String constructNewDestFolder(int startCounter) {

		String destFolderPattern = map
				.fetch(InitConstants.DVDMAKER_DESTFOLDERPATTERN);
		DecimalFormat df = new DecimalFormat(map
				.fetch(InitConstants.DVDMAKER_STARTCOUNTERPATTERN));
		String counter = df.format(startCounter);
		String dest = map.fetch(InitConstants.DVDMAKER_DESTFOLDER);
		dest = dest.endsWith("\\") || dest.endsWith("/") ? dest : dest + "/";
		dest = dest + destFolderPattern + counter;

		return dest;

	}

	private void updateIniFile(long currentSize, int startCounter) {
		map.put(InitConstants.DVDMAKER_STARTCOUNTER, Integer
				.toString(startCounter));
		map.put(InitConstants.DVDMAKER_SIZE, Long.toString(currentSize));
	}

}
