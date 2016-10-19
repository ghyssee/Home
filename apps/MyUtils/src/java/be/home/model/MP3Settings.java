package be.home.model;

/**
 * Created by ghyssee on 8/07/2016.
 */
public class MP3Settings {
    public String album;
    public int lastPlayedSleep;

    public Synchronizer synchronizer;
    public Rating rating;
    public Mezzmo mezzmo;

    public class Synchronizer{
        public String startDirectory;
        public boolean updateRating;
    }

    public class Rating {
        public int rating1;
        public int rating2;
        public int rating3;
        public int rating4;
        public int rating5;
    }

    public class Mezzmo {
        public String base;
        public Playlist playlist;

    }

    public class Playlist {
        public String path;
        public String top20;

    }
}
