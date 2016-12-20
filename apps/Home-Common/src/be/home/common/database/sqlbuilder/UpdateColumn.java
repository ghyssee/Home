package be.home.common.database.sqlbuilder;

import be.home.common.database.DatabaseColumn;

/**
 * Created by Gebruiker on 20/12/2016.
 */
class UpdateColumn {
    DatabaseColumn column;
    String value;
    Type type;

    UpdateColumn(DatabaseColumn column, String value) {
        this.column = column;
        this.value = value;
        this.type = Type.VALUE;
    }

    UpdateColumn(DatabaseColumn column, String value, Type type) {
        this.column = column;
        this.value = value;
        this.type = type;
    }
}
