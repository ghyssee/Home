package be.home.common.main;

import java.io.*;

import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.utils.JSONUtils;
import be.home.model.ConfigTO;
import be.home.model.ParamTO;
import org.apache.commons.lang3.StringUtils;
import java.net.*;
import java.security.CodeSource;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public abstract class BatchJobV2 {

    public static String workingDir = System.getProperty("user.dir");
    public static Logger log = null;

    public static Logger getMainLog(Class className){
        setLogFile(className.getSimpleName() + ".log");
        Logger log = Logger.getLogger(className);
        return log;
    }

    public static Logger getLogger(Class className){
        if (log == null) {
            log = Logger.getLogger(className);
        }
        return log;
    }

    private static Logger setLogFile(String name){
        String logFile = Setup.getInstance().getFullPath(Constants.Path.LOG) + File.separator + name;
        System.setProperty("logfile.name", logFile);
        System.setProperty("p6spy.config.logfile", Setup.getInstance().getFullPath(Constants.Path.LOG) + File.separator + "P6Spy." + name);
        java.util.logging.Logger.getLogger("org.jaudiotagger").setLevel(java.util.logging.Level.OFF);
        Logger log = Logger.getLogger(BatchJobV2.class);
        log.info("Setting Log4J Log file to:" + logFile);
        return log;
    }

    public Map <String,String> validateParams(String[] args, ParamTO paramArray []) {
        Map <String,String> params = initParams(args, paramArray);
            if (paramArray != null){
                for (int i = 0; i < paramArray.length; i++) {
                    if (paramArray[i].isRequired()) {
                        String value = params.get(paramArray[i].getId().toUpperCase());
                        if (value == null) {
                            printParameterList("Required Parameter is Missing: " + paramArray[i].getId(), paramArray);
                        }
                    }
                }
                System.out.println("List of startup parameters:");
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    System.out.println(entry.getKey() + "=" + entry.getValue());
                }
                System.out.println(StringUtils.repeat("=", 50));
        }
        return params;
    }

    public void printHeader(String title, String lineSeperator){
        System.out.println(title);
        System.out.println(StringUtils.repeat(lineSeperator, title.length()));
    }

    public void printParameterList(String errorMessage, ParamTO requiredParams []){
        int maxLength = 0;
        int maxLengthDescription = 0;
        final String REQ = "Required";
        int lengthRequired = REQ.length();

        System.out.println(errorMessage + "\n");


        for (int i=0; i < requiredParams.length; i++){
            maxLength = Math.max(maxLength, requiredParams[i].getId().length());
            for (int j=0; j < requiredParams[i].getDescription().length; j++){
                maxLengthDescription = Math.max(maxLengthDescription, requiredParams[i].getDescription()[j].length());
            }
        }
        maxLength++;
        lengthRequired++;
        System.out.println("List of parameters");
        System.out.println(StringUtils.repeat("=", maxLength + lengthRequired + maxLengthDescription));
        System.out.println(StringUtils.rightPad("Id", maxLength) + StringUtils.rightPad(REQ, lengthRequired));
        System.out.println("Description");
        System.out.println(StringUtils.repeat("-", maxLength + lengthRequired + maxLengthDescription));
        for (int i=0; i < requiredParams.length; i++){
            String logMessage = StringUtils.rightPad(requiredParams[i].getId(), maxLength);
            logMessage += StringUtils.rightPad(requiredParams[i].isRequired() ? "Y" : "N", lengthRequired);
            for (int j=0; j < requiredParams[i].getDescription().length; j++){
                if (j==0) {
                    logMessage += requiredParams[i].getDescription()[j];
                }
                else {
                    logMessage += StringUtils.repeat(" ", maxLength + lengthRequired) + requiredParams[i].getDescription()[j];
                    System.out.println(logMessage);
                }
            }
        }
        System.out.println(StringUtils.repeat("=", maxLength + lengthRequired + maxLengthDescription));
        System.exit(1);
    }

    public String getParam(String param, Map <String,String> params){
        if (param != null){
            return params.get(param.toUpperCase());
        }
        return null;
    }

    private Map <String,String> initParams(String args[], ParamTO requiredParams []){

        Map <String,String> params = new HashMap ();
        String key = null;
        String value = null;

        if (args != null){
            for (int i=0; i < args.length; i++) {
                if ((i % 2) == 0) {
                    if (!args[i].startsWith("-")) {
                        printParameterList("Invalid argument : " + args[i] + "\n" + "Key should start with a -, seperated with a SPACE followed by the Value", requiredParams);
                    }
                    key = args[i];
                } else {
                    value = args[i];
                    params.put(key.toUpperCase(), value);
                }
            }

        }
        return params;
    }
    public ConfigTO.Config init() throws IOException {
        return init("MyUtils.log");
    }

    public ConfigTO.Config init(String log) throws IOException {
        String paramIniFile = Setup.getInstance().getFullPath(Constants.Path.CONFIG) + File.separator + "config.json";
        File iniFile = new File(paramIniFile);
        if (!iniFile.exists()){
            iniFile = new File(workingDir + "/../config/config.json");
        }
        if (!iniFile.exists()){

            CodeSource codeSource = BatchJobV2.class.getProtectionDomain().getCodeSource();
            File jarFile = null;
            try {
                jarFile = new File(codeSource.getLocation().toURI().getPath());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            workingDir = jarFile.getParentFile().getPath();
            iniFile = new File(workingDir + "/config/config.json");
        }
        System.out.println("Ini File:" + iniFile.getAbsolutePath());
        ConfigTO.Config config = ( ConfigTO.Config) JSONUtils.openJSON(iniFile.getAbsolutePath(), ConfigTO.Config.class);
        // check if log directory exists, if not create it
        File logDir = new File(workingDir + File.separator + config.logDir);
        System.out.println("LogDir = " + logDir.getAbsolutePath());
        if (!logDir.exists()){
            //log.warn("Log Directory does not exist.... Creating " + logDir.getAbsolutePath());
            logDir.mkdirs();
        }
        return config;
    }

    public abstract void run();

}
