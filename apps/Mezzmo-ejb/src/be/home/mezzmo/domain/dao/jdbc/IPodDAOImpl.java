package be.home.mezzmo.domain.dao.jdbc;

import be.home.common.dao.jdbc.*;
import be.home.common.database.sqlbuilder.Comparator;
import be.home.common.database.sqlbuilder.SQLBuilder;
import be.home.common.database.sqlbuilder.SortOrder;
import be.home.common.database.sqlbuilder.Type;
import be.home.common.model.TransferObject;
import be.home.mezzmo.domain.dao.definition.ipod.AlbumColumns;
import be.home.mezzmo.domain.dao.definition.ipod.ItemColumns;
import be.home.mezzmo.domain.dao.definition.ipod.Item_StatsColumns;
import be.home.mezzmo.domain.dao.definition.ipod.iPodTables;
import be.home.mezzmo.domain.model.*;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ghyssee on 9/02/2016.
 */
public class IPodDAOImpl extends IPodDB {

    private static final String LIST_PLAYCOUNT_OLD = "SELECT " +
            "item.pid ID, title TITLE, artist ARTIST, item.ALBUM_ARTIST ALBUM_ARTIST, album ALBUM, track_number TRACK, disc_number DISC, ITEM_STATS.play_count_user PLAYCOUNT, " +
            "ITEM_STATS.date_played DATELASTPLAYED, ALBUM.pid ALBUMID, ALBUM.name ALBUMNAME " +
            "FROM item " +
            "LEFT OUTER JOIN DYNAMIC.item_stats ITEM_STATS ON (item.pid = ITEM_STATS.item_pid) " +
            "LEFT OUTER JOIN album ALBUM ON (item.album_pid = ALBUM.pid) " +
            "WHERE ITEM_STATS.play_count_user > 0 " +
            "ORDER BY ITEM_STATS.date_played ";

    public static final String LIST_PLAYCOUNT = SQLBuilder.getInstance()
            .select()
            .addTable(iPodTables.Item)
            .addColumns(iPodTables.Item)
            .addColumns(iPodTables.Item_Stats)
            .addColumns(iPodTables.Album)
            .addRelation(iPodTables.Item_Stats, Item_StatsColumns.ITEMID, iPodTables.Item, ItemColumns.ID)
            .addRelation(iPodTables.Album, AlbumColumns.ALBUMID, iPodTables.Item, ItemColumns.ALBUMID)
            .addCondition(iPodTables.Item_Stats.alias(), Item_StatsColumns.PLAYCOUNT, Comparator.GREATER, 0)
            .orderBy(iPodTables.Item_Stats, Item_StatsColumns.DATELASTPLAYED, SortOrder.ASC)
            .render();

    public static final String RESET_PLAYCOUNT = SQLBuilder.getInstance()
            .update()
            .addTable(iPodTables.Item_Stats)
            .updateColumn(Item_StatsColumns.PLAYCOUNT, Type.PARAMETER)
            .updateColumn(Item_StatsColumns.HAS_BEEN_PLAYED, Type.VALUE, 0)
            .updateColumn(Item_StatsColumns.DATELASTPLAYED, Type.VALUE, 0)
            .addCondition(iPodTables.Item_Stats.alias(), Item_StatsColumns.ITEMID, Comparator.EQUALS)
            .render();


    private static final String RESET_PLAYCOUNT_OLD = "UPDATE DYNAMIC.item_stats " +
                                                  "SET play_count_user = ? " +
                                                  " ,has_been_played = 0" +
                                                   " ,date_played = 0 " +
                                                  "WHERE item_pid = ? ";

    public List<MGOFileAlbumCompositeTO> listPlayCount()
    {
        List<MGOFileAlbumCompositeTO> customers  = getInstance().getJDBCTemplate().query(LIST_PLAYCOUNT, new FileAlbumRowMapper());
        return customers;
    }

    public int updatePlayCount(Long pid, int playCount)  {
        Object[] params = {playCount, pid};
        return getInstance().getJDBCTemplate().update(RESET_PLAYCOUNT, params);

    }

    public class FileAlbumRowMapper implements RowMapper
    {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            MGOFileAlbumCompositeTO fileAlbumComposite = new MGOFileAlbumCompositeTO();
            fileAlbumComposite.setFileTO(mapFileTO(rs, rowNum));
            fileAlbumComposite.setAlbumArtistTO(mapAlbumAritstTO(rs, rowNum));
            fileAlbumComposite.setFileAlbumTO(mapFileAlbumTO(rs, rowNum));
            return fileAlbumComposite;
        }

    }

    public class FileAlbumRowMapperOld implements RowMapper
    {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            MGOFileAlbumCompositeTO fileAlbumComposite = new MGOFileAlbumCompositeTO();
            MGOFileTO fileTO = fileAlbumComposite.getFileTO();
            fileTO.setId(rs.getLong("ID"));
            fileTO.setTitle(rs.getString("TITLE").trim());
            fileTO.setTrack(rs.getInt("TRACK"));
            fileTO.setDisc(rs.getInt("DISC"));
            fileTO.setPlayCount(rs.getInt("PLAYCOUNT"));
            fileTO.setDateLastPlayed(SQLiteUtils.convertiPodDateToDate(rs.getLong("DATELASTPLAYED")));
            MGOFileArtistTO fileArtistTO = fileAlbumComposite.getFileArtistTO();
            fileArtistTO.setArtist(rs.getString("ARTIST").trim());
            MGOFileAlbumTO fileAlbumTO = fileAlbumComposite.getFileAlbumTO();
            fileAlbumTO.setName(rs.getString("ALBUMNAME").trim());
            MGOAlbumArtistTO albumArtistTO = fileAlbumComposite.getAlbumArtistTO();
            albumArtistTO.setName(rs.getString("ALBUM_ARTIST").trim());
            return fileAlbumComposite;
        }

    }

    private static MGOFileTO mapFileTO(ResultSet rs, int rowNum)throws SQLException{
        MGOFileTO fileTO = new MGOFileTO();
        fileTO.setId(getLong(rs, ItemColumns.ID));
        fileTO.setTitle(getString(rs, ItemColumns.TITLE).trim());
        fileTO.setTrack(getInteger(rs, ItemColumns.TRACK));
        fileTO.setDisc(getInteger(rs, ItemColumns.DISC));
        fileTO.setPlayCount(getInteger(rs, Item_StatsColumns.PLAYCOUNT));
        fileTO.setDateLastPlayed(SQLiteUtils.convertiPodDateToDate(getLong(rs, Item_StatsColumns.DATELASTPLAYED)));
        return fileTO;
    }

    private static MGOAlbumArtistTO mapAlbumAritstTO(ResultSet rs, int rowNum)throws SQLException{
        MGOAlbumArtistTO albumArtistTO = new MGOAlbumArtistTO();
        albumArtistTO.setName(getString(rs, ItemColumns.ALBUM_ARTIST));
        return albumArtistTO;
    }

    private static MGOFileAlbumTO mapFileAlbumTO(ResultSet rs, int rowNum)throws SQLException{
        MGOFileAlbumTO fileAlbumTO = new MGOFileAlbumTO();
        fileAlbumTO.setName(getString(rs, ItemColumns.ALBUM).trim());
        return fileAlbumTO;
    }

}
