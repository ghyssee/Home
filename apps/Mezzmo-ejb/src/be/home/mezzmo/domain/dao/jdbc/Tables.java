package be.home.mezzmo.domain.dao.jdbc;

import be.home.common.exceptions.ApplicationException;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gebruiker on 14/12/2016.
 */
public enum Tables {
    MGOPlaylist ("PLAYLIST"),
    MGOPlaylistSQL ("PLAYLISTSQL"),
    MGOPlaylist_To_File ("PLAYLISTFILE"),
    MGOFile ("FILE"),
    MGOFileAlbum ("FILEALBUM"),
    MGOFileArtist ("FILEARTIST"),
    MGOF

    private String alias;
    private String tableAlias;
    private static final Map lookup = new HashMap();

    Tables(String s) {

        alias = s;
        tableAlias = this.name() + " AS " + s;
    }

    public String alias() {
        return alias;
    }

    public String tableAlias() {
        return tableAlias;
    }

    // Populate the lookup table on loading time
    static {
        for (Tables s : EnumSet.allOf(Tables.class))
            lookup.put(s.name(), s);
    }

    // This method can be used for reverse lookup purpose
    public static Tables get(Tables tmp) {

        Tables t = (Tables) lookup.get(tmp.name());
        return t;
    }

}
