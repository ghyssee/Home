package be.home.common.main;

import java.io.*;
import java.net.URL;

import be.home.model.ConfigTO;
import be.home.model.ParamTO;
import be.home.model.UltratopConfig;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.xml.DOMConfigurator;
import java.net.*;
import java.security.CodeSource;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import be.home.common.constants.InitConstants;
import be.home.common.exceptions.ApplicationException;

import org.apache.log4j.Logger;

public abstract class BatchJobV2 {

    private static final Logger log = Logger.getLogger(BatchJobV2.class);
    public static String workingDir = System.getProperty("user.dir");
    private String paramIniFile = workingDir + "/config/config.json";

    public Map <String,String> validateParams(String[] args, ParamTO paramArray []) {
        Map <String,String> params = initParams(args, paramArray);
            if (paramArray != null){
                //printParameterList(requiredParams);
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
        System.out.print(StringUtils.rightPad("Id", maxLength));
        System.out.print(StringUtils.rightPad(REQ, lengthRequired));
        System.out.println("Description");
        System.out.println(StringUtils.repeat("-", maxLength  + lengthRequired + maxLengthDescription));
        for (int i=0; i < requiredParams.length; i++){
            System.out.print(StringUtils.rightPad(requiredParams[i].getId(), maxLength));
            System.out.print(StringUtils.rightPad(requiredParams[i].isRequired() ? "Y" : "N", lengthRequired));
            for (int j=0; j < requiredParams[i].getDescription().length; j++){
                if (j==0) {
                    System.out.println(requiredParams[i].getDescription()[j]);
                }
                else {
                    System.out.println(StringUtils.repeat(" ", maxLength + lengthRequired) + requiredParams[i].getDescription()[j]);
                }
            }
            //System.out.println(requiredParams[i].getDescription());
        }
        System.out.println(StringUtils.repeat("=", maxLength + lengthRequired +  maxLengthDescription));
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

    private Map <String,String> initParamsOld(String args[], ParamTO requiredParams []){

        Map <String,String> params = new HashMap ();
        String key = null;
        String value = null;

        if (args != null){
            for (int i=0; i < args.length; i++){
                if ((i%2) == 0){

                }
                String cmds[] = args[i].split(" -");
                System.out.println(" args length : " + args.length);
                if (cmds == null){
                    printParameterList("Invalid argument : " + args[i] + "\n" + "Key And Value should be seperated by a SPACE", requiredParams);
                }
                else {
                    for (int j=0; j < cmds.length; j++){
                        String param[] = cmds[j].split("=");
                        System.out.println(" args[i] = " +  args[i]);
                        System.out.println(" cmds[j] = " +  cmds[j]);
                        if (param == null || param.length != 2){
                            printParameterList("Invalid argument : " + args[i] + "\n" + "Key And Value should be seperated by a SPACE", requiredParams);
                        }
                        else {
                            params.put(param[0].toUpperCase(), param[1]);
                        }
                    }
                }
            }
        }
        return params;
    }

    public void invalidParam() {
        throw new IllegalArgumentException(
                "Missing argument(s) ; correct usage is : "
                        + "-DiniFile=<location of ini file>"
                        + "-Dbatch=<batchtype>");

    }

    public ConfigTO.Config init() throws FileNotFoundException, UnsupportedEncodingException, IOException {
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
        InputStream i = new FileInputStream(iniFile.getAbsolutePath());
        Reader reader = new InputStreamReader(i, "UTF-8");
        JsonReader r = new JsonReader(reader);
        Gson gson = new Gson();
        ConfigTO.Config config = gson.fromJson(r, ConfigTO.Config.class);
        // check if log directory exists, if not create it
        File logDir = new File(workingDir + File.separator + config.logDir);
        System.out.println("LogDir = " + logDir.getAbsolutePath());
        if (!logDir.exists()){
            log.warn("Log Directory does not exist.... Creating " + logDir.getAbsolutePath());
            logDir.mkdirs();
        }

        r.close();
        return config;
    }


    public UltratopConfig.Config init(String configFile) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        InputStream i = new FileInputStream(configFile);
        Reader reader = new InputStreamReader(i, "UTF-8");
        JsonReader r = new JsonReader(reader);
        Gson gson = new Gson();
        UltratopConfig.Config config = gson.fromJson(r, UltratopConfig.Config.class);
        r.close();
        return config;
    }

    public abstract void run();

}
