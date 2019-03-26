package be.home.mezzmo.domain.dao.definition;

import be.home.common.database.DatabaseColumn;
import be.home.common.database.FieldType;

public enum VersionColumns implements DatabaseColumn {
    VERSION("Version", FieldType.NORMAL),
    LASTUPDATED("Lastupdated", FieldType.NORMAL);

    public String columnName;
    public FieldType fieldType;

    VersionColumns(String s, FieldType t) {

        columnName = s;
        fieldType = t;
    }

    public String getColumnName() {
        return columnName;
    }
    public FieldType getFieldType() {
        return fieldType;
    }
    public String getColumnAndAlias() {
        return this + " AS " + columnName;
    }

}

