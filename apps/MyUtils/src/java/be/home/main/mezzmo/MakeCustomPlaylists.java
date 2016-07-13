package be.home.main.mezzmo;

import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.dao.jdbc.SQLiteJDBC;
import be.home.common.logging.Log4GE;
import be.home.common.main.BatchJobV2;
import be.home.common.mp3.PlayList;
import be.home.common.utils.JSONUtils;
import be.home.mezzmo.domain.model.MGOFileAlbumCompositeTO;
import be.home.mezzmo.domain.service.MezzmoServiceImpl;
import be.home.mezzmo.domain.util.Utils;
import be.home.model.ConfigTO;
import be.home.model.Playlist;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gebruiker on 10/07/2016.
 */
public class MakeCustomPlaylists extends BatchJobV2{

    public static MezzmoServiceImpl mezzmoService = null;

    public static Log4GE log4GE;
    public static ConfigTO.Config config;
    private static final Logger log = Logger.getLogger(MakeCustomPlaylists.class);

    public static void main(String args[]) {

        System.setProperty("logfile.name", Setup.getInstance().getFullPath(Constants.Path.LOG) + File.separator + "MyUtlis.log");
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
        //for (MGOFileAlbumCompositeTO comp : list){
            // get relative path
            //Path pathAbsolute = Paths.get(comp.getFileTO().getFile());
            //Path pathBase = Paths.get(config.mezzmo.base + File.separator + config.mezzmo.playlist.path);
            //Path pathRelative = pathBase.relativize(pathAbsolute);
            //comp.getFileTO().setFile(pathRelative.toString());
        //}
        //MGOFileTO fileTO = getMezzmoService().findByFile("H:\\Shared\\Mijn Muziek\\Eric\\Albums\\100 Nr 1 Hits Volume 1 (2002)\\104 Liquid Ft. Silvy - Turn The Tide.mp3");

    }

    private void processPlayList(Playlist playlist) throws IOException {
        log.info("Processing PlayList " + playlist.name);
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
        List<MGOFileAlbumCompositeTO> list = getMezzmoService().getCustomPlayListSongs(albums, playlist.limit);
        Utils mezzmoUtils = new Utils();
        for (MGOFileAlbumCompositeTO comp : list){
            System.out.println(comp.getFileTO().getFile());
            System.out.println(comp.getFileAlbumTO().getName());
            comp.getFileTO().setFile(mezzmoUtils.relativizeFile(comp.getFileTO().getFile(), config.mezzmo));

        }
        if (list.size() > 0){
            PlayList pl = new PlayList();
            pl.make(list, config.mezzmo.base + File.separator + config.mezzmo.playlist.path, playlist.id + PlayList.EXTENSION);
        }
        else {
            log.warn("No songs found for playlist " + playlist.name);
        }
    }

    public static MezzmoServiceImpl getMezzmoService(){

        if (mezzmoService == null) {
            return MezzmoServiceImpl.getInstance();
        }
        return mezzmoService;
    }

}

