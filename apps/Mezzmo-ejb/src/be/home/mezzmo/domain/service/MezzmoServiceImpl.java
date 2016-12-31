package be.home.mezzmo.domain.service;

import be.home.common.enums.MP3Tag;
import be.home.common.model.TransferObject;
import be.home.common.utils.LogUtils;
import be.home.mezzmo.domain.bo.MezzmoBO;
import be.home.mezzmo.domain.bo.PlaylistBO;
import be.home.mezzmo.domain.dao.definition.TablesEnum;
import be.home.mezzmo.domain.model.*;
import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by ghyssee on 13/04/2016.
 */
public class MezzmoServiceImpl {

    private static MezzmoServiceImpl instance = null;
    private static final Logger log = Logger.getLogger(MezzmoServiceImpl.class);

    protected MezzmoServiceImpl(){
    }


    public int updatePlayCount(String fileTitle, String album, int playCount, java.util.Date dateLastPlayed) throws SQLException {
        MezzmoBO bo = new MezzmoBO();
        return bo.updatePlayCount(fileTitle, album, playCount, dateLastPlayed);
    }

    public int updateSong (MGOFileAlbumCompositeTO comp, MP3Tag type) throws SQLException {
        MezzmoBO bo = new MezzmoBO();
        return bo.updateSong(comp, type);
    }

    public List<MGOFileTO> findMP3Files(MGOFileAlbumCompositeTO compSearchTO){
        MezzmoBO bo = new MezzmoBO();
        return bo.findMP3Files(compSearchTO);

    }

    public List<MGOFileAlbumCompositeTO> getMP3FilesWithPlayCount(TransferObject to){
        MezzmoBO bo = new MezzmoBO();
        return bo.getMP3FilesWithPlayCount(to);
    }

    public List<MGOFileAlbumCompositeTO> getAlbums(MGOFileAlbumTO albumTO, TransferObject to){
        MezzmoBO bo = new MezzmoBO();
        return bo.getAlbums(albumTO, to);
    }

    public List<MGOFileAlbumCompositeTO> getAlbumTracks(TransferObject to){
        MezzmoBO bo = new MezzmoBO();
        return bo.getAlbumTracks(to);
    }

    public List<MGOFileAlbumCompositeTO> getTop20(){
        MezzmoBO bo = new MezzmoBO();
        return bo.getTop20();
    }

    public List<MGOFileAlbumCompositeTO> getCustomPlayListSongs(List <MGOFileAlbumCompositeTO> albums, int limit){
        MezzmoBO bo = new MezzmoBO();
        return bo.getCustomPlayListSongs(albums, limit);
    }



    public List<MGOFileTO> getFiles(MGOFileAlbumCompositeTO compSearchTO){
        MezzmoBO bo = new MezzmoBO();
        return bo.getFiles(compSearchTO);
    }

    public MGOFileTO findByFile(String file){
        MezzmoBO bo = new MezzmoBO();
        return bo.findByFile(file);
    }

    public MGOFileTO findCoverArt(Long albumId){
        MezzmoBO bo = new MezzmoBO();
        return bo.findCoverArt(albumId);
    }


    public List<MGOFileAlbumCompositeTO> findAlbum(String album, String albumArtist){
        MezzmoBO bo = new MezzmoBO();
        return bo.findAlbum(album, albumArtist);
    }

    public List<MGOPlaylistTO> findPlaylist (MGOPlaylistTO search) {
        MezzmoBO bo = new MezzmoBO();
        return bo.findPlaylist(search);

    }

    public int insertPlaylist (MGOPlaylistTO playlist) {
        MezzmoBO bo = new MezzmoBO();
        return bo.insertPlaylist(playlist);
    }

    public int updateRanking (Long fileID, int ranking) throws SQLException {
        MezzmoBO bo = new MezzmoBO();
        return bo.updateRanking(fileID, ranking);
    }

    public MGOPlaylistTO cleanUpPlaylistWithChildren (String playlistName) {
        PlaylistBO bo = new PlaylistBO();
        System.out.println(TablesEnum.MGOPlaylist.tableAlias());
        return bo.cleanUpPlaylistWithChildren(playlistName);
        //return null;
    }

    public List<String> validateAndInsertCondition(PlaylistSetup.Condition condition, Integer playlistID){
        PlaylistBO bo = new PlaylistBO();
        return bo.validateAndInsertCondition(condition, playlistID);
    }

    public MGOFileTO findByTitleAndAlbum(MGOFileAlbumCompositeTO comp){
        MezzmoBO bo = new MezzmoBO();
        return bo.findByTitleAndAlbum(comp);
    }

    public List <MGOFileAlbumCompositeTO> findSongsByAlbum(MGOFileAlbumCompositeTO comp){
        MezzmoBO bo = new MezzmoBO();
        return bo.findSongsByAlbum(comp);
    }

    public List<MGOFileAlbumCompositeTO> getSongsAlbum(Long albumId, Long albumArtistId) {
        MezzmoBO bo = new MezzmoBO();
        return bo.getSongsAlbum(albumId, albumArtistId);
    }

    public List<MGOFileAlbumCompositeTO> getLastPlayed(int number) {
        MezzmoBO bo = new MezzmoBO();
        return bo.getLastPlayed(number);
    }

    public MGOFileArtistTO findArtist(MGOFileArtistTO artist){
        MezzmoBO bo = new MezzmoBO();
        return bo.findArtist(artist);
    }

    public int updateArtist (MGOFileAlbumCompositeTO comp) throws SQLException {
        MezzmoBO bo = new MezzmoBO();
        int nr = 0;
        MGOFileArtistTO artist = null;
        try {
            artist = bo.findArtistById(comp.getFileArtistTO());
            if (artist.getArtist().toUpperCase().equals(comp.getFileArtistTO().getArtist().toUpperCase())){
                // artist is case insensitive equal, example Jojo == JoJo
                // just update the artist and it is ok
                log.info("Artist is case insensitive equal: " + comp.getFileArtistTO().getArtist());
                nr = bo.updateArtist(comp.getFileArtistTO());
            }
            else {
                try {
                    artist = bo.findArtist(comp.getFileArtistTO());
                    // artist is different, and exist
                    // update artist to be sure it is case sensitive correct
                    // check if there are other songs linked to the old artist, if not, delete it
                    log.info("Artist is different, but exist already: " + comp.getFileArtistTO().getArtist());
                    artist.setArtist(comp.getFileArtistTO().getArtist());
                    nr = bo.updateArtist(artist);
                    MGOFileArtistTO oldArtist = comp.getFileArtistTO();
                    comp.setFileArtistTO(artist);
                    nr = bo.updateLinkFileArtist2(comp);
                    checkArtistLinked(oldArtist);
                }
                catch (EmptyResultDataAccessException e){
                    // artist is different, and does not exist already, example Jojo Ft. Dodo <==> Jojo Feat. Dodo
                    // insert new artist and link it to the file
                    // maybe check if there are other songs linked to the old artist, if not, delete it
                    Integer artistId = bo.insertArtist(comp.getFileArtistTO());
                    if (artistId != null){
                        log.info("New Artist created: " + comp.getFileArtistTO().getArtist() + "/Id: " + artistId);
                        MGOFileArtistTO oldArtist = new MGOFileArtistTO();
                        oldArtist.setID(comp.getFileArtistTO().getID());
                        comp.getFileArtistTO().setID(new Long(artistId));
                        nr = bo.updateLinkFileArtist2(comp);
                        checkArtistLinked(oldArtist);
                    }
                    else {
                        // if insert failed nr will be empty and tag info of the song will not be updated
                        log.error("Insert Artist failed: " + comp.getFileArtistTO().getArtist());
                    }
                }
            }
        }
        catch (EmptyResultDataAccessException e){
            // artist not found By Id
            // this should never occur
            log.error("Find Artist By Id failed: " + comp.getFileArtistTO().getArtist() + " Id: " + comp.getFileArtistTO().getID());
            nr = 0;
        }
        return nr;
    }

    private void checkArtistLinked(MGOFileArtistTO artist) {

        MezzmoBO bo = new MezzmoBO();
            List<MGOFileAlbumCompositeTO> list = bo.findLinkedArtist(artist);
            if (list == null || list.size() == 0) {
                // nothing linked to it, safe to delete the link + the artist
                int del = bo.deleteArtist(artist);
                log.info("Nr Of Artists Deleted: " + del + " /Id: " + artist.getID());
            }
    }


    public Result updateLinkFileArtist(MGOFileArtistTO artist, Long newId){
        MezzmoBO bo = new MezzmoBO();
        return bo.updateLinkFileArtist(artist, newId);
    }

    public Map<String, MGOFileAlbumCompositeTO> getMaxDisc() {
        MezzmoBO bo = new MezzmoBO();
        return bo.getMaxDisc();
    }

    public static String constructFileTitle( Map<String, MGOFileAlbumCompositeTO> map, MGOFileAlbumCompositeTO comp){
        MezzmoBO bo = new MezzmoBO();
        return bo.constructFileTitle(map, comp);

    }

    public synchronized static MezzmoServiceImpl getInstance() {
        if(instance == null) {
            instance = new MezzmoServiceImpl();
        }
        return instance;
    }

    }
