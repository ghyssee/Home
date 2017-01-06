package be.home.model.json;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gebruiker on 30/12/2016.
 */
public class Backup {

    public List<Scheme> schemes = new ArrayList<>();

    public class Scheme {
        public String id;
        public String backupFile;
        public List<Item> items = new ArrayList<>();

        public class Item {
            public boolean subFolder;
            public String path;
            public String file;
            public String zipPath;
        }

    }
}
