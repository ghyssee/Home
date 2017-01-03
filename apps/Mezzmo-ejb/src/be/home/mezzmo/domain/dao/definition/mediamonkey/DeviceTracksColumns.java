package be.home.mezzmo.domain.dao.definition.mediamonkey;

import be.home.common.database.DatabaseColumn;
import be.home.common.database.FieldType;

/**
 * Created by ghyssee on 3/01/2017.
 */
public enum DeviceTracksColumns implements DatabaseColumn {
    PLAYCOUNT("playcount", FieldType.NORMAL);

    public String columnName;
    public FieldType fieldType;

    DeviceTracksColumns(String s, FieldType t) {

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
