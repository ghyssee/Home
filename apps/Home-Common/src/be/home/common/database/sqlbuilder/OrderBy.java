package be.home.common.database.sqlbuilder;

import be.home.common.database.DatabaseColumn;

/**
 * Created by Gebruiker on 20/12/2016.
 */
class OrderBy {
    String orderField;

    OrderBy(String alias, DatabaseColumn column, SortOrder sortOrder) {
        this.orderField = alias + " " + sortOrder.name();
    }

    OrderBy(DatabaseTables table, DatabaseColumn column, SortOrder sortOrder) {
        this.orderField = table.alias() + "." + column.getColumnName() + " " + sortOrder.name();
    }
}
