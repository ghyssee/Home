package be.home.mezzmo.domain.dao.jdbc;

import be.home.common.database.DatabaseColumn;
import be.home.common.database.FieldType;

/**
 * Created by ghyssee on 14/12/2016.
 */
public enum MGOFileAlbumColumns implements DatabaseColumn  {
    ID("ALBUMID", FieldType.SEQUENCE),
    Data("ALBUM", FieldType.NORMAL);

    public String columnName;
    public FieldType fieldType;
    public TablesEnum table = TablesEnum.MGOFileAlbum;

    MGOFileAlbumColumns(String s, FieldType t) {

        columnName = s;
        fieldType = t;
    }

    public String getColumnName() {
        return columnName;
    }
    public FieldType getFieldType() {
        return fieldType;
    }
    public TablesEnum getTable() { return table; }

}

