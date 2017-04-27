package be.home.mezzmo.domain.model.json;

import java.util.List;

/**
 * Created by Gebruiker on 10/03/2017.
 */
public class MultiArtistConfig {

    public List<Splitter> splitters;
    public String splitterEndId;

    public class Splitter {

        public String id;
        public String value1;
        public String value2;
        public String getId() {
            return id;
        }
    }

    public List<Item> list;

    public class Item{

        public String id;
        public boolean exactPosition;
        public String master;
        public List<Artist> artists;
        public List<ArtistSequenceItem> artistSequence;
        public String getId() {
            return id;
        }
        public class Artist {
            public String id;
        }

        public class ArtistSequenceItem {
            public String artistId;
            public String splitterId;
        }
    }

}
