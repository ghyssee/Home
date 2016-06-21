package be.home.main;

import be.home.common.archiving.Archiver;
import be.home.common.archiving.ZipArchiver;
import be.home.common.logging.Log4GE;
import be.home.common.main.BatchJobV2;
import be.home.common.utils.DateUtils;
import be.home.model.*;
import com.mpatric.mp3agic.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.CodeSource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by ghyssee on 20/02/2015.
 */
public class MP3Processor extends BatchJobV2 {

    private static final String VERSION = "V1.0";

    public static Log4GE log4GE;
    public static ConfigTO.Config config;
    private static final Logger log = Logger.getLogger(ZipFiles.class);
    private static ParamTO PARAMS [] = {new ParamTO("-source", new String[]{"This is the source directory to start the backup", "of files and folders"},
            ParamTO.REQUIRED),
            new ParamTO("-zipFile", new String[]{"This is the name of the zipfile"},
                    ParamTO.REQUIRED)
    };


    public static void main(String args[]) {

        String currentDir = System.getProperty("user.dir");
        log.info("Current Working dir: " + currentDir);

        MP3Processor instance = new MP3Processor();
        instance.printHeader("ZipFiles " + VERSION, "=");
        try {
            instance.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

    }

    public void start() throws IOException {

        List <Path> listOfFiles = fileList("C:/My Programs/Private Documents/test");
        for (Path path : listOfFiles) {
            log.info(path.toString());
            try {
                readMP3File(path.toString());
            }  catch (NotSupportedException e) {
                    e.printStackTrace();
            } catch (InvalidDataException e) {
                e.printStackTrace();
            } catch (UnsupportedTagException e) {
                e.printStackTrace();
            }
        }
        log.info("Finished processing MP3s");
    }

    class MyFileVisitor extends SimpleFileVisitor<Path> {

        @Override
        public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs)
                throws IOException {
            if (attrs.isDirectory()) {
                throw new IllegalStateException("WAT!? Visiting directory: " + file.toAbsolutePath().toString());
            }
            log.info("Visiting file: " + file.toAbsolutePath().toString());
            return super.visitFile(file, attrs);
        }
    }

    private void readMP3File(String fileName) throws InvalidDataException, IOException, UnsupportedTagException, NotSupportedException {
        Mp3File mp3file = new Mp3File(fileName);
        System.out.println("Length of this mp3 is: " + mp3file.getLengthInSeconds() + " seconds");
        System.out.println("Bitrate: " + mp3file.getBitrate() + " kbps " + (mp3file.isVbr() ? "(VBR)" : "(CBR)"));
        System.out.println("Sample rate: " + mp3file.getSampleRate() + " Hz");
        System.out.println("Has ID3v2 tag?: " + (mp3file.hasId3v2Tag() ? "YES" : "NO"));
        ID3v2 id3v2Tag = mp3file.getId3v2Tag();
        System.out.println("Track: " + id3v2Tag.getTrack());
        System.out.println("Artist: " + id3v2Tag.getArtist());
        System.out.println("Title: " + id3v2Tag.getTitle());
        System.out.println("Album: " + id3v2Tag.getAlbum());
        System.out.println("Year: " + id3v2Tag.getYear());
        System.out.println("Genre: " + id3v2Tag.getGenre() + " (" + id3v2Tag.getGenreDescription() + ")");
        System.out.println("Comment: " + id3v2Tag.getComment());
        id3v2Tag.setAlbum("Test Album");
        mp3file.setId3v2Tag(id3v2Tag);
        File originalFile = new File(fileName);
        File newFile = new File(fileName + ".bak");
        /*
        mp3file.save(newFile.getAbsolutePath());
        if (originalFile.delete()){
            newFile.renameTo(originalFile);
        }
        else {
            System.err.println("There was a problem deleting the file " + fileName);
        }*/
    }

    public static List<Path> fileList(String directory) {

        DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
            public boolean accept(Path file) throws IOException {
                String fileExt = FilenameUtils.getExtension(file.toString()).toLowerCase();
                System.out.println("ext = " + fileExt);
                return "mp3".equals(fileExt);
            }
        };

        List<Path> fileNames = new ArrayList<>();
        try (
                DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(directory), filter);
        ) {
            for (Path path : directoryStream) {
                fileNames.add(path);
            }
            directoryStream.close();
        } catch (IOException ex) {
        }
        Collections.sort(fileNames, new Comparator<Path>() {
            @Override
            public int compare(Path f1, Path f2) {
                return f1.toString().compareTo(f2.toString());
            }

        });
        return fileNames;
    }

}
