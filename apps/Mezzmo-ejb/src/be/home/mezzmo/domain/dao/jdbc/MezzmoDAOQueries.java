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

    private static SQLBuilder addRelationFileWithAritst(){
        SQLBuilder tst = SQLBuilder.getInstance()
                .addRelation(TablesEnum.MGOFileArtist, MGOFileArtistColumns.ARTISTID, TablesEnum.MGOFileArtistRelationship, MGOFileArtistRelationshipColumns.ID)
                .addRelation(TablesEnum.MGOFileArtistRelationship, MGOFileArtistRelationshipColumns.FILEID, TablesEnum.MGOFile, MGOFileColumns.ID);
        return tst;
    }

    private static SQLBuilder addRelationFileWithAlbum(){
        SQLBuilder tst = SQLBuilder.getInstance()
                .addRelation(TablesEnum.MGOFileAlbum, MGOFileAlbumColumns.ALBUMID, TablesEnum.MGOFileAlbumRelationship, MGOFileAlbumRelationshipColumns.ID)
                .addRelation(TablesEnum.MGOFileAlbumRelationship, MGOFileAlbumRelationshipColumns.FILEID, TablesEnum.MGOFile, MGOFileColumns.ID);
        return tst;
    }

    private static SQLBuilder addRelationAlbumWithArtist(){
        SQLBuilder tst = SQLBuilder.getInstance()
                .addRelation(TablesEnum.MGOAlbumArtist, MGOAlbumArtistColumns.ALBUMARTISTID, TablesEnum.MGOAlbumArtistRelationship, MGOAlbumArtistRelationshipColumns.ID)
                .addRelation(TablesEnum.MGOAlbumArtistRelationship, MGOAlbumArtistRelationshipColumns.FILEID, TablesEnum.MGOFileAlbumRelationship, MGOFileAlbumRelationshipColumns.FILEID);
        return tst;
    }

    protected static SQLBuilder FILE_FIND_BASIC = SQLBuilder.getInstance()
            .select()
            .addTable(TablesEnum.MGOFile)
            .addColumns(TablesEnum.MGOFile)
            .addColumns(TablesEnum.MGOFileAlbum)
            .addColumns(TablesEnum.MGOFileArtist)
            .addColumns(TablesEnum.MGOAlbumArtist)
            .addRelation(addRelationFileWithAlbum())
            .addRelation(addRelationFileWithAritst())
            .addRelation(addRelationAlbumWithArtist())
            .addRelation(TablesEnum.MGOFileExtension, MGOFileExtensionColumns.ID, TablesEnum.MGOFile, MGOFileColumns.EXTENSION_ID)
            .addCondition(TablesEnum.MGOFileExtension.alias(), MGOFileExtensionColumns.DATA, Comparator.EQUALS, MP3_EXT);
    protected static final String FIND_ALBUM = SQLBuilder.getInstance()
            .select()
            .addTable(TablesEnum.MGOFile)
            .enableDistinct()
            .addColumns(TablesEnum.MGOFileAlbum)
            .addColumn(TablesEnum.MGOAlbumArtist.alias(), MGOAlbumArtistColumns.ALBUMARTIST)
            .addRelation(addRelationFileWithAlbum())
            .addRelation(addRelationFileWithAritst())
            .addRelation(addRelationAlbumWithArtist())
            .addRelation(TablesEnum.MGOFileExtension, MGOFileExtensionColumns.ID, TablesEnum.MGOFile, MGOFileColumns.EXTENSION_ID)
            .addCondition(TablesEnum.MGOFileExtension.alias(), MGOFileExtensionColumns.DATA, Comparator.EQUALS, MP3_EXT)
            .addCondition(TablesEnum.MGOFileAlbum.alias(), MGOFileAlbumColumns.ALBUM, Comparator.LIKE)
            .addCondition(TablesEnum.MGOAlbumArtist.alias(), MGOAlbumArtistColumns.ALBUMARTIST, Comparator.LIKE)
            .render();

    protected static final String FILE_FIND_TAGINFO_CRITERIA = ((SQLBuilder) SerializationUtils.clone(FILE_FIND_BASIC))
            .addCondition(TablesEnum.MGOFile.alias(), MGOFileColumns.TRACK, Comparator.LIKE)
            .addCondition(TablesEnum.MGOFileArtist.alias(), MGOFileArtistColumns.ARTIST, Comparator.LIKE)
            .addCondition(TablesEnum.MGOFile.alias(), MGOFileColumns.TITLE, Comparator.LIKE)
            .addCondition(TablesEnum.MGOFileAlbum.alias(), MGOFileAlbumColumns.ALBUM, Comparator.LIKE)
            .render();

    protected static final String FILE_FIND_TAGINFO_BY_ALBUMID = ((SQLBuilder) SerializationUtils.clone(FILE_FIND_BASIC))
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
            .addRelation(addRelationFileWithAlbum())
            .addCondition(TablesEnum.MGOFileAlbum.alias(), MGOFileAlbumColumns.ALBUM, Comparator.LIKE, SQLBuilder.PARAMETER)
            .addCondition(TablesEnum.MGOFile.alias(), MGOFileColumns.FILETITLE, Comparator.LIKE, SQLBuilder.PARAMETER);

    protected static final String FILE_UPDATE_PLAYCOUNT = SQLBuilder.getInstance()
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

    protected static final String FILE_SYNC_PLAYCOUNT = SQLBuilder.getInstance()
            .update()
            .addTable(TablesEnum.MGOFile)
            .updateColumn(MGOFileColumns.PLAYCOUNT, Type.PARAMETER)
            .updateColumn(MGOFileColumns.DATELASTPLAYED, Type.PARAMETER)
            .addCondition(MGOFileColumns.ID, Comparator.EQUALS, SQLBuilder.PARAMETER)
            .render();

    protected static String LIST_ALBUMS = SQLBuilder.getInstance()
            .select()
            .enableDistinct()
            .addTable(TablesEnum.MGOFile)
            .addColumn(TablesEnum.MGOFileAlbum.alias(), MGOFileAlbumColumns.ALBUM )
            .addColumn(TablesEnum.MGOAlbumArtist.alias(), MGOAlbumArtistColumns.ALBUMARTIST)
            .addColumn(TablesEnum.MGOFileAlbum.alias(), MGOFileAlbumColumns.ALBUMID )
            .addColumn(TablesEnum.MGOFile.alias(), SQLFunction.MAX, MGOFileColumns.YEAR )
            .addRelation(TablesEnum.MGOFileExtension, MGOFileExtensionColumns.ID, TablesEnum.MGOFile, MGOFileColumns.EXTENSION_ID)
            .addRelation(addRelationFileWithAlbum())
            .addRelation(addRelationFileWithAritst())
            .addRelation(addRelationAlbumWithArtist())
            .addCondition(TablesEnum.MGOFileExtension.alias(), MGOFileExtensionColumns.DATA, Comparator.EQUALS, MP3_EXT)
            .addCondition(TablesEnum.MGOFileAlbum.alias(), MGOFileAlbumColumns.ALBUM, Comparator.LIKE, SQLBuilder.PARAMETER)
            .addGroup(TablesEnum.MGOFileAlbum.alias(), MGOFileAlbumColumns.ALBUM)
            .addGroup(TablesEnum.MGOAlbumArtist.alias(), MGOAlbumArtistColumns.ALBUMARTIST)
            .addGroup(TablesEnum.MGOFileAlbum.alias(), MGOFileAlbumColumns.ALBUMID)
            .orderBy(TablesEnum.MGOFileAlbum, MGOFileAlbumColumns.ALBUM, SortOrder.ASC)
            .render();


    protected static String LIST_ALBUMS_TRACKS = SQLBuilder.getInstance()
            .select()
            .enableDistinct()
            .addTable(TablesEnum.MGOFile)
            .addColumns(TablesEnum.MGOFileAlbum)
            .addColumns(TablesEnum.MGOFileArtist )
            .addColumns(TablesEnum.MGOAlbumArtist )
            .addColumn(TablesEnum.MGOFile.alias(), SQLFunction.MAX, MGOFileColumns.YEAR )
            .addRelation(TablesEnum.MGOFileExtension, MGOFileExtensionColumns.ID, TablesEnum.MGOFile, MGOFileColumns.EXTENSION_ID)
            .addRelation(addRelationFileWithAlbum())
            .addRelation(addRelationFileWithAritst())
            .addRelation(addRelationAlbumWithArtist())
            .addCondition(TablesEnum.MGOFileExtension.alias(), MGOFileExtensionColumns.DATA, Comparator.EQUALS, MP3_EXT)
            .addGroup(TablesEnum.MGOFileAlbum.alias(), MGOFileAlbumColumns.ALBUM)
            .addGroup(TablesEnum.MGOAlbumArtist.alias(), MGOAlbumArtistColumns.ALBUMARTIST)
            .addGroup(TablesEnum.MGOFileAlbum.alias(), MGOFileAlbumColumns.ALBUMID)
            .orderBy(TablesEnum.MGOFileAlbum, MGOFileAlbumColumns.ALBUM, SortOrder.ASC)
            .render();


    protected static final String LIST_TOP20 = SQLBuilder.getInstance()
            .select()
            .addTable(TablesEnum.MGOFile)
            .addColumns(TablesEnum.MGOFileArtist)
            .addRelation(TablesEnum.MGOFileExtension, MGOFileExtensionColumns.ID, TablesEnum.MGOFile, MGOFileColumns.EXTENSION_ID)
            .addRelation(addRelationFileWithAritst())
            .addRelation(TablesEnum.MGOPlaylist, PlaylistColumns.ID, TablesEnum.MGOPlaylist_To_File, Playlist_To_FileColumns.PLAYLISTID)
            .addRelation(TablesEnum.MGOFile, MGOFileColumns.ID, TablesEnum.MGOPlaylist_To_File, Playlist_To_FileColumns.FILEID)
            .addCondition(TablesEnum.MGOFileExtension.alias(), MGOFileExtensionColumns.DATA, Comparator.EQUALS, MP3_EXT)
            .addCondition(TablesEnum.MGOPlaylist.alias(), PlaylistColumns.TYPE, Comparator.EQUALS, 32)
            .addCondition(TablesEnum.MGOPlaylist.alias(), PlaylistColumns.NAME, Comparator.EQUALS, "11 Top Of The Moment")
            .orderBy(TablesEnum.MGOPlaylist_To_File, Playlist_To_FileColumns.ROWID, SortOrder.ASC)
            .limitBy(0,20)
            .render();


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

    protected static final String FIND_COVER_ART = SQLBuilder.getInstance()
            .select()
            .addTable(TablesEnum.MGOFile)
            .addColumns(TablesEnum.MGOFile)
            .addRelation(TablesEnum.MGOFileExtension, MGOFileExtensionColumns.ID, TablesEnum.MGOFile, MGOFileColumns.EXTENSION_ID)
            .addRelation(addRelationFileWithAlbum())
            .addCondition(TablesEnum.MGOFileExtension.alias(), MGOFileExtensionColumns.DATA, Comparator.EQUALS, MP3_EXT)
            .addCondition(TablesEnum.MGOFileAlbum.alias(), MGOFileAlbumColumns.ALBUMID, Comparator.EQUALS)
            .limitBy(0,1)
            .render();


    protected static final String FIND_COVER_ART_OLD = "SELECT MGOFile.File AS FILE " +
            "FROM MGOFileAlbumRelationship " +
            "INNER JOIN MGOFileAlbum ON (MGOFileAlbum.ID = MGOFileAlbumRelationship.ID) " +
            "INNER JOIN MGOFile ON (MGOFile.ID = MGOFileAlbumRelationship.fileID) " +
            "INNER JOIN MGOFileExtension ON (MGOFileExtension.ID = MGOFILE.extensionID) " +
            "WHERE 1=1 " +
            "AND MGOFileExtension.data = 'mp3' " +
            "AND MGOFileAlbum.id like ? " +
            "LIMIT 0,1";

    protected static SQLBuilder LIST_CUSTOM = ((SQLBuilder) SerializationUtils.clone(FILE_FIND_BASIC))
            .orderBy("RANDOM()")
            .limitBy(0);

    protected static final String FILE_UPDATE_RATING = SQLBuilder.getInstance()
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
            .addRelation(addRelationFileWithAlbum())
            .addRelation(addRelationFileWithAritst())
            .addRelation(addRelationAlbumWithArtist())
            .addRelation(TablesEnum.MGOFileExtension, MGOFileExtensionColumns.ID, TablesEnum.MGOFile, MGOFileColumns.EXTENSION_ID)
            .addCondition(TablesEnum.MGOFileExtension.alias(), MGOFileExtensionColumns.DATA, Comparator.EQUALS, MP3_EXT)
            .addCondition(TablesEnum.MGOFileAlbum.alias(), MGOFileAlbumColumns.ALBUMID, Comparator.EQUALS, null)
            .addCondition(TablesEnum.MGOAlbumArtist.alias(), MGOAlbumArtistColumns.ALBUMARTISTID, Comparator.EQUALS, null)
            .orderBy(TablesEnum.MGOFile, MGOFileColumns.DISC, SortOrder.ASC)
            .orderBy(TablesEnum.MGOFile, MGOFileColumns.TRACK, SortOrder.ASC)
            .render();

    protected static final String FIND_LAST_PLAYED = SQLBuilder.getInstance()
            .select()
            .addTable(TablesEnum.MGOFile)
            .addColumns(TablesEnum.MGOFile)
            .addColumns(TablesEnum.MGOFileAlbum)
            .addColumns(TablesEnum.MGOFileArtist)
            .addRelation(addRelationFileWithAlbum())
            .addRelation(addRelationFileWithAritst())
            .addRelation(addRelationAlbumWithArtist())
            .addRelation(TablesEnum.MGOFileExtension, MGOFileExtensionColumns.ID, TablesEnum.MGOFile, MGOFileColumns.EXTENSION_ID)
            .addCondition(TablesEnum.MGOFileExtension.alias(), MGOFileExtensionColumns.DATA, Comparator.EQUALS, MP3_EXT)
            .orderBy(TablesEnum.MGOFile, MGOFileColumns.DATELASTPLAYED, SortOrder.DESC)
            .limitBy(0)
            .render();

    protected static final String MAX_DISC  = SQLBuilder.getInstance()
            .select()
            .addTable(TablesEnum.MGOFile)
            .addColumn(TablesEnum.MGOFileAlbum.alias(), SQLFunction.MAX, MGOFileAlbumColumns.ALBUM)
            .addColumn(TablesEnum.MGOAlbumArtist.alias(), SQLFunction.MAX, MGOAlbumArtistColumns.ALBUMARTIST)
            .addColumn(TablesEnum.MGOFile.alias(), SQLFunction.MAX, MGOFileColumns.DISC)
            .addRelation(addRelationFileWithAlbum())
            .addRelation(addRelationAlbumWithArtist())
            .addRelation(TablesEnum.MGOFileExtension, MGOFileExtensionColumns.ID, TablesEnum.MGOFile, MGOFileColumns.EXTENSION_ID)
            .addCondition(TablesEnum.MGOFileExtension.alias(), MGOFileExtensionColumns.DATA, Comparator.EQUALS, MP3_EXT)
            .addGroup(TablesEnum.MGOFileAlbum.alias(), MGOFileAlbumColumns.ALBUM)
            .addGroup(TablesEnum.MGOAlbumArtist.alias(), MGOAlbumArtistColumns.ALBUMARTIST)
            .render();

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

    protected static final String FILE_UPDATE_TRACK = SQLBuilder.getInstance()
            .update()
            .addTable(TablesEnum.MGOFile)
            .updateColumn(MGOFileColumns.TRACK, Type.PARAMETER)
            .addCondition(MGOFileColumns.ID, Comparator.EQUALS, SQLBuilder.PARAMETER)
            .render();

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

    protected static final String FIND_ARTIST = SQLBuilder.getInstance()
            .select()
            .addTable(TablesEnum.MGOFileArtist)
            .addColumns(TablesEnum.MGOFileArtist)
            .addCondition(MGOFileArtistColumns.ARTIST, Comparator.LIKE, SQLBuilder.PARAMETER)
            //.addOption("COLLATE BINARY")
            .render();

    protected static final String FIND_ARTIST_BY_ID = SQLBuilder.getInstance()
            .select()
            .addTable(TablesEnum.MGOFileArtist)
            .addColumns(TablesEnum.MGOFileArtist)
            .addCondition(MGOFileArtistColumns.ARTISTID, Comparator.EQUALS, SQLBuilder.PARAMETER)
            //.addOption("COLLATE BINARY")
            .render();

    protected static final String FIND_LINKED_ARTIST = SQLBuilder.getInstance()
            .select()
            .addTable(TablesEnum.MGOFileArtistRelationship)
            .addColumns(TablesEnum.MGOFileArtistRelationship)
            .addCondition(MGOFileArtistRelationshipColumns.ID, Comparator.EQUALS, SQLBuilder.PARAMETER)
            .render();

    protected static final String UPDATE_LINK_FILE_ARTIST = SQLBuilder.getInstance()
            .update()
            .addTable(TablesEnum.MGOFileArtistRelationship)
            .updateColumn(MGOFileArtistRelationshipColumns.ID, Type.PARAMETER)
            .addCondition(MGOFileArtistRelationshipColumns.ID, Comparator.EQUALS, SQLBuilder.PARAMETER)
            .render();

    protected static final String UPDATE_LINK_FILE_ARTIST2 = SQLBuilder.getInstance()
            .update()
            .addTable(TablesEnum.MGOFileArtistRelationship)
            .updateColumn(MGOFileArtistRelationshipColumns.ID, Type.PARAMETER)
            .addCondition(MGOFileArtistRelationshipColumns.FILEID, Comparator.EQUALS, SQLBuilder.PARAMETER)
            .render();

    protected static final String DELETE_ARTIST = SQLBuilder.getInstance()
            .delete()
            .addTable(TablesEnum.MGOFileArtist)
            .addCondition(MGOFileArtistColumns.ARTISTID, Comparator.EQUALS, SQLBuilder.PARAMETER)
            .render();

    protected static final String INSERT_ARTIST = SQLBuilder.getInstance()
            .insert()
            .addTable(TablesEnum.MGOFileArtist)
            .addColumns(TablesEnum.MGOFileArtist)
            .render();
}
