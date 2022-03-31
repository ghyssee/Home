package be.home.main;

import be.home.model.ConfigTO;
import be.home.model.ParamTO;
import be.home.common.logging.Log4GE;
import be.home.common.main.BatchJobV2;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.regex.Pattern;

/**
 * Created by ghyssee on 20/02/2015.
 */
public class MP3Scanner extends BatchJobV2 {

    private static final String VERSION = "V1.0";

    public static Log4GE log4GE;
    public static ConfigTO.Config config;
    private static final int BASE = 0;
    private static final int OUTPUT = 1;
    private static final int INDENT_ID = 2;
    private static final String DEFAULT_FILE = "dirlist.txt";
    private static int INDENT = 5;
    private static ParamTO PARAMS [] = {new ParamTO("-base", new String[] {"This is the base directory to start the scanning", "of files and folders"}, ParamTO.REQUIRED),
            new ParamTO("-output", new String[] {"This is the output file to which the result will be written",
                    "Default output file is <current directory>/" + DEFAULT_FILE}),
            new ParamTO("-indent", new String[] {"Number of characters to append after a new level", "DEFAULT is " + String.valueOf(INDENT)})};


    public static void main(String args[]) {

        MP3Scanner instance = new MP3Scanner();
        instance.printHeader("MP3Scanner " + VERSION, "=");
        //Map <String,String> params = instance.validateParams(args, PARAMS);
        instance.printHeader("MP3Scanner: ", "");
        //instance.start(instance.getParam(PARAMS[BASE].getId(), params), instance.getParam(PARAMS[OUTPUT].getId(), params), instance.getParam(PARAMS[INDENT_ID].getId(), params));
        instance.start();
    }

    @Override
    public void run() {

    }

    public static void start(){
        String ROOT = "C:\\My Programs\\OneDrive\\AutoIt";
        FileVisitor<Path> fileProcessor = new ProcessFile();
        try {
            Files.walkFileTree(Paths.get(ROOT), fileProcessor);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final class ProcessFile extends SimpleFileVisitor<Path> {
        @Override public FileVisitResult visitFile(
                Path aFile, BasicFileAttributes aAttrs
        ) throws IOException {
            String fileName = aFile.getFileName().toString();
            if (fileName.length() > 40){
                //System.out.println("Filename too long:" + fileName);
            }
            else if (containsFrench(fileName)){
                System.out.println("Filename with special characters:" + aFile);
            }
            //System.out.println("Processing file:" + fileName);
            return FileVisitResult.CONTINUE;
        }

        @Override  public FileVisitResult preVisitDirectory(
                Path aDir, BasicFileAttributes aAttrs
        ) throws IOException {
            //System.out.println("Processing directory:" + aDir);
            return FileVisitResult.CONTINUE;
        }
    }

    public static boolean containsFrench(String s) {
        Pattern frenchPattern = Pattern.compile("(?i)[çœÁÉÍÓÚÝáéíóúýÄËÏÖÜäëïöüÿÀÈÌÒÙàèìòùÂÊÎÔÛâêîôûÅå]");
        return frenchPattern.matcher(s).find();
    }



}
