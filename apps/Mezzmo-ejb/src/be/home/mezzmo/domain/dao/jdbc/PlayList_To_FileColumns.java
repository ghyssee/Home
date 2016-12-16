package be.home.mezzmo.domain.dao.jdbc;

import be.home.common.database.DatabaseColumn;
import be.home.common.database.FieldType;

/**
 * Created by ghyssee on 14/12/2016.
 */
public enum Playlist_To_FileColumns implements DatabaseColumn {
    PLAYLISTID("PlaylistID", FieldType.NORMAL),
    FILEID ("FileID", FieldType.NORMAL);

    public String columnName;
    public FieldType fieldType;

    Playlist_To_FileColumns(String s, FieldType t) {

        columnName = s;
        fieldType = t;
    }

    public String getColumnName() {
        return columnName;
    }
    public FieldType getFieldType() {
        return fieldType;
    }
    public String getColumnAndAlias() {
        return this + " AS " + columnName;
    }

}

