package be.home.mezzmo.domain.dao.definition;

import be.home.common.database.DatabaseColumn;
import be.home.common.database.FieldType;

/**
 * Created by ghyssee on 14/12/2016.
 */
public enum MGOFileArtistColumns implements DatabaseColumn  {
    ARTISTID("ID", FieldType.SEQUENCE),
    ARTIST("Data", FieldType.NORMAL);

    public String columnName;
    public FieldType fieldType;

    MGOFileArtistColumns(String s, FieldType t) {

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

