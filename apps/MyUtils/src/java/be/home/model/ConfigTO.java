package be.home.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ghyssee on 20/02/2015.
 */
public class ConfigTO {

    public class Log4J{
        public String config;
        public String toString() {
            StringBuilder b = new StringBuilder();
            b.append("Log4J config : " + config + "\n");
            return b.toString();
        }
    }

    public class Wiki {
        public String inputDir;
        public String outputDir;
        public String inputFile;
        public String outputFile;
        public String maxAppsFile;
        public String resultLog;

        public String toString() {
            StringBuilder b = new StringBuilder();
            b.append("Wiki inputDir : " + inputDir + "\n");
            b.append("Wiki outputDir : " + outputDir + "\n");
            b.append("Wiki inputFile : " + inputFile + "\n");
            b.append("Wiki outputFile : " + outputFile + "\n");
            b.append("Wiki maxAppsFile : " + maxAppsFile + "\n");
            b.append("Wiki resultLog : " + resultLog + "\n");
            return b.toString();
        }
    }

    public class Mezzmo {
        public String database;
        public String toString() {
            StringBuilder b = new StringBuilder();
            b.append("Mezzmo Database: " + database + "\n");
            return b.toString();
        }
    }

    public class Parts {
        public String id;
        public String name;
    }

    public class ExcludePaths {
        public String path;
        public String expression;
    }

    public class Movies {
        public String importFile;
        public String outputDir;
        public String synchronizeDir;
        public String stockPlace;
        public String log;
        public List <ExcludePaths> excludePaths = new ArrayList <ExcludePaths>();

        public String toString() {
            StringBuilder b = new StringBuilder();
            b.append("Movies importFile : " + importFile + "\n");
            b.append("Movies outputDir : " + outputDir + "\n");
            b.append("synchronizeDir outputDir : " + synchronizeDir + "\n");
            b.append("stockPlace : " + stockPlace + "\n");
            b.append("log : " + log + "\n");
            for (ExcludePaths path : excludePaths){
                b.append("excludePath : " + path.path + "\n");
                b.append("expression : " + path.expression + "\n");
            }
            return b.toString();
        }
    }

    private static String getMessage(Object o){

        if (o == null){
            return "Error";
        }
        else {
            return o.toString();
        }
    }

    public class Config {
        public String oneDriveDir;
        public String configDir;
        public String logDir;
        public String logFile;
        public Log4J log4J;
        public Wiki wiki;
        public Movies movies;
        public Mezzmo mezzmo;
        public List <Parts> parts = new ArrayList <Parts>();

        public String getFullPathConfigDir(){
            return configDir;
        }

        public void Config(){};


        public String toString() {
            StringBuilder b = new StringBuilder();
            b.append("OneDriveDir : " + oneDriveDir + "\n");
            b.append("ConfigDir : " + configDir + "\n");
            b.append("logDir : " + logDir + "\n");
            b.append("logFile : " + logFile + "\n");
            b.append(getMessage(log4J));
            b.append(getMessage(wiki));
            return b.toString();
        }
    }
}
