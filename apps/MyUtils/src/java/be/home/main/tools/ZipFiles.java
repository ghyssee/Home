package be.home.main.tools;

import be.home.common.archiving.Archiver;
import be.home.common.archiving.ZipArchiver;
import be.home.model.ConfigTO;
import be.home.model.ParamTO;
import be.home.common.logging.Log4GE;
import be.home.common.main.BatchJobV2;
import be.home.common.utils.DateUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

/**
 * Created by ghyssee on 20/02/2015.
 */
public class ZipFiles extends BatchJobV2 {

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

        ZipFiles instance = new ZipFiles();
        instance.printHeader("ZipFiles " + VERSION, "=");
        Map <String,String> params = instance.validateParams(args, PARAMS);
        try {
            config = instance.init();
            instance.start(instance.getParam(PARAMS[0].getId(), params), instance.getParam(PARAMS[1].getId(), params));
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

    }

    public void start(String source, String zipFile) throws IOException {

        String configFile = config.movies.importFile;

        String ext = FilenameUtils.getExtension(zipFile);
        zipFile = FilenameUtils.removeExtension(zipFile);

        zipFile = zipFile + "." + DateUtils.formatDate(new Date(), DateUtils.YYYYMMDDHHMMSS) + "." + ext;

        //zip();
        List <Path> listOfFiles = fileList(source);
        for (Path path : listOfFiles) {
            log.info(path.toString());
        }
        //Files.walkFileTree(Paths.get("/temp"), new MyFileVisitor());
       //Archiver zipArchiver = new ZipArchiver("/temp/3/A Cinderella Story (2004).NFO", "/temp/1/ziptest.zip");
        Archiver zipArchiver = new ZipArchiver(source, zipFile);
        //Archiver zipArchiver = new ZipArchiver("C:/My Test", "c:/My Backups/zipFile.zip");
        zipArchiver.run();
        log.info("Finished zipping files");

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
        List<Path> fileNames = new ArrayList<>();
        try (
                DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(directory))
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
