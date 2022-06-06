package be.home.picmgt.main;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import be.home.common.constants.InitConstants;
import be.home.common.exceptions.ApplicationException;
import be.home.common.utils.ini.CommonIniFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ini4j.Profile.Section;

public class PicMgt extends BatchJob {

	private static final Logger log = LogManager.getLogger(PicMgt.class);

	public static void main(String args[]) {
		PicMgt instance = new PicMgt();
		instance.initialize(args);
	}

	public void run(Map batchArgs) {

		String section = (String) batchArgs.get(InitConstants.PARAM_BATCH);
		Section map = readIniFile(section);

		Class c = null;
		try {
			String clazzName = (String) map.get("class");
			c = Class.forName(clazzName);
			Object o = c.newInstance();
			Class[] params = { CommonIniFile.class, String.class };
			Method mainMethod = c.getMethod("start", params);
			Object[] obj = { this.ini, section };
			mainMethod.invoke(o, obj);
		} catch (ApplicationException e) {
			String errorMessage = "ApplicationException";
			log.fatal(errorMessage, e);
			System.exit(1);
		} catch (ClassNotFoundException e) {
			String errorMessage = "ClassNotFoundException";
			log.fatal(errorMessage, e);
			throw new ApplicationException(errorMessage, e);
		} catch (IllegalArgumentException e) {
			String errorMessage = "IllegalArgumentException";
			log.fatal(errorMessage, e);
			throw new ApplicationException(errorMessage, e);
		} catch (IllegalAccessException e) {
			String errorMessage = "IllegalAccessException";
			log.fatal(errorMessage, e);
			throw new ApplicationException(errorMessage, e);
		} catch (InvocationTargetException e) {
			String errorMessage = "InvocationTargetException";
			log.fatal(errorMessage, e);
			throw new ApplicationException(errorMessage, e);
		} catch (SecurityException e) {
			String errorMessage = "SecurityException";
			log.fatal(errorMessage, e);
			throw new ApplicationException(errorMessage, e);
		} catch (NoSuchMethodException e) {
			String errorMessage = "NoSuchMethodException";
			log.fatal(errorMessage, e);
			throw new ApplicationException(errorMessage, e);
		} catch (InstantiationException e) {
			String errorMessage = "InstantiationException";
			log.fatal(errorMessage, e);
			throw new ApplicationException(errorMessage, e);
		}

		updateIniFile();

	}

}
