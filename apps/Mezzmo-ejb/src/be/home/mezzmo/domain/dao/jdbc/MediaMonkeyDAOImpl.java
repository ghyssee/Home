package be.home.mezzmo.domain.dao.jdbc;

import be.home.common.dao.jdbc.MediaMonkeyDB;
import be.home.common.dao.jdbc.MezzmoDB;
import be.home.common.dao.jdbc.SQLiteUtils;
import be.home.common.model.TransferObject;
import be.home.mezzmo.domain.model.*;
import org.springframework.jdbc.core.RowMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ghyssee on 9/02/2016.
 */
public class MediaMonkeyDAOImpl extends MediaMonkeyDB {

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


    private static final String RESET_PLAYCOUNT = "UPDATE songs " +
                                                   "SET playcounter = 0, " +
                                                   "LASTTIMEPLAYED = 0 " +
                                                   "WHERE playcounter > 0";

    private static final String RESET_DEVICETRACKS = "UPDATE devicetracks " +
            "SET playcount = 0 " +
            "WHERE playcount > 0";

    public List<MGOFileAlbumCompositeTO> getTop20()
    {
        List<MGOFileAlbumCompositeTO> list  = getInstance().getJDBCTemplate().query(LIST_TOP20, new FileAlbumRowMapper());
        return list;
    }

    public int resetPlayCount(){
        getInstance().getJDBCTemplate().update(RESET_DEVICETRACKS);
        return getInstance().getJDBCTemplate().update(RESET_PLAYCOUNT);
    }

    public class FileAlbumRowMapper implements RowMapper
    {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            MGOFileAlbumCompositeTO fileAlbumComposite = new MGOFileAlbumCompositeTO();
            MGOFileTO fileTO = fileAlbumComposite.getFileTO();
            fileTO.setId(rs.getLong("FILE_ID"));
            fileTO.setFileTitle(rs.getString("FILETITLE"));
            fileTO.setFile(rs.getString("FILE"));
            fileTO.setTitle(rs.getString("TITLE"));
            fileTO.setPlayCount(rs.getInt("PLAYCOUNT"));
            fileTO.setDuration(rs.getInt("DURATION"));
            MGOFileArtistTO fileArtistTO = fileAlbumComposite.getFileArtistTO();
            fileArtistTO.setArtist(rs.getString("ARTIST"));
            MGOPlaylistTO playlistTO = fileAlbumComposite.getPlaylistTO();
            playlistTO.setID(rs.getInt("PLAYLIST_ID"));
            return fileAlbumComposite;
        }

    }

}
