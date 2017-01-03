package be.home.mezzmo.domain.dao.definition.mediamonkey;

import be.home.common.database.DatabaseColumn;
import be.home.common.database.FieldType;

/**
 * Created by ghyssee on 3/01/2017.
 */
public enum PlaylistSongsColumns implements DatabaseColumn {
    SONGID("IDSONG", FieldType.NORMAL),
    PLAYLISTID("IDPLAYLIST", FieldType.NORMAL),
    SONGORDER("SONGORDER", FieldType.NORMAL);

    public String columnName;
    public FieldType fieldType;

    PlaylistSongsColumns(String s, FieldType t) {

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
