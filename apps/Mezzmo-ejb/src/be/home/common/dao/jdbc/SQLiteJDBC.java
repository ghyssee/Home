package be.home.common.dao.jdbc;

/**
 * Created by ghyssee on 9/02/2016.
 */
import be.home.common.constants.Constants;
import be.home.common.model.DataBaseConfiguration;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.apache.log4j.Logger;

import java.io.*;
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
            c = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
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

    public static void initialize(String workingDir){
        InputStream i = null;
        //String localConfig = workingDir + "/config/localDatabases.json";
        File file = new File (Constants.Path.BASE_LOCAL_CONFIG_DIR + File.separator + "localDatabases.json");
        if (!file.exists()){
            //file = new File(workingDir + "/config/databases.json");
            file = new File(Constants.Path.BASE_CONFIG_DIR + File.separator + "databases.json");
        }
        log.info("Database Config file = " + file.getAbsolutePath());
        try {
            i = new FileInputStream(file);
            Reader reader = new InputStreamReader(i, "UTF-8");
            JsonReader r = new JsonReader(reader);
            Gson gson = new Gson();
            DataBaseConfiguration dbConfig = gson.fromJson(r, DataBaseConfiguration.class);
            config = dbConfig;
            r.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to find database configuration file");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Problem processing database configuration file");
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