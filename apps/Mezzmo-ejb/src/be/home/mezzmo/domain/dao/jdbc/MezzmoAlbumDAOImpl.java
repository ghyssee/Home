package be.home.mezzmo.domain.dao.jdbc;

import be.home.mezzmo.domain.model.MGOAlbumArtistTO;
import be.home.mezzmo.domain.model.MGOFileAlbumCompositeTO;
import be.home.mezzmo.domain.model.MGOFileAlbumTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
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
        list = getInstance().getJDBCTemplate().query(FIND_ALBUM, new AlbumArtistRowMapper(), params);
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


    public List<MGOFileAlbumCompositeTO> findAlbum2 (String album, String albumArtist){
        PreparedStatement stmt = null;
        List<MGOFileAlbumCompositeTO> list = new ArrayList <MGOFileAlbumCompositeTO> ();
        boolean error = false;
        try {
            Connection c = getInstance().getConnection();

            stmt = c.prepareStatement(FIND_ALBUM_OLD);
            stmt.setString(1, album);
            stmt.setString(2, albumArtist == null ? "%"  : albumArtist);
            ResultSet rs = stmt.executeQuery();
            while ( rs.next() ) {
                MGOFileAlbumCompositeTO comp = new MGOFileAlbumCompositeTO();
                MGOFileAlbumTO fileAlbumTO = comp.getFileAlbumTO();
                fileAlbumTO.setId(rs.getLong("ID"));
                fileAlbumTO.setName(rs.getString("NAME"));
                MGOAlbumArtistTO albumArtistTO = comp.getAlbumArtistTO();
                albumArtistTO.setName(rs.getString("ALBUMARTIST"));
                list.add(comp);
            }
            rs.close();
            stmt.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        return list;
    }


}
