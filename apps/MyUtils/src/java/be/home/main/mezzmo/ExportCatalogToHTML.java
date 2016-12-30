package be.home.main.mezzmo;

import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.dao.jdbc.SQLiteJDBC;
import be.home.common.main.BatchJobV2;
import be.home.common.model.TransferObject;
import be.home.common.utils.DateUtils;
import be.home.common.utils.JSONUtils;
import be.home.common.utils.LogUtils;
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
import java.util.*;

/**
 * Created by ghyssee on 9/02/2016.
 */
public class ExportCatalogToHTML extends BatchJobV2{

    public static MezzmoServiceImpl mezzmoService = null;

    public static ConfigTO.Config config;
    private static final Logger log = getMainLog(ExportCatalogToHTML.class);

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
        HTMLSettings htmlSettings = (HTMLSettings) JSONUtils.openJSONWithCode(Constants.JSON.HTML, HTMLSettings.class);
        processAlbums(htmlSettings);

    }

    private boolean checkIfAlbumInGroup(String album, HTMLSettings.Group group) {
        int fromLength = Math.min(group.from.length(), album.length());
        int toLength = Math.min(group.to.length(), album.length());

        if (group.names != null){
            for (HTMLSettings.Name name : group.names) {
                if (album.startsWith(name.name)){
                    return true;
                }
            }
        }
        else {
            if (album.substring(0, fromLength).compareTo(group.from) >= 0 &&
                album.substring(0, toLength).compareTo(group.to) <= 0) {

                boolean exceptionFound = false;
                if (group.exceptions != null) {
                    for (HTMLSettings.Exception ex : group.exceptions) {
                        if (album.startsWith(ex.name)) {
                            exceptionFound = true;
                            break;
                        }

                    }
                }
                if (!exceptionFound) {
                    return true;
                }
            }

            /*
            if (StringUtils.isBlank(group.exception) || !album.startsWith(group.exception)) {
                return true;
            }*/
        }
        return false;
    }

    public void processAlbums(HTMLSettings htmlSettings) {
        List<MGOFileAlbumCompositeTO> list = getMezzmoService().getAlbumTracks(new TransferObject());
        List<MGOFileAlbumCompositeTO> others = new ArrayList<MGOFileAlbumCompositeTO>();
        /* sort array
         */
        List <HTMLSettings.Group> list2 = htmlSettings.export.groups;
        // sort the list on priority and from
        Collections.sort(list2, new Comparator<HTMLSettings.Group>() {
            public int compare(HTMLSettings.Group o1, HTMLSettings.Group o2) {
                int c = o2.priority - o1.priority;
                if (c == 0){
                    c = o1.getFrom().compareTo(o2.getFrom());
                }
                return c;
            }
        });

        for (MGOFileAlbumCompositeTO comp : list){
            boolean found = false;
            for (HTMLSettings.Group group : htmlSettings.export.groups){
                if (checkIfAlbumInGroup(comp.getFileAlbumTO().getName(), group)){
                        log.info(comp.getFileAlbumTO().getName());
                        log.info("Group found:" + group.from + "/" + group.to);
                        if (group.list == null) {
                            group.list = new ArrayList<MGOFileAlbumCompositeTO>();
                        }
                        group.list.add(comp);
                        found = true;
                        break;
                }
            }
            if (!found){
               others.add(comp);
               log.info("No group found for album: " + comp.getFileAlbumTO().getName());
            }
        }
        int idx = 1;
        for (HTMLSettings.Group group : htmlSettings.export.groups){
            log.info("Processing group " + group.from + "-" + group.to);
            group.setFilename(Setup.getPath(Constants.Path.WEB_MUSIC_ALBUMS) + File.separator + group.from + "_" + group.to + ".html");
            try {
                if (group.list != null) {
                    for (MGOFileAlbumCompositeTO comp : group.list) {
                        comp.setFilename("s" + idx + ".html");
                        processAlbumSongs(comp);
                        idx++;
                    }
                    export(group.list, group.getFilename());
                }
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
                LogUtils.logError(log, e);
            }
        }
        else {
            log.error("Problem finding this album");
        }

    }

    public void exportAlbumIndex(List<HTMLSettings.Group> list, String outputFile) throws IOException {

        // sort the list on from
        Collections.sort(list, new Comparator<HTMLSettings.Group>() {
            public int compare(HTMLSettings.Group o1, HTMLSettings.Group o2) {
                return o1.getFrom().compareTo(o2.getFrom());
            }
        });

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

