package be.home.common.database.sqlbuilder;

import be.home.common.database.DatabaseColumn;

import java.io.Serializable;

/**
 * Created by Gebruiker on 20/12/2016.
 */
class GroupBy implements Serializable {
    String groupbyField;

    GroupBy(String alias, DatabaseColumn column) {
        this.groupbyField = alias + "." + column.getColumnName();
    }
}
