package be.home.mezzmo.domain.service;

import be.home.common.model.TransferObject;
import be.home.mezzmo.domain.bo.MezzmoBO;
import be.home.mezzmo.domain.bo.PlaylistBO;
import be.home.mezzmo.domain.model.MGOFileAlbumCompositeTO;
import be.home.mezzmo.domain.model.MGOFileTO;
import be.home.mezzmo.domain.model.MGOPlaylistTO;
import be.home.mezzmo.domain.model.PlaylistSetup;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by ghyssee on 13/04/2016.
 */
public class MezzmoServiceImpl {

    private static MezzmoServiceImpl instance = null;

    protected MezzmoServiceImpl(){
    }


    public int updatePlayCount(String fileID, String album, int playCount, java.util.Date dateLastPlayed) throws SQLException {
        MezzmoBO bo = new MezzmoBO();
        return bo.updatePlayCount(fileID, album, playCount, dateLastPlayed);
    }

    public int synchronizePlayCount(Long fileID, int playCount) throws SQLException {
        MezzmoBO bo = new MezzmoBO();
        return bo.synchronizePlayCount(fileID, playCount);
    }

    public List<MGOFileTO> findMP3Files(MGOFileAlbumCompositeTO compSearchTO){
        MezzmoBO bo = new MezzmoBO();
        return bo.findMP3Files(compSearchTO);

    }

    public List<MGOFileAlbumCompositeTO> getMP3FilesWithPlayCount(TransferObject to){
        MezzmoBO bo = new MezzmoBO();
        return bo.getMP3FilesWithPlayCount(to);
    }

    public List<MGOFileAlbumCompositeTO> getAlbums(TransferObject to){
        MezzmoBO bo = new MezzmoBO();
        return bo.getAlbums(to);
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

    public MGOFileTO findCoverArt(int albumId){
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

    public int cleanUpPlaylistSQL (Integer playlistId) {
        PlaylistBO bo = new PlaylistBO();
        return bo.cleanUpPlaylist(playlistId);
    }

    public List<String> validateAndInsertCondition(PlaylistSetup.Condition condition, Integer playlistID){
        PlaylistBO bo = new PlaylistBO();
        return bo.validateAndInsertCondition(condition, playlistID);
    }

    public MGOFileTO findByTitleAndAlbum(MGOFileAlbumCompositeTO comp){
        MezzmoBO bo = new MezzmoBO();
        return bo.findByTitleAndAlbum(comp);
    }

    public List<MGOFileAlbumCompositeTO> getSongsAlbum(Long albumId, Long albumArtistId) {
        MezzmoBO bo = new MezzmoBO();
        return bo.getSongsAlbum(albumId, albumArtistId);
    }

    public List<MGOFileAlbumCompositeTO> getLastPlayed() {
        MezzmoBO bo = new MezzmoBO();
        return bo.getLastPlayed();
    }

    public synchronized static MezzmoServiceImpl getInstance() {
        if(instance == null) {
            instance = new MezzmoServiceImpl();
        }
        return instance;
    }

    }
