package be.home.main.mezzmo;

import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.dao.jdbc.SQLiteJDBC;
import be.home.common.main.BatchJobV2;
import be.home.common.utils.DateUtils;
import be.home.common.utils.WinUtils;
import be.home.mezzmo.domain.model.MGOFileAlbumCompositeTO;
import be.home.mezzmo.domain.service.MezzmoServiceImpl;
import be.home.model.ConfigTO;
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

    public void process() {
        List<MGOFileAlbumCompositeTO> list = getMezzmoService().getLastPlayed();
        String filename = "c:/reports/Music/LastPlayed.html";
        for (MGOFileAlbumCompositeTO comp : list){
            System.out.println(isCurrentlyPlaying(comp));
        }
        try {
            export(list, filename);
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
        Template t = ve.getTemplate( "LastPlayed.vm" );
        /*  create a context and add data */
        VelocityContext context = new VelocityContext();
        context.put("active", false);
        context.put("date",new DateTool());
        context.put("esc",new EscapeTool());
        context.put("du",new DateUtils());
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
        System.out.println(cal.getTime());
        System.out.println(lastPlayed.after(currDate));
        Date tst = new Date(1475514259*1000);
        System.out.println("tst = " + tst);
        return false;
    }

}

