package be.home.mezzmo.domain.bo;

import be.home.common.model.BusinessObject;
import be.home.common.model.TransferObject;
import be.home.mezzmo.domain.dao.jdbc.MezzmoAlbumDAOImpl;
import be.home.mezzmo.domain.dao.jdbc.MezzmoDAOImpl;
import be.home.mezzmo.domain.dao.jdbc.MezzmoPlaylistDAOImpl;
import be.home.mezzmo.domain.model.*;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ghyssee on 13/04/2016.
 */
public class MezzmoBO extends BusinessObject {

    public MezzmoBO (){
    }


    public int updatePlayCount(String fileID, String album, int playCount, java.util.Date dateLastPlayed) throws SQLException {
        return getMezzmoDAO().updatePlayCount(fileID, album, playCount, dateLastPlayed);
    }

    public int synchronizePlayCount(Long fileID, int playCount, Date lastPlayed) throws SQLException {
        return getMezzmoDAO().synchronizePlayCount(fileID, playCount, lastPlayed);
    }
    public List<MGOFileTO> findMP3Files(MGOFileAlbumCompositeTO compSearchTO){
        List<MGOFileTO> list = getMezzmoDAO().getFiles(compSearchTO);
        return list;

    }

    public List<MGOFileTO> getFiles(MGOFileAlbumCompositeTO compSearchTO){
        return getMezzmoDAO().getFiles(compSearchTO);
    }

    public List<MGOFileAlbumCompositeTO> getMP3FilesWithPlayCount(TransferObject to){
        return getMezzmoDAO().getMP3FilesWithPlayCount(to);
    }

    public List<MGOFileAlbumCompositeTO> getAlbums(MGOFileAlbumTO albumTO, TransferObject to){
        return getMezzmoDAO().getAlbums(albumTO, to);
    }

    public List<MGOFileAlbumCompositeTO> getAlbumTracks(TransferObject to){
        return getMezzmoDAO().getAlbumTracks(to);
    }

    public List<MGOFileAlbumCompositeTO> getTop20(){
        return getMezzmoDAO().getTop20();
    }

    public List<MGOFileAlbumCompositeTO> getCustomPlayListSongs(List <MGOFileAlbumCompositeTO> albums, int limit){
        return getMezzmoDAO().getCustomPlayListSongs(albums, limit);
    }
    public MGOFileTO findByFile(String file){
        return getMezzmoDAO().findByFile(file);
    }

    public MGOFileTO findCoverArt(int albumId){
        return getMezzmoDAO().findCoverArt(albumId);
    }

    public List<MGOFileAlbumCompositeTO> findAlbum (String album, String albumArtist) {
        return getMezzmoAlbumDAO().findAlbum(album, albumArtist);

    }

    public List<MGOPlaylistTO> findPlaylist (MGOPlaylistTO search) {
        return getMezzmoPlaylistDAO().findPlaylist(search);

    }

    public int insertPlaylist (MGOPlaylistTO playlist) {
        return getMezzmoPlaylistDAO().insertPlaylist(playlist);

    }

    public MGOFileTO findByTitleAndAlbum(MGOFileAlbumCompositeTO comp){
        return getMezzmoDAO().findByTitleAndAlbum(comp);
    }

    public MGOFileArtistTO findArtist(MGOFileArtistTO artist){
        return getMezzmoDAO().findArtist(artist);
    }

    public List <MGOFileAlbumCompositeTO> findSongsByAlbum(MGOFileAlbumCompositeTO comp){
        return getMezzmoDAO().findSongsByAlbum(comp);
    }

    public int updateRanking (Long fileID, int ranking) throws SQLException {
        return getMezzmoDAO().updateRanking(fileID, ranking);
    }

    public int updateSong (MGOFileAlbumCompositeTO comp, String type) throws SQLException {
        return getMezzmoDAO().updateSong(comp, type);
    }

    public List<MGOFileAlbumCompositeTO> getSongsAlbum(Long albumId, Long albumArtistId) {
        return getMezzmoDAO().getSongsAlbum(albumId, albumArtistId);
    }

    public List<MGOFileAlbumCompositeTO> getLastPlayed(int number) {
        return getMezzmoDAO().getLastPlayed(number);
    }

    public Result updateLinkFileArtist(MGOFileArtistTO artist, Long newId){
        return getMezzmoDAO().updateLinkFileArtist(artist, newId);
    }

        public Map<String, MGOFileAlbumCompositeTO> getMaxDisc() {

        List<MGOFileAlbumCompositeTO> list = getMezzmoDAO().getMaxDisc();
        Map<String, MGOFileAlbumCompositeTO> map = new HashMap<> ();
        for (MGOFileAlbumCompositeTO comp : list){
            map.put(comp.getFileAlbumTO().getName() + comp.getAlbumArtistTO().getName(), comp);
        }
        return map;
    }

    public static int findMaxDiscLength(Map<String, MGOFileAlbumCompositeTO> map, MGOFileAlbumCompositeTO comp) {
        String key = comp.getFileAlbumTO().getName() + comp.getAlbumArtistTO().getName();
        MGOFileAlbumCompositeTO foundComp = map.get(key);
        if (foundComp == null){
            // default track size
            return 2;
        }
        else {
            int length = String.valueOf(foundComp.getFileTO().getDisc()).length();
            return length;
        }
    }


    public String constructFileTitle( Map<String, MGOFileAlbumCompositeTO> map, MGOFileAlbumCompositeTO comp){
        String fileTitle = formatTrack(map, comp) + " " + comp.getFileArtistTO().getArtist() + " - " +
                comp.getFileTO().getTitle();
        return fileTitle;
    }

    private String formatTrack(Map<String, MGOFileAlbumCompositeTO> map, MGOFileAlbumCompositeTO comp){
        String track = StringUtils.leftPad(String.valueOf(comp.getFileTO().getTrack()), findMaxDiscLength(map, comp), '0');
        return track;
    }

    public MezzmoDAOImpl getMezzmoDAO(){
        return new MezzmoDAOImpl();
    }

    public MezzmoAlbumDAOImpl getMezzmoAlbumDAO(){
        return new MezzmoAlbumDAOImpl();
    }

    public MezzmoPlaylistDAOImpl getMezzmoPlaylistDAO() { return new MezzmoPlaylistDAOImpl(); }


}
