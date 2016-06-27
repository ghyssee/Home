package be.home.main.mezzmo;

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
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by ghyssee on 9/02/2016.
 */
public class MakeTop20 extends BatchJobV2{

    public static MezzmoServiceImpl mezzmoService = null;

    public static Log4GE log4GE;
    public static ConfigTO.Config config;
    private static final Logger log = Logger.getLogger(ExportPlayCount.class);
    public static final String MP3_PLAYLIST = "H:/Shared/Mijn Muziek/Eric/playlist";

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
            makeTop20(base, "MezzmoDB.PlayCount.V12.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void makeTop20(String base, String fileName) throws IOException {
        List <MGOFileAlbumCompositeTO> list = getMezzmoService().getTop20();
        for (MGOFileAlbumCompositeTO comp : list){
            // get relative path
            Path pathAbsolute = Paths.get(comp.getFileTO().getFile());
            Path pathBase = Paths.get(config.mezzmo.base + File.separator + config.mezzmo.playlist.path);
            Path pathRelative = pathBase.relativize(pathAbsolute);
            comp.getFileTO().setFile(pathRelative.toString());
        }
        if (list.size() > 0){
            writePlaylist(list);
        }
        else {
            log.warn("No MP3 files found for the playlist");
        }

    }

    private void writePlaylist(List <MGOFileAlbumCompositeTO> list) throws IOException {
        Properties p = new Properties();
        p.setProperty("file.resource.loader.path", Constants.Path.VELOCITY_DIR);

        Path outputFolder = Paths.get(config.mezzmo.base + File.separator + config.mezzmo.playlist.path);
        if (Files.notExists(outputFolder)){
            outputFolder = Paths.get(Constants.Path.BASE_DATA_DIR_PLAYLIST);
        }
        log.info("Playlist folder: " + outputFolder.toString());

        VelocityEngine ve = new VelocityEngine();
        ve.init(p);
        /*  next, get the Template  */
        Template t = ve.getTemplate( "Top20.vm" );
        /*  create a context and add data */
        VelocityContext context = new VelocityContext();
        context.put("list", list);
        Path file = Paths.get( outputFolder + File.separator + config.mezzmo.playlist.top20);
        BufferedWriter writer = null;
        try {
            writer = Files.newBufferedWriter(file, Charset.defaultCharset());
            t.merge(context, writer);
        }
        finally {
            if (writer != null){
                    writer.flush();
                    writer.close();
                    log.info("Playlist created: " + file.toString());
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

