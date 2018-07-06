package be.home.mezzmo.domain.model.json;

import java.util.List;

/**
 * Created by ghyssee on 18/07/2016.
 */
public class PlaylistSetup {

    public class PlaylistRecord {
        public String name;
        public String parent;
        public String type;
        public int[] limitBy;
        public int startId;
        public String combineAnd;
        public String mediaType;
        public String orderByColumn;
        public String sorting;
        public List<Condition> conditions;
    }

    public class Condition {
        public String field;
        public String columnType;
        public String operand;
        public String valueOne;
        public String valueTwo;
    }

    public List<PlaylistRecord> records;
    public int playlistTitleIndexLength;

}
