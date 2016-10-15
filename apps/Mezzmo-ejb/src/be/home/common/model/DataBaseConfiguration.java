package be.home.common.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ghyssee on 29/04/2016.
 */
public class DataBaseConfiguration {

        public String defaultPath;
        public Enabled sqlLogging;
        public List <DataBase> databases = new ArrayList <DataBase>();

    public class Enabled {
        public boolean enabled;
    }

    public class DataBase{
        public String id;
        public String path;
        public String name;
        public LinkDataBase linkDB;
    }

    public class LinkDataBase {
        public String name;
        public String alias;
    }

    public Map <String, DataBase> getMap(){
        Map <String, DataBase> map = new HashMap<String, DataBase>();
        for (DataBase db : databases){
            map.put(db.id, db);
        }
        return map;
    }

}
