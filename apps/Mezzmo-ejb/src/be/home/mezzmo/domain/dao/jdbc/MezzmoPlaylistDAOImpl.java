package be.home.mezzmo.domain.dao.jdbc;

import be.home.common.dao.jdbc.MezzmoDB;
import be.home.common.database.sqlbuilder.Comparator;
import be.home.common.database.sqlbuilder.SQLBuilder;
import be.home.common.database.sqlbuilder.SQLTypes;
import be.home.mezzmo.domain.dao.definition.PlaylistColumns;
import be.home.mezzmo.domain.dao.definition.Playlist_To_FileColumns;
import be.home.mezzmo.domain.dao.definition.TablesEnum;
import be.home.mezzmo.domain.model.*;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by ghyssee on 14/07/2016.
 */
public class MezzmoPlaylistDAOImpl extends MezzmoDB {

    private static final String FIND_PLAYLIST = SQLBuilder.getInstance()
            .select()
            .addTable(TablesEnum.MGOPlaylist)
            .addColumns(TablesEnum.MGOPlaylist)
            .addColumn("PL2", PlaylistColumns.NAME, "PARENTNAME")
            .addRelation(TablesEnum.MGOPlaylist, "PL2", PlaylistColumns.ID, TablesEnum.MGOPlaylist, PlaylistColumns.PARENTID)
            .addCondition(TablesEnum.MGOPlaylist.alias(), PlaylistColumns.NAME, Comparator.LIKE)
            .addCondition(TablesEnum.MGOPlaylist.alias(), PlaylistColumns.TYPE, Comparator.EQUALS)
            .render();

    private static final String FIND_PLAYLIST_BY_NAME = SQLBuilder.getInstance()
            .select()
            .addTable(TablesEnum.MGOPlaylist)
            .addColumns(TablesEnum.MGOPlaylist)
            .addCondition(TablesEnum.MGOPlaylist.alias(), PlaylistColumns.NAME, Comparator.LIKE)
            .render();

    private static final String FIND_PLAYLIST_CHILDREN = SQLBuilder.getInstance()
            .select()
            .addTable(TablesEnum.MGOPlaylist)
            .addColumns(TablesEnum.MGOPlaylist)
            .addCondition(TablesEnum.MGOPlaylist.alias(), PlaylistColumns.PARENTID, Comparator.EQUALS)
            .render();

    private static final String INSERT_PLAYLIST = SQLBuilder.getInstance()
            .insert()
            .addColumns(TablesEnum.MGOPlaylist, SQLTypes.INSERT)
            .addTable(TablesEnum.MGOPlaylist)
            .render();

    private static final String INSERT_PLAYLIST_SQL = SQLBuilder.getInstance()
            .insert()
            .addColumns(TablesEnum.MGOPlaylistSQL, SQLTypes.INSERT)
            .addTable(TablesEnum.MGOPlaylistSQL)
            .render();

    private static final String DELETE_PLAYLIST = SQLBuilder.getInstance()
            .delete()
            .addTable(TablesEnum.MGOPlaylist)
            .addCondition(PlaylistColumns.ID, Comparator.EQUALS, null)
            .render();

    private static final String DELETE_PLAYLIST_FILE = SQLBuilder.getInstance()
            .delete()
            .addTable(TablesEnum.MGOPlaylist_To_File)
            .addCondition(Playlist_To_FileColumns.PLAYLISTID, Comparator.EQUALS, null)
            .render();

    public class PlayListRowMapper implements RowMapper
    {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            MGOPlaylistTO playlistTO = new MGOPlaylistTO();
            playlistTO.setID(getInteger(rs, PlaylistColumns.ID));
            playlistTO.setName(getString(rs, PlaylistColumns.NAME));
            playlistTO.setParentID(getInteger(rs, PlaylistColumns.PARENTID));
            playlistTO.setParentName(getString(rs, PlaylistColumns.PARENTNAME));
            return playlistTO;
        }

    }

    public List<MGOPlaylistTO> findPlaylist(MGOPlaylistTO playlist) {
        Object[] params = {playlist.getName(), playlist.getType()};
        List<MGOPlaylistTO> list = getInstance().getJDBCTemplate().query(FIND_PLAYLIST, new PlayListRowMapper(), params);
        return list;
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
