package be.home.common.dao.jdbc;

/**
 * Created by ghyssee on 9/02/2016.
 */
import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.model.DataBaseConfiguration;
import be.home.common.utils.JSONUtils;
import be.home.common.utils.WinUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.sql.*;
import java.util.*;

public class SQLiteJDBC
{
    private static SQLiteJDBC instance = null;
    public static DataBaseConfiguration config = null;
    public static Map <String, DataBaseConfiguration.DataBase> dbMap = new HashMap <String, DataBaseConfiguration.DataBase>();
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
        File file = new File (Setup.getInstance().getFullPath(Constants.Path.LOCAL_CONFIG) + File.separator + "localDatabases.json");
        Map <String, DataBaseConfiguration.DataBase> map = new HashMap <String, DataBaseConfiguration.DataBase>();
        if (file.exists()){
            log.debug("Local Database Configuration File found: " + file.getAbsolutePath());
            DataBaseConfiguration localConfig = (DataBaseConfiguration) JSONUtils.openJSON(file.getAbsolutePath(), DataBaseConfiguration.class);
            map = localConfig.getMap();
        }
        //file = new File(workingDir + "/config/databases.json");
        file = new File(Constants.Path.BASE_CONFIG_DIR + File.separator + "databases.json");
        log.debug("Master Database Configuration File found: " + file.getAbsolutePath());
        config = (DataBaseConfiguration) JSONUtils.openJSON(file.getAbsolutePath(), DataBaseConfiguration.class);
        List <DataBaseConfiguration.DataBase> newList = new ArrayList<DataBaseConfiguration.DataBase>();
        for (DataBaseConfiguration.DataBase db : config.databases) {
            DataBaseConfiguration.DataBase tmpDB = null;
            if (map.containsKey(db.id)) {
                tmpDB = map.get(db.id);
                log.debug("Overriding DB Config with Local DB Config for id: " + db.id);
            }
            else {
                tmpDB = db;
            }
            replaceEnvrionmentVariables(tmpDB);
            newList.add(tmpDB);
        }
        config.databases = newList;



        log.info("Database Config file = " + file.getAbsolutePath());

    }

    private static void replaceEnvrionmentVariables(DataBaseConfiguration.DataBase db){
        final String ONEDRIVE = "%ONEDRIVE%";
        if (db.path.contains(ONEDRIVE)){
            db.path = db.path.replace(ONEDRIVE, WinUtils.getOneDrivePath());
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