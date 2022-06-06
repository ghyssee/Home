package be.home.picmgt.model;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ini4j.Profile.Section;

import be.home.common.constants.InitConstants;
import be.home.common.exceptions.ApplicationException;
import be.home.common.logging.Log4GE;
import be.home.common.utils.ini.CommonIniFile;

public abstract class BasePicMgt {

	private static final Logger log = LogManager.getLogger(BasePicMgt.class);

	public CommonIniFile configIni = null;
	
	public Section map = null;

	public Log4GE log4GE = null;
	
	public BasePicMgt(){}

	public void start(CommonIniFile ini, String section){
		this.configIni = ini;
		this.map = ini.get(section);
		log4GE = Log4GE.getLogger(map.fetch(InitConstants.LOGFILE));
		start();
	}
	
	public abstract void start();
	
	public void validateParam(String param){
		
		String value = map.fetch(param);
		if (value == null){
			throw new ApplicationException("Param " + param + " cannot be null");
		}
	}
	
	
}
