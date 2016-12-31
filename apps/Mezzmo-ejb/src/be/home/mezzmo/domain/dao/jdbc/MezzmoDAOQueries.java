package be.home.mezzmo.domain.dao.jdbc;

import be.home.common.dao.jdbc.MezzmoDB;
import be.home.common.database.sqlbuilder.*;
import be.home.mezzmo.domain.dao.definition.*;
import be.home.mezzmo.domain.model.MGOFileArtistTO;
import org.apache.commons.lang.SerializationUtils;

/**
 * Created by Gebruiker on 8/12/2016.
 */
public class MezzmoDAOQueries extends MezzmoDB {

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

    protected static SQLBuilder FILE_FIND_BASIC = SQLBuilder.getInstance()
            .select()
            .addTable(TablesEnum.MGOFile)
            .addColumns(TablesEnum.MGOFile)
            .addColumns(TablesEnum.MGOFileAlbum)
            .addColumns(TablesEnum.MGOFileArtist)
            .addColumns(TablesEnum.MGOAlbumArtist)
            .addRelation(TablesEnum.MGOFileAlbum, MGOFileAlbumColumns.ALBUMID, TablesEnum.MGOFileAlbumRelationship, MGOFileAlbumRelationshipColumns.ID)
            .addRelation(TablesEnum.MGOFileAlbumRelationship, MGOFileAlbumRelationshipColumns.FILEID, TablesEnum.MGOFile, MGOFileColumns.ID)
            .addRelation(TablesEnum.MGOFileExtension, MGOFileExtensionColumns.ID, TablesEnum.MGOFile, MGOFileColumns.EXTENSION_ID)
            .addRelation(TablesEnum.MGOFileArtist, MGOFileArtistColumns.ARTISTID, TablesEnum.MGOFileArtistRelationship, MGOFileArtistRelationshipColumns.ID)
            .addRelation(TablesEnum.MGOFileArtistRelationship, MGOFileArtistRelationshipColumns.FILEID, TablesEnum.MGOFile, MGOFileColumns.ID)
            .addRelation(TablesEnum.MGOAlbumArtist, MGOAlbumArtistColumns.ALBUMARTISTID, TablesEnum.MGOAlbumArtistRelationship, MGOAlbumArtistRelationshipColumns.ID)
            .addRelation(TablesEnum.MGOAlbumArtistRelationship, MGOAlbumArtistRelationshipColumns.FILEID, TablesEnum.MGOFile, MGOFileColumns.ID)
            .addCondition(TablesEnum.MGOFileExtension.alias(), MGOFileExtensionColumns.DATA, Comparator.EQUALS, "mp3");

    public static final String FILE_FIND_TAGINFO_CRITERIA = ((SQLBuilder) SerializationUtils.clone(FILE_FIND_BASIC))
            .addCondition(TablesEnum.MGOFile.alias(), MGOFileColumns.TRACK, Comparator.LIKE)
            .addCondition(TablesEnum.MGOFileArtist.alias(), MGOFileArtistColumns.ARTIST, Comparator.LIKE)
            .addCondition(TablesEnum.MGOFile.alias(), MGOFileColumns.TITLE, Comparator.LIKE)
            .addCondition(TablesEnum.MGOFileAlbum.alias(), MGOFileAlbumColumns.ALBUM, Comparator.LIKE)
            .render();

    public static final String FILE_FIND_TAGINFO_BY_ALBUMID = ((SQLBuilder) SerializationUtils.clone(FILE_FIND_BASIC))
            .addCondition(TablesEnum.MGOFileAlbum.alias(), MGOFileAlbumColumns.ALBUMID, Comparator.LIKE)
            .render();

    protected static final String FILE_SELECT_TITLE = ((SQLBuilder) SerializationUtils.clone(FILE_FIND_BASIC))
            .addCondition(TablesEnum.MGOFile.alias(), MGOFileColumns.FILETITLE, Comparator.LIKE)
            .addCondition(TablesEnum.MGOFileAlbum.alias(), MGOFileAlbumColumns.ALBUM, Comparator.LIKE)
            .render();

    protected static final String FILE_PLAYCOUNT = ((SQLBuilder) SerializationUtils.clone(FILE_FIND_BASIC))
            .addCondition(MGOFileColumns.PLAYCOUNT, Comparator.GREATER, new Integer(0))
            .orderBy(TablesEnum.MGOFile, MGOFileColumns.DATELASTPLAYED, SortOrder.ASC)
            .limitBy()
            .render();

    protected static final SQLBuilder FILE_UPDATE_PLAYCOUNT_SUBSELECT = SQLBuilder.getInstance()
            .select()
            .addTable(TablesEnum.MGOFile)
            .addColumn(TablesEnum.MGOFile.alias(), MGOFileColumns.ID, null)
            .addRelation(TablesEnum.MGOFileAlbum, MGOFileAlbumColumns.ALBUMID, TablesEnum.MGOFileAlbumRelationship, MGOFileAlbumRelationshipColumns.ID)
            .addRelation(TablesEnum.MGOFileAlbumRelationship, MGOFileAlbumRelationshipColumns.FILEID, TablesEnum.MGOFile, MGOFileColumns.ID)
            .addCondition(TablesEnum.MGOFileAlbum.alias(), MGOFileAlbumColumns.ALBUM, Comparator.LIKE, SQLBuilder.PARAMETER)
            .addCondition(TablesEnum.MGOFile.alias(), MGOFileColumns.FILETITLE, Comparator.LIKE, SQLBuilder.PARAMETER);

    public static final String FILE_UPDATE_PLAYCOUNT = SQLBuilder.getInstance()
            .update()
            .addTable(TablesEnum.MGOFile)
            .updateColumn(MGOFileColumns.PLAYCOUNT, Type.PARAMETER)
            .updateColumn(MGOFileColumns.DATELASTPLAYED, Type.PARAMETER)
            .addCondition(MGOFileColumns.FILETITLE, Comparator.LIKE, SQLBuilder.PARAMETER)
            .addCondition(MGOFileColumns.PLAYCOUNT, Comparator.LESS, SQLBuilder.PARAMETER)

            .addCondition(MGOFileColumns.ID, Comparator.IN,
                    FILE_UPDATE_PLAYCOUNT_SUBSELECT
            )
            .render();

    protected static final String FILE_SYNC_PLAYCOUNT = "UPDATE MGOFile " +
            " SET PlayCount = ?" +
            " ,DateLastPlayed = ?" +
            " WHERE ID = ?";


    public static String LIST_ALBUMS = SQLBuilder.getInstance()
            .select()
            .enableDistinct()
            .addTable(TablesEnum.MGOFile)
            .addColumn(TablesEnum.MGOFileAlbum.alias(), MGOFileAlbumColumns.ALBUM )
            .addColumn(TablesEnum.MGOAlbumArtist.alias(), MGOAlbumArtistColumns.ALBUMARTIST)
            .addColumn(TablesEnum.MGOFileAlbum.alias(), MGOFileAlbumColumns.ALBUMID )
            .addColumn(TablesEnum.MGOFile.alias(), SQLFunction.MAX, MGOFileColumns.YEAR )
            .addRelation(TablesEnum.MGOFileAlbum, MGOFileAlbumColumns.ALBUMID, TablesEnum.MGOFileAlbumRelationship, MGOFileAlbumRelationshipColumns.ID)
            .addRelation(TablesEnum.MGOFileAlbumRelationship, MGOFileAlbumRelationshipColumns.FILEID, TablesEnum.MGOFile, MGOFileColumns.ID)
            .addRelation(TablesEnum.MGOFileExtension, MGOFileExtensionColumns.ID, TablesEnum.MGOFile, MGOFileColumns.EXTENSION_ID)
            .addRelation(TablesEnum.MGOFileArtist, MGOFileArtistColumns.ARTISTID, TablesEnum.MGOFileArtistRelationship, MGOFileArtistRelationshipColumns.ID)
            .addRelation(TablesEnum.MGOFileArtistRelationship, MGOFileArtistRelationshipColumns.FILEID, TablesEnum.MGOFile, MGOFileColumns.ID)
            .addRelation(TablesEnum.MGOAlbumArtist, MGOAlbumArtistColumns.ALBUMARTISTID, TablesEnum.MGOAlbumArtistRelationship, MGOAlbumArtistRelationshipColumns.ID)
            .addRelation(TablesEnum.MGOAlbumArtistRelationship, MGOAlbumArtistRelationshipColumns.FILEID, TablesEnum.MGOFileAlbumRelationship, MGOFileAlbumRelationshipColumns.FILEID)
            .addCondition(TablesEnum.MGOFileExtension.alias(), MGOFileExtensionColumns.DATA, Comparator.EQUALS, "mp3")
            .addCondition(TablesEnum.MGOFileAlbum.alias(), MGOFileAlbumColumns.ALBUM, Comparator.LIKE, SQLBuilder.PARAMETER)
            .addGroup(TablesEnum.MGOFileAlbum.alias(), MGOFileAlbumColumns.ALBUM)
            .addGroup(TablesEnum.MGOAlbumArtist.alias(), MGOAlbumArtistColumns.ALBUMARTIST)
            .addGroup(TablesEnum.MGOFileAlbum.alias(), MGOFileAlbumColumns.ALBUMID)
            .orderBy(TablesEnum.MGOFileAlbum, MGOFileAlbumColumns.ALBUM, SortOrder.ASC)
            .render();


    protected static final String LIST_ALBUMS_OLD = "SELECT DISTINCT MGOFileAlbum.data AS ALBUMNAME," +
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

    protected static final String LIST_ALBUMS_TRACKSOLD = "SELECT DISTINCT " +
            " MGOFileAlbum.id AS ALBUMID," +
            " MGOFileAlbum.data AS ALBUMNAME," +
            " MGOAlbumArtist.id AS ALBUMARTISTID, " +
            " MGOAlbumArtist.data AS ALBUMARTISTNAME," +
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

    public static String LIST_ALBUMS_TRACKS = SQLBuilder.getInstance()
            .select()
            .enableDistinct()
            .addTable(TablesEnum.MGOFile)
            .addColumns(TablesEnum.MGOFileAlbum)
            .addColumns(TablesEnum.MGOFileArtist )
            .addColumns(TablesEnum.MGOAlbumArtist )
            .addColumn(TablesEnum.MGOFile.alias(), SQLFunction.MAX, MGOFileColumns.YEAR )
            .addRelation(TablesEnum.MGOFileAlbum, MGOFileAlbumColumns.ALBUMID, TablesEnum.MGOFileAlbumRelationship, MGOFileAlbumRelationshipColumns.ID)
            .addRelation(TablesEnum.MGOFileAlbumRelationship, MGOFileAlbumRelationshipColumns.FILEID, TablesEnum.MGOFile, MGOFileColumns.ID)
            .addRelation(TablesEnum.MGOFileExtension, MGOFileExtensionColumns.ID, TablesEnum.MGOFile, MGOFileColumns.EXTENSION_ID)
            .addRelation(TablesEnum.MGOFileArtist, MGOFileArtistColumns.ARTISTID, TablesEnum.MGOFileArtistRelationship, MGOFileArtistRelationshipColumns.ID)
            .addRelation(TablesEnum.MGOFileArtistRelationship, MGOFileArtistRelationshipColumns.FILEID, TablesEnum.MGOFile, MGOFileColumns.ID)
            .addRelation(TablesEnum.MGOAlbumArtist, MGOAlbumArtistColumns.ALBUMARTISTID, TablesEnum.MGOAlbumArtistRelationship, MGOAlbumArtistRelationshipColumns.ID)
            .addRelation(TablesEnum.MGOAlbumArtistRelationship, MGOAlbumArtistRelationshipColumns.FILEID, TablesEnum.MGOFileAlbumRelationship, MGOFileAlbumRelationshipColumns.FILEID)
            .addCondition(TablesEnum.MGOFileExtension.alias(), MGOFileExtensionColumns.DATA, Comparator.EQUALS, "mp3")
            .addGroup(TablesEnum.MGOFileAlbum.alias(), MGOFileAlbumColumns.ALBUM)
            .addGroup(TablesEnum.MGOAlbumArtist.alias(), MGOAlbumArtistColumns.ALBUMARTIST)
            .addGroup(TablesEnum.MGOFileAlbum.alias(), MGOFileAlbumColumns.ALBUMID)
            .orderBy(TablesEnum.MGOFileAlbum, MGOFileAlbumColumns.ALBUM, SortOrder.ASC)
            .render();


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

    protected static final String FIND_BY_FILE = SQLBuilder.getInstance()
            .select()
            .addTable(TablesEnum.MGOFile)
            .addColumns(TablesEnum.MGOFile)
            .addCondition(
                    TablesEnum.MGOFile.alias(),
                    SQLFunction.UPPER,
                    MGOFileColumns.FILE,
                    Comparator.LIKE,
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

    public static final String FILE_UPDATE_RATING = SQLBuilder.getInstance()
            .update()
            .addTable(TablesEnum.MGOFile)
            .updateColumn(MGOFileColumns.RANKING, Type.PARAMETER)
            .addCondition(MGOFileColumns.ID, Comparator.EQUALS, SQLBuilder.PARAMETER)
            .render();

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

            .addCondition(TablesEnum.MGOFileExtension.alias(), MGOFileExtensionColumns.DATA, Comparator.EQUALS, "mp3")
            .addCondition(TablesEnum.MGOFileAlbum.alias(), MGOFileAlbumColumns.ALBUMID, Comparator.EQUALS, null)
            .addCondition(TablesEnum.MGOAlbumArtist.alias(), MGOAlbumArtistColumns.ALBUMARTISTID, Comparator.EQUALS, null)
            .orderBy(TablesEnum.MGOFile, MGOFileColumns.DISC, SortOrder.ASC)
            .orderBy(TablesEnum.MGOFile, MGOFileColumns.TRACK, SortOrder.ASC)
            .render();

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
            .addCondition(TablesEnum.MGOFileExtension.alias(), MGOFileExtensionColumns.DATA, Comparator.EQUALS, "mp3")
            .orderBy(TablesEnum.MGOFile, MGOFileColumns.DATELASTPLAYED, SortOrder.DESC)
            .limitBy(0)
            .render();

    public static final String MAX_DISC  = SQLBuilder.getInstance()
            .select()
            .addTable(TablesEnum.MGOFile)
            .addColumn(TablesEnum.MGOFileAlbum.alias(), SQLFunction.MAX, MGOFileAlbumColumns.ALBUM)
            .addColumn(TablesEnum.MGOAlbumArtist.alias(), SQLFunction.MAX, MGOAlbumArtistColumns.ALBUMARTIST)
            .addColumn(TablesEnum.MGOFile.alias(), SQLFunction.MAX, MGOFileColumns.DISC)
            .addRelation(TablesEnum.MGOFileAlbum, MGOFileAlbumColumns.ALBUMID, TablesEnum.MGOFileAlbumRelationship, MGOFileAlbumRelationshipColumns.ID)
            .addRelation(TablesEnum.MGOFileAlbumRelationship, MGOFileAlbumRelationshipColumns.FILEID, TablesEnum.MGOFile, MGOFileColumns.ID)
            .addRelation(TablesEnum.MGOFileExtension, MGOFileExtensionColumns.ID, TablesEnum.MGOFile, MGOFileColumns.EXTENSION_ID)
            .addRelation(TablesEnum.MGOAlbumArtist, MGOAlbumArtistColumns.ALBUMARTISTID, TablesEnum.MGOAlbumArtistRelationship, MGOAlbumArtistRelationshipColumns.ID)
            .addRelation(TablesEnum.MGOAlbumArtistRelationship, MGOAlbumArtistRelationshipColumns.FILEID, TablesEnum.MGOFileAlbumRelationship, MGOFileAlbumRelationshipColumns.FILEID)
            .addCondition(TablesEnum.MGOFileExtension.alias(), MGOFileExtensionColumns.DATA, Comparator.EQUALS, "mp3")
            .addGroup(TablesEnum.MGOFileAlbum.alias(), MGOFileAlbumColumns.ALBUM)
            .addGroup(TablesEnum.MGOAlbumArtist.alias(), MGOAlbumArtistColumns.ALBUMARTIST)
            .render();

    protected static final String FILE_UPDATE_ARTISTOld = "UPDATE MGOFileArtist " +
            " SET data = ?" +
            " WHERE ID = ?";

    protected static final String FILE_UPDATE_ARTIST = SQLBuilder.getInstance()
            .update()
            .addTable(TablesEnum.MGOFileArtist)
            .updateColumn(MGOFileArtistColumns.ARTIST, Type.PARAMETER)
            .addCondition(MGOFileArtistColumns.ARTISTID, Comparator.EQUALS, SQLBuilder.PARAMETER)
            .render();

    protected static final String FILE_UPDATE_ALBUM = SQLBuilder.getInstance()
            .update()
            .addTable(TablesEnum.MGOFileAlbum)
            .updateColumn(MGOFileAlbumColumns.ALBUM, Type.PARAMETER)
            .addCondition(MGOFileAlbumColumns.ALBUMID, Comparator.EQUALS, SQLBuilder.PARAMETER)
            .render();


    protected static final String FILE_UPDATE_TITLEOld = "UPDATE MGOFile " +
            " SET title = ?, " +
            " sortTitle = ? " +
            " WHERE ID = ?";

    protected static final String FILE_UPDATE_TITLE = SQLBuilder.getInstance()
            .update()
            .addTable(TablesEnum.MGOFile)
            .updateColumn(MGOFileColumns.TITLE, Type.PARAMETER)
            .updateColumn(MGOFileColumns.SORTTILE, Type.PARAMETER)
            .addCondition(MGOFileColumns.ID, Comparator.EQUALS, SQLBuilder.PARAMETER)
            .render();

    protected static final String FILE_UPDATE_DISC = SQLBuilder.getInstance()
            .update()
            .addTable(TablesEnum.MGOFile)
            .updateColumn(MGOFileColumns.DISC, Type.PARAMETER)
            .addCondition(MGOFileColumns.ID, Comparator.EQUALS, SQLBuilder.PARAMETER)
            .render();

    protected static final String FILE_UPDATE_TRACKOld = "UPDATE MGOFile " +
            " SET track = ?" +
            " WHERE ID = ?";

    protected static final String FILE_UPDATE_TRACK = SQLBuilder.getInstance()
            .update()
            .addTable(TablesEnum.MGOFile)
            .updateColumn(MGOFileColumns.TRACK, Type.PARAMETER)
            .addCondition(MGOFileColumns.ID, Comparator.EQUALS, SQLBuilder.PARAMETER)
            .render();

    protected static final String FILE_UPDATE_FILEOld = "UPDATE MGOFile " +
            " SET file = ?," +
            " fileTitle = ? " +
            " WHERE ID = ?";

    protected static final String FILE_UPDATE_FILE = SQLBuilder.getInstance()
            .update()
            .addTable(TablesEnum.MGOFile)
            .updateColumn(MGOFileColumns.FILE, Type.PARAMETER)
            .updateColumn(MGOFileColumns.FILETITLE, Type.PARAMETER)
            .addCondition(MGOFileColumns.ID, Comparator.EQUALS, SQLBuilder.PARAMETER)
            .render();

    protected static final String FILE_UPDATE_FILETITLE = SQLBuilder.getInstance()
            .update()
            .addTable(TablesEnum.MGOFile)
            .updateColumn(MGOFileColumns.FILETITLE, Type.PARAMETER)
            .addCondition(MGOFileColumns.ID, Comparator.EQUALS, SQLBuilder.PARAMETER)
            .render();

    public static final String FIND_ARTIST = SQLBuilder.getInstance()
            .select()
            .addTable(TablesEnum.MGOFileArtist)
            .addColumns(TablesEnum.MGOFileArtist)
            .addCondition(MGOFileArtistColumns.ARTIST, Comparator.LIKE, SQLBuilder.PARAMETER)
            //.addOption("COLLATE BINARY")
            .render();

    public static final String FIND_ARTIST_BY_ID = SQLBuilder.getInstance()
            .select()
            .addTable(TablesEnum.MGOFileArtist)
            .addColumns(TablesEnum.MGOFileArtist)
            .addCondition(MGOFileArtistColumns.ARTISTID, Comparator.EQUALS, SQLBuilder.PARAMETER)
            //.addOption("COLLATE BINARY")
            .render();

    public static final String FIND_LINKED_ARTIST = SQLBuilder.getInstance()
            .select()
            .addTable(TablesEnum.MGOFileArtistRelationship)
            .addColumns(TablesEnum.MGOFileArtistRelationship)
            .addCondition(MGOFileArtistRelationshipColumns.ID, Comparator.EQUALS, SQLBuilder.PARAMETER)
            .render();

    public static final String UPDATE_LINK_FILE_ARTIST = SQLBuilder.getInstance()
            .update()
            .addTable(TablesEnum.MGOFileArtistRelationship)
            .updateColumn(MGOFileArtistRelationshipColumns.ID, Type.PARAMETER)
            .addCondition(MGOFileArtistRelationshipColumns.ID, Comparator.EQUALS, SQLBuilder.PARAMETER)
            .render();

    public static final String UPDATE_LINK_FILE_ARTIST2 = SQLBuilder.getInstance()
            .update()
            .addTable(TablesEnum.MGOFileArtistRelationship)
            .updateColumn(MGOFileArtistRelationshipColumns.ID, Type.PARAMETER)
            .addCondition(MGOFileArtistRelationshipColumns.FILEID, Comparator.EQUALS, SQLBuilder.PARAMETER)
            .render();

    public static final String DELETE_ARTIST = SQLBuilder.getInstance()
            .delete()
            .addTable(TablesEnum.MGOFileArtist)
            .addCondition(MGOFileArtistColumns.ARTISTID, Comparator.EQUALS, SQLBuilder.PARAMETER)
            .render();

    public static final String INSERT_ARTIST = SQLBuilder.getInstance()
            .insert()
            .addTable(TablesEnum.MGOFileArtist)
            .addColumns(TablesEnum.MGOFileArtist)
            .render();
}
