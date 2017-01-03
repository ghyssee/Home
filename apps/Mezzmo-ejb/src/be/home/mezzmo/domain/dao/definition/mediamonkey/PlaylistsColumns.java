package be.home.mezzmo.domain.dao.definition.mediamonkey;

import be.home.common.database.DatabaseColumn;
import be.home.common.database.FieldType;

/**
 * Created by ghyssee on 3/01/2017.
 */
public enum PlaylistsColumns implements DatabaseColumn {
    PLAYLISTID("IDPLAYLIST", FieldType.NORMAL),
    PLAYLISTNAME("PLAYLISTNAME", FieldType.NORMAL),
    PARENTPLAYLIST("PARENTPLAYLIST", FieldType.NORMAL);

    public String columnName;
    public FieldType fieldType;

    PlaylistsColumns(String s, FieldType t) {

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
