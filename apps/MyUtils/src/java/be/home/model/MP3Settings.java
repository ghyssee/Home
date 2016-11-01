package be.home.model;

/**
 * Created by ghyssee on 8/07/2016.
 */
public class MP3Settings {
    public String album;

    public Synchronizer synchronizer;
    public Rating rating;
    public Mezzmo mezzmo;
    public MediaMonkey mediaMonkey;

    public LastPlayedSong getLastPlayedSong() {
        return lastPlayedSong;
    }

    public LastPlayedSong lastPlayedSong;

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

    public class MediaMonkey {
        public String base;
        public Playlist playlist;
    }

    public class LastPlayedSong {
        public int number;
        public String scrollBackgroundColor;
        public String scrollColor;
        public boolean scrollShowAlbum;

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getScrollBackgroundColor() {
            return scrollBackgroundColor;
        }

        public void setScrollBackgroundColor(String scrollBackgroundColor) {
            this.scrollBackgroundColor = scrollBackgroundColor;
        }

        public String getScrollColor() {
            return scrollColor;
        }

        public void setScrollColor(String scrollColor) {
            this.scrollColor = scrollColor;
        }

        public boolean isScrollShowAlbum() {
            return scrollShowAlbum;
        }

        public void setScrollShowAlbum(boolean scrollShowAlbum) {
            this.scrollShowAlbum = scrollShowAlbum;
        }

    }

}
