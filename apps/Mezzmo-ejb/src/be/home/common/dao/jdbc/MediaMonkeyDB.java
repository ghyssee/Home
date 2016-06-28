package be.home.common.dao.jdbc;

import be.home.common.model.DataBaseConfiguration;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ghyssee on 29/04/2016.
 */
public class MediaMonkeyDB extends SQLiteJDBC {

    public static Map instances = new HashMap();
    private static final String database = Databases.MEDIAMONKEY.name();

    protected MediaMonkeyDB(){
    }

    private DataBaseConfiguration.DataBase db = null;

    public synchronized static SQLiteJDBC getInstance() {
        SQLiteJDBC instance = (SQLiteJDBC) instances.get(database);
        if(instance == null) {
            instance = new SQLiteJDBC();
            instance.openDatabase(database);
            instances.put(database, instance);
        }
        return instance;
    }

}
