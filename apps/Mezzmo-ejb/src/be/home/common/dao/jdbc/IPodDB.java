package be.home.common.dao.jdbc;

import be.home.common.model.DataBaseConfiguration;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ghyssee on 27/09/2016.
 */
public class IPodDB extends SQLiteJDBC {

    public static Map instances = new HashMap();
    private static final String database = Databases.IPOD.name();

    protected IPodDB(){
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
