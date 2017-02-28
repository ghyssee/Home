package be.home.model.json;

import java.util.List;

/**
 * Created by ghyssee on 28/02/2017.
 */
public class SongCorrections {

    public List<Item> items;

    public class Item {

        public Long fileId;
        public String artist;
        public String title;
        public int track;
        public boolean done;
    }
}
