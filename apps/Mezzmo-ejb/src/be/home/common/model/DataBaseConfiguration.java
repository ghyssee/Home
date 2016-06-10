package be.home.common.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ghyssee on 29/04/2016.
 */
public class DataBaseConfiguration {

        public String defaultPath;
        public List <DataBase> databases = new ArrayList <DataBase>();

    public class DataBase{
        public String id;
        public String path;
        public String name;
    }

}
