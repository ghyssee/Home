package be.home.mezzmo.domain.dao.jdbc;

import be.home.common.dao.jdbc.MediaMonkeyDB;
import be.home.common.dao.jdbc.MezzmoDB;
import be.home.common.dao.jdbc.SQLiteUtils;
import be.home.common.model.TransferObject;
import be.home.mezzmo.domain.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ghyssee on 9/02/2016.
 */
public class MediaMonkeyDAOImpl extends MediaMonkeyDB {

    private static final String FILE_SELECT = "select file from MGOFile where File like '%Boyzone%';";


    private static final String LIST_TOP20 = "SELECT  SongTitle AS FILETITLE, " +
                                             "PlayCounter AS PLAYCOUNT, " +
                                             "SongTitle AS TITLE,  " +
                                             "Artist AS ARTIST, " +
                                             "0 AS PLAYLIST_ID, " +
                                             "ID AS FILE_ID, " +
                                             "SongPath AS FILE, " +
                                             "SongLength AS DURATION "+
                                             "FROM songs " +
                                             "INNER JOIN PLAYLISTSONGS AS PS ON (PS.IDSONG = SONGS.ID) " +
                                             "INNER JOIN PLAYLISTS AS PL ON (PL.IDPLAYLIST = PS.IDPLAYLIST) " +
                                             "WHERE PL.PLAYLISTNAME LIKE 'Top Of The Moment' " +
                                             "AND PL.PARENTPLAYLIST like 0 " +
                                             "ORDER BY PS.SONGORDER " +
                                             "LIMIT 0,20 ";

    public List<MGOFileAlbumCompositeTO> getTop20()
    {
        PreparedStatement stmt = null;
        List<MGOFileAlbumCompositeTO> list = new ArrayList<MGOFileAlbumCompositeTO>();
        try {
            Connection c = getInstance().getConnection();

            //stmt = c.createStatement();
            stmt = c.prepareStatement(LIST_TOP20);
            //stmt.setLong(1, to.getIndex());
            //stmt.setLong(2, to.getLimit());
            //System.out.println(FILE_SELECT_TITLE);
            ResultSet rs = stmt.executeQuery();
            while ( rs.next() ) {

                MGOFileAlbumCompositeTO fileAlbumComposite = new MGOFileAlbumCompositeTO();
                MGOFileTO fileTO = fileAlbumComposite.getFileTO();
                fileTO.setId(rs.getInt("FILE_ID"));
                fileTO.setFileTitle(rs.getString("FILETITLE"));
                fileTO.setFile(rs.getString("FILE"));
                fileTO.setTitle(rs.getString("TITLE"));
                fileTO.setPlayCount(rs.getInt("PLAYCOUNT"));
                fileTO.setDuration(rs.getInt("DURATION"));
                MGOFileArtistTO fileArtistTO = fileAlbumComposite.getFileArtistTO();
                fileArtistTO.setArtist(rs.getString("ARTIST"));
                MGOPlaylistTO playlistTO = fileAlbumComposite.getPlaylistTO();
                playlistTO.setID(rs.getInt("PLAYLIST_ID"));
                list.add(fileAlbumComposite);
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
