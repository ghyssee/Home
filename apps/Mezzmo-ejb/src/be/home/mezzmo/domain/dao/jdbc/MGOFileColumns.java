package be.home.mezzmo.domain.dao.jdbc;

import be.home.common.database.DatabaseColumn;
import be.home.common.database.FieldType;

/**
 * Created by ghyssee on 14/12/2016.
 */
public enum MGOFileColumns implements DatabaseColumn  {
    ID("ID", FieldType.SEQUENCE),
    FILE("File", FieldType.NORMAL),
    TITLE("Title", FieldType.NORMAL),
    PLAYCOUNT("Playcount", FieldType.NORMAL),
    RANKING("Ranking", FieldType.NORMAL),
    DISC("Disc", FieldType.NORMAL),
    TRACK("Track", FieldType.NORMAL),
    FILETITLE("FileTitle", FieldType.NORMAL),
    DATELASTPLAYED("DateLastPlayed", FieldType.NORMAL),
    DURATION("Duration", FieldType.NORMAL),
    EXTENSION_ID("ExtensionID", FieldType.NORMAL),
    YEAR("Year", FieldType.NORMAL);

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

