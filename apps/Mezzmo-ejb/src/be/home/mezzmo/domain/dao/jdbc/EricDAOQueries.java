package be.home.mezzmo.domain.dao.jdbc;

import be.home.common.dao.jdbc.EricDB;
import be.home.common.database.sqlbuilder.SQLBuilder;
import be.home.mezzmo.domain.dao.definition.eric.EricTables;

/**
 * Created by ghyssee on 21/09/2017.
 */
public class EricDAOQueries extends EricDB {
    protected static final String INSERT_MEZZMOFILE = SQLBuilder.getInstance()
            .insert()
            .addTable(EricTables.MezzmoFile)
            .addColumns(EricTables.MezzmoFile)
            .render();
}
