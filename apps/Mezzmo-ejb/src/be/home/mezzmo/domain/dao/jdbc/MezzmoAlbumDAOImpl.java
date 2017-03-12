package be.home.mezzmo.domain.dao.jdbc;

import be.home.mezzmo.domain.model.MGOFileAlbumCompositeTO;
import be.home.mezzmo.domain.model.MGOFileAlbumTO;

import java.util.List;

/**
 * Created by ghyssee on 14/07/2016.
 */
public class MezzmoAlbumDAOImpl extends MezzmoRowMappers {


    private static final String FIND_ALBUM_OLD = "SELECT DISTINCT ALBUM.ID AS ID, " +
                                                             "ALBUM.Data AS NAME, " +
                                                             "MGOAlbumArtist.data AS ALBUMARTIST " +
                                             "FROM MGOFileAlbum AS ALBUM " +
                                             "INNER JOIN MGOFileAlbumRelationship ON (ALBUM.ID = MGOFileAlbumRelationship.ID) " +
                                             "INNER JOIN MGOAlbumArtistRelationship ON (MGOAlbumArtistRelationship.fileID = MGOFileAlbumRelationship.fileID) " +
                                             "INNER JOIN MGOAlbumArtist ON (MGOAlbumArtist.ID = MGOAlbumArtistRelationship.ID) " +
                                             "INNER JOIN MGOFile ON (MGOFile.ID = MGOAlbumArtistRelationship.fileID) " +
                                             "INNER JOIN MGOFileExtension ON (MGOFileExtension.ID = MGOFILE.extensionID) " +
                                             "INNER JOIN MGOFileArtistRelationship ON (MGOFileArtistRelationship.fileID = MGOFile.id) " +
                                             "INNER JOIN MGOFileArtist ON (MGOFileArtist.id = MGOFileArtistRelationship.id) " +
                                             "WHERE 1=1 " +
                                             "AND MGOFileExtension.data = 'mp3' " +
                                             "AND ALBUM.Data like ? " +
                                             "AND MGOAlbumArtist.Data like ?";

    public List<MGOFileAlbumCompositeTO> findAlbum(String album, String albumArtist){
        List<MGOFileAlbumCompositeTO> list = null;
        Object[] params = {album, albumArtist == null ? "%"  : albumArtist};
        list = getInstance().getJDBCTemplate().query(FIND_ALBUM, new FileAlbumAlbumArtistRowMapper(), params);
        return list;
    }

    public MGOFileAlbumTO findAlbumByName(String album, String albumArtist){
        Object[] params = {album, albumArtist == null ? "%"  : albumArtist};
        return (MGOFileAlbumTO) getInstance().getJDBCTemplate().queryForObject(FIND_ALBUM, new SingleAlbumRowMapper(), params);
    }


    public MGOFileAlbumTO findAlbumById(long id){
        Object[] params = {id};
        return (MGOFileAlbumTO) getInstance().getJDBCTemplate().queryForObject(FIND_ALBUM_BY_ID, new SingleAlbumRowMapper(), params);
    }
}
