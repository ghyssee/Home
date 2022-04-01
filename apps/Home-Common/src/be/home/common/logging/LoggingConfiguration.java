package be.home.common.logging;

import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.main.BatchJobV2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.File;

public class LoggingConfiguration {
    public static Logger log = null;

    public static Logger getMainLog(Class className){
        setLogFile(className.getSimpleName() + ".log");
        Logger log = LogManager.getLogger(className);
        return log;
    }

    public static Logger getLogger(Class className){
        if (log == null) {
            log = log = LogManager.getLogger(className);
        }
        return log;
    }

    private static Logger setLogFile(String name){
        String logDir = Setup.getInstance().getFullPath(Constants.Path.LOG);
        System.setProperty("logDir", logDir);
        System.setProperty("logFilename", name);
        System.setProperty("p6spy.config.logfile", Setup.getInstance().getFullPath(Constants.Path.LOG) + File.separator + "P6Spy." + name);
        java.util.logging.Logger.getLogger("org.jaudiotagger").setLevel(java.util.logging.Level.OFF);
        Logger log = LogManager.getLogger(BatchJobV2.class);
        log.info("Setting Log4J Log file to:" + logDir + File.separator + name);
        return log;
    }

    private static Logger setLogFileOld(String name){
        String logFile = Setup.getInstance().getFullPath(Constants.Path.LOG) + File.separator + name;
        System.setProperty("logfile.name", logFile);
        System.setProperty("p6spy.config.logfile", Setup.getInstance().getFullPath(Constants.Path.LOG) + File.separator + "P6Spy." + name);
        java.util.logging.Logger.getLogger("org.jaudiotagger").setLevel(java.util.logging.Level.OFF);
        Logger log = LogManager.getLogger((BatchJobV2.class));
        log.info("Setting Log4J Log file to:" + logFile);
        return log;
    }
}
