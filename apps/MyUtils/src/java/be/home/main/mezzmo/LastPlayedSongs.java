package be.home.main.mezzmo;

import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.dao.jdbc.SQLiteJDBC;
import be.home.common.dao.jdbc.SQLiteUtils;
import be.home.common.logging.Log4GE;
import be.home.common.main.BatchJobV2;
import be.home.common.model.TransferObject;
import be.home.common.utils.DateUtils;
import be.home.common.utils.JSONUtils;
import be.home.common.utils.WinUtils;
import be.home.mezzmo.domain.model.MGOFileAlbumCompositeTO;
import be.home.mezzmo.domain.service.MezzmoServiceImpl;
import be.home.model.ConfigTO;
import be.home.model.HTMLSettings;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.EscapeTool;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Created by ghyssee on 9/02/2016.
 */
public class LastPlayedSongs extends BatchJobV2{

    public static MezzmoServiceImpl mezzmoService = null;

    public static ConfigTO.Config config;
    private static final Logger log = Logger.getLogger(ExportCatalogToHTML.class);

    public static void main(String args[]) {

        LastPlayedSongs instance = new LastPlayedSongs();
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
        final String batchJob = "Export Catalog";

        String base = WinUtils.getOneDrivePath();
        log.info("OneDrive Path: " + base);
        //System.out.println(ConvertSecondToHHMMSSString(250));
        process();


    }

    public void process(){
        processAlbums();

    }

    public void processAlbums() {
        HTMLSettings htmlSettings = (HTMLSettings) JSONUtils.openJSON(
                Setup.getInstance().getFullPath(Constants.Path.CONFIG) + File.separator + "HTML.json", HTMLSettings.class);
        List<MGOFileAlbumCompositeTO> list = getMezzmoService().getAlbumTracks(new TransferObject());
        List<MGOFileAlbumCompositeTO> others = new ArrayList<MGOFileAlbumCompositeTO>();
        for (MGOFileAlbumCompositeTO comp : list){
            String firstChar = comp.getFileAlbumTO().getName().toUpperCase();
            boolean found = false;
            for (HTMLSettings.Group group : htmlSettings.export.groups){
                if (firstChar.substring(0, group.from.length()).compareTo(group.from) >= 0 && firstChar.substring(0,group.to.length()).compareTo(group.to) <= 0){
                    System.out.println(comp.getFileAlbumTO().getName());
                    System.out.println("Group found:" + group.from + "/" + group.to);
                    if (group.list == null){
                        group.list = new ArrayList<MGOFileAlbumCompositeTO>();
                    }
                    group.list.add(comp);
                    found = true;
                    break;
                }
            }
            if (!found){
                others.add(comp);
            }
        }
        int idx = 1;
        for (HTMLSettings.Group group : htmlSettings.export.groups){
            log.info("Processing group " + group.from + "-" + group.to);
            group.setFilename("Albums/" + group.from + "_" + group.to + ".html");
            try {
                for (MGOFileAlbumCompositeTO comp : group.list){
                    comp.setFilename("s" + idx + ".html");
                    processAlbumSongs(comp);
                    idx++;
                }
                export(group.list, group.getFilename());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String filename = "c:/reports/Music/index.html";
        try {
            exportIndex(htmlSettings.export.groups, filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void processAlbumSongs(MGOFileAlbumCompositeTO comp) throws IOException {
        List<MGOFileAlbumCompositeTO> songs = getMezzmoService().getSongsAlbum(new Long(comp.getFileAlbumTO().getId()),
                new Long(comp.getAlbumArtistTO().getId()));
        System.out.println("ALBUM:" + comp.getFileAlbumTO().getName());
        if (songs != null && songs.size() > 0) {
            String file = "c:/reports/Music/Songs/" + comp.getFilename();
            try {
                exportAlbumSongs(comp, songs, file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            System.err.println("Problem finding this album");
        }

    }

    public void exportIndex(List<HTMLSettings.Group> list, String outputFile) throws IOException {
        for (HTMLSettings.Group group : list){
            System.out.println(group.from);
        }

        Properties p = new Properties();
        p.setProperty("file.resource.loader.path", Setup.getInstance().getFullPath(Constants.Path.VELOCITY));

        VelocityEngine ve = new VelocityEngine();
        ve.init(p);
        /*  next, get the Template  */
        Template t = ve.getTemplate( "music/Index.htm" );
        /*  create a context and add data */
        VelocityContext context = new VelocityContext();
        context.put("esc",new EscapeTool());
        context.put("list", list);
        Path file = Paths.get(outputFile);
        BufferedWriter writer = null;
        try {
            writer = Files.newBufferedWriter(file, Charset.defaultCharset());
            t.merge(context, writer);
        } finally {
            if (writer != null){
                writer.flush();
                writer.close();
                log.info("Index created: " + file.toString());
            }
        }
    }

    public void export(List<MGOFileAlbumCompositeTO> list, String outputFile) throws IOException {
        Properties p = new Properties();
        p.setProperty("file.resource.loader.path", Setup.getInstance().getFullPath(Constants.Path.VELOCITY));

        VelocityEngine ve = new VelocityEngine();
        ve.init(p);
        /*  next, get the Template  */
        Template t = ve.getTemplate( "music/Album.htm" );
        /*  create a context and add data */
        VelocityContext context = new VelocityContext();
        context.put("esc",new EscapeTool());
        context.put("list", list);
        Path file = Paths.get("c:/reports/Music/" + outputFile);
        BufferedWriter writer = null;
        try {
            writer = Files.newBufferedWriter(file, Charset.defaultCharset());
            t.merge(context, writer);
        } finally {
            if (writer != null){
                writer.flush();
                writer.close();
                log.info("PlayList created: " + file.toString());
            }
        }
    }

    public void exportAlbumSongs(MGOFileAlbumCompositeTO comp, List<MGOFileAlbumCompositeTO> list, String outputFile) throws Exception {
        Properties p = new Properties();
        p.setProperty("file.resource.loader.path", Setup.getInstance().getFullPath(Constants.Path.VELOCITY));

        VelocityEngine ve = new VelocityEngine();
        ve.init(p);
        /*  next, get the Template  */
        Template t = ve.getTemplate( "music/Song.htm" );
        /*  create a context and add data */
        VelocityContext context = new VelocityContext();

        context.put("album", comp);
        context.put("list", list);
        context.put("esc", new EscapeTool());
        context.put("du", new DateUtils());
        Path file = Paths.get(outputFile);
        BufferedWriter writer = null;
        try {
            writer = Files.newBufferedWriter(file, Charset.defaultCharset());
            t.merge(context, writer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null){
                writer.flush();
                writer.close();
            }
        }
    }

    public static MezzmoServiceImpl getMezzmoService(){

        if (mezzmoService == null) {
            return MezzmoServiceImpl.getInstance();
        }
        return mezzmoService;
    }
}

