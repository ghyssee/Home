package be.home.mezzmo.domain.dao.jdbc;

import be.home.mezzmo.domain.model.MGOAlbumArtistTO;
import be.home.mezzmo.domain.model.MGOFileAlbumCompositeTO;
import be.home.mezzmo.domain.model.Result;

import java.util.List;

/**
 * Created by Gebruiker on 12/03/2017.
 */
public class MezzmoAlbumArtistDAOImpl extends MezzmoRowMappers {

    public MGOAlbumArtistTO findAlbumArtist(MGOAlbumArtistTO albumArtist){
        Object[] params = {
                albumArtist.getName()
        };
        MGOAlbumArtistTO albumArtistTO = (MGOAlbumArtistTO) getInstance().getJDBCTemplate().
                queryForObject(FIND_ALBUM_ARTIST, new AlbumArtistRowMapper(), params);
        return albumArtistTO;
    }

    public MGOAlbumArtistTO findAlbumArtistById(MGOAlbumArtistTO albumArtist){
        Object[] params = {
                albumArtist.getId()
        };
        MGOAlbumArtistTO albumArtistTO = (MGOAlbumArtistTO) getInstance().getJDBCTemplate().
                queryForObject(FIND_ALBUM_ARTIST_BY_ID, new AlbumArtistRowMapper(), params);
        return albumArtistTO;
    }

    public int updateAlbumArtist(MGOAlbumArtistTO albumArtist) {
        Object params[] = {albumArtist.getName(), albumArtist.getId()};
        int nr = getInstance().getJDBCTemplate().update(FILE_UPDATE_ALBUM_ARTIST, params);
        return nr;
    }

    public int updateLinkFileAlbumArtist2(MGOFileAlbumCompositeTO comp){
        Object[] params;
        params = new Object[] {comp.getAlbumArtistTO().getId(), comp.getFileTO().getId()};
        int nr = getInstance().getJDBCTemplate().update(UPDATE_LINK_FILE_ALBUM_ARTIST2, params);
        return nr;
    }

    public List<MGOFileAlbumCompositeTO> findLinkedAlbumArtist(MGOAlbumArtistTO albumArtist){
        Object[] params = {
                albumArtist.getId()
        };
        List <MGOFileAlbumCompositeTO> list = getInstance().getJDBCTemplate().query(FIND_LINKED_ALBUM_ARTIST, new FileAlbumArtistRowMapper(), params);
        return list;
    }

    public Result updateLinkFileAlbumArtist(MGOAlbumArtistTO albumArtist, Long newId){
        Object[] params;
        Result result = new Result();
        params = new Object[] {newId, albumArtist.getId()};
        result.setNr1(getInstance().getJDBCTemplate().update(UPDATE_LINK_FILE_ALBUM_ARTIST, params));
        return result;
    }

    public int deleteAlbumArtist(MGOAlbumArtistTO albumArtist){
        Object[] params;
        params = new Object[] {albumArtist.getId()};
        int nr = getInstance().getJDBCTemplate().update(DELETE_ALBUM_ARTIST, params);
        return nr;
    }

    public Long insertAlbumArtist(final MGOAlbumArtistTO albumArtist){
        Object[] params = {albumArtist.getName()};
        Long key = insertJDBC(getInstance().getJDBCTemplate(), params, INSERT_ALBUM_ARTIST, "id");
        return key;
    }

}
