package be.home.mezzmo.domain.dao.definition.mediamonkey;

import be.home.common.database.DatabaseColumn;
import be.home.common.database.FieldType;

/**
 * Created by ghyssee on 3/01/2017.
 */
public enum SongsColumns implements DatabaseColumn {
    ID("ID", FieldType.NORMAL),
    PLAYCOUNT("PlayCounter", FieldType.NORMAL),
    TITLE("SongTitle", FieldType.NORMAL),
    FILETITLE("SongTitle", FieldType.NORMAL),
    ARTIST("Artist", FieldType.NORMAL),
    FILE("SongPath", FieldType.NORMAL),
    DURATION("SongLength", FieldType.NORMAL),
    LASTTIMEPLAYED("LastTimePlayed", FieldType.NORMAL);

    public String columnName;
    public FieldType fieldType;

    SongsColumns(String s, FieldType t) {

        columnName = s;
        fieldType = t;
    }

    public String getColumnName() {
        return columnName;
    }
    public FieldType getFieldType() {
        return fieldType;
    }

}

