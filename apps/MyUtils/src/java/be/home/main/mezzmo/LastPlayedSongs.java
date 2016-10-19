package be.home.main.mezzmo;

import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.dao.jdbc.SQLiteJDBC;
import be.home.common.main.BatchJobV2;
import be.home.common.utils.DateUtils;
import be.home.common.utils.JSONUtils;
import be.home.mezzmo.domain.model.MGOFileAlbumCompositeTO;
import be.home.mezzmo.domain.service.MezzmoServiceImpl;
import be.home.model.ConfigTO;
import be.home.model.MP3Settings;
import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.EscapeTool;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Created by ghyssee on 9/02/2016.
 */
public class LastPlayedSongs extends BatchJobV2{

    public static MezzmoServiceImpl mezzmoService = null;

    public static ConfigTO.Config config;
    private static final Logger log = Logger.getLogger(LastPlayedSongs.class);

    public static void main(String args[]) throws InterruptedException {

        LastPlayedSongs instance = new LastPlayedSongs();
        final String MP3_SETTINGS = Setup.getInstance().getFullPath(Constants.Path.CONFIG) + File.separator +
                "MP3Settings.json";
        MP3Settings mp3Settings = (MP3Settings) JSONUtils.openJSON(MP3_SETTINGS, MP3Settings.class, "UTF-8");

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
                Thread.sleep(sleep*1000);
            }
            while (true);
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("Batch ended: " + log.getName());

    }

    public long start() {
        return process();


    }

    public void run(){

    }

    public long process() {
        List<MGOFileAlbumCompositeTO> list = getMezzmoService().getLastPlayed();
        String base = "c:/reports/Music/";
        String filename =  base + "LastPlayedSongs.html";
        int rec = 1;
        long refresh = 60L;
        for (MGOFileAlbumCompositeTO comp : list){
            if (rec == 1) {
                //long ms = (new Date().getTime()) - comp.getFileTO().getDuration()*1000 - 11*60*1000;
                //Date dd = new Date(ms);
                //comp.getFileTO().setDateLastPlayed(dd);
                comp.setCurrentlyPlaying(isCurrentlyPlaying(comp));
                refresh = getRefreshTime(comp);
                log.info("Refresh: " + getRefreshTime(comp));
                try {
                    exportLastPlayed(comp, base + "LastPlayed.html", "LastPlayed.vm", refresh);
                    exportLastPlayed(comp, base + "LastPlayedV2.html", "LastPlayedV2.vm", refresh);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                rec++;
            }
        }
        try {
            export(list, filename, refresh);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return refresh;
    }

    private void setRefreshTime(VelocityContext context, long refresh){
        context.put("refresh", refresh);
    }

    public void exportLastPlayed(MGOFileAlbumCompositeTO comp, String outputFile, String template, long refresh) throws IOException {
        Properties p = new Properties();
        p.setProperty("file.resource.loader.path", Setup.getInstance().getFullPath(Constants.Path.VELOCITY));

        VelocityEngine ve = new VelocityEngine();
        ve.init(p);
        /*  next, get the Template  */
        Template t = ve.getTemplate( template );
        /*  create a context and add data */
        VelocityContext context = new VelocityContext();
        context.put("date",new DateTool());
        context.put("esc",new EscapeTool());
        context.put("du",new DateUtils());
        context.put("song", comp);
        setRefreshTime(context, refresh);
        Path file = Paths.get(outputFile);
        BufferedWriter writer = null;
        try {
            writer = Files.newBufferedWriter(file, Charset.defaultCharset());
            t.merge(context, writer);
        } finally {
            if (writer != null){
                writer.flush();
                writer.close();
                log.info("LastPlayed created: " + file.toString());
            }
        }
    }


    public void export(List<MGOFileAlbumCompositeTO> list, String outputFile, long refresh) throws IOException {
        Properties p = new Properties();
        p.setProperty("file.resource.loader.path", Setup.getInstance().getFullPath(Constants.Path.VELOCITY));

        VelocityEngine ve = new VelocityEngine();
        ve.init(p);
        /*  next, get the Template  */
        Template t = ve.getTemplate( "LastPlayedSongs.vm" );
        /*  create a context and add data */
        VelocityContext context = new VelocityContext();
        context.put("date",new DateTool());
        context.put("esc",new EscapeTool());
        context.put("du",new DateUtils());
        context.put("list", list);
        setRefreshTime(context, refresh);
        Path file = Paths.get(outputFile);
        BufferedWriter writer = null;
        try {
            writer = Files.newBufferedWriter(file, Charset.defaultCharset());
            t.merge(context, writer);
        } finally {
            if (writer != null){
                writer.flush();
                writer.close();
                log.info("LastPlayed created: " + file.toString());
            }
        }
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
                // lats played song is more than 10 minutes aggo
                seconds = 60L;
            }
        }
        return seconds;
    }

}

