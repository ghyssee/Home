package be.home.mezzmo.domain.dao.jdbc;

import be.home.common.database.DatabaseColumn;
import be.home.common.database.FieldType;

/**
 * Created by ghyssee on 14/12/2016.
 */
public enum MGOFileColumns implements DatabaseColumn  {
    ID("ID", FieldType.SEQUENCE),
    File("FILE", FieldType.NORMAL),
    Title("TITLE", FieldType.NORMAL),
    PlayCount("PLAYCOUNT", FieldType.NORMAL),
    Ranking("RANKING", FieldType.NORMAL),
    Disc("DISC", FieldType.NORMAL),
    Track("TRACK", FieldType.NORMAL),
    FileTitle("FILETITLE", FieldType.NORMAL),
    DateLastPlayed("DATELASTPLAYED", FieldType.NORMAL);

    public String columnName;
    public FieldType fieldType;

    MGOFileColumns(String s, FieldType t) {

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

