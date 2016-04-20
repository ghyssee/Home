package be.home.picmgt.model;

import java.io.File;

import org.apache.log4j.Logger;
import org.ini4j.Profile.Section;

import be.home.common.constants.InitConstants;
import be.home.common.exceptions.ApplicationException;
import be.home.common.logging.Log4GE;
import be.home.common.utils.ini.CommonIniFile;

public abstract class BasePicMgt {

	private static final Logger log = Logger.getLogger(BasePicMgt.class);

	public CommonIniFile configIni = null;
	
	public Section map = null;

	public Log4GE log4GE = null;
	
	public BasePicMgt(){}

	public BasePicMgt(Section map) {
		this.map = map;
		log4GE = Log4GE.getLogger(map.fetch(InitConstants.LOGFILE));
	}

	public void removeEmptySubFolders(String sourceFolder) {

		File sourceFile = new File(sourceFolder);
		File[] inputFolders = sourceFile.listFiles();
		for (int i = 0; i < inputFolders.length; i++) {
			removeEmptySubFolder(inputFolders[i]);

		}

	}

	public void start(CommonIniFile ini, String section){
		this.configIni = ini;
		this.map = ini.get(section);
		log4GE = Log4GE.getLogger(map.fetch(InitConstants.LOGFILE));
		start();
	}
	
	public abstract void start();
	
	public void removeEmptySubFolder(File subFolderToDelete) {

		File[] listFiles = subFolderToDelete.listFiles();
		if (listFiles.length == 0) {
			if (subFolderToDelete.delete()) {
				// subfolder is deleted
				String message = "Folder "
						+ subFolderToDelete.getAbsolutePath() + " deleted";
				log.info(message);
				log4GE.info(message);
			} else {
				// there was a problem removing the subfolder
				String message = "Folder "
						+ subFolderToDelete.getAbsolutePath()
						+ " could not be removed";
				log.info(message);
				log4GE.info(message);
			}
		} else {
			// subfolder is not empty
			String message = "Folder " + subFolderToDelete.getAbsolutePath()
					+ " is not empty";
			log.info(message);
			log4GE.info(message);
		}

	}

	public int getNumberOfFiles(File folder) {

		int nrOfFiles = 0;
		if (folder != null) {
			File[] tmpFiles = folder.listFiles();
			nrOfFiles = tmpFiles == null ? 0 : tmpFiles.length;
		}
		return nrOfFiles;

	}

	public void validateParam(String param){
		
		String value = map.fetch(param);
		if (value == null){
			throw new ApplicationException("Param " + param + " cannot be null");
		}
	}
	
	
}
