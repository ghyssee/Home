package be.home.main;

import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.exceptions.ApplicationException;
import be.home.common.utils.MyFileWriter;
import be.home.domain.model.MP3Helper;
import be.home.domain.model.service.MP3Exception;
import be.home.domain.model.service.MP3JAudioTaggerServiceImpl;
import be.home.domain.model.service.MP3Service;
import be.home.common.main.BatchJobV2;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.FileUtils;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by ghyssee on 20/02/2015.
 */
public class MP3Scanner extends BatchJobV2 {

    private static final String VERSION = "V1.0";

    private static final Logger log = getMainLog(MP3Scanner.class);
    private static String timeStamp = new SimpleDateFormat("yyyyMM.dd.HH.mm.ss").format(new java.util.Date());
    private static String ROOT = "p:\\Shared\\My Music\\MP3Scanner";
    private static String ROOT2 = "t:\\My Music\\iPod\\Ultratop 50 20210102 02 Januari 2021";
    private static String ROOT3 = "c:\\My Data\\tmp\\Java\\MP3Processor\\test\\Ultratop 50 20200201 01 Februari 2020";
    private static final boolean OVERWRITE = true;
    private static final String BACKUP = Setup.getInstance().getFullPath(Constants.Path.TMP) + File.separator + "Backup";
    private static final String MP3VAL = "C:\\My Programs\\mp3val\\mp3val.exe";

    public static void main(String args[]) {

        MP3Scanner instance = new MP3Scanner();
        instance.printHeader("MP3Scanner " + VERSION, "=");
        instance.printHeader("MP3Scanner: ", "");
        try {
            instance.start();
        } catch (MP3Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

    }

        public void start() throws MP3Exception {
            MyFileWriter myFile = null;
            MyFileWriter mp3PRoblemFile = null;
            String logFile = Setup.getInstance().getFullPath(Constants.Path.MP3SCANNER) +
                    File.separator + "MP3Scanner." + timeStamp + ".log";
            String problemFile = Setup.getInstance().getFullPath(Constants.Path.MP3SCANNER) +
                    File.separator + "MP3Problems." + timeStamp + ".txt";
            try {
                myFile = new MyFileWriter(logFile, false);
                mp3PRoblemFile = new MyFileWriter(problemFile, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileVisitor<Path> fileProcessor = new ProcessFile(myFile, mp3PRoblemFile);
            try {
                Files.walkFileTree(Paths.get(ROOT), fileProcessor);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    private void addWarning(ArrayList<String> warnings, String message){
        warnings.add(message);
        log.warn(message);
    }



    private void checkFile(String fileToCheck, ArrayList<String> warnings, String album, MyFileWriter mp3ProblemFile) throws IOException {
        //File myFile = new File("c:\\My Data\\tmp\\Backup\\Ultratop 50 20200104 04 Januari 2020.20230424\\Test.mp3");
        StringBuffer sb = validateFile(fileToCheck, false);
        String newline = System.getProperty("line.separator");
        log.info(sb);
        String[] array = sb.toString().split(newline);
        for (String line : array) {
            if (line.startsWith("WARNING:")){
                addWarning(warnings, "mp3val: WARNING found: fixing file");
                fixFile(fileToCheck, warnings, album, mp3ProblemFile);
                break;
            }
        }
    }


    private void fixFile(String file, ArrayList<String> warnings, String album, MyFileWriter mp3ProblemFile) throws IOException {
        String BACKUP_DIR = BACKUP + File.separator
            + MP3Helper.getInstance().stripFilename(album) + "." + timeStamp;
        be.home.common.utils.FileUtils.checkDirectory(BACKUP_DIR);
        String newline = System.getProperty("line.separator");
        StringBuffer sb = validateFile(file, true);
        log.info(sb);
        String[] array = sb.toString().split(newline);
        boolean fixed = false;
        for (String line : array) {
            if (line.startsWith("FIXED:")) {
                fixed = true;
                File backup = new File(file + ".bak");
                if (backup.exists()){
                    File destinationFile = new File(BACKUP_DIR + File.separator + backup.getName());
                    be.home.common.utils.FileUtils.checkDirectory(BACKUP_DIR);
                    try {
                        Files.move(backup.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        throw new ApplicationException("There was a problem moving the file " + backup.toString(), e);
                    }
                    addWarning(warnings, "File fixed + moved to " + destinationFile.getAbsolutePath());
                }
                else {
                    mp3ProblemFile.append(file);
                    addWarning(warnings, "No Backup found!");
                }
            }
        }
        if (!fixed) {
            mp3ProblemFile.append(file);
            addWarning(warnings, "Problem fixing " + file);
        }
    }

    private StringBuffer validateFile(String file, boolean fix)  {
        File myFile = new File("c:\\My Data\\tmp\\Backup\\Ultratop 50 20200104 04 Januari 2020.20230424\\Test.mp3");
        // Execute command
        List<String> params = new ArrayList();
        params.add(MP3VAL);
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
            throw new ApplicationException("There was a problem while running the program " + MP3VAL, e);
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
            throw new ApplicationException("There was a problem getting exit code while running program " + MP3VAL, e);
        }
        log.info("Exited with " + exitCode);
        return sb;
    }

    private final class ProcessFile extends SimpleFileVisitor<Path> {
        private MyFileWriter myFile = null;
        private final PathMatcher matcher;
        private MyFileWriter mp3ProblemFile = null;

        public ProcessFile(MyFileWriter myFile, MyFileWriter mp3ProblemFile) throws MP3Exception {
            this.myFile = myFile;
            this.mp3ProblemFile = mp3ProblemFile;
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
                        saveMP3(mp3File);
                    }
                    if (mp3File.isWarning()) {
                        printHeader(myFile, aFile);
                    }
                    ArrayList<String> warnings = mp3File.getWarnings();
                    for (String warningMessage : warnings){
                        myFile.append(warningMessage);
                    }
                    warnings.clear();
                    checkFile(aFile.toString(), warnings, mp3File.getAlbum(), mp3ProblemFile);
                    if (warnings.size() > 0 && !mp3File.isWarning()){
                        printHeader(myFile, aFile);
                    }
                    for (String warningMessage : warnings){
                        myFile.append(warningMessage);
                    }
                }
                catch (MP3Exception ex){
                    log.info(ex.getMessage());

                } catch (TagException e) {
                    e.printStackTrace();
                } catch (CannotReadException e) {
                    e.printStackTrace();
                } catch (InvalidAudioFrameException e) {
                    e.printStackTrace();
                } catch (ReadOnlyFileException e) {
                    e.printStackTrace();
                }
            }

            return FileVisitResult.CONTINUE;
        }

        @Override  public FileVisitResult preVisitDirectory(
                Path aDir, BasicFileAttributes aAttrs
        )  {
            //System.out.println("Processing directory:" + aDir);
            return FileVisitResult.CONTINUE;
        }
    }

    private void printHeader(MyFileWriter myFile, Path aFile) throws IOException {
        myFile.append(StringUtils.repeat('=', 100));
        myFile.append("Processing " + aFile.getFileName().toString());
        myFile.append("Location: " + aFile.getParent().toString());
    }

    private void saveMP3(MP3Service mp3File) throws IOException, TagException, CannotReadException, InvalidAudioFrameException, ReadOnlyFileException, MP3Exception {
        if (OVERWRITE){
            File oldFile = new File(mp3File.getFile());
            String strippedAlbum = MP3Helper.getInstance().stripFilename(mp3File.getAlbum());
            File destinationDir = new File(BACKUP + File.separator +
                    MP3Helper.getInstance().stripFilename(strippedAlbum) + "." + timeStamp);
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

    public boolean containsFrench(String s) {
        Pattern frenchPattern = Pattern.compile("(?i)[çœÁÉÍÓÚÝáéíóúýÄËÏÖÜäëïöüÿÀÈÌÒÙàèìòùÂÊÎÔÛâêîôûÅå]");
        return frenchPattern.matcher(s).find();
    }



}
