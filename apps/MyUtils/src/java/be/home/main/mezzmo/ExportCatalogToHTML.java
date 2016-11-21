package be.home.main.mezzmo;

import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.dao.jdbc.SQLiteJDBC;
import be.home.common.main.BatchJobV2;
import be.home.common.model.TransferObject;
import be.home.common.utils.DateUtils;
import be.home.common.utils.JSONUtils;
import be.home.common.utils.VelocityUtils;
import be.home.mezzmo.domain.model.MGOFileAlbumCompositeTO;
import be.home.mezzmo.domain.service.MezzmoServiceImpl;
import be.home.model.ConfigTO;
import be.home.model.HTMLSettings;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.EscapeTool;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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

        process();

    }

    public void process(){
        HTMLSettings htmlSettings = (HTMLSettings) JSONUtils.openJSON(
                Setup.getInstance().getFullPath(Constants.JSON.HTML), HTMLSettings.class);
        processAlbums(htmlSettings);

    }

    public void processAlbums(HTMLSettings htmlSettings) {
        List<MGOFileAlbumCompositeTO> list = getMezzmoService().getAlbumTracks(new TransferObject());
        List<MGOFileAlbumCompositeTO> others = new ArrayList<MGOFileAlbumCompositeTO>();
        for (MGOFileAlbumCompositeTO comp : list){
            String firstChar = comp.getFileAlbumTO().getName().toUpperCase();
            boolean found = false;
            for (HTMLSettings.Group group : htmlSettings.export.groups){
                if (firstChar.substring(0, group.from.length()).compareTo(group.from) >= 0 && firstChar.substring(0,group.to.length()).compareTo(group.to) <= 0){
                    log.info(comp.getFileAlbumTO().getName());
                    log.info("Group found:" + group.from + "/" + group.to);
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
            group.setFilename(Setup.getPath(Constants.Path.WEB_MUSIC_ALBUMS) + File.separator + group.from + "_" + group.to + ".html");
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
        String filename = Setup.getFullPath(Constants.Path.WEB) + "/Music/MusicCatalog.html";
        try {
            exportAlbumIndex(htmlSettings.export.groups, filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void processAlbumSongs(MGOFileAlbumCompositeTO comp) throws IOException {
        List<MGOFileAlbumCompositeTO> songs = getMezzmoService().getSongsAlbum(new Long(comp.getFileAlbumTO().getId()),
                                                                               new Long(comp.getAlbumArtistTO().getId()));
        log.info("ALBUM:" + comp.getFileAlbumTO().getName());
        if (songs != null && songs.size() > 0) {
            String file = Setup.getFullPath(Constants.Path.WEB_MUSIC_SONGS) + File.separator + comp.getFilename();
            try {
                exportAlbumSongs(comp, songs, file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            log.error("Problem finding this album");
        }

    }

    public void exportAlbumIndex(List<HTMLSettings.Group> list, String outputFile) throws IOException {

        VelocityUtils vu = new VelocityUtils();

        /*  create a context and add data */
        VelocityContext context = new VelocityContext();
        context.put("esc",new EscapeTool());
        context.put("list", list);
        vu.makeFile("music/MusicCatalog.htm", outputFile, context);
        log.info("Album Catalog created: " + outputFile);
    }

    public void export(List<MGOFileAlbumCompositeTO> list, String outputFile) throws IOException {
        VelocityUtils vu = new VelocityUtils();
        outputFile = Setup.getFullPath(Constants.Path.WEB_MUSIC) + File.separator + outputFile;

        VelocityContext context = new VelocityContext();
        context.put("esc",new EscapeTool());
        context.put("list", list);
        vu.makeFile("music/Album.htm", outputFile, context);
        log.info("Album File created: " + outputFile);
    }

    public void exportAlbumSongs(MGOFileAlbumCompositeTO comp, List<MGOFileAlbumCompositeTO> list, String outputFile) throws Exception {
        VelocityUtils vu = new VelocityUtils();
        VelocityContext context = new VelocityContext();
        context.put("album", comp);
        context.put("list", list);
        context.put("date",new DateTool());
        context.put("esc", new EscapeTool());
        context.put("du", new DateUtils());
        context.put("su", new StringUtils());
        vu.makeFile("music/Song.htm", outputFile, context);
        log.info("Song File created: " + outputFile);
    }

    public static MezzmoServiceImpl getMezzmoService(){

        if (mezzmoService == null) {
            return MezzmoServiceImpl.getInstance();
        }
        return mezzmoService;
    }
}

