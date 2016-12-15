package be.home.mezzmo.domain.dao.jdbc;

import be.home.common.database.DatabaseColumn;

/**
 * Created by ghyssee on 14/12/2016.
 */
public enum PlaylistSQLColumns implements DatabaseColumn  {
    ID("ID", "PRIMARYKEY"),
    PlaylistID("PLAYLISTID", "FIELD"),
    ColumnNum("COLUMNNUM", "FIELD"),
    ColumnType("COLUMNTYPE", "FIELD"),
    Operand("OPERAND", "FIELD"),
    ValueOneText("VALUEONETEXT", "FIELD"),
    ValueTwoText ("VALUETWOTEXT", "FIELD"),
    ValueOneInt("VALUEONEINT", "FIELD"),
    ValueTwoInt ("VALUETWOINT", "FIELD"),
    GroupNr ("GROUPNR", "FIELD");

    public String columnName;
    public String type;

    PlaylistSQLColumns(String s, String t) {

        columnName = s;
        type = t;
    }

    public String getColumnName() {
        return columnName;
    }
    public String getType() {
        return type;
    }

}

