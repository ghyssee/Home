package be.home.common.database.sqlbuilder;

import be.home.common.database.DatabaseColumn;

/**
 * Created by Gebruiker on 20/12/2016.
 */
class Relation {
    DatabaseTables table1;
    DatabaseColumn column1;
    String alias1;
    DatabaseTables table2;
    DatabaseColumn column2;

    Relation(DatabaseTables table1, String alias1, DatabaseColumn column1, DatabaseTables table2, DatabaseColumn column2) {
        this.table1 = table1;
        this.column1 = column1;
        this.alias1 = alias1;
        this.table2 = table2;
        this.column2 = column2;
    }
}
