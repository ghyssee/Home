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

    private static final String[] COLUMNS_SQL = {"ID", "PlaylistID",
        "ColumnNum", "ColumnType", "Operand", "ValueOneText",
        "ValueTwoText","ValueOneInt","ValueTwoInt","GroupNr"};

    private static final String INSERT_PLAYLIST = "INSERT INTO MGOPlaylist (" + getColumns(COLUMNS) + ") " +
                                                  "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";


    private static final String INSERT_PLAYLIST_SQL = "INSERT INTO MGOPlaylistSQLTO (" + getColumns(COLUMNS) + ") " +
            "VALUES (?,?,?,?,?,?,?,?,?,?)";

    private Integer getInteger(ResultSet rs, String id) throws SQLException {
        return new Integer(rs.getInt(id));
    }

    private void setInteger(PreparedStatement ps, int index, Integer value) throws SQLException {
        if (value == null){
            ps.setNull(index, java.sql.Types.INTEGER);
        }
        else {
            ps.setInt(index, value.intValue());
        }
    }

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
                playlistTO.setID(getInteger(rs, "ID"));
                playlistTO.setName(rs.getString("NAME"));
                playlistTO.setParentID(getInteger(rs, "PARENTID"));
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
            setInteger(stmt, idx++, playlist.getType());
            stmt.setString(idx++, playlist.getDescription());
            setInteger(stmt, idx++, playlist.getParentID());
            setInteger(stmt, idx++, playlist.getAuthor());
            setInteger(stmt, idx++, playlist.getIcon());
            stmt.setString(idx++, playlist.getFile());
            stmt.setString(idx++, playlist.getTraverseFolder());
            stmt.setString(idx++, playlist.getFolderPath());
            stmt.setString(idx++, playlist.getFilter());
            stmt.setString(idx++, playlist.getDynamicTreeToken());
            stmt.setString(idx++, playlist.getRunTime());
            stmt.setString(idx++, playlist.getStreamNum());
            setInteger(stmt, idx++, playlist.getOrderByColumn());
            setInteger(stmt, idx++, playlist.getOrderByDirection());
            setInteger(stmt, idx++, playlist.getLimitBy());
            setInteger(stmt, idx++, playlist.getCombineAnd());
            setInteger(stmt, idx++, playlist.getLimitType());
            setInteger(stmt, idx++, playlist.getPlaylistOrder());
            setInteger(stmt, idx++, playlist.getMediaType());
            setInteger(stmt, idx++, playlist.getThumbnailID());
            setInteger(stmt, idx++, playlist.getThumbnailAuthor());
            setInteger(stmt, idx++, playlist.getContentRatingID());
            setInteger(stmt, idx++, playlist.getBackdropArtworkID());
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

    public int insertPlaylistSQL(MGOPlaylistSQLTO playlistSQL) {
        PreparedStatement stmt = null;
        List<MGOFileTO> list = new ArrayList<MGOFileTO>();
        Connection c = null;
        int rec = 0;
        try {
            c = getInstance().getConnection();

            //stmt = c.createStatement();
            stmt = c.prepareStatement(INSERT_PLAYLIST);
            int idx = 1;

            setInteger(stmt, idx++, playlistSQL.getPlaylistId());
            setInteger(stmt, idx++, playlistSQL.getColumnNum());
            setInteger(stmt, idx++, playlistSQL.getColumnType());
            setInteger(stmt, idx++, playlistSQL.getOperand());
            stmt.setString(idx++, playlistSQL.getValueOneText());
            stmt.setString(idx++, playlistSQL.getValueTwoText());
            setInteger(stmt, idx++, playlistSQL.getValueOneInt());
            setInteger(stmt, idx++, playlistSQL.getValueTwoInt());
            setInteger(stmt, idx++, playlistSQL.getGroupNr());
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
