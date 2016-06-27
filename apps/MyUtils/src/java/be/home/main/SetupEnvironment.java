package be.home.main;

import be.home.common.archiving.Archiver;
import be.home.common.archiving.ZipArchiver;
import be.home.common.constants.Constants;
import be.home.common.logging.Log4GE;
import be.home.common.main.BatchJobV2;
import be.home.common.utils.DateUtils;
import be.home.model.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.CodeSource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by ghyssee on 20/02/2015.
 */
public class SetupEnvironment extends BatchJobV2 {

    private static final String VERSION = "V1.0";

    public static ConfigTO.Config config;
    private static final Logger log = Logger.getLogger(SetupEnvironment.class);

    public static void main(String args[]) {

        String currentDir = System.getProperty("user.dir");
        log.info("Current Working dir: " + currentDir);

        SetupEnvironment instance = new SetupEnvironment();
        instance.printHeader("Setup Environment " + VERSION, "=");
        try {
            config = instance.init();
            instance.start();
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() throws IOException {
        checkDirectory(Constants.Path.MP3_BASEDIR);
        checkDirectory(Constants.Path.MP3_ALBUM);
        checkDirectory(Constants.Path.MP3_NEW);
        checkDirectory(Constants.Path.MP3_PREPROCESSOR);
        checkDirectory(Constants.Path.MP3_PROCESSOR);
        checkDirectory(Constants.Path.BASE_DIR);
        checkDirectory(Constants.Path.BASE_CONFIG_DIR);
        checkDirectory(Constants.Path.BASE_DATA_DIR);
        checkDirectory(Constants.Path.BASE_LOCAL_CONFIG_DIR);

    }

    private void checkDirectory(String directory) throws IOException {
        Path pathToFile = Paths.get(directory);
        if (!Files.exists(pathToFile)) {
            try {
                Files.createDirectories(pathToFile);
                log.info("Creating directory " + directory);
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }


    @Override
    public void run() {

    }
}
