package be.home.mezzmo.domain.dao.jdbc;

import be.home.common.dao.jdbc.MezzmoDB;

/**
 * Created by Gebruiker on 8/12/2016.
 */
public class MezzmoDAOQueries extends MezzmoDB {
    protected static final String FILE_SELECT = "select file from Tables where File like '%Boyzone%';";

    protected static final String COLUMNS[] = {"Tables.ID as FILEID", "Tables.File as FILE",
            "Tables.PlayCount as PLAYCOUNT", "Tables.Ranking as RANKING",
            "Tables.FileTitle as FILETITLE", "Tables.DateLastPlayed as DATELASTPLAYED",
            "Tables.Ranking as RANKING",
            "MGOFileAlbum.ID as ALBUMID", "MGOFileAlbum.Data as ALBUMNAME" };

    protected static final String COLUMNS_FILE[] = {"Tables.ID as FILEID", "Tables.File as FILE",
            "Tables.PlayCount as PLAYCOUNT", "Tables.Ranking as RANKING"};

    protected static final String COLUMNS_ALBUMS[] = {"Tables.ID as FILEID", "Tables.File as FILE",
            "Tables.PlayCount as PLAYCOUNT", "Tables.Ranking as RANKING"};

    protected static final String COLUMNS_MP3[] = {
            "Tables.ID as FILEID",
            "Tables.File as FILE",
            "Tables.Title as TITLE",
            "Tables.FileTitle as FILETITLE",
            "Tables.PlayCount as PLAYCOUNT",
            "Tables.Ranking as RANKING",
            "MGOFileArtist.ID as ARTISTID",
            "MGOFileArtist.DATA as ARTIST",
            "Tables.Duration as DURATION",
            "Tables.disc as DISC",
            "Tables.Track as TRACK",
            "MGOFileAlbum.ID as ALBUMID",
            "MGOFileAlbum.Data as ALBUMNAME",
            "Tables.DateLastPlayed as DATELASTPLAYED"};

    protected static final String FILEALBUM_SELECT = "SELECT " + getColumns(COLUMNS) + " FROM MGOFileAlbumRelationship " +
            " INNER JOIN Tables ON (MGOFileAlbumRelationship.FileID = Tables.ID)" +
            " INNER JOIN MGOFileAlbum ON (MGOFileAlbum.ID = MGOFileAlbumRelationship.ID)" +
            " WHERE 1=1" +
            " AND MGOFileAlbum.data like ?"; //"'Ultratop 50 2015%'";

    private static final String FILE_FIND_TAGINFO = "SELECT " + getColumns(COLUMNS_MP3) + " FROM Tables " +
            " INNER JOIN MGOFileAlbumRelationship ON (MGOFileAlbumRelationship.FileID = MGOFILE.id)" +
            " INNER JOIN MGOFileAlbum ON (MGOFileAlbum.ID = MGOFileAlbumRelationship.ID)" +
            " INNER JOIN MGOFileExtension ON (MGOFileExtension.ID = MGOFILE.extensionID)" +
            " INNER JOIN MGOFileArtistRelationship ON (MGOFileArtistRelationship.FileID = MGOFILE.id)" +
            " INNER JOIN MGOFileArtist ON (MGOFileArtist.ID = MGOFileArtistRelationship.ID)" +
            " INNER JOIN MGOAlbumArtistRelationship ON (MGOAlbumArtistRelationship.FileID = MGOFILE.id)" +
            " WHERE MGOFileExtension.data = 'mp3'";

    protected static final String FILE_FIND_TAGINFO_CRITERIA = FILE_FIND_TAGINFO +
            " AND Tables.Track like ?" +
            " AND MGOFileArtist.data like ?" +
            " AND Tables.Title like ?" +
            " AND MGOFileAlbum.data like ?";

    protected static final String FILE_FIND_TAGINFO_BY_ALBUMID = FILE_FIND_TAGINFO +
            " AND MGOFileAlbum.id like ?";

    protected static final String FILE_SELECT_TITLE = "SELECT " + getColumns(COLUMNS) + " FROM MGOFileAlbumRelationship " +
            " INNER JOIN Tables ON (MGOFileAlbumRelationship.FileID = Tables.ID)" +
            " INNER JOIN MGOFileAlbum ON (MGOFileAlbum.ID = MGOFileAlbumRelationship.ID)" +
            " WHERE 1=1" +
            " AND Tables.FileTitle like ?" +
            " AND MGOFileAlbum.data like ?";

    protected static final String FILE_PLAYCOUNT = "SELECT " + getColumns(COLUMNS) + " FROM MGOFileAlbumRelationship " +
            " INNER JOIN Tables ON (MGOFileAlbumRelationship.FileID = Tables.ID)" +
            " INNER JOIN MGOFileAlbum ON (MGOFileAlbum.ID = MGOFileAlbumRelationship.ID)" +
            " INNER JOIN MGOFileExtension ON (MGOFileExtension.ID = Tables.extensionID)" +
            " WHERE 1=1" +
            " AND MGOFileExtension.data = 'mp3'" +
            " AND Tables.PlayCount > 0" +
            " ORDER BY datetime(Tables.DateLastPlayed, 'unixepoch', 'localtime')  ASC" +
            " LIMIT ?,?";

    protected static final String FILE_UPDATE_PLAYCOUNT = "UPDATE Tables " +
            " SET PlayCount = ? " +
            " ,DateLastPlayed = ? " +
            " WHERE FileTitle like ? " +
            " AND PlayCount < ? " +
            " AND ID IN (" +
            " SELECT FileID FROM MGOFileAlbumRelationship" +
            " INNER JOIN Tables ON (MGOFileAlbumRelationship.FileID = Tables.ID)" +
            " INNER JOIN MGOFileAlbum ON (MGOFileAlbum.ID = MGOFileAlbumRelationship.ID)" +
            " WHERE 1=1" +
            " AND MGOFileAlbum.data like ?" +
            " AND Tables.FileTitle like ?" +
            ")";

    protected static final String FILE_SYNC_PLAYCOUNT = "UPDATE Tables " +
            " SET PlayCount = ?" +
            " ,DateLastPlayed = ?" +
            " WHERE ID = ?";

    protected static final String LIST_ALBUMS = "SELECT DISTINCT MGOFileAlbum.data AS ALBUMNAME," +
            " MGOAlbumArtist.data AS ALBUMARTISTNAME," +
            " MGOFileAlbum.id AS ALBUMID," +
            " MAX(Tables.Year) AS YEAR" +
            " FROM MGOFileAlbumRelationship" +
            " INNER JOIN MGOFileAlbum ON (MGOFileAlbum.ID = MGOFileAlbumRelationship.ID)" +
            " INNER JOIN MGOAlbumArtistRelationship ON (MGOAlbumArtistRelationship.fileID = MGOFileAlbumRelationship.fileID)" +
            " INNER JOIN MGOAlbumArtist ON (MGOAlbumArtist.ID = MGOAlbumArtistRelationship.ID)" +
            " INNER JOIN Tables ON (Tables.ID = MGOAlbumArtistRelationship.fileID)" +
            " INNER JOIN MGOFileExtension ON (MGOFileExtension.ID = MGOFILE.extensionID)" +
            " WHERE 1=1" +
            " AND MGOFileExtension.data = 'mp3'" +
            " AND MGOFileAlbum.data like ?" +
            " GROUP BY ALBUMNAME, ALBUMARTISTNAME, ALBUMID" +
            " ORDER BY MGOFileAlbum.data";

    protected static final String LIST_ALBUMS_TRACKS = "SELECT DISTINCT MGOFileAlbum.data AS ALBUMNAME," +
            " MGOAlbumArtist.id AS ALBUMARTISTID, " +
            " MGOAlbumArtist.data AS ALBUMARTISTNAME," +
            " MGOFileAlbum.id AS ALBUMID," +
            " MAX(Tables.Year) AS YEAR" +
//            " Tables.Track AS TRACK, " +
//            " MGOFileArtist.data AS ARTIST, " +
//            " Tables.title AS TITLE" +
            " FROM MGOFileAlbumRelationship" +
            " INNER JOIN MGOFileAlbum ON (MGOFileAlbum.ID = MGOFileAlbumRelationship.ID)" +
            " INNER JOIN MGOAlbumArtistRelationship ON (MGOAlbumArtistRelationship.fileID = MGOFileAlbumRelationship.fileID)" +
            " INNER JOIN MGOAlbumArtist ON (MGOAlbumArtist.ID = MGOAlbumArtistRelationship.ID)" +
            " INNER JOIN Tables ON (Tables.ID = MGOAlbumArtistRelationship.fileID)" +
            " INNER JOIN MGOFileExtension ON (MGOFileExtension.ID = MGOFILE.extensionID)" +
            " INNER JOIN MGOFileArtistRelationship ON (MGOFileArtistRelationship.fileID = Tables.id) " +
            " INNER JOIN MGOFileArtist ON (MGOFileArtist.id = MGOFileArtistRelationship.id)" +
            " WHERE 1=1" +
            " AND MGOFileExtension.data = 'mp3'" +
            " GROUP BY ALBUMNAME, ALBUMARTISTNAME, ALBUMID" +
            " ORDER BY MGOFileAlbum.data";

    protected static final String LIST_TOP20 = "SELECT FileTitle AS FILETITLE, PlayCount AS PLAYCOUNT, Title AS TITLE, FA.DATA AS ARTIST, PLL.ID AS PLAYLIST_ID, MGoFile.ID AS FILE_ID, Tables.File AS FILE, Tables.duration AS DURATION" +
            " FROM MGOPlaylist_To_File AS PLF" +
            " INNER JOIN Tables ON (PLF.FileID = Tables.ID)" +
            " INNER JOIN MGOPlaylist AS PLL ON (PLF.PlayListID = PLL.ID)" +
            " INNER JOIN MGOFileArtistRelationship AS FAR ON (Tables.ID = FAR.FileID)" +
            " INNER JOIN MGOFileArtist AS FA ON (FAR.ID = FA.ID)" +
            " WHERE PLL.type = 32" +
            " AND PLL.Name = '11 Top Of The Moment'" +
            " ORDER BY PLF.rowid " +
            " LIMIT 0,20";

    protected static final String FIND_BY_FILE = "SELECT ID AS FILEID " +
            "FROM MGOfile " +
            "WHERE UPPER(FILE) LIKE UPPER(?)";

    protected static final String FIND_COVER_ART = "SELECT Tables.File AS FILE " +
            "FROM MGOFileAlbumRelationship " +
            "INNER JOIN MGOFileAlbum ON (MGOFileAlbum.ID = MGOFileAlbumRelationship.ID) " +
            "INNER JOIN Tables ON (Tables.ID = MGOFileAlbumRelationship.fileID) " +
            "INNER JOIN MGOFileExtension ON (MGOFileExtension.ID = MGOFILE.extensionID) " +
            "WHERE 1=1 " +
            "AND MGOFileExtension.data = 'mp3' " +
            "AND MGOFileAlbum.id like ? " +
            "LIMIT 0,1";

    protected static final String LIST_CUSTOM = "SELECT Tables.File AS FILE, " +
            "Tables.FileTitle AS FILETITLE, " +
            "Tables.Duration AS DURATION, " +
            "Tables.Playcount AS PLAYCOUNT, " +
            "Tables.Title AS TITLE, " +
            "Tables.Id AS FILE_ID, " +
            "MGOFileAlbum.data AS ALBUM, " +
            "MGOFileArtist.data AS ARTIST " +
            "FROM MGOFileAlbumRelationship " +
            "INNER JOIN MGOFileAlbum ON (MGOFileAlbum.ID = MGOFileAlbumRelationship.ID) " +
            "INNER JOIN MGOAlbumArtistRelationship ON (MGOAlbumArtistRelationship.fileID = MGOFileAlbumRelationship.fileID) " +
            "INNER JOIN MGOAlbumArtist ON (MGOAlbumArtist.ID = MGOAlbumArtistRelationship.ID) " +
            "INNER JOIN Tables ON (Tables.ID = MGOAlbumArtistRelationship.fileID) " +
            "INNER JOIN MGOFileExtension ON (MGOFileExtension.ID = MGOFILE.extensionID) " +
            "INNER JOIN MGOFileArtistRelationship ON (MGOFileArtistRelationship.fileID = Tables.id) " +
            "INNER JOIN MGOFileArtist ON (MGOFileArtist.id = MGOFileArtistRelationship.id) " +
            "WHERE 1=1 " +
            "AND MGOFileExtension.data = 'mp3' " +
            "AND ( " +
            "{WHERE} " +
            ") " +
            "ORDER BY RANDOM() " +
            "LIMIT 0,? ";

    protected static final String FILE_UPDATE_RATING = "UPDATE Tables " +
            " SET Ranking = ? " +
            " WHERE ID = ?";

    protected static final String FIND_SONGS_ALBUM = "SELECT " + getColumns(COLUMNS_MP3) +
            " FROM Tables " +
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
            "ORDER BY Tables.disc, Tables.track";

    protected static final String FIND_LAST_PLAYED = "SELECT " + getColumns(COLUMNS_MP3) +
            " FROM Tables " +
            "INNER JOIN MGOFileExtension ON (MGOFileExtension.ID = MGOFILE.extensionID) " +
            "INNER JOIN MGOFileArtist ON (MGOFileArtist.ID = MGOFileArtistRelationship.ID) " +
            "INNER JOIN MGOFileArtistRelationship ON (MGOFileArtistRelationship.FileID = MGOFILE.ID) " +
            "INNER JOIN MGOFileAlbumRelationship ON (MGOFileAlbumRelationship.FileID = MGOFILE.ID) " +
            "INNER JOIN MGOFileAlbum ON (MGOFileAlbum.ID = MGOFileAlbumRelationship.ID) " +
            "INNER JOIN MGOAlbumArtistRelationship ON (MGOAlbumArtistRelationship.fileID = MGOFileAlbumRelationship.fileID) " +
            "INNER JOIN MGOAlbumArtist ON (MGOAlbumArtist.ID = MGOAlbumArtistRelationship.ID) " +
            "WHERE MGOFileExtension.data = 'mp3' " +
            "ORDER BY mgofile.datelastplayed DESC " +
            "LIMIT 0,? ";


    protected static final String MAX_DISC = "SELECT max (MGOFileAlbum.data) ALBUM,  max (MGOAlbumArtist.data) ALBUM_ARTIST, max(Tables.disc) DISC " +
            "FROM Tables " +
            "INNER JOIN MGOFileAlbumRelationship ON (MGOFileAlbumRelationship.FileID = MGOFILE.id) " +
            "INNER JOIN MGOFileAlbum ON (MGOFileAlbum.ID = MGOFileAlbumRelationship.ID) " +
            "INNER JOIN MGOAlbumArtistRelationship ON (MGOAlbumArtistRelationship.fileID = MGOFileAlbumRelationship.fileID) " +
            "INNER JOIN MGOAlbumArtist ON (MGOAlbumArtist.ID = MGOAlbumArtistRelationship.ID) " +
            "INNER JOIN MGOFileExtension ON (MGOFileExtension.ID = MGOFILE.extensionID) " +
            "WHERE MGOFileExtension.data = 'mp3' " +
            "AND Tables.disc >= 9 " +
            "GROUP BY MGOFileAlbum.data, MGOAlbumArtist.data ";


    protected static final String FILE_UPDATE_ARTIST = "UPDATE MGOFileArtist " +
            " SET data = ?" +
            " WHERE ID = ?";

    protected static final String FILE_UPDATE_TITLE = "UPDATE Tables " +
            " SET title = ?, " +
            " sortTitle = ? " +
            " WHERE ID = ?";

    protected static final String FILE_UPDATE_TRACK = "UPDATE Tables " +
            " SET track = ?" +
            " WHERE ID = ?";

    protected static final String FILE_UPDATE_FILE = "UPDATE Tables " +
            " SET file = ?," +
            " fileTitle = ? " +
            " WHERE ID = ?";

    protected static final String FIND_ARTIST = "SELECT ID, DATA " +
            " FROM MGOFileArtist " +
            " WHERE data = ? " +
            "  COLLATE BINARY"; // case sensitive search
            //" AND ID <> ?";

    protected static final String UPDATE_LINK_FILE_ARTIST = "UPDATE MGOFILEARTISTRELATIONSHIP " +
            " SET ID = ? " +
            " WHERE ID = ?";

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

}
