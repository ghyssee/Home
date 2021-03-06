package be.home.mezzmo.domain.dao.definition;

import be.home.common.database.DatabaseColumn;
import be.home.common.database.sqlbuilder.DatabaseTables;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gebruiker on 14/12/2016.
 */
public enum TablesEnum implements DatabaseTables {
    MGOPlaylist ("PLAYLIST", PlaylistColumns.values()),
    MGOPlaylistSQL ("PLAYLISTSQL", PlaylistSQLColumns.values()),
    MGOPlaylist_To_File ("PLAYLISTFILE", PlaylistColumns.values()),
    MGOFile ("FILE", MGOFileColumns.values()),
    MGOFileAlbum ("FILEALBUM", MGOFileAlbumColumns.values()),
    MGOFileAlbumRelationship ("FILEALBUMREL", MGOFileAlbumRelationshipColumns.values()),
    MGOFileArtist ("FILEARTIST", MGOFileArtistColumns.values()),
    MGOFileArtistRelationship ("FILEARTISTREL", MGOFileArtistRelationshipColumns.values()),
    MGOAlbumArtist ("ALBUMARTIST", MGOAlbumArtistColumns.values()),
    MGOAlbumArtistRelationship ("ALBUMARTISTREL", MGOAlbumArtistRelationshipColumns.values()),
    MGOFileExtension ("FILEEXTENSION", MGOFileExtensionColumns.values()),
    VERSION ("VERSION", VersionColumns.values());

    private String alias;
    private String tableAlias;
    private DatabaseColumn[] columns;
    private static final Map lookup = new HashMap();

    TablesEnum(String s, DatabaseColumn[] t) {

        alias = s;
        tableAlias = this.name() + " AS " + s;
        columns = t;
    }

    public String alias() {
        return alias;
    }
    public DatabaseColumn[] columns() {
        return columns;
    }

    public String tableAlias() {
        return tableAlias;
    }

    // Populate the lookup table on loading time
    static {
        for (TablesEnum s : EnumSet.allOf(TablesEnum.class))
            lookup.put(s.name(), s);
    }

    // This method can be used for reverse lookup purpose
    public static TablesEnum get(TablesEnum tmp) {

        TablesEnum t = (TablesEnum) lookup.get(tmp.name());
        return t;
    }

}
