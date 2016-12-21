package be.home.common.database.sqlbuilder;

import java.io.Serializable;

/**
 * Created by Gebruiker on 20/12/2016.
 */
class Column implements Serializable {
    //DatabaseColumn column;
    //String dbAlias;
    //String columnAlias;
    String field;

    Column(String field) {
        this.field = field;
    }

    /*
    Column(DatabaseColumn column, String alias){
        this.column = column;
        this.dbAlias = alias;
    }

    Column(DatabaseColumn column, String alias, String columnAlias){
        this.column = column;
        this.dbAlias = alias;
        this.columnAlias = columnAlias;
    }*/
}
