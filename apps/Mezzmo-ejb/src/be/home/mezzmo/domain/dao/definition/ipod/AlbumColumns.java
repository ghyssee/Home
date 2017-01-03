package be.home.mezzmo.domain.dao.definition.ipod;

import be.home.common.database.DatabaseColumn;
import be.home.common.database.FieldType;

/**
 * Created by ghyssee on 3/01/2017.
 */
public enum AlbumColumns implements DatabaseColumn {
    ALBUMID("pid", FieldType.NORMAL),
    ALBUMNAME("name", FieldType.NORMAL);

    public String columnName;
    public FieldType fieldType;

    AlbumColumns(String s, FieldType t) {

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