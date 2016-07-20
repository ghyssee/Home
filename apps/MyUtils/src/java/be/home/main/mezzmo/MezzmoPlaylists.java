package be.home.main.mezzmo;

import be.home.common.constants.Constants;
import be.home.common.dao.jdbc.SQLiteJDBC;
import be.home.common.logging.Log4GE;
import be.home.common.main.BatchJobV2;
import be.home.common.utils.JSONUtils;
import be.home.mezzmo.domain.model.*;
import be.home.mezzmo.domain.service.MediaMonkeyServiceImpl;
import be.home.mezzmo.domain.service.MezzmoServiceImpl;
import be.home.model.ConfigTO;
import be.home.common.configuration.Setup;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;

/**
 * Created by ghyssee on 9/02/2016.
 */
public class MezzmoPlaylists extends BatchJobV2{

    public static MezzmoServiceImpl mezzmoService = null;
    public static MediaMonkeyServiceImpl mediaMonkeyService = null;

    public static Log4GE log4GE;
    public static ConfigTO.Config config;
    private static final Logger log = Logger.getLogger(ExportPlayCount.class);
    public static final String MP3_PLAYLIST = "H:/Shared/Mijn Muziek/Eric/playlist";

    public static void main(String args[]) {

        MezzmoPlaylists instance = new MezzmoPlaylists();
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

        PlaylistSetup playlistSetup = (PlaylistSetup) JSONUtils.openJSON(
                Setup.getInstance().getFullPath(Constants.Path.CONFIG) + File.separator + "PlaylistSetup.json", PlaylistSetup.class);

        String tst = "Is";
        Operand f = Operand.get(tst);
        System.out.println("Is: " + f);

        try {
            makePlaylists(playlistSetup);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void makePlaylists(PlaylistSetup config) throws IOException {
        for (PlaylistSetup.PlaylistRecord rec : config.records){
            checkPlaylist(rec);
        }
    }

    public MGOPlaylistTO findParent(String parent){
        MGOPlaylistTO playlist = new MGOPlaylistTO();
        playlist.setName(parent);
        // Parent can only be a Normal Playlist, Never Smart, External Or Active
        playlist.setType(new Integer(PlaylistType.NORMAL.getValue()));
        List<MGOPlaylistTO> playlists = getMezzmoService().findPlaylist(playlist);
        if (playlists != null && playlists.size() == 1){
            return playlists.get(0);
        }
        return null;
    }

    public MGOPlaylistTO fillPlaylist(String name, String type, int parentID){
        MGOPlaylistTO playlist = new MGOPlaylistTO();
        playlist.setName(name);
        playlist.setType(PlaylistType.get(type).getValue());
        playlist.setParentID(parentID);
        playlist.setThumbnailID(-1);
        playlist.setAuthor(5);
        playlist.setIcon(0);
        playlist.setPlaylistOrder(0);
        playlist.setThumbnailAuthor(0);
        playlist.setContentRatingID(0);
        playlist.setBackdropArtworkID(0);
        playlist.setOrderByColumn(7);
        playlist.setOrderByDirection(1);
        playlist.setDescription("");
        playlist.setDisplayTitleFormat("");
        playlist.setRunTime("0");
        playlist.setLimitBy(null);
        return playlist;
    }

    public void createPlaylist(PlaylistSetup.PlaylistRecord playlistRec){
        MGOPlaylistTO parentTO = findParent(playlistRec.parent);
        if (parentTO != null){
            log.info("Parent Found: " + parentTO.getID() + " / " + parentTO.getName());
            // insert the playlist
            int nr = getMezzmoService().insertPlaylist(fillPlaylist(playlistRec.name, playlistRec.type, parentTO.getID()));
            log.info("Nr Of Records created: " + nr);
        }
        else {
            log.error("Parent Not Found in DB: " + playlistRec.parent + " for playlist " + playlistRec.name);
        }
    }


    public void checkPlaylist(PlaylistSetup.PlaylistRecord playlistRec) throws IOException {
        log.info("Processing playlist: " + playlistRec.name);
        MGOPlaylistTO playlist = new MGOPlaylistTO();
        playlist.setName(playlistRec.name);
        PlaylistType type = PlaylistType.get(playlistRec.type);
        if (type == null){
            log.error("Invalid Type: " + playlistRec.type);
        }
        else if (!type.equals(PlaylistType.NORMAL)){
            log.error("Playlist type not supported: " + playlistRec.type);
        }
        else {
            playlist.setType(type.getValue());
            List<MGOPlaylistTO> playlists = getMezzmoService().findPlaylist(playlist);
            if (playlists.size() == 0) {
                log.info("Playlist Not Found: " + playlist.getName());
                // creating the playlist
                // lookup parent
                createPlaylist(playlistRec);
            } else if (playlists.size() == 1) {
                log.info("Playlist Found: " + playlist.getName());
                String parentName = playlistRec.parent;
                String parentDBName = playlists.get(0).getParentName();
                if (parentName.equals(parentDBName)) {
                    log.info("Parent Found: " + parentDBName);
                } else {
                    log.error("Parent is not correct: " + "Parent DB: " + parentDBName +
                            " / Parent Config: " + parentName);
                }

            } else {
                log.error("Multiple Playlists Found: " + playlist.getName());
            }

            System.out.println(playlists.size());
        }
    }


    public static MezzmoServiceImpl getMezzmoService(){

        if (mezzmoService == null) {
            return MezzmoServiceImpl.getInstance();
        }
        return mezzmoService;
    }

}

