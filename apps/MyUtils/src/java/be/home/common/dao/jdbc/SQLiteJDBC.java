package be.home.common.dao.jdbc;

/**
 * Created by ghyssee on 9/02/2016.
 */
import java.sql.*;

public class SQLiteJDBC
{
    private static SQLiteJDBC instance = null;
    protected Connection c = null;

    protected SQLiteJDBC() {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:C:\\My Data\\Mezzmo\\Mezzmo.db");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Initializing done");
    }

    public Connection getConnection(){
        return c;
    }

    public void commit() throws SQLException {
        if (c != null){
            c.commit();
        }
    }

    public synchronized static SQLiteJDBC getInstance() {
        if(instance == null) {
            System.out.println("Initializing DB");
            instance = new SQLiteJDBC();
        }
        return instance;
    }

}