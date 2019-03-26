package be.home.common.dao.jdbc;

import be.home.common.model.DataBaseConfiguration;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ghyssee on 29/04/2016.
 */
public class MezzmoDB extends SQLiteJDBC {

    public static Map instances = new HashMap();
    private static final String database = Databases.MEZZMO.name();
    public static final String MP3_EXT = "mp3";

    protected MezzmoDB(){
    }

    private DataBaseConfiguration.DataBase db = null;

    public synchronized static SQLiteJDBC getInstance() {
        SQLiteJDBC.initialize();
        SQLiteJDBC instance = (SQLiteJDBC) instances.get(database);
        if(instance == null) {
            instance = new SQLiteJDBC();
            instance.openDatabase(database);
            instances.put(database, instance);
        }
        return instance;
    }

    public synchronized static SQLiteJDBC getInstance(String newDatabase) {
        SQLiteJDBC.initialize();
        if (newDatabase == null){
            newDatabase = database;
        }
        SQLiteJDBC instance = (SQLiteJDBC) instances.get(newDatabase);
        if(instance == null) {
            instance = new SQLiteJDBC();
            instance.openDatabase(newDatabase);
            instances.put(newDatabase, instance);
        }
        return instance;
    }

}
