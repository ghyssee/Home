package be.home.mezzmo.domain.dao.jdbc;

import be.home.common.dao.jdbc.*;
import be.home.common.model.TransferObject;
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

    private static final String LIST_PLAYCOUNT = "SELECT " +
            "item.pid ID, title TITLE, artist ARTIST, album ALBUM, track_number TRACK, disc_number DISC, ITEM_STATS.play_count_user PLAYCOUNT, " +
            "ITEM_STATS.date_played DATELASTPLAYED, ALBUM.pid ALBUMID, ALBUM.name ALBUMNAME " +
            "FROM item " +
            "LEFT OUTER JOIN DYNAMIC.item_stats ITEM_STATS ON (item.pid = ITEM_STATS.item_pid) " +
            "LEFT OUTER JOIN album ALBUM ON (item.album_pid = ALBUM.pid) " +
            "WHERE ITEM_STATS.play_count_user > 0 " +
            "ORDER BY ITEM_STATS.date_played ";

    private static final String RESET_PLAYCOUNT = "UPDATE DYNAMIC.item_stats " +
                                                  "SET play_count_user = ? " +
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
            MGOFileTO fileTO = fileAlbumComposite.getFileTO();
            fileTO.setId(rs.getLong("ID"));
            fileTO.setTitle(rs.getString("TITLE"));
            fileTO.setTrack(rs.getInt("TRACK"));
            fileTO.setDisc(rs.getInt("DISC"));
            fileTO.setPlayCount(rs.getInt("PLAYCOUNT"));
            fileTO.setDateLastPlayed(SQLiteUtils.convertiPodDateToDate(rs.getLong("DATELASTPLAYED")));
            MGOFileArtistTO fileArtistTO = fileAlbumComposite.getFileArtistTO();
            fileArtistTO.setArtist(rs.getString("ARTIST"));
            MGOFileAlbumTO fileAlbumTO = fileAlbumComposite.getFileAlbumTO();
            fileAlbumTO.setName(rs.getString("ALBUMNAME"));
            return fileAlbumComposite;
        }

    }

}
