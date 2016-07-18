package be.home.mezzmo.domain.model;

import java.util.List;

/**
 * Created by ghyssee on 18/07/2016.
 */
public class PlaylistConfig {

    public class PlaylistRecord {
        public String name;
        public String parent;
        public int type;
    }

    public List<PlaylistRecord> records;

}
