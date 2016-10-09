package be.home.mezzmo.domain.dao.jdbc;

import be.home.common.dao.jdbc.MezzmoDB;
import be.home.common.dao.jdbc.QueryBuilder;
import be.home.common.dao.jdbc.SQLiteUtils;
import be.home.common.exceptions.MultipleOccurencesException;
import be.home.common.model.TransferObject;
import be.home.mezzmo.domain.model.*;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ghyssee on 9/02/2016.
 */
public class MezzmoDAOImpl extends MezzmoDB {

    private static final String FILE_SELECT = "select file from MGOFile where File like '%Boyzone%';";

    private static final String COLUMNS[] = {"MGOFile.ID as FILEID", "MGOFile.File as FILE",
            "MGOFile.PlayCount as PLAYCOUNT", "MGOFile.Ranking as RANKING",
            "MGOFile.FileTitle as FILETITLE", "MGOFile.DateLastPlayed as DATELASTPLAYED",
            "MGOFile.Ranking as RANKING",
            "MGOFileAlbum.ID as FILEALBUMID", "MGOFileAlbum.Data as ALBUMNAME" };

    private static final String COLUMNS_FILE[] = {"MGOFile.ID as FILEID", "MGOFile.File as FILE",
            "MGOFile.PlayCount as PLAYCOUNT", "MGOFile.Ranking as RANKING"};

    private static final String COLUMNS_ALBUMS[] = {"MGOFile.ID as FILEID", "MGOFile.File as FILE",
            "MGOFile.PlayCount as PLAYCOUNT", "MGOFile.Ranking as RANKING"};

    private static final String COLUMNS_MP3[] = {"MGOFile.ID as FILEID", "MGOFile.File as FILE",
            "MGOFile.Title as TITLE",
            "MGOFile.PlayCount as PLAYCOUNT", "MGOFile.Ranking as RANKING",
            "MGOFileArtist.DATA as ARTIST", "MGOFile.Duration as DURATION",
            "MGOFile.disc as DISC",
            "MGOFile.Track as TRACK", "MGOFile.Ranking as RANKING",
            "MGOFile.DateLastPlayed as DATELASTPLAYED"};

    private static final String FILEALBUM_SELECT = "SELECT " + getColumns(COLUMNS) + " FROM MGOFileAlbumRelationship " +
            " INNER JOIN MGOFile ON (MGOFileAlbumRelationship.FileID = MGOFile.ID)" +
            " INNER JOIN MGOFileAlbum ON (MGOFileAlbum.ID = MGOFileAlbumRelationship.ID)" +
            " WHERE 1=1" +
            " AND MGOFileAlbum.data like ?"; //"'Ultratop 50 2015%'";

    private static final String FILE_FIND_TAGINFO = "SELECT " + getColumns(COLUMNS) + " FROM MGOFile " +
            " INNER JOIN MGOFileAlbumRelationship ON (MGOFileAlbumRelationship.FileID = MGOFILE.id)" +
            " INNER JOIN MGOFileAlbum ON (MGOFileAlbum.ID = MGOFileAlbumRelationship.ID)" +
            " INNER JOIN MGOFileExtension ON (MGOFileExtension.ID = MGOFILE.extensionID)" +
            " INNER JOIN MGOFileArtistRelationship ON (MGOFileArtistRelationship.FileID = MGOFILE.id)" +
            " INNER JOIN MGOFileArtist ON (MGOFileArtist.ID = MGOFileArtistRelationship.ID)" +
            " INNER JOIN MGOAlbumArtistRelationship ON (MGOAlbumArtistRelationship.FileID = MGOFILE.id)" +
            " WHERE MGOFileExtension.data = 'mp3'" +
            " AND MGOFile.Track like ?" +
            " AND MGOFileArtist.data like ?" +
            " AND MGOFile.Title like ?" +
            " AND MGOFileAlbum.data like ?";

    private static final String FILE_SELECT_TITLE = "SELECT " + getColumns(COLUMNS) + " FROM MGOFileAlbumRelationship " +
            " INNER JOIN MGOFile ON (MGOFileAlbumRelationship.FileID = MGOFile.ID)" +
            " INNER JOIN MGOFileAlbum ON (MGOFileAlbum.ID = MGOFileAlbumRelationship.ID)" +
            " WHERE 1=1" +
            " AND MGOFile.FileTitle like ?" +
            " AND MGOFileAlbum.data like ?";

    private static final String FILE_FIND = "SELECT " + getColumns(COLUMNS) + " FROM MGOFileAlbumRelationship " +
            " INNER JOIN MGOFile ON (MGOFileAlbumRelationship.FileID = MGOFile.ID)" +
            " INNER JOIN MGOFileAlbum ON (MGOFileAlbum.ID = MGOFileAlbumRelationship.ID)" +
            " WHERE 1=1" +
            " AND MGOFile.FileTitle like ?" +
            " AND MGOFileAlbum.data like ?";

    private static final String FILE_PLAYCOUNT = "SELECT " + getColumns(COLUMNS) + " FROM MGOFileAlbumRelationship " +
            " INNER JOIN MGOFile ON (MGOFileAlbumRelationship.FileID = MGOFile.ID)" +
            " INNER JOIN MGOFileAlbum ON (MGOFileAlbum.ID = MGOFileAlbumRelationship.ID)" +
            " INNER JOIN MGOFileExtension ON (MGOFileExtension.ID = MGOFile.extensionID)" +
            " WHERE 1=1" +
            " AND MGOFileExtension.data = 'mp3'" +
            " AND MGOFile.PlayCount > 0" +
            " ORDER BY datetime(MGOFile.DateLastPlayed, 'unixepoch', 'localtime')  ASC" +
            " LIMIT ?,?";

    private static final String FILE_UPDATE_PLAYCOUNT = "UPDATE MGOFile " +
            " SET PlayCount = ? " +
            " ,DateLastPlayed = ? " +
            " WHERE FileTitle = ? " +
            " AND PlayCount < ? " +
            " AND ID IN (" +
            " SELECT FileID FROM MGOFileAlbumRelationship" +
            " INNER JOIN MGOFile ON (MGOFileAlbumRelationship.FileID = MGOFile.ID)" +
            " INNER JOIN MGOFileAlbum ON (MGOFileAlbum.ID = MGOFileAlbumRelationship.ID)" +
            " WHERE 1=1" +
            " AND MGOFileAlbum.data like ?" +
            " AND MGOFile.FileTitle like ?" +
            ")";

    private static final String FILE_SYNC_PLAYCOUNT = "UPDATE MGOFile " +
            " SET PlayCount = ? " +
            " WHERE ID = ?";

    private static final String LIST_ALBUMS = "SELECT DISTINCT MGOFileAlbum.data AS ALBUMNAME," +
                                " MGOAlbumArtist.data AS ALBUMARTISTNAME," +
                                " MGOFileAlbum.id AS ALBUMID," +
                                " MAX(MGOFile.Year) AS YEAR" +
                                " FROM MGOFileAlbumRelationship" +
                                " INNER JOIN MGOFileAlbum ON (MGOFileAlbum.ID = MGOFileAlbumRelationship.ID)" +
                                " INNER JOIN MGOAlbumArtistRelationship ON (MGOAlbumArtistRelationship.fileID = MGOFileAlbumRelationship.fileID)" +
                                " INNER JOIN MGOAlbumArtist ON (MGOAlbumArtist.ID = MGOAlbumArtistRelationship.ID)" +
                                " INNER JOIN MGOFile ON (MGOFile.ID = MGOAlbumArtistRelationship.fileID)" +
                                " INNER JOIN MGOFileExtension ON (MGOFileExtension.ID = MGOFILE.extensionID)" +
                                " WHERE 1=1" +
                                " AND MGOFileExtension.data = 'mp3'" +
                                " GROUP BY ALBUMNAME, ALBUMARTISTNAME, ALBUMID" +
                                " ORDER BY MGOFileAlbum.data";

    private static final String LIST_ALBUMS_TRACKS = "SELECT DISTINCT MGOFileAlbum.data AS ALBUMNAME," +
            " MGOAlbumArtist.id AS ALBUMARTISTID, " +
            " MGOAlbumArtist.data AS ALBUMARTISTNAME," +
            " MGOFileAlbum.id AS ALBUMID," +
            " MAX(MGOFile.Year) AS YEAR" +
//            " MGOFile.Track AS TRACK, " +
//            " MGOFileArtist.data AS ARTIST, " +
//            " MGOFile.title AS TITLE" +
            " FROM MGOFileAlbumRelationship" +
            " INNER JOIN MGOFileAlbum ON (MGOFileAlbum.ID = MGOFileAlbumRelationship.ID)" +
            " INNER JOIN MGOAlbumArtistRelationship ON (MGOAlbumArtistRelationship.fileID = MGOFileAlbumRelationship.fileID)" +
            " INNER JOIN MGOAlbumArtist ON (MGOAlbumArtist.ID = MGOAlbumArtistRelationship.ID)" +
            " INNER JOIN MGOFile ON (MGOFile.ID = MGOAlbumArtistRelationship.fileID)" +
            " INNER JOIN MGOFileExtension ON (MGOFileExtension.ID = MGOFILE.extensionID)" +
            " INNER JOIN MGOFileArtistRelationship ON (MGOFileArtistRelationship.fileID = MGOFile.id) " +
            " INNER JOIN MGOFileArtist ON (MGOFileArtist.id = MGOFileArtistRelationship.id)" +
            " WHERE 1=1" +
            " AND MGOFileExtension.data = 'mp3'" +
            " GROUP BY ALBUMNAME, ALBUMARTISTNAME, ALBUMID" +
            " ORDER BY MGOFileAlbum.data";

    private static final String LIST_TOP20 = "SELECT FileTitle AS FILETITLE, PlayCount AS PLAYCOUNT, Title AS TITLE, FA.DATA AS ARTIST, PLL.ID AS PLAYLIST_ID, MGoFile.ID AS FILE_ID, MGOFile.File AS FILE, MGOFile.duration AS DURATION" +
                                             " FROM MGOPlaylist_To_File AS PLF" +
                                             " INNER JOIN MGOFile ON (PLF.FileID = MGOFile.ID)" +
                                             " INNER JOIN MGOPlaylist AS PLL ON (PLF.PlayListID = PLL.ID)" +
                                             " INNER JOIN MGOFileArtistRelationship AS FAR ON (MGOFile.ID = FAR.FileID)" +
                                             " INNER JOIN MGOFileArtist AS FA ON (FAR.ID = FA.ID)" +
                                             " WHERE PLL.type = 32" +
                                             " AND PLL.Name = '11 Top Of The Moment'" +
                                             " ORDER BY PLF.rowid " +
                                             " LIMIT 0,20";

    private static final String FIND_BY_FILE = "SELECT ID AS FILEID " +
                                               "FROM MGOfile " +
                                               "WHERE UPPER(FILE) LIKE UPPER(?)";

    private static final String FIND_COVER_ART = "SELECT MGOFile.File AS FILE " +
                                                  "FROM MGOFileAlbumRelationship " +
                                                  "INNER JOIN MGOFileAlbum ON (MGOFileAlbum.ID = MGOFileAlbumRelationship.ID) " +
                                                  "INNER JOIN MGOFile ON (MGOFile.ID = MGOFileAlbumRelationship.fileID) " +
                                                  "INNER JOIN MGOFileExtension ON (MGOFileExtension.ID = MGOFILE.extensionID) " +
                                                  "WHERE 1=1 " +
                                                  "AND MGOFileExtension.data = 'mp3' " +
                                                  "AND MGOFileAlbum.id like ? " +
                                                  "LIMIT 0,1";

    private static final String LIST_CUSTOM = "SELECT MGOFile.File AS FILE, " +
                                                "MGOFile.FileTitle AS FILETITLE, " +
                                                "MGOFile.Duration AS DURATION, " +
                                                "MGOFile.Playcount AS PLAYCOUNT, " +
                                                "MGOFile.Title AS TITLE, " +
                                                "MGOFile.Id AS FILE_ID, " +
                                                "MGOFileAlbum.data AS ALBUM, " +
                                                "MGOFileArtist.data AS ARTIST " +
                                                "FROM MGOFileAlbumRelationship " +
                                                "INNER JOIN MGOFileAlbum ON (MGOFileAlbum.ID = MGOFileAlbumRelationship.ID) " +
                                                "INNER JOIN MGOAlbumArtistRelationship ON (MGOAlbumArtistRelationship.fileID = MGOFileAlbumRelationship.fileID) " +
                                                "INNER JOIN MGOAlbumArtist ON (MGOAlbumArtist.ID = MGOAlbumArtistRelationship.ID) " +
                                                "INNER JOIN MGOFile ON (MGOFile.ID = MGOAlbumArtistRelationship.fileID) " +
                                                "INNER JOIN MGOFileExtension ON (MGOFileExtension.ID = MGOFILE.extensionID) " +
                                                "INNER JOIN MGOFileArtistRelationship ON (MGOFileArtistRelationship.fileID = MGOFile.id) " +
                                                "INNER JOIN MGOFileArtist ON (MGOFileArtist.id = MGOFileArtistRelationship.id) " +
                                                "WHERE 1=1 " +
                                                "AND MGOFileExtension.data = 'mp3' " +
                                                "AND ( " +
                                                "{WHERE} " +
                                                ") " +
                                                "ORDER BY RANDOM() " +
                                                "LIMIT 0,? ";

    private static final String FILE_UPDATE_RATING = "UPDATE MGOFile " +
            " SET Ranking = ? " +
            " WHERE ID = ?";

    private static final String FIND_SONGS_ALBUM = "SELECT " + getColumns(COLUMNS_MP3) +
            " FROM MGOFile " +
            "INNER JOIN MGOFileExtension ON (MGOFileExtension.ID = MGOFILE.extensionID) " +
            "INNER JOIN MGOFileArtist ON (MGOFileArtist.ID = MGOFileArtistRelationship.ID) " +
            "INNER JOIN MGOFileArtistRelationship ON (MGOFileArtistRelationship.FileID = MGOFILE.ID) " +
            "INNER JOIN MGOFileAlbumRelationship ON (MGOFileAlbumRelationship.FileID = MGOFILE.ID) " +
            "INNER JOIN MGOFileAlbum ON (MGOFileAlbum.ID = MGOFileAlbumRelationship.ID) " +
            "INNER JOIN MGOAlbumArtistRelationship ON (MGOAlbumArtistRelationship.fileID = MGOFileAlbumRelationship.fileID) " +
            "INNER JOIN MGOAlbumArtist ON (MGOAlbumArtist.ID = MGOAlbumArtistRelationship.ID) " +
            "WHERE  MGOFileAlbum.id = ? " +
            "AND  MGOAlbumArtist.id = ? " +
            "AND MGOFileExtension.data = 'mp3' " +
            "ORDER BY MGOFile.disc, MGOFile.track";

    private static final Logger log = Logger.getLogger(MezzmoDAOImpl.class);


    public static String getColumns(String[] columns){
        String col = "";
        for (int i=0; i < columns.length; i++){
            if (i > 0){
                col += ", ";
            }
            col += columns[i];
        }
        return col;
    }



    public List<MGOFileTO> getFiles(MGOFileAlbumCompositeTO compSearchTO)
    {
        Object[] params = {compSearchTO.getFileTO().getFileTitle(), "%"};
        if (compSearchTO.getFileAlbumTO() != null && compSearchTO.getFileAlbumTO().getName() != null){
            params[1] = compSearchTO.getFileAlbumTO().getName();
        }
        List<MGOFileTO> list  = getInstance().getJDBCTemplate().query(FILE_SELECT_TITLE, new MezzmoRowMappers.FileRowMapper(), params);
        return list;
    }

    public int updatePlayCount(String fileID, String album, int playCount, java.util.Date dateLastPlayed) throws SQLException {

        Object[] params = {playCount, SQLiteUtils.convertDateToLong(dateLastPlayed), fileID, playCount,
                           album == null ? "%" : album, fileID
                          };
        return getInstance().getJDBCTemplate().update(FILE_SYNC_PLAYCOUNT, params);


    }

    public int synchronizePlayCount(Long fileID, int playCount) throws SQLException {
        Object[] params = {playCount, fileID};
        return getInstance().getJDBCTemplate().update(FILE_SYNC_PLAYCOUNT, params);
    }

    public List<MGOFileAlbumCompositeTO> getMP3FilesWithPlayCount(TransferObject to)
    {
        Object[] params = {to.getIndex(), to.getLimit()};
        List<MGOFileAlbumCompositeTO> list  = getInstance().getJDBCTemplate().query(FILE_PLAYCOUNT, new MezzmoRowMappers.FileAlbumPlayCountMapper(), params);
        if (list.size() == 0 || list.size() < to.getLimit()){
            to.setEndOfList(true);
        }
        to.addIndex(list.size());
        return list;
    }

    public List<MGOFileAlbumCompositeTO> getAlbums(TransferObject to)
    {
        List<MGOFileAlbumCompositeTO> list  = getInstance().getJDBCTemplate().query(LIST_ALBUMS, new MezzmoRowMappers.AlbumRowMapper());
        return list;
    }


    public List<MGOFileAlbumCompositeTO> getAlbumTracks(TransferObject to)
    {
        List<MGOFileAlbumCompositeTO> list  = getInstance().getJDBCTemplate().query(LIST_ALBUMS_TRACKS, new MezzmoRowMappers.AlbumTrackRowMapper());
        return list;
    }

    public List<MGOFileAlbumCompositeTO> getTop20()
    {
        List<MGOFileAlbumCompositeTO> list  = getInstance().getJDBCTemplate().query(LIST_TOP20, new MezzmoRowMappers.FileAlbumRowMapper());
        return list;

    }

    public MGOFileTO findByFile(String file) {
        Object[] params = {file};
        MGOFileTO fileTO = (MGOFileTO) getInstance().getJDBCTemplate().queryForObject(FIND_BY_FILE, new MezzmoRowMappers.FileIdRowMapper(), params);
        return fileTO;
    }

    public MGOFileTO findByTitleAndAlbum(MGOFileAlbumCompositeTO comp){
        Object[] params = {comp.getFileTO().getTrack(), comp.getFileArtistTO().getArtist(), comp.getFileTO().getTitle(), comp.getFileAlbumTO().getName() };
        MGOFileTO fileTO = (MGOFileTO) getInstance().getJDBCTemplate().queryForObject(FILE_FIND_TAGINFO, new MezzmoRowMappers.FileRowMapper(), params);
        return fileTO;
    }

    public MGOFileTO findCoverArt(int albumId){
        Object[] params = {albumId };
        MGOFileTO fileTO = (MGOFileTO) getInstance().getJDBCTemplate().queryForObject(FIND_COVER_ART, new MezzmoRowMappers.FileNameRowMapper(), params);
        return fileTO;
    }

    public List<MGOFileAlbumCompositeTO> getCustomPlayListSongs(List <MGOFileAlbumCompositeTO> albums, int limit)
    {
        String query = LIST_CUSTOM;
        String orClause = "(MGOFileAlbum.data like ? AND MGOFile.ranking > ? AND MGOAlbumArtist.data like ? AND MGOFile.playcount < 2) ";
        query = QueryBuilder.buildOrCondition(query, orClause, albums);

        List params = new ArrayList();
        for (MGOFileAlbumCompositeTO album : albums){
            params.add(album.getFileAlbumTO().getName());
            params.add(0L);
            String albumArtist = album.getAlbumArtistTO().getName();
            params.add(albumArtist == null ? "%" : albumArtist);
        }
        params.add(limit);

        List<MGOFileAlbumCompositeTO>  list = getInstance().getJDBCTemplate().query(query, new MezzmoRowMappers.CustomAlbumRowMapper(), params.toArray());
        return list;

    }

    public List<MGOFileAlbumCompositeTO> getSongsAlbum(Long albumId, Long albumArtistId)
    {
        String query = FIND_SONGS_ALBUM;
        Object[] params = {albumId, albumArtistId};

        List<MGOFileAlbumCompositeTO>  list = getInstance().getJDBCTemplate().query(query, new MezzmoRowMappers.SongsAlbumRowMapper(), params);
        return list;

    }

    public int updateRanking(Long fileID, int ranking) throws SQLException {
        Object[] params = {ranking, fileID};
        return getInstance().getJDBCTemplate().update(FILE_UPDATE_RATING, params);
    }



}
