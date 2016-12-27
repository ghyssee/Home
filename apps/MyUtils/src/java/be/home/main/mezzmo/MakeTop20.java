package be.home.main.mezzmo;

import be.home.common.constants.Constants;
import be.home.common.dao.jdbc.SQLiteJDBC;
import be.home.common.logging.Log4GE;
import be.home.common.main.BatchJobV2;
import be.home.common.utils.JSONUtils;
import be.home.common.utils.LogUtils;
import be.home.common.utils.VelocityUtils;
import be.home.common.utils.WinUtils;
import be.home.domain.model.MezzmoUtils;
import be.home.main.tools.ZipFiles;
import be.home.mezzmo.domain.dao.jdbc.MezzmoDAOQueries;
import be.home.mezzmo.domain.model.MGOFileAlbumCompositeTO;
import be.home.mezzmo.domain.model.MGOFileTO;
import be.home.mezzmo.domain.service.MediaMonkeyServiceImpl;
import be.home.mezzmo.domain.service.MezzmoServiceImpl;
import be.home.model.ConfigTO;
import be.home.common.configuration.Setup;
import be.home.model.MP3Settings;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by ghyssee on 9/02/2016.
 */
public class MakeTop20 extends BatchJobV2{

    public static MezzmoServiceImpl mezzmoService = null;
    public static MediaMonkeyServiceImpl mediaMonkeyService = null;

    public static Log4GE log4GE;
    public static ConfigTO.Config config;
    private static final Logger log = getMainLog(MakeTop20.class);

    public static void main(String args[]) {

        MakeTop20 instance = new MakeTop20();
        try {
            config = instance.init();
            SQLiteJDBC.initialize(workingDir);
            instance.run();
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {

        final String batchJob = "Export PlayCount";

        String base = WinUtils.getOneDrivePath();
        log.info("OneDrive Path: " + base);
        base += "\\Muziek\\Export\\";
        //System.out.println(MezzmoDAOQueries.LIST_ALBUMS_TRACKS);

/*
        try {
            makeTop20();
        } catch (IOException e) {
            LogUtils.logError(log, e);
        }
*/

    }

    public void makeTop20() throws IOException {


        MP3Settings mp3Settings = (MP3Settings) JSONUtils.openJSONWithCode(Constants.JSON.MP3SETTINGS, MP3Settings.class);
        String base = MezzmoUtils.getMezzmoBase(mp3Settings.mezzmo);

        /* based on Mezzmo DB
        List <MGOFileAlbumCompositeTO> list = getMezzmoService().getTop20();
        for (MGOFileAlbumCompositeTO comp : list){
            // get relative path
            Path pathAbsolute = Paths.get(comp.getFileTO().getFile());
            Path pathBase = Paths.get(config.mezzmo.base + File.separator + config.mezzmo.playlist.path);
            Path pathRelative = pathBase.relativize(pathAbsolute);
            comp.getFileTO().setFile(pathRelative.toString());
        }
        if (list.size() > 0){
            writePlaylist(list, config.mezzmo.playlist.top20);
        }
        else {
            log.warn("No MP3 files found for the playlist");
        }*/

        //MGOFileTO fileTO = getMezzmoService().findByFile("H:\\Shared\\Mijn Muziek\\Eric\\Albums\\100 Nr 1 Hits Volume 1 (2002)\\104 Liquid Ft. Silvy - Turn The Tide.mp3");

        // based on MediaMonkey DB
        List <MGOFileAlbumCompositeTO> list2 = getMediaMonkeyService().getTop20();
        for (MGOFileAlbumCompositeTO comp : list2){
            // get relative path
            String DRIVE = "I";
            Path pathAbsolute = Paths.get(DRIVE + comp.getFileTO().getFile());
            Path pathBase = Paths.get(DRIVE + ":" + File.separator + mp3Settings.mediaMonkey.base + File.separator + mp3Settings.mediaMonkey.playlist.path);
            log.info("pathBase: " + pathBase.toString());
            log.info("pathAbsolute: " + pathAbsolute.toString());
            Path iPodBase = Paths.get(DRIVE + ":" + File.separator + mp3Settings.mediaMonkey.base);
            Path mezzmoBase = Paths.get(base + File.separator + "Eric");
            log.info("iPodBase: " + iPodBase.toString());
            log.info("MezzmoBase: " + mezzmoBase.toString());
            String file = pathAbsolute.toString().replace(iPodBase.toString(), mezzmoBase.toString());
            System.out.println("Replace: " + file);
            MGOFileTO fileTO = getMezzmoService().findByFile(file);
            if (fileTO != null && fileTO.getId() > 0){
                log.info("FOUND: " + fileTO.getId());
            }
            else {
                log.info("NOT FOUND: " + file);
            }

            Path pathRelative = pathBase.relativize(pathAbsolute);
            comp.getFileTO().setFile(pathRelative.toString());
            int duration = (comp.getFileTO().getDuration())/1000;
            comp.getFileTO().setDuration(duration);
        }
        if (list2.size() > 0){
            writePlaylist(mp3Settings, list2, mp3Settings.mezzmo.playlist.top20);
        }
        else {
            log.warn("No MP3 files found for the playlist");
        }

    }

    private void writePlaylist(MP3Settings mp3Settings, List <MGOFileAlbumCompositeTO> list, String outputFile) throws IOException {
        VelocityUtils vu = new VelocityUtils();

        String filename = mp3Settings.mezzmo.base + File.separator + mp3Settings.mezzmo.playlist.path;
        Path outputFolder = Paths.get(filename);
        if (Files.notExists(outputFolder)){
            outputFolder = Paths.get(Setup.getInstance().getFullPath(Constants.Path.PLAYLIST));
        }
        log.info("PlayList folder: " + outputFolder.toString());

        VelocityContext context = new VelocityContext();
        context.put("list", list);

        outputFile = outputFolder + File.separator + outputFile;

        vu.makeFile("Top20.vm", outputFile, context);
        log.info("PlayList created: " + outputFile);
    }

    public static MezzmoServiceImpl getMezzmoService(){

        if (mezzmoService == null) {
            return MezzmoServiceImpl.getInstance();
        }
        return mezzmoService;
    }

    public static MediaMonkeyServiceImpl getMediaMonkeyService(){

        if (mediaMonkeyService == null) {
            return MediaMonkeyServiceImpl.getInstance();
        }
        return mediaMonkeyService;
    }
}

