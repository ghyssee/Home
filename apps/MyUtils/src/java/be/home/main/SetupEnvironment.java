package be.home.main;

import be.home.common.utils.FileUtils;
import be.home.model.ConfigTO;
import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.main.BatchJobV2;
import org.apache.logging.log4j.Logger;

import java.io.*;

/**
 * Created by ghyssee on 20/02/2015.
 */
public class SetupEnvironment extends BatchJobV2 {

    private static final String VERSION = "V1.0";

    public static ConfigTO.Config config;
    private static Logger log =  getMainLog(SetupEnvironment.class);

    public static void main(String args[]) {

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
        // this directory will normally automatically be created by Log4J
        FileUtils.checkDirectory(setup.getFullPath(Constants.Path.LOG));
        FileUtils.checkDirectory(setup.getFullPath(Constants.Path.WEB_MUSIC_ALBUMS));
        FileUtils.checkDirectory(setup.getFullPath(Constants.Path.WEB_MUSIC_SONGS));
        FileUtils.checkDirectory(setup.getFullPath(Constants.Path.TMP_JAVA));

    }


    @Override
    public void run() {

    }
}
