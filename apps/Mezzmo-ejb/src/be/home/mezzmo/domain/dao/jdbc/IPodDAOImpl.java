package be.home.mezzmo.domain.dao.jdbc;

import be.home.common.dao.jdbc.IPodDB;
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
public class IPodDAOImpl extends IPodDB {

    private static final String LIST_PLAYCOUNT = "SELECT " +
                                                 "pid ID, title TITLE, artist ARTIST, album ALBUM, track_number TRACK, disc_number DISC, ITEM_STATS.play_count_user PLAYCOUNT, " +
                                                  "ITEM_STATS.date_played DATELASTPLAYED " +
                                                  "FROM item " +
                                                  "LEFT OUTER JOIN DYNAMIC.item_stats ITEM_STATS ON (item.pid = ITEM_STATS.item_pid) " +
                                                  "WHERE ITEM_STATS.play_count_user > 0";

    public List<MGOFileAlbumCompositeTO> listPlayCount()
    {
        PreparedStatement stmt = null;
        List<MGOFileAlbumCompositeTO> list = new ArrayList<MGOFileAlbumCompositeTO>();
        try {
            Connection c = getInstance().getConnection();

            stmt = c.prepareStatement(LIST_PLAYCOUNT);
            ResultSet rs = stmt.executeQuery();
            while ( rs.next() ) {

                MGOFileAlbumCompositeTO fileAlbumComposite = new MGOFileAlbumCompositeTO();
                MGOFileTO fileTO = fileAlbumComposite.getFileTO();
                fileTO.setId(rs.getInt("ID"));
                fileTO.setTitle(rs.getString("TITLE"));
                fileTO.setTrack(rs.getInt("TRACK"));
                fileTO.setDisc(rs.getInt("DISC"));
                fileTO.setPlayCount(rs.getInt("PLAYCOUNT"));
                fileTO.setDateLastPlayed(SQLiteUtils.convertiPodToDate(rs.getLong("DATELASTPLAYED")));
                MGOFileArtistTO fileArtistTO = fileAlbumComposite.getFileArtistTO();
                fileArtistTO.setArtist(rs.getString("ARTIST"));
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
