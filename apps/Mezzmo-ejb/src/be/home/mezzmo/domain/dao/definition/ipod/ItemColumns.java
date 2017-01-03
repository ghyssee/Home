package be.home.mezzmo.domain.dao.definition.ipod;

import be.home.common.database.DatabaseColumn;
import be.home.common.database.FieldType;

/**
 * Created by ghyssee on 3/01/2017.
 */
public enum ItemColumns implements DatabaseColumn {
    ID("pid", FieldType.NORMAL),
    ALBUMID("album_pid", FieldType.NORMAL),
    TITLE("title", FieldType.NORMAL),
    ARTIST("artist", FieldType.NORMAL),
    ALBUM_ARTIST("album_artist", FieldType.NORMAL),
    ALBUM("album", FieldType.NORMAL),
    TRACK("track_number", FieldType.NORMAL),
    DISC("disc_number", FieldType.NORMAL);

    public String columnName;
    public FieldType fieldType;

    ItemColumns(String s, FieldType t) {

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
