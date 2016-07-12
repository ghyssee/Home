package be.home.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gebruiker on 10/07/2016.
 */
public class Playlist {

        public String id;
        public String name;
        public int limit;
        public List <Album> albums;


        public class Album {
                public String name;
        }
}
