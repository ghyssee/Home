package be.home.picmgt.main;

import java.net.URL;

import org.apache.log4j.xml.DOMConfigurator;
import java.net.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.io.File;

import be.home.common.constants.InitConstants;
import be.home.common.exceptions.ApplicationException;
import be.home.common.utils.ini.CommonIniFile;
import be.home.common.utils.ini.CommonIniFileImpl;

import java.io.IOException;
import org.apache.log4j.Logger;
import org.ini4j.Profile.Section;

public abstract class BatchJob {

	private static final Logger log = Logger.getLogger(BatchJob.class);

	public CommonIniFile ini = null;

	public void initialize(String[] allArgs) {

		if (allArgs == null || allArgs.length == 0) {
			invalidParam();
		}
		File iniFile = null;
		Map params = readParams(allArgs);

		String paramIniFile = (String) params.get(InitConstants.PARAM_INI);
		String workingDir = System.getProperty("user.dir");
		if (paramIniFile == null) {
			iniFile = new File(workingDir + "/PicMgt/data/picmgt.ini");
		} else {
			iniFile = new File(paramIniFile);
		}
		if (!iniFile.exists()) {
			String errorMessage = "Error in initializing Batch - INI file not found "
					+ iniFile.getAbsolutePath();
			throw new ApplicationException(errorMessage);
		}
		// read the ini-file
		this.ini = new CommonIniFileImpl(iniFile);
		try {
			ini.read();
		} catch (IOException ex) {
			String errorMessage = "Error in initializing Batch - OPEN/READ INI file";
			throw new ApplicationException(errorMessage, ex);
		}
		// read the configuration file for Log4J
		URL log4jConfigURL = null;
		try {
			log4jConfigURL = new URL("file:"
					+ ini.get(InitConstants.SECTION_LOG4J, "config"));
		} catch (MalformedURLException ex) {
		}
		if (log4jConfigURL != null) {
			DOMConfigurator.configure(log4jConfigURL);
		} else {
			System.err.println("Error in initializing Log4J - config file not found");
		}

		// show all the parameters
		
		log.info("Inifile = " + iniFile.getAbsolutePath());
		Set set = params.keySet();
		log.info("Batch Parameters:");
		for (Iterator i = set.iterator(); i.hasNext();) {
			String key = (String) i.next();
			log.info(key + "=" + params.get(key));
		}
		run(params);

	}

	public Section readIniFile(String section) {

		Section map = null;

		// read the ini-file
		map = ini.get(section);
		
		return map;

	}

	public void updateIniFile() {

		// update the ini file
		try {
			this.ini.commit();
		} catch (IOException ex) {
			String errorMessage = "Error WRITING INI file";
			log.error(errorMessage, ex);
			throw new ApplicationException(errorMessage, ex);
		}
	}

	public Map readParams(String[] batchArgs) {
		Map params = new HashMap();
		for (int i = 0; i < batchArgs.length; i++) {
			String param = batchArgs[i];
			if (param != null) {
				if (param.startsWith("-D")) {
					readKeyValuePair(param, params);
				} else {
					invalidParam();
				}
			}
		}
		return params;
	}

	@SuppressWarnings("unchecked")
	public void readKeyValuePair(String param, Map params) {
		StringTokenizer st = new StringTokenizer(param.substring(2), "=");
		String key = "";
		String value = "";
		boolean first = true;
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (first) {
				key = token;
				first = false;
			} else {
				value = value + token;
			}
		}
		params.put(key, value);
	}

	public void invalidParam() {
		throw new IllegalArgumentException(
				"Missing argument(s) ; correct usage is : "
						+ "-DiniFile=<location of ini file>"
						+ "-Dbatch=<batchtype>");

	}

	public abstract void run(Map batchArgs);

}
