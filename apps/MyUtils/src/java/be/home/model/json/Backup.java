package be.home.model.json;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gebruiker on 30/12/2016.
 */
public class Backup {

    public List<Scheme> schemeList = new ArrayList<>();
    public List<DefaultScheme> defaultSchemes;
    public List<SchemeItem> schemes = new ArrayList<>();

    public class SchemeItem {
        public String id;
        public List<SchemeId> list;
    }

    public class SchemeId {
        public String id;
    }

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

    public class DefaultScheme {
        public String host;
        public String id;
    }
}
