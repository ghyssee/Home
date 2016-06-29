package be.home.main;

import be.home.common.logging.Log4GE;
import be.home.common.main.BatchJobV2;
import be.home.common.utils.FileUtils;
import be.home.common.utils.WinUtils;
import be.home.model.ConfigTO;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Created by ghyssee on 20/02/2015.
 */
public class MP3ListMaker extends BatchJobV2 {

    public static Log4GE log4GE;
    public static ConfigTO.Config config;
    private static String LIST_TITLE = "List 2016 V2.txt";
    private static String BASE_DIR = "r:/My Music/iPod";
    //private static String BASE_DIR = "g:/My Music/ultratop";
    private static String DESTINATION_DIR = WinUtils.getOneDrivePath() + File.separator+ "Muziek/Lists/";
    private static String FILTER = "Ultratop 50 2016";


    public static void main(String args[])  {
        MP3ListMaker instance = new MP3ListMaker();
        try {
            config = instance.init();
            System.out.println(config.toString());
            System.out.println(config.log4J);
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

        File destinationFile = new File(DESTINATION_DIR + LIST_TITLE);
        PrintWriter writer = null;
        try {
            //writer = new PrintWriter(destinationFile);
            writer= new PrintWriter(new OutputStreamWriter(new FileOutputStream(destinationFile),
                    StandardCharsets.UTF_8), true);
            File folder = new File(BASE_DIR);
            //BufferedWriter writer = new BufferedWriter(new PrintWriter(destinationFile));
            if (folder.isDirectory()) {
                File[] listOfFiles = folder.listFiles();
                Arrays.sort(listOfFiles);
                for (int i = 0; i < listOfFiles.length; i++) {
                    File tmpFile = listOfFiles[i];
                    if (tmpFile.isDirectory()) {
                        System.out.println("Directory : " + tmpFile.getName());
                        if (tmpFile.getName().toUpperCase().startsWith(FILTER.toUpperCase())){
                            processDirectory(tmpFile, writer);
                        }
                        else {
                            System.out.println("Ignoring directory " + tmpFile.getName());
                        }
                    } else {
                        System.out.println("Ignoring file " + tmpFile.getName());
                    }
                }
            } else {
                System.err.println("Invalid Directory: " + BASE_DIR);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
                System.out.println("Ignoring folder " + file.getName());
            }
            else {
                System.out.println("File: " + file.getName());
                if (file.getName().toUpperCase().endsWith(".MP3")){
                    System.out.println("Processing file: " + file.getName());
                    String path = file.getParentFile().getName();
                    dest.println("..\\" + path + "\\" + FileUtils.encodeFilename(file.getName()));
                    //System.out.println("path = " + path);
                    //String relative = new File(BASE_DIR).toURI().relativize(new File(BASE_DIR).toURI()).getPath();
                    //System.out.println("relative = " + relative);
                }
                else {
                    System.out.println("Ignoring file: " + file.getName());
                }
            }
        }
        dest.println("");

    }
}
