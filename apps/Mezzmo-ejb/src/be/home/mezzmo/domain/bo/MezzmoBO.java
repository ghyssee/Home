package be.home.mezzmo.domain.bo;

import be.home.common.enums.MP3Tag;
import be.home.common.model.BusinessObject;
import be.home.common.model.TransferObject;
import be.home.mezzmo.domain.dao.jdbc.MezzmoAlbumDAOImpl;
import be.home.mezzmo.domain.dao.jdbc.MezzmoDAOImpl;
import be.home.mezzmo.domain.dao.jdbc.MezzmoPlaylistDAOImpl;
import be.home.mezzmo.domain.model.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.EmptyResultDataAccessException;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ghyssee on 13/04/2016.
 */
public class MezzmoBO extends BusinessObject {

    private String db = null;

    public MezzmoBO (){
    }

    public MezzmoBO (String db){
        this.db = db;
    }


    public int updatePlayCount(String fileTitle, String album, int playCount, java.util.Date dateLastPlayed) throws SQLException {
        return getMezzmoDAO().updatePlayCount(fileTitle, album, playCount, dateLastPlayed);
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

    public List<MGOFileAlbumCompositeTO> getAlbumsWithExcludeList(MGOFileAlbumTO albumTO, List<String> list,
                                                                  TransferObject to){
        return getMezzmoDAO().getAlbumsWithExcludeList(albumTO, list, to);
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
        MGOFileTO mgoFileTO = null;
        try {
            mgoFileTO = getMezzmoDAO().findByFile(file);
        }
        catch (EmptyResultDataAccessException e){
            // do nothing
        }
        return mgoFileTO;
    }

    public MGOFileTO findCoverArt(Long albumId){
        return getMezzmoDAO().findCoverArt(albumId);
    }

    public List<MGOFileAlbumCompositeTO> findAlbum (String album, String albumArtist) {
        return getMezzmoAlbumDAO().findAlbum(album, albumArtist);

    }

    public MGOFileAlbumTO findAlbumByName (String album, String albumArtist) {
        return getMezzmoAlbumDAO().findAlbumByName(album, albumArtist);
    }

    public MGOFileAlbumTO findDistinctAlbumByName (String album) {
        return getMezzmoAlbumDAO().findDistinctAlbumByName(album);
    }

    public MGOFileAlbumTO findAlbumById (long id) {
        return getMezzmoAlbumDAO().findAlbumById(id);
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

    public MGOFileArtistTO findArtistById(MGOFileArtistTO artist){
        return getMezzmoDAO().findArtistById(artist);
    }

    public List <MGOFileAlbumCompositeTO> findLinkedArtist(MGOFileArtistTO artist){
        return getMezzmoDAO().findLinkedArtist(artist);
    }

    public Long insertArtist(MGOFileArtistTO artist){
        return getMezzmoDAO().insertArtist(artist);
    }

    public int updateArtist(MGOFileArtistTO artist){
        return getMezzmoDAO().updateArtist(artist);
    }

    public List <MGOFileAlbumCompositeTO> findSongsByAlbum(MGOFileAlbumCompositeTO comp){
        return getMezzmoDAO().findSongsByAlbum(comp);
    }

    public int updateRanking (Long fileID, int ranking) throws SQLException {
        return getMezzmoDAO().updateRanking(fileID, ranking);
    }

    public int updateSong (MGOFileAlbumCompositeTO comp, MP3Tag type) throws SQLException {
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

    public int updateLinkFileArtist2(MGOFileAlbumCompositeTO comp){
        return getMezzmoDAO().updateLinkFileArtist2(comp);
    }

    public List <MGOFileAlbumCompositeTO> findLinkedAlbum(MGOFileAlbumTO album){
        return getMezzmoDAO().findLinkedAlbum(album);
    }

    public int updateLinkFileAlbum(MGOFileAlbumCompositeTO comp, long newAlbumId){
        return getMezzmoDAO().updateLinkFileAlbum(comp, newAlbumId);
    }
    public int deleteArtist(MGOFileArtistTO artist){
        return getMezzmoDAO().deleteArtist(artist);
    }
    public int deleteAlbum(MGOFileAlbumTO album){
        return getMezzmoDAO().deleteAlbum(album);
    }
    public Long insertAlbum(MGOFileAlbumTO album){
        return getMezzmoDAO().insertAlbum(album);
    }
    public Map<String, MGOFileAlbumCompositeTO> getMaxDisc() {

        List<MGOFileAlbumCompositeTO> list = getMezzmoDAO().getMaxDisc();
        Map<String, MGOFileAlbumCompositeTO> map = new HashMap<> ();
        for (MGOFileAlbumCompositeTO comp : list){
            map.put(comp.getFileAlbumTO().getName() + comp.getAlbumArtistTO().getName(), comp);
        }
        return map;
    }

    public MGOFileAlbumCompositeTO findFileById(long id){
        return getMezzmoDAO().findFileById(id);
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

    public VersionTO findVersion(String version) {
            return getMezzmoDAO().findVersion(version);
    }

    public int addVersion(String version) {
        return getMezzmoDAO().addVersion(version);
    }

    public List<MGOFileAlbumCompositeTO> getAllMP3Files(TransferObject to){
        return getMezzmoDAO().getAllMP3Files(to);
    }

    public MezzmoDAOImpl getMezzmoDAO(){
        return new MezzmoDAOImpl(db);
    }

    public MezzmoAlbumDAOImpl getMezzmoAlbumDAO(){
        return new MezzmoAlbumDAOImpl();
    }

    public MezzmoPlaylistDAOImpl getMezzmoPlaylistDAO() { return new MezzmoPlaylistDAOImpl(); }


}
