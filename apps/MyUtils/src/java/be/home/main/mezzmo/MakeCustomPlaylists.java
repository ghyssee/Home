package be.home.main.mezzmo;

import be.home.common.configuration.Setup;
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
import be.home.model.Playlist;
import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by Gebruiker on 10/07/2016.
 */
public class MakeCustomPlaylists extends BatchJobV2{

    public static MezzmoServiceImpl mezzmoService = null;

    public static Log4GE log4GE;
    public static ConfigTO.Config config;
    private static final Logger log = Logger.getLogger(MakeCustomPlaylists.class);
    public static final String MP3_PLAYLIST = "H:/Shared/Mijn Muziek/Eric/playlist";

    public static void main(String args[]) {

        MakeCustomPlaylists instance = new MakeCustomPlaylists();
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

        final String batchJob = "Make Custom Playlists";

        Playlist[] playlists = (Playlist[]) JSONUtils.openJSON(
                Setup.getInstance().getFullPath(Constants.Path.CONFIG) + File.separator + "Playlists.json", Playlist[].class);
        try {
            makeCustom(playlists);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void makeCustom(Playlist[] playlists) throws IOException {


        for (int i=0; i < playlists.length; i++){
            processPlayList(playlists[i]);
        }
        List <MGOFileAlbumCompositeTO> list = getMezzmoService().getTop20();
        //for (MGOFileAlbumCompositeTO comp : list){
            // get relative path
            //Path pathAbsolute = Paths.get(comp.getFileTO().getFile());
            //Path pathBase = Paths.get(config.mezzmo.base + File.separator + config.mezzmo.playlist.path);
            //Path pathRelative = pathBase.relativize(pathAbsolute);
            //comp.getFileTO().setFile(pathRelative.toString());
        //}
        //MGOFileTO fileTO = getMezzmoService().findByFile("H:\\Shared\\Mijn Muziek\\Eric\\Albums\\100 Nr 1 Hits Volume 1 (2002)\\104 Liquid Ft. Silvy - Turn The Tide.mp3");

    }

    private void processPlayList(Playlist playlist){
        log.info("Processing Playlist " + playlist.name);
        List <String> albums = new ArrayList();
        int i=0;
        for (Playlist.Album album: playlist.albums){
            albums.add(album.name);
            i++;
        }
        if (albums.size() > 0){

        }
        else {
            log.warn("No albums found ");
        }
        log.info("albums " + albums.toString());
    }

    private void writePlaylist(List <MGOFileAlbumCompositeTO> list, String outputFile) throws IOException {
        Properties p = new Properties();
        p.setProperty("file.resource.loader.path", Setup.getInstance().getFullPath(Constants.Path.VELOCITY));

        String filename = config.mezzmo.base + File.separator + config.mezzmo.playlist.path;
        Path outputFolder = Paths.get(filename);
        if (Files.notExists(outputFolder)){
            outputFolder = Paths.get(Setup.getInstance().getFullPath(Constants.Path.PLAYLIST));
        }
        log.info("Playlist folder: " + outputFolder.toString());

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

