package be.home.picmgt.model;

import java.io.File;
import java.util.Arrays;
import be.home.common.utils.FileComparator;
import java.util.Date;
import org.apache.log4j.Logger;
import be.home.common.constants.InitConstants;
import be.home.common.exceptions.ApplicationException;

import java.text.DecimalFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import be.home.common.utils.FileUtils;
import be.home.picmgt.model.to.ErrorFilesTO;

public class FilesForMyDoc extends BasePicMgt {

	private static final Logger log = Logger.getLogger(FilesForMyDoc.class);

	public FilesForMyDoc() {
	}

	public void start() {

		log.info("Batchjob FilesForMyDoc started on " + new Date());
		log4GE.start("FilesForMyDoc");
		String sourceFolder = (String) map.fetch("sourceFolder");
		readRootFolders(sourceFolder);
		removeEmptySubFolders(sourceFolder);
		log.info("Batchjob FilesForMyDoc ended on " + new Date());
		log4GE.end();

	}

	public void readRootFolders(String source) {

		File sourceFile = new File(source);
		if (!sourceFile.exists()){
			throw new ApplicationException("Source folder " + sourceFile + " does not exist");
		}
		File[] inputFolders = sourceFile.listFiles();
		List<ErrorFilesTO> errorFiles = new ArrayList<ErrorFilesTO>();
		for (int i = 0; i < inputFolders.length; i++) {
			File sourceFolder = inputFolders[i];
			// moveFiles(moveFolder);

			File destFolder = validateDestinationFolder();
			moveFiles(sourceFolder, destFolder, errorFiles);

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
	}

	private void moveFiles(File sourceFolder, File destFolder, List<ErrorFilesTO> errorFiles) {
		File[] sourceFiles = sourceFolder.listFiles();
		int nrOfFiles = getNumberOfFiles(destFolder);
		int maxFilesDir = Integer.parseInt(map
				.fetch(InitConstants.MYDOCS_MAXFILES_DIR));

		log.info("Foldername             : " + sourceFolder);
		log.info("Number of files to move: "
				+ new Integer(sourceFiles == null ? 0 : sourceFiles.length));
		// sorting the files
		Arrays.sort(sourceFiles, new FileComparator());
		for (int i = 0; i < sourceFiles.length; i++) {

			if (nrOfFiles >= maxFilesDir) {
				updateStartCounter();
				destFolder = validateDestinationFolder();
				nrOfFiles = getNumberOfFiles(destFolder);
			}
			File fileToMove = sourceFiles[i];

			// move the file
			if (FileUtils.isJPGFile(fileToMove)) {
				// check if size of JPG is larger than a specific amount
				String maxSize = map.fetch(InitConstants.MYDOCS_MAX_FILESIZE);
				if (maxSize != null && Integer.parseInt(maxSize) > 0
						&& fileToMove.length() < Integer.parseInt(maxSize)) {
					boolean success = fileToMove.renameTo(new File(destFolder,
							fileToMove.getName()));
					if (!success) {
						// File was not successfully moved
						log.warn("Problem moving file " + fileToMove);
						errorFiles.add(new ErrorFilesTO(fileToMove,
								"already exists"));
					} else {
						// File successfully moved
						log.info("Move file: " + fileToMove + " - "
								+ destFolder);
						nrOfFiles++;
					}
				} else {
					// file is too big => possibly not a valid JPG
					log.warn("File " + fileToMove
							+ " is larger than the maximum size - " + maxSize);
					errorFiles.add(new ErrorFilesTO(fileToMove, "too big"));

				}

			} else {
				// File is not a JPG file
				log.warn("File " + fileToMove + " has invalid file extension ");
				errorFiles.add(new ErrorFilesTO(fileToMove,
						"invalid file extension"));
			}

		}

	}

	private void updateStartCounter() {
		int startCounter = Integer.parseInt(map
				.fetch(InitConstants.MYDOCS_STARTCOUNTER));
		map.put(InitConstants.MYDOCS_STARTCOUNTER, Integer
				.toString(++startCounter));
	}

	private File validateDestinationFolder() {

		File newDestFolder = null;
		int startCounter = Integer.parseInt(map
				.fetch(InitConstants.MYDOCS_STARTCOUNTER));
		String dest = constructNewDestFolder(startCounter);
		int maxFilesDir = Integer.parseInt(map
				.fetch(InitConstants.MYDOCS_MAXFILES_DIR));
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
				.fetch(InitConstants.MYDOCS_DESTFOLDERPATTERN);
		DecimalFormat df = new DecimalFormat(map
				.fetch(InitConstants.MYDOCS_STARTCOUNTERPATTERN));
		String counter = df.format(startCounter);
		String dest = map.fetch(InitConstants.MYDOCS_DESTFOLDER);
		dest = dest.endsWith("\\") || dest.endsWith("/") ? dest : dest + "/";
		dest = dest + destFolderPattern + counter;

		return dest;

	}


}
