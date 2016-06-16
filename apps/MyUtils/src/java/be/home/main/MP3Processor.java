package be.home.main;

import be.home.common.archiving.Archiver;
import be.home.common.archiving.ZipArchiver;
import be.home.common.logging.Log4GE;
import be.home.common.main.BatchJobV2;
import be.home.common.utils.DateUtils;
import be.home.model.*;
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
        }
        catch (IOException ex) {}
        return fileNames;
    }

}
