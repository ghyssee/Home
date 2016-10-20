package be.home.main.mezzmo;

import be.home.common.constants.Constants;
import be.home.common.dao.jdbc.SQLiteJDBC;
import be.home.common.logging.Log4GE;
import be.home.common.main.BatchJobV2;
import be.home.common.utils.JSONUtils;
import be.home.common.utils.WinUtils;
import be.home.mezzmo.domain.model.MGOFileAlbumCompositeTO;
import be.home.mezzmo.domain.model.MGOFileTO;
import be.home.mezzmo.domain.service.MediaMonkeyServiceImpl;
import be.home.mezzmo.domain.service.MezzmoServiceImpl;
import be.home.model.ConfigTO;
import be.home.common.configuration.Setup;
import be.home.model.MP3Settings;
import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.*;
import java.nio.charset.Charset;
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
    private static final Logger log = Logger.getLogger(ExportPlayCount.class);
    public static final String MP3_PLAYLIST = "H:/Shared/Mijn Muziek/Eric/playlist";
    private static final String MP3_SETTINGS = Setup.getInstance().getFullPath(Constants.Path.CONFIG) + File.separator +
            "MP3Settings.json";

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

        try {
            makeTop20();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void makeTop20() throws IOException {


        MP3Settings mp3Settings = (MP3Settings) JSONUtils.openJSON(MP3_SETTINGS, MP3Settings.class, "UTF-8");


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
            System.out.println("pathBase: " + pathBase.toString());
            System.out.println("pathAbsolute: " + pathAbsolute.toString());
            Path iPodBase = Paths.get(DRIVE + ":" + File.separator + mp3Settings.mediaMonkey.base);
            Path mezzmoBase = Paths.get(mp3Settings.mezzmo.base + File.separator + "Eric");
            System.out.println("iPodBase: " + iPodBase.toString());
            System.out.println("MezzmoBase: " + mezzmoBase.toString());
            String file = pathAbsolute.toString().replace(iPodBase.toString(), mezzmoBase.toString());
            System.out.println("Replace: " + file);
            MGOFileTO fileTO = getMezzmoService().findByFile(file);
            if (fileTO != null && fileTO.getId() > 0){
                System.out.println("FOUND: " + fileTO.getId());
            }
            else {
                System.out.println("NOT FOUND: " + file);
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
        Properties p = new Properties();
        p.setProperty("file.resource.loader.path", Setup.getInstance().getFullPath(Constants.Path.VELOCITY));

        String filename = mp3Settings.mezzmo.base + File.separator + mp3Settings.mezzmo.playlist.path;
        Path outputFolder = Paths.get(filename);
        if (Files.notExists(outputFolder)){
            outputFolder = Paths.get(Setup.getInstance().getFullPath(Constants.Path.PLAYLIST));
        }
        log.info("PlayList folder: " + outputFolder.toString());

        VelocityEngine ve = new VelocityEngine();
        ve.init(p);
        /*  next, get the Template  */
        Template t = ve.getTemplate( "Top20.vm" );
        /*  create a context and add data */
        VelocityContext context = new VelocityContext();
        context.put("list", list);
        Path file = Paths.get( outputFolder + File.separator + outputFile);
        BufferedWriter writer = null;
        try {
            writer = Files.newBufferedWriter(file, Charset.defaultCharset());
            t.merge(context, writer);
        }
        finally {
            if (writer != null){
                    writer.flush();
                    writer.close();
                    log.info("PlayList created: " + file.toString());
            }
        }
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

