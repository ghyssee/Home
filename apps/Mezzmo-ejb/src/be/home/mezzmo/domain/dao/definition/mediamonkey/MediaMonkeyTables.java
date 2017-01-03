package be.home.mezzmo.domain.dao.definition.mediamonkey;

import be.home.common.database.DatabaseColumn;
import be.home.common.database.sqlbuilder.DatabaseTables;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ghyssee on 3/01/2017.
 */
    public enum MediaMonkeyTables implements DatabaseTables {
        Playlists("PLAYLISTS", PlaylistsColumns.values()),
        Songs("SONGS", SongsColumns.values()),
        PlaylistSongs("PLAYLISTSONGS", PlaylistSongsColumns.values()),
        DeviceTracks("DEVICETRACKS", DeviceTracksColumns.values());

        private String alias;
        private String tableAlias;
        private DatabaseColumn[] columns;
        private static final Map lookup = new HashMap();

    MediaMonkeyTables(String s, DatabaseColumn[] t) {

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
            for (be.home.mezzmo.domain.dao.definition.TablesEnum s : EnumSet.allOf(be.home.mezzmo.domain.dao.definition.TablesEnum.class))
                lookup.put(s.name(), s);
        }

        // This method can be used for reverse lookup purpose
        public static be.home.mezzmo.domain.dao.definition.TablesEnum get(be.home.mezzmo.domain.dao.definition.TablesEnum tmp) {

            be.home.mezzmo.domain.dao.definition.TablesEnum t = (be.home.mezzmo.domain.dao.definition.TablesEnum) lookup.get(tmp.name());
            return t;
        }

}