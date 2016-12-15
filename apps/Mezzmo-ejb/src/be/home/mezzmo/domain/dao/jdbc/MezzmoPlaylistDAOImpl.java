package be.home.mezzmo.domain.dao.jdbc;

import be.home.common.dao.jdbc.MezzmoDB;
import be.home.common.database.DatabaseColumn;
import be.home.common.database.FieldType;
import be.home.mezzmo.domain.dao.SQLBuilder;
import be.home.mezzmo.domain.model.*;
import org.springframework.jdbc.core.RowMapper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by ghyssee on 14/07/2016.
 */
public class MezzmoPlaylistDAOImpl extends MezzmoDB {


    private static final String[] COLUMNS_SQL = {"PlaylistID",
            "ColumnNum", "ColumnType", "Operand", "ValueOneText",
            "ValueTwoText","ValueOneInt","ValueTwoInt","GroupNr"};

    private static final String FIND_PLAYLIST = SQLBuilder.getInstance()
            .select()
            .addTable(TablesEnum.MGOPlaylist)
            .addColumns(TablesEnum.MGOPlaylist)
            .addColumn("PL2", PlayListColumns.NAME, "PARENTNAME")
            .addRelation(TablesEnum.MGOPlaylist, "PL2", PlayListColumns.ID, TablesEnum.MGOPlaylist, PlayListColumns.PARENTID)
            .addCondition(TablesEnum.MGOPlaylist.alias(), PlayListColumns.NAME, SQLBuilder.Comparator.LIKE)
            .addCondition(TablesEnum.MGOPlaylist.alias(), PlayListColumns.TYPE, SQLBuilder.Comparator.EQUALS)
            .render();

    private static final String FIND_PLAYLIST_BY_NAME = SQLBuilder.getInstance()
            .select()
            .addTable(TablesEnum.MGOPlaylist)
            .addColumns(TablesEnum.MGOPlaylist)
            .addCondition(TablesEnum.MGOPlaylist.alias(), PlayListColumns.NAME, SQLBuilder.Comparator.LIKE)
            .render();

    private static final String FIND_PLAYLIST_CHILDREN = SQLBuilder.getInstance()
            .select()
            .addTable(TablesEnum.MGOPlaylist)
            .addColumns(TablesEnum.MGOPlaylist)
            .addCondition(TablesEnum.MGOPlaylist.alias(), PlayListColumns.PARENTID, SQLBuilder.Comparator.EQUALS)
            .render();

    private static final String INSERT_PLAYLIST = SQLBuilder.getInstance()
            .insert()
            .addColumns(TablesEnum.MGOPlaylist, SQLBuilder.SQLTypes.INSERT)
            .addTable(TablesEnum.MGOPlaylist)
            .render();

    private static final String INSERT_PLAYLIST_SQL2 = "INSERT INTO " + TablesEnum.MGOPlaylistSQL + " (" +
            getColumns(COLUMNS_SQL) + ") " +
            "VALUES (?,?,?,?,?,?,?,?,?)";

    private static final String INSERT_PLAYLIST_SQL = SQLBuilder.getInstance()
            .insert()
            .addColumns(TablesEnum.MGOPlaylistSQL, SQLBuilder.SQLTypes.INSERT)
            .addTable(TablesEnum.MGOPlaylistSQL)
            .render();

    private static final String DELETE_PLAYLIST = SQLBuilder.getInstance()
            .delete()
            .addTable(TablesEnum.MGOPlaylist)
            .addCondition(PlayListColumns.ID, SQLBuilder.Comparator.EQUALS, null)
            .render();

    private static final String DELETE_PLAYLIST_FILE = "DELETE FROM " + TablesEnum.MGOPlaylist_To_File +
            " WHERE PlaylistID = ?";

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

    public static String getColumnsWithAlias(DatabaseColumn[] enumValues, String alias){
        String columns = "";
        boolean first = true;
        for (DatabaseColumn tmp : enumValues){
            columns += (first ? "" : ", ") + alias + "." + tmp.getColumnName() + " AS " + tmp.name();
            first = false;
        }
        return columns;

    }

    public static String getColumns(DatabaseColumn[] enumValues){
        String columns = "";
        boolean first = true;
        for (DatabaseColumn tmp : enumValues) {
            if (tmp.getFieldType() != FieldType.PRIMARYKEY) {
                columns += (first ? "" : ", ") + tmp.getColumnName();
            first = false;
            }
        }
        return columns;

    }

    public class PlayListRowMapper implements RowMapper
    {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            MGOPlaylistTO playlistTO = new MGOPlaylistTO();
            playlistTO.setID(getInteger(rs, PlayListColumns.ID.columnName));
            playlistTO.setName(rs.getString(PlayListColumns.NAME.columnName));
            playlistTO.setParentID(getInteger(rs, "PARENTID"));
            playlistTO.setParentName(rs.getString("PARENTNAME"));
            return playlistTO;
        }

    }

    public List<MGOPlaylistTO> findPlaylist(MGOPlaylistTO playlist) {
        Object[] params = {playlist.getName(), playlist.getType()};
        List<MGOPlaylistTO> list = getInstance().getJDBCTemplate().query(FIND_PLAYLIST, new PlayListRowMapper(), params);
        return list;

 /*
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
        return list;*/
    }

    public int insertPlaylist(MGOPlaylistTO playlist) {
        Object[] params = {playlist.getName(),
                playlist.getType(),
                playlist.getDescription(),
                playlist.getParentID(),
                playlist.getAuthor(),
                playlist.getIcon(),
                playlist.getFile(),
                playlist.getTraverseFolder(),
                playlist.getFolderPath(),
                playlist.getFilter(),
                playlist.getDynamicTreeToken(),
                playlist.getRunTime(),
                playlist.getStreamNum(),
                playlist.getOrderByColumn(),
                playlist.getOrderByDirection(),
                playlist.getLimitBy(),
                playlist.getCombineAnd(),
                playlist.getLimitType(),
                playlist.getPlaylistOrder(),
                playlist.getMediaType(),
                playlist.getThumbnailID(),
                playlist.getThumbnailAuthor(),
                playlist.getContentRatingID(),
                playlist.getBackdropArtworkID(),
                playlist.getDisplayTitleFormat()
                };
        return getInstance().getJDBCTemplate().update(INSERT_PLAYLIST, params);
    }

    /*
    public int insertPlaylistSQLOld(MGOPlaylistSQLTO playlistSQL) {
        PreparedStatement stmt = null;
        Connection c = null;
        int rec = 0;
        try {
            c = getInstance().getConnection();

            //stmt = c.createStatement();
            stmt = c.prepareStatement(INSERT_PLAYLIST_SQL);
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
    */

    public int insertPlaylistSQL(MGOPlaylistSQLTO playlistSQL) {

            Object[] params = {
                    playlistSQL.getPlaylistId(),
                    playlistSQL.getColumnNum(),
                    playlistSQL.getColumnType(),
                    playlistSQL.getOperand(),
                    playlistSQL.getValueOneText(),
                    playlistSQL.getValueTwoText(),
                    playlistSQL.getValueOneInt(),
                    playlistSQL.getValueTwoInt(),
                    playlistSQL.getGroupNr()

            };
            return getInstance().getJDBCTemplate().update(INSERT_PLAYLIST_SQL, params);
    }

    public MGOPlaylistTO findPlaylistByName(String name){
        Object[] params = {name };
        MGOPlaylistTO playlistTO = (MGOPlaylistTO) getInstance().getJDBCTemplate().queryForObject(FIND_PLAYLIST_BY_NAME, new MezzmoPlaylistRowMappers.PlaylistRowMapper(), params);
        return playlistTO;
    }

    public List <MGOPlaylistTO> findChildren(Integer id){
        Object[] params = {id };
        List <MGOPlaylistTO> list = getInstance().getJDBCTemplate().query(FIND_PLAYLIST_CHILDREN, new MezzmoPlaylistRowMappers.PlaylistRowMapper(), params);
        return list;
    }

    public int deletePlaylist(Integer playlistId) {
        Object[] params = {playlistId};
        return getInstance().getJDBCTemplate().update(DELETE_PLAYLIST, params);
    }

    public int deletePlaylistToFile(Integer playlistId) {
        Object[] params = {playlistId};
        return getInstance().getJDBCTemplate().update(DELETE_PLAYLIST_FILE, params);
    }


}
