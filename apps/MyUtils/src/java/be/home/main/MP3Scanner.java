package be.home.main;

import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.domain.model.service.MP3Exception;
import be.home.domain.model.service.MP3JAudioTaggerServiceImpl;
import be.home.domain.model.service.MP3Service;
import be.home.main.tools.ZipFiles;
import be.home.model.ConfigTO;
import be.home.model.ParamTO;
import be.home.common.logging.Log4GE;
import be.home.common.main.BatchJobV2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.TagException;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.regex.Pattern;

/**
 * Created by ghyssee on 20/02/2015.
 */
public class MP3Scanner extends BatchJobV2 {

    private static final String VERSION = "V1.0";

    private static final Logger log =  getMainLog(MP3Scanner.class);
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
        String ROOT = "t:\\My Music\\iPod\\Ultratop 50 20200104 04 Januari 2020";
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
            log.info("Processing " + fileName);
            if (fileName.length() > 40){
                //System.out.println("Filename too long:" + fileName);
            }
            else {
                if (containsFrench(fileName)){
                    System.out.println("Filename with special characters:" + aFile);
                    log.warn(fileName + ": " + "Filename contains special characters");
                }
            }
            try {
                MP3Service mp3File = new MP3JAudioTaggerServiceImpl(aFile.toString());
                if (mp3File.isSave()){
                    saveMP3(mp3File);
                }
            }
            catch (MP3Exception | TagException | CannotReadException | InvalidAudioFrameException |
                   ReadOnlyFileException ex){
                log.info(ex.getMessage());
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

    private static void saveMP3(MP3Service mp3File) throws IOException, TagException, CannotReadException, InvalidAudioFrameException, ReadOnlyFileException, MP3Exception {
        File oldFile = new File(mp3File.getFile());
        //File newFile = new File(Setup.getInstance().getFullPath(Constants.Path.NEW) + File.separator + prefixFileName + originalFile.getName());
        File newFile = new File("C:\\temp\\0\\Ultratop" + File.separator + oldFile.getName());
        Files.copy(oldFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        MP3Service newMP3File = new MP3JAudioTaggerServiceImpl(newFile.getAbsolutePath(), false);
        newMP3File.setTag(mp3File.getTag());
        newMP3File.commit();
    }

    public static boolean containsFrench(String s) {
        Pattern frenchPattern = Pattern.compile("(?i)[çœÁÉÍÓÚÝáéíóúýÄËÏÖÜäëïöüÿÀÈÌÒÙàèìòùÂÊÎÔÛâêîôûÅå]");
        return frenchPattern.matcher(s).find();
    }



}
