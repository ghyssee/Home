package be.home.main;

import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.logging.Log4GE;
import be.home.common.main.BatchJobV2;
import be.home.common.utils.FileUtils;
import be.home.common.utils.LogUtils;
import be.home.common.utils.WinUtils;
import be.home.model.ConfigTO;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Created by ghyssee on 20/02/2015.
 */
public class MP3ListMaker extends BatchJobV2 {

    public static Log4GE log4GE;
    public static ConfigTO.Config config;
    private static String LIST_TITLE = "List 2017 V2.txt";
    private static String FILTER = "Ultratop 50 2017";
    private static final Logger log = getMainLog(MP3ListMaker.class);

    public static void main(String args[])  {
        MP3ListMaker instance = new MP3ListMaker();
        try {
            config = instance.init();
            instance.start();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {

    }

    public void start() {

        final String batchJob = "MP3ListFileMaker";
        log4GE = new Log4GE(config.wiki.resultLog);
        log4GE.clear();
        log4GE.start("Make MP3 List File");

        File destinationFile = new File(Setup.getInstance().getFullPath(Constants.Path.MUSIC_LISTS) +
                File.separator + LIST_TITLE);
        PrintWriter writer = null;
        try {
            writer= new PrintWriter(new OutputStreamWriter(new FileOutputStream(destinationFile),
                    StandardCharsets.UTF_8), true);
            String iPodBase = Setup.getInstance().getFullPath(Constants.Path.IPOD);
            //String iPodBase = "o:/Shared/Mijn Muziek/Eric/Ultratop/";
            File folder = new File(iPodBase);
            if (folder.isDirectory()) {
                File[] listOfFiles = folder.listFiles();
                Arrays.sort(listOfFiles);
                for (int i = 0; i < listOfFiles.length; i++) {
                    File tmpFile = listOfFiles[i];
                    if (tmpFile.isDirectory()) {
                        log.info("Directory : " + tmpFile.getName());
                        if (tmpFile.getName().toUpperCase().startsWith(FILTER.toUpperCase())){
                            processDirectory(tmpFile, writer);
                        }
                        else {
                            log.info("Ignoring directory " + tmpFile.getName());
                        }
                    } else {
                        log.info("Ignoring file " + tmpFile.getName());
                    }
                }
            } else {
                log.error("Invalid Directory: " + iPodBase);
            }
        } catch (FileNotFoundException e) {
            LogUtils.logError(log, e);
        }
         finally{
             if (writer != null){
                 writer.close();
             }
         }


        log4GE.end();

    }

    private void processDirectory(File folder, PrintWriter dest){
        File[] listOfFiles = folder.listFiles();
        Arrays.sort(listOfFiles);
        for (int i=0; i < listOfFiles.length; i++){
            File file = listOfFiles[i];
            if (file.isDirectory()) {
                log.info("Ignoring folder " + file.getName());
            }
            else {
                log.info("File: " + file.getName());
                if (file.getName().toUpperCase().endsWith(".MP3")){
                    log.info("Processing file: " + file.getName());
                    String path = file.getParentFile().getName();
                    dest.println("..\\" + path + "\\" + FileUtils.encodeFilename(file.getName()));
                    //System.out.println("path = " + path);
                    //String relative = new File(BASE_DIR).toURI().relativize(new File(BASE_DIR).toURI()).getPath();
                    //System.out.println("relative = " + relative);
                }
                else {
                    log.info("Ignoring file: " + file.getName());
                }
            }
        }
        dest.println("");

    }
}
