package be.home.mezzmo.domain.dao.definition.eric;

import be.home.common.database.DatabaseColumn;
import be.home.common.database.FieldType;

/**
 * Created by ghyssee on 21/09/2017.
 */
public enum MezzmoFileColumns implements DatabaseColumn{
    ID("Id",FieldType.NORMAL),
    ARTISTID("ArtistId", FieldType.NORMAL),
    ARTISTNAME("ArtistName", FieldType.NORMAL),
    STATUS("Status", FieldType.NORMAL);

    public String columnName;
    public FieldType fieldType;

    MezzmoFileColumns(String s, FieldType t) {

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
