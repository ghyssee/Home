package be.home.main;

import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.utils.MyFileWriter;
import be.home.domain.model.MP3Helper;
import be.home.domain.model.service.MP3Exception;
import be.home.domain.model.service.MP3JAudioTaggerServiceImpl;
import be.home.domain.model.service.MP3Service;
import be.home.main.tools.ZipFiles;
import be.home.model.ConfigTO;
import be.home.model.ParamTO;
import be.home.common.logging.Log4GE;
import be.home.common.main.BatchJobV2;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.FileUtils;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.TagException;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Pattern;

/**
 * Created by ghyssee on 20/02/2015.
 */
public class MP3Scanner extends BatchJobV2 {

    private static final String VERSION = "V1.0";

    private static final Logger log = getMainLog(MP3Scanner.class);
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
    private static String timeStamp = new SimpleDateFormat("yyyyMM.dd.HH.mm.ss").format(new java.util.Date());
    private static String ROOT = "c:\\My Data\\tmp\\Java\\MP3Processor\\Test\\Ultratop 50 20200104 04 Januari 2020.20230424";
    private static String ROOT2 = "t:\\My Music\\iPod\\Ultratop 50 20210102 02 Januari 2021";
    private static final boolean OVERWRITE = true;

    public static void main(String args[]) {

        MP3Scanner instance = new MP3Scanner();
        instance.printHeader("MP3Scanner " + VERSION, "=");
        //Map <String,String> params = instance.validateParams(args, PARAMS);
        instance.printHeader("MP3Scanner: ", "");
        //instance.start(instance.getParam(PARAMS[BASE].getId(), params), instance.getParam(PARAMS[OUTPUT].getId(), params), instance.getParam(PARAMS[INDENT_ID].getId(), params));
        try {
            instance.start();
        } catch (MP3Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

    }


        public static void start() throws MP3Exception {
            MyFileWriter myFile = null;
            try {
                myFile = new MyFileWriter("c:\\My Data\\tmp\\MP3Scanner.log", false);
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileVisitor<Path> fileProcessor = new ProcessFile(myFile);
            try {
                Files.walkFileTree(Paths.get(ROOT), fileProcessor);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    private static void addWarning(ArrayList<String> warnings, String message){
        warnings.add(message);
        log.warn(message);
    }



    private static void checkFile(String fileToCheck, ArrayList<String> warnings)  {
        //File myFile = new File("c:\\My Data\\tmp\\Backup\\Ultratop 50 20200104 04 Januari 2020.20230424\\Test.mp3");
        StringBuffer sb = validateFile(fileToCheck, false);
        String newline = System.getProperty("line.separator");
        log.info(sb);
        String[] array = sb.toString().split(newline);
        for (String line : array) {
            if (line.startsWith("WARNING:")){
                addWarning(warnings, "WARNING found: fixing file");
                fixFile(fileToCheck, warnings);
            }
        }
    }


    private static void fixFile(String file, ArrayList<String> warnings) {
        String BACKUP_DIR = "c:\\My Data\\tmp\\Backup\\back";
        String newline = System.getProperty("line.separator");
        StringBuffer sb = validateFile(file, true);
        log.info(sb);
        String[] array = sb.toString().split(newline);
        boolean fixed = false;
        for (String line : array) {
            if (line.startsWith("FIXED:")) {
                addWarning(warnings, "FIXED file");
                fixed = true;
                File backup = new File(file + ".bak");
                if (backup.exists()){
                    File destinationFile = new File(BACKUP_DIR + File.separator + backup.getName());
                    try {
                        FileUtils.mkdir(new File(BACKUP_DIR), true);
                        Files.move(backup.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        addWarning(warnings, "File moved to " + destinationFile.getAbsolutePath());
                    } catch (IOException e) {
                        addWarning(warnings, "There was a problem moving the file to " + destinationFile.getAbsolutePath());
                        throw new RuntimeException(e);
                    }
                }
                else {
                    addWarning(warnings, "No Backup found!");
                }
            }
        }
        if (!fixed) {
            addWarning(warnings, "Problem fixing " + file);
        }
    }

    private static StringBuffer validateFile(String file, boolean fix)  {
        File myFile = new File("c:\\My Data\\tmp\\Backup\\Ultratop 50 20200104 04 Januari 2020.20230424\\Test.mp3");
        // Execute command
        List<String> params = new ArrayList();
        params.add("C:\\My Programs\\Personal\\mp3val\\mp3val.exe");
        if (fix) {
            params.add("-f");
        }
        params.add(file);

        ProcessBuilder processBuilder = new ProcessBuilder(params);

        processBuilder.redirectErrorStream(true);
        Process process = null;
        try {
            process = processBuilder.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        InputStream is = process.getInputStream();
        StringBuffer sb = new StringBuffer();
        int in = -1;
        try {
            while ((in = is.read()) != -1) {
                sb.append((char) in);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        int exitCode = 0;
        try {
            exitCode = process.waitFor();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("Exited with " + exitCode);
        return sb;
    }

    private static final class ProcessFile extends SimpleFileVisitor<Path> {
        private MyFileWriter myFile = null;
        private final PathMatcher matcher;

        public ProcessFile(MyFileWriter myFile) throws MP3Exception {
            this.myFile = myFile;
            matcher = FileSystems.getDefault()
                    .getPathMatcher("regex:.*(?i:mp3)");
        }


        @Override public FileVisitResult visitFile(
                Path aFile, BasicFileAttributes aAttrs
        ) throws IOException {

            if (matcher.matches(aFile)) {

                String fileName = aFile.getFileName().toString();
                log.info("Processing " + fileName);
                if (fileName.length() > 40){
                    //System.out.println("Filename too long:" + fileName);
                }
                else {
                    if (containsFrench(fileName)){
                        log.warn(fileName + ": " + "Filename contains special characters");
                    }
                }
                try {
                    MP3Service mp3File = new MP3JAudioTaggerServiceImpl(aFile.toString());
                    if (mp3File.isSave()){
                        //saveMP3(mp3File);
                    }
                    if (mp3File.isWarning()) {
                        myFile.append(StringUtils.repeat('=', 100));
                        myFile.append("Processing " + fileName);
                        myFile.append("Location: " + aFile.getParent().toString());
                    }
                    ArrayList<String> warnings = mp3File.getWarnings();
                    for (String warningMessage : warnings){
                        myFile.append(warningMessage);
                    }
                    warnings.clear();
                    checkFile(aFile.toString(), warnings);
                    for (String warningMessage : warnings){
                        myFile.append(warningMessage);
                    }
                }
                catch (MP3Exception ex){
                    log.info(ex.getMessage());
                    /*
                } catch (TagException e) {
                    e.printStackTrace();
                } catch (CannotReadException e) {
                    e.printStackTrace();
                } catch (InvalidAudioFrameException e) {
                    e.printStackTrace();
                } catch (ReadOnlyFileException e) {
                    e.printStackTrace();
                    */
                }

            }

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
        if (OVERWRITE){
            File oldFile = new File(mp3File.getFile());
            String strippedAlbum = MP3Helper.getInstance().stripFilename(mp3File.getAlbum());
            File destinationDir = new File(Setup.getInstance().getFullPath(Constants.Path.TMP) + File.separator + "Backup" +
                    File.separator + MP3Helper.getInstance().stripFilename(strippedAlbum) + "." + timeStamp);
            FileUtils.mkdir(destinationDir, true);
            File newFile = new File(destinationDir.getAbsolutePath() + File.separator + File.separator + oldFile.getName());
            Files.copy(oldFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            int rating = mp3File.getRating();
            mp3File.setRating(rating);
            mp3File.commit();
        }
        else {
            File oldFile = new File(mp3File.getFile());
            File newFile = new File(Setup.getInstance().getFullPath(Constants.Path.TMP) + File.separator + "Ultratop" + File.separator + oldFile.getName());
            Files.copy(oldFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            MP3Service newMP3File = new MP3JAudioTaggerServiceImpl(newFile.getAbsolutePath(), false);
            newMP3File.setTag(mp3File.getTag());
            // set rating to WMP
            int rating = newMP3File.getRating();
            newMP3File.setRating(rating);
            newMP3File.commit();
        }
    }

    public static boolean containsFrench(String s) {
        Pattern frenchPattern = Pattern.compile("(?i)[çœÁÉÍÓÚÝáéíóúýÄËÏÖÜäëïöüÿÀÈÌÒÙàèìòùÂÊÎÔÛâêîôûÅå]");
        return frenchPattern.matcher(s).find();
    }



}
