package be.home.mezzmo.domain.dao.jdbc;

import be.home.common.dao.jdbc.MediaMonkeyDB;
import be.home.common.dao.jdbc.MezzmoDB;
import be.home.common.dao.jdbc.SQLiteUtils;
import be.home.common.database.sqlbuilder.*;
import be.home.common.model.TransferObject;
import be.home.mezzmo.domain.dao.definition.*;
import be.home.mezzmo.domain.dao.definition.mediamonkey.*;
import be.home.mezzmo.domain.model.*;
import org.springframework.jdbc.core.RowMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ghyssee on 9/02/2016.
 */
public class MediaMonkeyDAOImpl extends MediaMonkeyDB {

    private static final String LIST_TOP20 = SQLBuilder.getInstance()
            .select()
            .addTable(MediaMonkeyTables.Songs)
            .addColumns(MediaMonkeyTables.Songs)
            .addRelation(MediaMonkeyTables.PlaylistSongs, PlaylistSongsColumns.SONGID, MediaMonkeyTables.Songs, SongsColumns.ID)
            .addRelation(MediaMonkeyTables.Playlists, PlaylistsColumns.PLAYLISTID, MediaMonkeyTables.PlaylistSongs, PlaylistSongsColumns.PLAYLISTID)
            .addCondition(MediaMonkeyTables.Playlists.alias(), PlaylistsColumns.PLAYLISTNAME, Comparator.LIKE, "Top Of The Moment")
            .addCondition(MediaMonkeyTables.Playlists.alias(), PlaylistsColumns.PARENTPLAYLIST, Comparator.LIKE, 0)
            .orderBy(MediaMonkeyTables.PlaylistSongs, PlaylistSongsColumns.SONGORDER, SortOrder.ASC)
            .limitBy(0,20)
            .render();


    private static final String LIST_TOP20_OLD = "SELECT  SongTitle AS FILETITLE, " +
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

    private static final String RESET_PLAYCOUNT_OLD = "UPDATE songs " +
                                                   "SET playcounter = 0, " +
                                                   "LASTTIMEPLAYED = 0 " +
                                                   "WHERE playcounter > 0";

    private static final String RESET_PLAYCOUNT = SQLBuilder.getInstance()
            .update()
            .addTable(MediaMonkeyTables.Songs)
            .updateColumn(SongsColumns.PLAYCOUNT, Type.VALUE, 0)
            .updateColumn(SongsColumns.LASTTIMEPLAYED, Type.VALUE, 0)
            .addCondition(SongsColumns.PLAYCOUNT, Comparator.GREATER, 0)
            .render();

    private static final String RESET_DEVICETRACKS_OLD = "UPDATE devicetracks " +
            "SET playcount = 0 " +
            "WHERE playcount > 0";

    private static final String RESET_DEVICETRACKS = SQLBuilder.getInstance()
            .update()
            .addTable(MediaMonkeyTables.DeviceTracks)
            .updateColumn(DeviceTracksColumns.PLAYCOUNT, Type.VALUE, 0)
            .addCondition(DeviceTracksColumns.PLAYCOUNT, Comparator.GREATER, 0)
            .render();

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
            fileAlbumComposite.setFileTO(mapFileTO(rs, rowNum));
            fileAlbumComposite.setFileArtistTO(mapFileArtistTO(rs, rowNum));
            return fileAlbumComposite;
        }

    }

    private static MGOFileTO mapFileTO(ResultSet rs, int rowNum)throws SQLException{
        MGOFileTO fileTO = new MGOFileTO();
        fileTO.setId(getLong(rs, SongsColumns.ID));
        fileTO.setFileTitle(getString(rs, SongsColumns.FILETITLE));
        fileTO.setFile(getString(rs, SongsColumns.FILE));
        fileTO.setTitle(getString(rs, SongsColumns.TITLE));
        fileTO.setPlayCount(getInteger(rs, SongsColumns.PLAYCOUNT));
        fileTO.setDuration(getInteger(rs, SongsColumns.DURATION));
        return fileTO;
    }

    private static MGOFileArtistTO mapFileArtistTO(ResultSet rs, int rowNum)throws SQLException{
        MGOFileArtistTO fileArtistTO = new MGOFileArtistTO();
        fileArtistTO.setArtist(getString(rs, SongsColumns.ARTIST));
        return fileArtistTO;
    }

    public class FileAlbumRowMapperOld implements RowMapper
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
