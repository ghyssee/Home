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
import be.home.model.MP3Settings;
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

        MakeCustomPlaylists instance = new MakeCustomPlaylists();
        try {
            config = instance.init();
            SQLiteJDBC.initialize();
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

        Playlist[] playlists = (Playlist[]) JSONUtils.openJSONWithCode(Constants.JSON.PLAYLIST, Playlist[].class);
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

    private List<String> validateAlbums(List <Playlist.Album> albums){
        return null;
    }

    private void processPlayList(Playlist playlist) throws IOException {

        MP3Settings mp3Settings = (MP3Settings) JSONUtils.openJSONWithCode(Constants.JSON.MP3SETTINGS, MP3Settings.class);

        log.info("Processing PlayList " + playlist.name);
        List <MGOFileAlbumCompositeTO> albums = new ArrayList();
        int i=0;
        for (Playlist.Album album: playlist.albums){

            List <MGOFileAlbumCompositeTO> list = getMezzmoService().findAlbum(album.name, album.albumArtist);
            if (list == null || list.size() == 0){
                log.warn("ALBUM Not Found: " + album.name);
            }
            else if (list.size() > 1){
                if (!album.name.contains("%")) {
                    log.warn("Multiple ALBUMS With Same name found for Album: " + album.name);
                    for (MGOFileAlbumCompositeTO comp : list) {
                        log.warn("Album Artist: " + comp.getAlbumArtistTO().getName());
                    }
                }
            }
            else {
                MGOFileAlbumCompositeTO comp = new MGOFileAlbumCompositeTO();
                comp.getFileAlbumTO().setName(album.name);
                comp.getAlbumArtistTO().setName(album.albumArtist);
                albums.add(comp);
            }
            i++;
        }
        if (albums.size() > 0){

        }
        else {
            log.warn("No albums found ");
        }
        if (albums != null && albums.size() > 0) {
            List<MGOFileAlbumCompositeTO> list = getMezzmoService().getCustomPlayListSongs(albums, playlist.limit);
            Utils mezzmoUtils = new Utils();
            for (MGOFileAlbumCompositeTO comp : list) {
                comp.getFileTO().setFile(mezzmoUtils.relativizeFile(comp.getFileTO().getFile(), mp3Settings.mezzmo));

            }
            if (list.size() > 0) {
                PlayList pl = new PlayList();
                pl.make(list, mp3Settings.mezzmo.base + File.separator + mp3Settings.mezzmo.playlist.path, playlist.id + PlayList.EXTENSION);
            } else {
                log.warn("No songs found for playlist " + playlist.name);
            }
        }
        else {
            log.warn("No Albums found for playlist " + playlist.name);
        }
    }

    public static MezzmoServiceImpl getMezzmoService(){

        if (mezzmoService == null) {
            return MezzmoServiceImpl.getInstance();
        }
        return mezzmoService;
    }

}

