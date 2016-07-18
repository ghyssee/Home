package be.home.mezzmo.domain.dao.jdbc;

import be.home.common.dao.jdbc.MezzmoDB;
import be.home.common.dao.jdbc.SQLiteUtils;
import be.home.common.exceptions.MultipleOccurencesException;
import be.home.mezzmo.domain.model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ghyssee on 14/07/2016.
 */
public class MezzmoPlaylistDAOImpl extends MezzmoDB {


    private static final String FIND_PLAYLIST = "SELECT PL.ID AS ID, PL.Name AS NAME, PL.ParentID AS PARENTID, PL2.Name AS PARENTNAME " +
                                                "FROM MGOPlaylist AS PL " +
                                                "INNER JOIN MGOPlaylist AS PL2 ON (PL2.ID = PL.ParentID) " +
                                                "WHERE PL.Name LIKE ? " +
                                                "AND PL.Type = ?";
    private static final String[] COLUMNS = {"Name", "Type", "Description", "ParentID",
            "Author", "Icon", "File", "TraverseFolder",
            "FolderPath", "Filter", "DynamicTreeToken", "RunTime",
            "StreamNum", "OrderByColumn", "OrderByDirection", "LimitBy",
            "CombineAnd", "LimitType", "PlaylistOrder", "MediaType",
            "ThumbnailID", "ThumbnailAuthor", "ContentRatingID", "BackdropArtworkID",
            "DisplayTitleFormat"};
    private static final String INSERT_PLAYLIST = "INSERT INTO MGOPlaylist (" + getColumns(COLUMNS) + ") " +
                                                  "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";


    public List<MGOPlaylistTO> findPlaylist(MGOPlaylistTO playlist){
        PreparedStatement stmt = null;
        List<MGOPlaylistTO> list = new ArrayList <MGOPlaylistTO> ();
        try {
            Connection c = getInstance().getConnection();

            stmt = c.prepareStatement(FIND_PLAYLIST);
            stmt.setString(1, playlist.getName());
            stmt.setInt(2, playlist.getType());
            ResultSet rs = stmt.executeQuery();
            while ( rs.next() ) {
                MGOPlaylistTO playlistTO = new MGOPlaylistTO();
                playlistTO.setID(rs.getInt("ID"));
                playlistTO.setName(rs.getString("NAME"));
                playlistTO.setParentID(rs.getInt("PARENTID"));
                playlistTO.setParentName(rs.getString("PARENTNAME"));
                list.add(playlistTO);
            }
            rs.close();
            stmt.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        return list;
    }

    public int insertPlaylist(MGOPlaylistTO playlist) {
        //System.out.println("Get List of Mp3's for specific FileTitle");
        PreparedStatement stmt = null;
        List<MGOFileTO> list = new ArrayList<MGOFileTO>();
        Connection c = null;
        int rec = 0;
        try {
            c = getInstance().getConnection();

            //stmt = c.createStatement();
            stmt = c.prepareStatement(INSERT_PLAYLIST);
            int idx = 1;

            stmt.setString(idx++, playlist.getName());
            stmt.setInt(idx++, playlist.getType());
            stmt.setString(idx++, playlist.getDescription());
            stmt.setInt(idx++, playlist.getParentID());
            stmt.setInt(idx++, playlist.getAuthor());
            stmt.setInt(idx++, playlist.getIcon());
            stmt.setString(idx++, playlist.getFile());
            stmt.setString(idx++, playlist.getTraverseFolder());
            stmt.setString(idx++, playlist.getFolderPath());
            stmt.setString(idx++, playlist.getFilter());
            stmt.setString(idx++, playlist.getDynamicTreeToken());
            stmt.setString(idx++, playlist.getRunTime());
            stmt.setString(idx++, playlist.getStreamNum());
            stmt.setInt(idx++, playlist.getOrderByColumn());
            stmt.setInt(idx++, playlist.getOrderByDirection());
            stmt.setInt(idx++, playlist.getLimitBy());
            stmt.setInt(idx++, playlist.getCombineAnd());
            stmt.setInt(idx++, playlist.getLimitType());
            stmt.setInt(idx++, playlist.getPlaylistOrder());
            stmt.setInt(idx++, playlist.getMediaType());
            stmt.setInt(idx++, playlist.getThumbnailID());
            stmt.setInt(idx++, playlist.getThumbnailAuthor());
            stmt.setInt(idx++, playlist.getContentRatingID());
            stmt.setInt(idx++, playlist.getBackdropArtworkID());
            stmt.setString(idx++, playlist.getDisplayTitleFormat());
            //System.out.println(FILE_SELECT_TITLE);
            rec = stmt.executeUpdate();
            c.commit();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            if (c != null) {
                try {
                    System.err.println("Transaction is being rolled back");
                    c.rollback();
                } catch (SQLException excep) {
                    System.err.println(excep.getClass().getName() + ": " + excep.getMessage());
                }
            }
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException excep) {
                    System.err.println(excep.getClass().getName() + ": " + excep.getMessage());
                }
            }
            return rec;
        }
    }
}
