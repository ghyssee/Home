package be.home.mezzmo.domain.bo;

import be.home.common.model.BusinessObject;
import be.home.common.model.TransferObject;
import be.home.mezzmo.domain.dao.jdbc.MezzmoAlbumDAOImpl;
import be.home.mezzmo.domain.dao.jdbc.MezzmoDAOImpl;
import be.home.mezzmo.domain.dao.jdbc.MezzmoPlaylistDAOImpl;
import be.home.mezzmo.domain.model.MGOFileAlbumCompositeTO;
import be.home.mezzmo.domain.model.MGOFileTO;
import be.home.mezzmo.domain.model.MGOPlaylistTO;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by ghyssee on 13/04/2016.
 */
public class MezzmoBO extends BusinessObject {

    public MezzmoBO (){
    }


    public int updatePlayCount(String fileID, String album, int playCount, java.util.Date dateLastPlayed) throws SQLException {
        return getMezzmoDAO().updatePlayCount(fileID, album, playCount, dateLastPlayed);
    }

    public int synchronizePlayCount(Long fileID, int playCount) throws SQLException {
        return getMezzmoDAO().synchronizePlayCount(fileID, playCount);
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

    public List<MGOFileAlbumCompositeTO> getAlbums(TransferObject to){
        return getMezzmoDAO().getAlbums(to);
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

    public int updateRanking (Long fileID, int ranking) throws SQLException {
        return getMezzmoDAO().updateRanking(fileID, ranking);
    }

    public List<MGOFileAlbumCompositeTO> getSongsAlbum(Long albumId) {
        return getMezzmoDAO().getSongsAlbum(albumId);
    }

    public MezzmoDAOImpl getMezzmoDAO(){
        return new MezzmoDAOImpl();
    }

    public MezzmoAlbumDAOImpl getMezzmoAlbumDAO(){
        return new MezzmoAlbumDAOImpl();
    }

    public MezzmoPlaylistDAOImpl getMezzmoPlaylistDAO() { return new MezzmoPlaylistDAOImpl(); }


}
