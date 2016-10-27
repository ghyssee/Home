package be.home.main.mezzmo;

import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.dao.jdbc.SQLiteJDBC;
import be.home.common.main.BatchJobV2;
import be.home.common.utils.DateUtils;
import be.home.common.utils.JSONUtils;
import be.home.common.utils.VelocityUtils;
import be.home.mezzmo.domain.model.MGOFileAlbumCompositeTO;
import be.home.mezzmo.domain.service.MezzmoServiceImpl;
import be.home.model.ConfigTO;
import be.home.model.MP3Settings;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.EscapeTool;

import java.io.*;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by ghyssee on 9/02/2016.
 */
public class LastPlayedSongs extends BatchJobV2{

    public static MezzmoServiceImpl mezzmoService = null;

    public static ConfigTO.Config config;
    private static final Logger log = Logger.getLogger(LastPlayedSongs.class);
    private static final String MP3_SETTINGS = Setup.getInstance().getFullPath(Constants.Path.CONFIG) + File.separator +
            "MP3Settings.json";

    public static void main(String args[]) throws IOException {

        LastPlayedSongs instance = new LastPlayedSongs();
        String tmpFile = Setup.getInstance().getFullPath(Constants.Path.TMP_JAVA) + File.separator +
                         instance.getClass().getSimpleName() + ".pid";
        File file = new File(tmpFile);
        if (file.exists()) {
            //String pid= FileUtils.readFileToString(file);
            long ms = new Date().getTime();
            long fms = file.lastModified();
            long diffSeconds = (ms-fms) / 1000;
            log.info("BathJob Running... Last Runtime: " + diffSeconds + " seconds ago");
            if (diffSeconds > 300){
                // more than 5 minutes ago, so it shouldn't be running anymore
                file.delete();
            }
            else {
                log.error("BathJob Already Running...");
                return;
            }
        }
        FileUtils.writeStringToFile(file, getPID());
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                log.info("doing some cleanup");
                cleanUp(file);
            }
        }));
        try {
            config = instance.init();
            SQLiteJDBC.initialize(workingDir);
            log.info("Batch started: " + log.getName());
            do {
                long sleep = instance.start();
                //long sleep = mp3Settings.lastPlayedSleep*1000;
                sleep = sleep - 5;
                sleep = Math.max(sleep, 3);
                log.info("Sleepig for " + sleep + " seconds");
                try {
                    Thread.sleep(sleep*1000);
                    FileUtils.touch(file);
                } catch (InterruptedException e) {
                    cleanUp(file);
                }
            }
            while (true && file.exists());
            cleanUp(file);
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        log.info("Batch ended: " + log.getName());

    }

    private static void cleanUp(File file){
        if (file.exists()){
            file.delete();
        }
    }

    public long start() {
        return process();
    }

    public void run(){

    }

    public long process() {
        MP3Settings mp3Settings = (MP3Settings) JSONUtils.openJSON(MP3_SETTINGS, MP3Settings.class, "UTF-8");
        log.info("Number of last played songs to show: " + mp3Settings.lastPlayedSong.number);
        List<MGOFileAlbumCompositeTO> list = getMezzmoService().getLastPlayed(mp3Settings.lastPlayedSong.number);
        String base = Setup.getFullPath(Constants.Path.WEB_MUSIC) + File.separator;
        String filename =  base + "LastPlayedSongs.html";
        int rec = 1;
        long refresh = 60L;
        for (MGOFileAlbumCompositeTO comp : list){
            if (rec == 1) {
                comp.setCurrentlyPlaying(isCurrentlyPlaying(comp));
                refresh = getRefreshTime(comp);
                log.info("Refresh: " + getRefreshTime(comp));
                try {
                    exportLastPlayed(mp3Settings, comp, base + "LastPlayed.html", "LastPlayed.vm", refresh);
                    exportLastPlayed(mp3Settings, comp, base + "LastPlayedScroll.html", "LastPlayedScroll.vm", refresh);
                } catch (IOException e) {
                    log.error(e);
                }
                rec++;
            }
        }
        try {
            export(list, filename, refresh);
        } catch (IOException e) {
            log.error(e);
        }
        return refresh;
    }

    private void setRefreshTime(VelocityContext context, long refresh){
        context.put("refresh", refresh);
    }

    public void exportLastPlayed(MP3Settings mp3Settings, MGOFileAlbumCompositeTO comp, String outputFile, String template, long refresh) throws IOException {
        VelocityUtils vu = new VelocityUtils();

        VelocityContext context = new VelocityContext();
        context.put("lastPlayedSong", mp3Settings.getLastPlayedSong());
        context.put("date",new DateTool());
        context.put("esc",new EscapeTool());
        context.put("du",new DateUtils());
        context.put("song", comp);
        setRefreshTime(context, refresh);
        vu.makeFile(template, outputFile, context);
        log.info("LastPlayed created: " + outputFile);
    }


    public void export(List<MGOFileAlbumCompositeTO> list, String outputFile, long refresh) throws IOException {

        VelocityUtils vu = new VelocityUtils();

        VelocityContext context = new VelocityContext();
        context.put("date",new DateTool());
        context.put("esc",new EscapeTool());
        context.put("du",new DateUtils());
        context.put("list", list);
        setRefreshTime(context, refresh);
        vu.makeFile("LastPlayedSongs.vm", outputFile, context);
        log.info("LastPlayed created: " + outputFile);
    }

    public static MezzmoServiceImpl getMezzmoService(){

        if (mezzmoService == null) {
            return MezzmoServiceImpl.getInstance();
        }
        return mezzmoService;
    }

    private boolean isCurrentlyPlaying(MGOFileAlbumCompositeTO song){
        Date lastPlayed = song.getFileTO().getDateLastPlayed();
        if (lastPlayed == null){
            return false;
        }
        Date currDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(lastPlayed);
        cal.add(Calendar.SECOND, song.getFileTO().getDuration());
        lastPlayed = cal.getTime();
        return lastPlayed.after(currDate);
    }

    private long getRefreshTime(MGOFileAlbumCompositeTO song){
        Date lastPlayed = song.getFileTO().getDateLastPlayed();
        long seconds = 90L;
        if (lastPlayed != null) {
            Date currDate = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(lastPlayed);
            cal.add(Calendar.SECOND, song.getFileTO().getDuration());
            lastPlayed = cal.getTime();
            long secBetween = DateUtils.getSecondsBetween(lastPlayed, currDate);
            if (song.isCurrentlyPlaying()) {
                seconds = secBetween / 2;
                seconds = Math.max(seconds, 5L);
                seconds = Math.min(seconds, 60L);
            }
            else if (secBetween >= -60){
                // played a song in the last minute
                seconds = 5L;
            }
            else if (secBetween >= -600){
                // played a song in the last 10 minutes
                seconds = 15L;
            }
            else {
                // lats played song is more than 10 minutes ago
                seconds = 60L;
            }
        }
        return seconds;
    }



    public static String getPID() {
        String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
        if (processName != null && processName.length() > 0) {
            try {
                return processName.split("@")[0];
            }
            catch (Exception e) {
                return "";
            }
        }

        return "";
    }


}

