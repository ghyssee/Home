package be.home.mezzmo.domain.dao.jdbc;

import be.home.common.database.DatabaseColumn;
import be.home.common.database.FieldType;

/**
 * Created by ghyssee on 14/12/2016.
 */
public enum PlaylistSQLColumns implements DatabaseColumn  {
    ID("ID", FieldType.SEQUENCE),
    PlaylistID("PLAYLISTID", FieldType.NORMAL),
    ColumnNum("COLUMNNUM", FieldType.NORMAL),
    ColumnType("COLUMNTYPE", FieldType.NORMAL),
    Operand("OPERAND", FieldType.NORMAL),
    ValueOneText("VALUEONETEXT", FieldType.NORMAL),
    ValueTwoText ("VALUETWOTEXT", FieldType.NORMAL),
    ValueOneInt("VALUEONEINT", FieldType.NORMAL),
    ValueTwoInt ("VALUETWOINT", FieldType.NORMAL),
    GroupNr ("GROUPNR", FieldType.NORMAL);

    public String columnName;
    public FieldType fieldType;

    PlaylistSQLColumns(String s, FieldType t) {

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

