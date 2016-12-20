package be.home.mezzmo.domain.dao.jdbc;

import be.home.common.dao.jdbc.MezzmoDB;
import be.home.mezzmo.domain.dao.SQLBuilder;

/**
 * Created by Gebruiker on 8/12/2016.
 */
public class MezzmoDAOQueries extends MezzmoDB {
    protected static final String FILE_SELECT = "select file from MGOFile where File like '%Boyzone%';";

    protected static final String COLUMNS[] = {"MGOFile.ID as FILEID", "MGOFile.File as FILE",
            "MGOFile.PlayCount as PLAYCOUNT", "MGOFile.Ranking as RANKING",
            "MGOFile.FileTitle as FILETITLE", "MGOFile.DateLastPlayed as DATELASTPLAYED",
            "MGOFile.Ranking as RANKING",
            "MGOFileAlbum.ID as ALBUMID", "MGOFileAlbum.Data as ALBUMNAME" };

    protected static final String COLUMNS_FILE[] = {"MGOFile.ID as FILEID", "MGOFile.File as FILE",
            "MGOFile.PlayCount as PLAYCOUNT", "MGOFile.Ranking as RANKING"};

    protected static final String COLUMNS_ALBUMS[] = {"MGOFile.ID as FILEID", "MGOFile.File as FILE",
            "MGOFile.PlayCount as PLAYCOUNT", "MGOFile.Ranking as RANKING"};

    protected static final String COLUMNS_MP3[] = {
            "MGOFile.ID as FILEID",
            "MGOFile.File as FILE",
            "MGOFile.Title as TITLE",
            "MGOFile.FileTitle as FILETITLE",
            "MGOFile.PlayCount as PLAYCOUNT",
            "MGOFile.Ranking as RANKING",
            "MGOFileArtist.ID as ARTISTID",
            "MGOFileArtist.DATA as ARTIST",
            "MGOFile.Duration as DURATION",
            "MGOFile.disc as DISC",
            "MGOFile.Track as TRACK",
            "MGOFileAlbum.ID as ALBUMID",
            "MGOFileAlbum.Data as ALBUMNAME",
            "MGOFile.DateLastPlayed as DATELASTPLAYED"};

    private static final String FILEALBUM_SELECTVeryOld = SQLBuilder.getInstance()
            .select()
            .addTable(TablesEnum.MGOFile)
            .addColumns(TablesEnum.MGOFile)
            .addColumns(TablesEnum.MGOFileAlbum)
            .addRelation(TablesEnum.MGOFileAlbum, MGOFileAlbumColumns.ALBUMID, TablesEnum.MGOFileAlbumRelationship, MGOFileAlbumRelationshipColumns.ID)
            .addRelation(TablesEnum.MGOFileAlbumRelationship, MGOFileAlbumRelationshipColumns.FILEID, TablesEnum.MGOFile, MGOFileColumns.ID)
            .addCondition(TablesEnum.MGOFileAlbum.alias(), MGOFileAlbumColumns.ALBUM, SQLBuilder.Comparator.LIKE)
            .render();

    private static final String FILE_FIND_TAGINFO = "SELECT " + getColumns(COLUMNS_MP3) + " FROM MGOFile " +
            " INNER JOIN MGOFileAlbumRelationship ON (MGOFileAlbumRelationship.FileID = MGOFILE.id)" +
            " INNER JOIN MGOFileAlbum ON (MGOFileAlbum.ID = MGOFileAlbumRelationship.ID)" +
            " INNER JOIN MGOFileExtension ON (MGOFileExtension.ID = MGOFILE.extensionID)" +
            " INNER JOIN MGOFileArtistRelationship ON (MGOFileArtistRelationship.FileID = MGOFILE.id)" +
            " INNER JOIN MGOFileArtist ON (MGOFileArtist.ID = MGOFileArtistRelationship.ID)" +
            " INNER JOIN MGOAlbumArtistRelationship ON (MGOAlbumArtistRelationship.FileID = MGOFILE.id)" +
            " WHERE MGOFileExtension.data = 'mp3'";

    public static SQLBuilder FILE_FIND_BASIC = SQLBuilder.getInstance()
            .select()
            .addTable(TablesEnum.MGOFile)
            .addColumns(TablesEnum.MGOFile)
            .addColumns(TablesEnum.MGOFileAlbum)
            .addColumns(TablesEnum.MGOFileArtist)
            .addRelation(TablesEnum.MGOFileAlbum, MGOFileAlbumColumns.ALBUMID, TablesEnum.MGOFileAlbumRelationship, MGOFileAlbumRelationshipColumns.ID)
            .addRelation(TablesEnum.MGOFileAlbumRelationship, MGOFileAlbumRelationshipColumns.FILEID, TablesEnum.MGOFile, MGOFileColumns.ID)
            .addRelation(TablesEnum.MGOFileExtension, MGOFileExtensionColumns.ID, TablesEnum.MGOFile, MGOFileColumns.EXTENSION_ID)
            .addRelation(TablesEnum.MGOFileArtist, MGOFileArtistColumns.ARTISTID, TablesEnum.MGOFileArtistRelationship, MGOFileArtistRelationshipColumns.ID)
            .addRelation(TablesEnum.MGOFileArtistRelationship, MGOFileArtistRelationshipColumns.FILEID, TablesEnum.MGOFile, MGOFileColumns.ID)
            .addCondition(TablesEnum.MGOFileExtension.alias(), MGOFileExtensionColumns.DATA, SQLBuilder.Comparator.EQUALS, "mp3");

    protected static final String FILE_FIND_TAGINFO_CRITERIA = FILE_FIND_TAGINFO +
            " AND MGOFile.Track like ?" +
            " AND MGOFileArtist.data like ?" +
            " AND MGOFile.Title like ?" +
            " AND MGOFileAlbum.data like ?";

    protected static final String FILE_FIND_TAGINFO_BY_ALBUMID = FILE_FIND_TAGINFO +
            " AND MGOFileAlbum.id like ?";

    protected static final String FILE_SELECT_TITLE = "SELECT " + getColumns(COLUMNS) + " FROM MGOFileAlbumRelationship " +
            " INNER JOIN MGOFile ON (MGOFileAlbumRelationship.FileID = MGOFile.ID)" +
            " INNER JOIN MGOFileAlbum ON (MGOFileAlbum.ID = MGOFileAlbumRelationship.ID)" +
            " WHERE 1=1" +
            " AND MGOFile.FileTitle like ?" +
            " AND MGOFileAlbum.data like ?";

    public static final String FILE_SELECT_TITLE2 = FILE_FIND_BASIC
            .addCondition(TablesEnum.MGOFile.alias(), MGOFileColumns.FILETITLE, SQLBuilder.Comparator.LIKE)
            .addCondition(TablesEnum.MGOFileAlbum.alias(), MGOFileAlbumColumns.ALBUM, SQLBuilder.Comparator.LIKE)
            .render();

    protected static final String FILE_PLAYCOUNT_OLD = "SELECT " + getColumns(COLUMNS) + " FROM MGOFileAlbumRelationship " +
            " INNER JOIN MGOFile ON (MGOFileAlbumRelationship.FileID = MGOFile.ID)" +
            " INNER JOIN MGOFileAlbum ON (MGOFileAlbum.ID = MGOFileAlbumRelationship.ID)" +
            " INNER JOIN MGOFileExtension ON (MGOFileExtension.ID = MGOFile.extensionID)" +
            " WHERE 1=1" +
            " AND MGOFileExtension.data = 'mp3'" +
            " AND MGOFile.PlayCount > 0" +
            " ORDER BY datetime(MGOFile.DateLastPlayed, 'unixepoch', 'localtime')  ASC" +
            " LIMIT ?,?";

    public static final String FILE_PLAYCOUNT = SQLBuilder.getInstance()
            .select()
            .addTable(TablesEnum.MGOFile)
            .addColumns(TablesEnum.MGOFile)
            .addColumns(TablesEnum.MGOFileAlbum)
            .addColumns(TablesEnum.MGOFileArtist)
            .addRelation(TablesEnum.MGOFileAlbum, MGOFileAlbumColumns.ALBUMID, TablesEnum.MGOFileAlbumRelationship, MGOFileAlbumRelationshipColumns.ID)
            .addRelation(TablesEnum.MGOFileAlbumRelationship, MGOFileAlbumRelationshipColumns.FILEID, TablesEnum.MGOFile, MGOFileColumns.ID)
            .addRelation(TablesEnum.MGOFileExtension, MGOFileExtensionColumns.ID, TablesEnum.MGOFile, MGOFileColumns.EXTENSION_ID)
            .addRelation(TablesEnum.MGOFileArtist, MGOFileArtistColumns.ARTISTID, TablesEnum.MGOFileArtistRelationship, MGOFileArtistRelationshipColumns.ID)
            .addRelation(TablesEnum.MGOFileArtistRelationship, MGOFileArtistRelationshipColumns.FILEID, TablesEnum.MGOFile, MGOFileColumns.ID)
            .addCondition(TablesEnum.MGOFileExtension.alias(), MGOFileExtensionColumns.DATA, SQLBuilder.Comparator.EQUALS, "mp3")
            .addCondition(MGOFileColumns.PLAYCOUNT, SQLBuilder.Comparator.GREATER, new Integer(0))
            .orderBy(TablesEnum.MGOFile, MGOFileColumns.DATELASTPLAYED, SQLBuilder.SORTORDER.ASC)
            .limitBy()
            .render();

    protected static final String FILE_UPDATE_PLAYCOUNT = "UPDATE MGOFile " +
            " SET PlayCount = ? " +
            " ,DateLastPlayed = ? " +
            " WHERE FileTitle like ? " +
            " AND PlayCount < ? " +
            " AND ID IN (" +
            " SELECT FileID FROM MGOFileAlbumRelationship" +
            " INNER JOIN MGOFile ON (MGOFileAlbumRelationship.FileID = MGOFile.ID)" +
            " INNER JOIN MGOFileAlbum ON (MGOFileAlbum.ID = MGOFileAlbumRelationship.ID)" +
            " WHERE 1=1" +
            " AND MGOFileAlbum.data like ?" +
            " AND MGOFile.FileTitle like ?" +
            ")";

    protected static final String FILE_SYNC_PLAYCOUNT = "UPDATE MGOFile " +
            " SET PlayCount = ?" +
            " ,DateLastPlayed = ?" +
            " WHERE ID = ?";

    protected static final String LIST_ALBUMS = "SELECT DISTINCT MGOFileAlbum.data AS ALBUMNAME," +
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
            " AND MGOFileAlbum.data like ?" +
            " GROUP BY ALBUMNAME, ALBUMARTISTNAME, ALBUMID" +
            " ORDER BY MGOFileAlbum.data";

    protected static final String LIST_ALBUMS_TRACKS = "SELECT DISTINCT MGOFileAlbum.data AS ALBUMNAME," +
            " MGOAlbumArtist.id AS ALBUMARTISTID, " +
            " MGOAlbumArtist.data AS ALBUMARTISTNAME," +
            " MGOFileAlbum.id AS ALBUMID," +
            " MAX(MGOFile.Year) AS YEAR" +
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

    protected static final String LIST_TOP20 = "SELECT FileTitle AS FILETITLE, PlayCount AS PLAYCOUNT, Title AS TITLE, FA.DATA AS ARTIST, PLL.ID AS PLAYLIST_ID, MGoFile.ID AS FILE_ID, MGOFile.File AS FILE, MGOFile.duration AS DURATION" +
            " FROM MGOPlaylist_To_File AS PLF" +
            " INNER JOIN MGOFile ON (PLF.FileID = MGOFile.ID)" +
            " INNER JOIN MGOPlaylist AS PLL ON (PLF.PlayListID = PLL.ID)" +
            " INNER JOIN MGOFileArtistRelationship AS FAR ON (MGOFile.ID = FAR.FileID)" +
            " INNER JOIN MGOFileArtist AS FA ON (FAR.ID = FA.ID)" +
            " WHERE PLL.type = 32" +
            " AND PLL.Name = '11 Top Of The Moment'" +
            " ORDER BY PLF.rowid " +
            " LIMIT 0,20";

    protected static final String FIND_BY_FILE2 = "SELECT ID AS FILEID " +
            "FROM MGOfile " +
            "WHERE UPPER(FILE) LIKE UPPER(?)";

    protected static final String FIND_BY_FILE = SQLBuilder.getInstance()
            .select()
            .addTable(TablesEnum.MGOFile)
            .addColumns(TablesEnum.MGOFile)
            .addCondition(
                    TablesEnum.MGOFile.alias(),
                    SQLFunction.UPPER,
                    MGOFileColumns.FILE,
                    SQLBuilder.Comparator.LIKE,
                    null)
            .render();


    protected static final String FIND_COVER_ART = "SELECT MGOFile.File AS FILE " +
            "FROM MGOFileAlbumRelationship " +
            "INNER JOIN MGOFileAlbum ON (MGOFileAlbum.ID = MGOFileAlbumRelationship.ID) " +
            "INNER JOIN MGOFile ON (MGOFile.ID = MGOFileAlbumRelationship.fileID) " +
            "INNER JOIN MGOFileExtension ON (MGOFileExtension.ID = MGOFILE.extensionID) " +
            "WHERE 1=1 " +
            "AND MGOFileExtension.data = 'mp3' " +
            "AND MGOFileAlbum.id like ? " +
            "LIMIT 0,1";

    protected static final String LIST_CUSTOM = "SELECT MGOFile.File AS FILE, " +
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

    protected static final String FILE_UPDATE_RATING = "UPDATE MGOFile " +
            " SET Ranking = ? " +
            " WHERE ID = ?";

    protected static final String FIND_SONGS_ALBUM2 = "SELECT " + getColumns(COLUMNS_MP3) +
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

    protected static final String FIND_SONGS_ALBUM = SQLBuilder.getInstance()
            .select()
            .addTable(TablesEnum.MGOFile)
            .addColumns(TablesEnum.MGOFile)
            .addColumns(TablesEnum.MGOFileAlbum)
            .addColumns(TablesEnum.MGOFileArtist)
            .addRelation(TablesEnum.MGOFileAlbum, MGOFileAlbumColumns.ALBUMID, TablesEnum.MGOFileAlbumRelationship, MGOFileAlbumRelationshipColumns.ID)
            .addRelation(TablesEnum.MGOFileAlbumRelationship, MGOFileAlbumRelationshipColumns.FILEID, TablesEnum.MGOFile, MGOFileColumns.ID)
            .addRelation(TablesEnum.MGOFileExtension, MGOFileExtensionColumns.ID, TablesEnum.MGOFile, MGOFileColumns.EXTENSION_ID)
            .addRelation(TablesEnum.MGOFileArtist, MGOFileArtistColumns.ARTISTID, TablesEnum.MGOFileArtistRelationship, MGOFileArtistRelationshipColumns.ID)
            .addRelation(TablesEnum.MGOFileArtistRelationship, MGOFileArtistRelationshipColumns.FILEID, TablesEnum.MGOFile, MGOFileColumns.ID)

            .addRelation(TablesEnum.MGOAlbumArtist, MGOAlbumArtistColumns.ALBUMARTISTID, TablesEnum.MGOAlbumArtistRelationship, MGOAlbumArtistRelationshipColumns.ID)
            .addRelation(TablesEnum.MGOAlbumArtistRelationship, MGOAlbumArtistRelationshipColumns.FILEID, TablesEnum.MGOFileAlbumRelationship, MGOFileAlbumRelationshipColumns.FILEID)

            .addCondition(TablesEnum.MGOFileExtension.alias(), MGOFileExtensionColumns.DATA, SQLBuilder.Comparator.EQUALS, "mp3")
            .addCondition(TablesEnum.MGOFileAlbum.alias(), MGOFileAlbumColumns.ALBUMID, SQLBuilder.Comparator.EQUALS, null)
            .addCondition(TablesEnum.MGOAlbumArtist.alias(), MGOAlbumArtistColumns.ALBUMARTISTID, SQLBuilder.Comparator.EQUALS, null)
            .orderBy(TablesEnum.MGOFile, MGOFileColumns.DISC, SQLBuilder.SORTORDER.ASC)
            .orderBy(TablesEnum.MGOFile, MGOFileColumns.TRACK, SQLBuilder.SORTORDER.ASC)
            .render();

    protected static final String FIND_LAST_PLAYED2 = "SELECT " + getColumns(COLUMNS_MP3) +
            " FROM MGOFile " +
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

    public static final String FIND_LAST_PLAYED = SQLBuilder.getInstance()
            .select()
            .addTable(TablesEnum.MGOFile)
            .addColumns(TablesEnum.MGOFile)
            .addColumns(TablesEnum.MGOFileAlbum)
            .addColumns(TablesEnum.MGOFileArtist)
            .addRelation(TablesEnum.MGOFileAlbum, MGOFileAlbumColumns.ALBUMID, TablesEnum.MGOFileAlbumRelationship, MGOFileAlbumRelationshipColumns.ID)
            .addRelation(TablesEnum.MGOFileAlbumRelationship, MGOFileAlbumRelationshipColumns.FILEID, TablesEnum.MGOFile, MGOFileColumns.ID)
            .addRelation(TablesEnum.MGOFileExtension, MGOFileExtensionColumns.ID, TablesEnum.MGOFile, MGOFileColumns.EXTENSION_ID)
            .addRelation(TablesEnum.MGOFileArtist, MGOFileArtistColumns.ARTISTID, TablesEnum.MGOFileArtistRelationship, MGOFileArtistRelationshipColumns.ID)
            .addRelation(TablesEnum.MGOFileArtistRelationship, MGOFileArtistRelationshipColumns.FILEID, TablesEnum.MGOFile, MGOFileColumns.ID)
            .addRelation(TablesEnum.MGOAlbumArtist, MGOAlbumArtistColumns.ALBUMARTISTID, TablesEnum.MGOAlbumArtistRelationship, MGOAlbumArtistRelationshipColumns.ID)
            .addRelation(TablesEnum.MGOAlbumArtistRelationship, MGOAlbumArtistRelationshipColumns.FILEID, TablesEnum.MGOFileAlbumRelationship, MGOFileAlbumRelationshipColumns.FILEID)
            .addCondition(TablesEnum.MGOFileExtension.alias(), MGOFileExtensionColumns.DATA, SQLBuilder.Comparator.EQUALS, "mp3")
            .orderBy(TablesEnum.MGOFile, MGOFileColumns.DATELASTPLAYED, SQLBuilder.SORTORDER.DESC)
            .limitBy(0)
            .render();


    public static final String MAX_DISC_OLD = "SELECT max (MGOFileAlbum.data) ALBUM,  max (MGOAlbumArtist.data) ALBUM_ARTIST, max(MGOFile.disc) DISC " +
            "FROM MGOFile " +
            "INNER JOIN MGOFileAlbumRelationship ON (MGOFileAlbumRelationship.FileID = MGOFILE.id) " +
            "INNER JOIN MGOFileAlbum ON (MGOFileAlbum.ID = MGOFileAlbumRelationship.ID) " +
            "INNER JOIN MGOAlbumArtistRelationship ON (MGOAlbumArtistRelationship.fileID = MGOFileAlbumRelationship.fileID) " +
            "INNER JOIN MGOAlbumArtist ON (MGOAlbumArtist.ID = MGOAlbumArtistRelationship.ID) " +
            "INNER JOIN MGOFileExtension ON (MGOFileExtension.ID = MGOFILE.extensionID) " +
            "WHERE MGOFileExtension.data = 'mp3' " +
            "AND MGOFile.disc >= 9 " +
            "GROUP BY MGOFileAlbum.data, MGOAlbumArtist.data ";


    public static final String MAX_DISC  = SQLBuilder.getInstance()
            .select()
            .addTable(TablesEnum.MGOFile)
            .addColumn(TablesEnum.MGOFileAlbum.alias(), SQLFunction.MAX, MGOFileAlbumColumns.ALBUM, null)
            .addColumn(TablesEnum.MGOAlbumArtist.alias(), SQLFunction.MAX, MGOAlbumArtistColumns.ALBUMARTIST, null)
            .addColumn(TablesEnum.MGOFile.alias(), SQLFunction.MAX, MGOFileColumns.DISC, null)
            .addRelation(TablesEnum.MGOFileAlbum, MGOFileAlbumColumns.ALBUMID, TablesEnum.MGOFileAlbumRelationship, MGOFileAlbumRelationshipColumns.ID)
            .addRelation(TablesEnum.MGOFileAlbumRelationship, MGOFileAlbumRelationshipColumns.FILEID, TablesEnum.MGOFile, MGOFileColumns.ID)
            .addRelation(TablesEnum.MGOFileExtension, MGOFileExtensionColumns.ID, TablesEnum.MGOFile, MGOFileColumns.EXTENSION_ID)
            .addRelation(TablesEnum.MGOAlbumArtist, MGOAlbumArtistColumns.ALBUMARTISTID, TablesEnum.MGOAlbumArtistRelationship, MGOAlbumArtistRelationshipColumns.ID)
            .addRelation(TablesEnum.MGOAlbumArtistRelationship, MGOAlbumArtistRelationshipColumns.FILEID, TablesEnum.MGOFileAlbumRelationship, MGOFileAlbumRelationshipColumns.FILEID)
            .addCondition(TablesEnum.MGOFileExtension.alias(), MGOFileExtensionColumns.DATA, SQLBuilder.Comparator.EQUALS, "mp3")
            .addGroup(TablesEnum.MGOFileAlbum.alias(), MGOFileAlbumColumns.ALBUM)
            .addGroup(TablesEnum.MGOAlbumArtist.alias(), MGOAlbumArtistColumns.ALBUMARTIST)
            .render();

    protected static final String FILE_UPDATE_ARTIST = "UPDATE MGOFileArtist " +
            " SET data = ?" +
            " WHERE ID = ?";

    protected static final String FILE_UPDATE_TITLE = "UPDATE MGOFile " +
            " SET title = ?, " +
            " sortTitle = ? " +
            " WHERE ID = ?";

    protected static final String FILE_UPDATE_TRACK = "UPDATE MGOFile " +
            " SET track = ?" +
            " WHERE ID = ?";

    protected static final String FILE_UPDATE_FILE = "UPDATE MGOFile " +
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
