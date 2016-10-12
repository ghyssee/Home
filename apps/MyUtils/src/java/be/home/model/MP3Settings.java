package be.home.model;

/**
 * Created by ghyssee on 8/07/2016.
 */
public class MP3Settings {
    public String album;
    public int lastPlayedSleep;

    public Synchronizer synchronizer;
    public Rating rating;

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
}
