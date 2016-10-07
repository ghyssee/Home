package be.home.main.mezzmo;

import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.dao.jdbc.SQLiteJDBC;
import be.home.common.dao.jdbc.SQLiteUtils;
import be.home.common.logging.Log4GE;
import be.home.common.main.BatchJobV2;
import be.home.common.model.TransferObject;
import be.home.common.utils.DateUtils;
import be.home.common.utils.WinUtils;
import be.home.mezzmo.domain.model.MGOFileAlbumCompositeTO;
import be.home.mezzmo.domain.service.MezzmoServiceImpl;
import be.home.model.ConfigTO;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Created by ghyssee on 9/02/2016.
 */
public class ExportCatalogToHTML extends BatchJobV2{

    public static MezzmoServiceImpl mezzmoService = null;

    public static ConfigTO.Config config;
    private static final Logger log = Logger.getLogger(ExportCatalogToHTML.class);

    public static void main(String args[]) {

        ExportCatalogToHTML instance = new ExportCatalogToHTML();
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
        process();

    }

    public void process(){

        List<MGOFileAlbumCompositeTO> list = getMezzmoService().getAlbumTracks(new TransferObject());
        for (MGOFileAlbumCompositeTO comp : list){
            String firstChar = comp.getFileAlbumTO().getName().substring(0,1);
            /*
            List<MGOFileAlbumCompositeTO> songs = getMezzmoService().getSongsAlbum(new Long(comp.getFileAlbumTO().getId()));
            System.out.println("ALBUM:" + comp.getFileAlbumTO().getName());
            if (songs != null && songs.size() > 0) {
                for (MGOFileAlbumCompositeTO song : songs) {
                    System.out.println(song.getFileTO().getDisc() + "" + song.getFileTO().getTrack() + " " + song.getFileArtistTO().getArtist() + " - " + song.getFileTO().getTitle());
                }
            }
            else {
                System.err.println("Problem finding this album");
            }
            */
        }
        String outputFile = "c:/reports/a.html";
        try {
            export(list, outputFile);
        } catch (IOException e) {
            e.printStackTrace();
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
        context.put("list", list);
        context.put("esc", new EscapeTool());
        //Path file = Paths.get( outputFolder + File.separator + outputFile);
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
}

