package be.home.main.tools;

import be.home.common.logging.Log4GE;
import be.home.common.main.BatchJobV2;
import be.home.common.utils.FileUtils;
import be.home.model.*;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.*;

/**
 * Created by ghyssee on 20/02/2015.
 */
public class DirList extends BatchJobV2 {

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

        DirList instance = new DirList();
        instance.printHeader("DIRLIST " + VERSION, "=");
        Map <String,String> params = instance.validateParams(args, PARAMS);
        instance.printHeader("Result: ", "");
        instance.start(instance.getParam(PARAMS[BASE].getId(), params), instance.getParam(PARAMS[OUTPUT].getId(), params), instance.getParam(PARAMS[INDENT_ID].getId(), params));
    }

    @Override
    public void run() {

    }

    public static void start(String baseDir, String output, String indent){

        if (indent != null){
            try {
                INDENT = Integer.parseInt(indent);
            }
            catch ( NumberFormatException e){
                System.err.println("Problem with indent " + indent + ". Using default indent of " + INDENT);
                // do nothing, use default
            }
        }

        if (output == null){
            output = BatchJobV2.workingDir + File.separator + DEFAULT_FILE;
        }
        File folder = new File(baseDir);
        List <FileTO> files = new ArrayList<FileTO>();
        if (!folder.isDirectory()){
            System.err.println("INVALID baseDir: " + baseDir);
            return;
        }
        else {
            files = walk(folder);
        }
        int level = 0;
        Collections.sort(files);
        int rootLevel = FileUtils.getLevel(folder);
        System.out.println(folder.getName());
        File destinationFile = new File(output);
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(destinationFile);
            writer.println(folder.getName());
            for (FileTO file : files){
                String margin = StringUtils.repeat(" ", (file.getLevel()-rootLevel)*INDENT);
                if (file.getFile().isDirectory()) {
                    System.out.println(margin + "<" + file.getFile().getName() + ">");
                    writer.println(margin + "<" + file.getFile().getName() + ">");
                }
                else {
                    margin += StringUtils.repeat(" ", INDENT);
                    System.out.println(margin + file.getFile().getName());
                    writer.println(margin + file.getFile().getName());
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        finally {
            if (writer != null) {
                writer.close();
            }
        }
        System.out.println("Output is written to " + output);
    }

    public static List <FileTO>  walk (File root){
        File[] list = root.listFiles();
        List <FileTO> files = new ArrayList<FileTO>();
        if (list == null) return null;

        for ( File f : list ) {
            FileTO fileTO = new FileTO();
            fileTO.setFile(f);
            fileTO.setLevel(FileUtils.getLevel(f));
            files.add(fileTO);
            if ( f.isDirectory() ) {
                files.addAll(walk(new File(f.getAbsolutePath())));
            }
        }
        return files;
    }

}
