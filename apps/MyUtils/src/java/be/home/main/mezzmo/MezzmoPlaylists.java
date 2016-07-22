package be.home.main.mezzmo;

import be.home.common.constants.Constants;
import be.home.common.dao.jdbc.SQLiteJDBC;
import be.home.common.logging.Log4GE;
import be.home.common.main.BatchJobV2;
import be.home.common.utils.JSONUtils;
import be.home.mezzmo.domain.bo.PlaylistBO;
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
    private static final Logger log = Logger.getLogger(MezzmoPlaylists.class);
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
            validateAndInsertPlaylist(rec);
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

    public MGOPlaylistTO fillPlaylist(List <String> errors, PlaylistSetup.PlaylistRecord pr, int parentID){
        PlaylistBO playlistBO = new PlaylistBO();
        PlaylistType playlistType = PlaylistType.get(pr.type);
        MGOPlaylistTO playlist = new MGOPlaylistTO();
        playlist.setName(pr.name);
        playlist.setType(playlistType.getValue());
        playlist.setParentID(parentID);
        playlist.setThumbnailID(-1);
        playlist.setAuthor(5);
        playlist.setIcon(0);
        playlist.setPlaylistOrder(0);
        playlist.setThumbnailAuthor(0);
        playlist.setContentRatingID(0);
        playlist.setOrderByColumn(playlistBO.validateOrderByColumn(errors, pr.orderByColumn));
        playlist.setOrderByDirection(playlistBO.validateSorting(errors, pr.sorting));
        playlist.setDescription("");
        playlist.setDisplayTitleFormat("");
        playlist.setRunTime("0");

        switch (playlistType){
            case NORMAL:
                playlist.setLimitBy(null);
                playlist.setBackdropArtworkID(0);
                break;
            case SMART:
                playlist.setDynamicTreeToken("");
                playlist.setLimitBy(pr.limitBy);
                // default limit time is not implemented, so always items
                playlist.setLimitType(LimitType.Items.getValue());
                playlist.setMediaType(playlistBO.validateMediaType(errors, pr.mediaType));
                playlist.setCombineAnd(playlistBO.validateCombineAnd(errors, pr.combineAnd));
                playlist.setBackdropArtworkID(-1);
                break;
        }
        return playlist;
    }

    public void createPlaylist(PlaylistSetup.PlaylistRecord playlistRec, List <String> errors){
        MGOPlaylistTO parentTO = findParent(playlistRec.parent);
        if (parentTO != null) {
            log.info("Parent Found: " + parentTO.getID() + " / " + parentTO.getName());
            // insert the playlist
            MGOPlaylistTO playlistTO = fillPlaylist(errors, playlistRec, parentTO.getID());
            if (errors.size() == 0) {
                int nr = getMezzmoService().insertPlaylist(playlistTO);
                log.info("Nr Of Records created: " + nr);
            }
        }
        else {
            String error = "Parent Not Found in DB: " + playlistRec.parent + " for playlist " + playlistRec.name;
            log.error(error);
            errors.add(error);
        }
    }


    public void validateAndInsertPlaylist(PlaylistSetup.PlaylistRecord playlistRec) throws IOException {
        log.info("Processing playlist: " + playlistRec.name);
        MGOPlaylistTO playlist = new MGOPlaylistTO();
        playlist.setName(playlistRec.name);
        PlaylistType type = PlaylistType.get(playlistRec.type);
        if (type == null){
            log.error("Invalid Type: " + playlistRec.type);
        }
        else {
            playlist.setType(type.getValue());
            List <String> errors = new ArrayList<String>();
            MGOPlaylistTO playlistTO = null;
            switch (type){
                case NORMAL:
                    playlistTO = checkPlaylist(playlist, playlistRec, errors);
                    if (playlistTO == null && errors.size() == 0){
                        createPlaylist(playlistRec, errors);
                    }
                    break;
                case SMART:
                    playlistTO = checkPlaylist(playlist, playlistRec, errors);
                    if (playlistTO == null && errors.size() == 0){
                        createPlaylist(playlistRec, errors);
                        // look it up again to get the playlist ID
                        if (errors.size() == 0) {
                            playlistTO = checkPlaylist(playlist, playlistRec, errors);
                        }
                        if (errors.size() == 0) {

                            for (PlaylistSetup.Condition c : playlistRec.conditions) {
                                errors = getMezzmoService().validateAndInsertCondition(c, playlistTO.getID());
                                if (errors.size() > 0) {
                                    log.info("Errors Found");
                                    for (String message : errors) {
                                        log.info("ERROR Found: " + message);
                                    }
                                }
                            }
                        }
                    }
                    break;
                default:
                    log.error("Playlist type not supported: " + playlistRec.type);
            }
        }
    }

    private MGOPlaylistTO checkPlaylist(MGOPlaylistTO playlist, PlaylistSetup.PlaylistRecord playlistRec, List <String> errors){
        boolean insert = false;
        MGOPlaylistTO playlistTO = null;
        List<MGOPlaylistTO> playlists = getMezzmoService().findPlaylist(playlist);
        if (playlists.size() == 0) {
            log.info("Playlist Not Found: " + playlist.getName());
            // creating the playlist
            // lookup parent
            //createPlaylist(playlistRec);
            insert = true;
        } else if (playlists.size() == 1) {
            log.info("Playlist Found: " + playlist.getName());
            playlistTO = playlists.get(0);
            String parentName = playlistRec.parent;
            String parentDBName = playlistTO.getParentName();
            if (parentName.equals(parentDBName)) {
                log.info("Parent Found: " + parentDBName);
            } else {
                log.error("Parent is not correct: " + "Parent DB: " + parentDBName +
                        " / Parent Config: " + parentName);
            }

        } else {
            errors.add("Multiple Playlists Found: " + playlist.getName());
            log.error("Multiple Playlists Found: " + playlist.getName());
        }
        return playlistTO;
    }


    public static MezzmoServiceImpl getMezzmoService(){

        if (mezzmoService == null) {
            return MezzmoServiceImpl.getInstance();
        }
        return mezzmoService;
    }

}

