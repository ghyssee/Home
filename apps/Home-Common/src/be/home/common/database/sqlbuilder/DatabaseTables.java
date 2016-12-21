package be.home.common.database.sqlbuilder;

import be.home.common.database.DatabaseColumn;

import java.io.Serializable;

/**
 * Created by Gebruiker on 20/12/2016.
 */
public interface DatabaseTables {

    String alias();
    DatabaseColumn[] columns();
    String tableAlias();
    String name();

}
