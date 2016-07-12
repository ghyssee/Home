package be.home.main;

import be.home.common.exceptions.ApplicationException;
import be.home.common.utils.FileUtils;
import be.home.model.ConfigTO;
import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.main.BatchJobV2;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.*;

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
        Setup setup = Setup.getInstance();
        FileUtils.checkDirectory(setup.getFullPath(Constants.Path.BASEDIR));
        FileUtils.checkDirectory(setup.getFullPath(Constants.Path.ALBUM));
        FileUtils.checkDirectory(setup.getFullPath(Constants.Path.NEW));
        FileUtils.checkDirectory(setup.getFullPath(Constants.Path.PREPROCESS));
        FileUtils.checkDirectory(setup.getFullPath(Constants.Path.PROCESS));
        FileUtils.checkDirectory(setup.getFullPath(Constants.Path.ONEDRIVE));
        FileUtils.checkDirectory(setup.getFullPath(Constants.Path.CONFIG));
        FileUtils.checkDirectory(setup.getFullPath(Constants.Path.DATA));
        FileUtils.checkDirectory(setup.getFullPath(Constants.Path.PLAYLIST));
        FileUtils.checkDirectory(setup.getFullPath(Constants.Path.LOCAL_CONFIG));
        FileUtils.checkDirectory(setup.getFullPath(Constants.Path.VELOCITY));
        FileUtils.checkDirectory(setup.getFullPath(Constants.Path.RESOURCES));

    }


    @Override
    public void run() {

    }
}