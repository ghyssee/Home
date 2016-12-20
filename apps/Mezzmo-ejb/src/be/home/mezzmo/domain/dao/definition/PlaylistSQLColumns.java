package be.home.mezzmo.domain.dao.definition;

import be.home.common.database.DatabaseColumn;
import be.home.common.database.FieldType;

/**
 * Created by ghyssee on 14/12/2016.
 */
public enum PlaylistSQLColumns implements DatabaseColumn  {
    ID("ID", FieldType.SEQUENCE),
    PLAYLISTID("PlaylistID", FieldType.NORMAL),
    COLUMNNUM("ColumnNum", FieldType.NORMAL),
    COLUMNTYPE("ColumnType", FieldType.NORMAL),
    OPERAND("Operand", FieldType.NORMAL),
    VALUEONETEXT("ValueOneText", FieldType.NORMAL),
    VALUETWOTEXT ("ValueTwoText", FieldType.NORMAL),
    VALUEONEINT("ValueOneInt", FieldType.NORMAL),
    VALUETWOINT ("ValueTwoInt", FieldType.NORMAL),
    GROUPNR ("GroupNr", FieldType.NORMAL);

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

