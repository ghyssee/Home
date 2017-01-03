package be.home.mezzmo.domain.dao.definition.ipod;

import be.home.common.database.DatabaseColumn;
import be.home.common.database.FieldType;

/**
 * Created by ghyssee on 3/01/2017.
 */
public enum Item_StatsColumns implements DatabaseColumn {
    ITEMID("item_pid", FieldType.NORMAL),
    PLAYCOUNT("play_count_user", FieldType.NORMAL),
    HAS_BEEN_PLAYED("has_been_played", FieldType.NORMAL),
    DATELASTPLAYED("date_played", FieldType.NORMAL);

    public String columnName;
    public FieldType fieldType;

    Item_StatsColumns(String s, FieldType t) {

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
