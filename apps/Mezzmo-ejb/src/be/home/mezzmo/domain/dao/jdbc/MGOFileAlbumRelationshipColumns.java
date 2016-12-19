package be.home.mezzmo.domain.dao.jdbc;

import be.home.common.database.DatabaseColumn;
import be.home.common.database.FieldType;

/**
 * Created by ghyssee on 14/12/2016.
 */
public enum MGOFileAlbumRelationshipColumns implements DatabaseColumn  {
    ID("ID", FieldType.NORMAL),
    FILEID("FileID", FieldType.NORMAL);

    public String columnName;
    public FieldType fieldType;

    MGOFileAlbumRelationshipColumns(String s, FieldType t) {

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

