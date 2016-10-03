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
                                                 "item.pid ID, title TITLE, artist ARTIST, album ALBUM, track_number TRACK, disc_number DISC, ITEM_STATS.play_count_user PLAYCOUNT, " +
                                                  "ITEM_STATS.date_played DATELASTPLAYED, ALBUM.pid ALBUMID, ALBUM.name ALBUMNAME " +
                                                  "FROM item " +
                                                  "LEFT OUTER JOIN DYNAMIC.item_stats ITEM_STATS ON (item.pid = ITEM_STATS.item_pid) " +
                                                  "LEFT OUTER JOIN album ALBUM ON (item.album_pid = ALBUM.pid) " +
                                                  "WHERE ITEM_STATS.play_count_user > 0 " +
                                                  "ORDER BY ITEM_STATS.date_played ";

    private static final String RESET_PLAYCOUNT = "UPDATE DYNAMIC.item_stats ITEM_STATS " +
                                                  "SET play_count_user = ? " +
                                                  "WHERE item_pid = ? ";

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
                fileTO.setDateLastPlayed(SQLiteUtils.convertiPodDateToDate(rs.getLong("DATELASTPLAYED")));
                MGOFileArtistTO fileArtistTO = fileAlbumComposite.getFileArtistTO();
                fileArtistTO.setArtist(rs.getString("ARTIST"));
                MGOFileAlbumTO fileAlbumTO = fileAlbumComposite.getFileAlbumTO();
                fileAlbumTO.setName(rs.getString("ALBUMNAME"));
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

    public int updatePlayCount(Long pid, int playCount)  {
        PreparedStatement stmt = null;
        List<MGOFileTO> list = new ArrayList<MGOFileTO>();
        Connection c = null;
        int rec = 0;
        try {
            c = getInstance().getConnection();

            //stmt = c.createStatement();
            stmt = c.prepareStatement(RESET_PLAYCOUNT);
            int idx = 1;
            stmt.setInt(idx++, playCount);
            stmt.setLong(idx++, pid);
            rec =  stmt.executeUpdate();
            c.commit();
        }
        catch (SQLException e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            if (c != null) {
                try {
                    System.err.println("Transaction is being rolled back");
                    c.rollback();
                } catch(SQLException excep) {
                    System.err.println( excep.getClass().getName() + ": " + excep.getMessage() );
                }
            }
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                }
            }
        }
        return rec;
        //System.out.println("Number of rows retrieved: " + list.size());
    }

}
