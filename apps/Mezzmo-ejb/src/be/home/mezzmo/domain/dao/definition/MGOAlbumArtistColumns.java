package be.home.mezzmo.domain.dao.definition;

import be.home.common.database.DatabaseColumn;
import be.home.common.database.FieldType;

/**
 * Created by ghyssee on 14/12/2016.
 */
public enum MGOAlbumArtistColumns implements DatabaseColumn  {
    ALBUMARTISTID("ID", FieldType.SEQUENCE),
    ALBUMARTIST("Data", FieldType.NORMAL);

    public String columnName;
    public FieldType fieldType;

    MGOAlbumArtistColumns(String s, FieldType t) {

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

