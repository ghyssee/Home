package be.home.common.dao.jdbc;

/**
 * Created by ghyssee on 9/02/2016.
 */
import be.home.model.DataBaseConfiguration;
import org.apache.log4j.Logger;

import java.io.File;
import java.sql.*;

public class SQLiteJDBC
{
    private static SQLiteJDBC instance = null;
    public static DataBaseConfiguration config = null;
    protected Connection c = null;
    private static final Logger log = Logger.getLogger(SQLiteJDBC.class);

    protected SQLiteJDBC() {
    }

    public void openDatabase(String db){

        DataBaseConfiguration.DataBase database = getDatabase(db);
        if (database == null){
            throw new RuntimeException("DB Id Not Found: " + db);
        }
        String path = database.path == null ? SQLiteJDBC.config.defaultPath : database.path;
        File file = new File(path + database.name);
        if (!file.exists()){
            throw new RuntimeException("DB Not Found: " + file.getAbsolutePath());
        }
        log.info("DB: " + file.getAbsolutePath());

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:C:\\My Data\\Mezzmo\\Mezzmo.db");
            c.setAutoCommit(false);
            log.info("Opened database successfully");
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        log.info("Initializing done");
    }

    public Connection getConnection(){
        return c;
    }

    public void commit() throws SQLException {
        if (c != null){
            c.commit();
        }
    }

    private DataBaseConfiguration.DataBase getDatabase (String Id){
        for (DataBaseConfiguration.DataBase db : config.databases){
            if (db.id.equals(Id)){
                return db;
            }
        }
        return null;
    }

}