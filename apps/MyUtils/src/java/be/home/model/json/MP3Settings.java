package be.home.model.json;

import be.home.common.configuration.Setup;

import java.util.List;

/**
 * Created by ghyssee on 8/07/2016.
 */
public class MP3Settings {
    public String album;
    public String albumArtist;
    public String albumYear;

    public Synchronizer synchronizer;
    public Rating rating;
    public Mezzmo mezzmo;
    public MediaMonkey mediaMonkey;
    public LastPlayedSong lastPlayedSong;

    public LastPlayedSong getLastPlayedSong() {
        return lastPlayedSong;
    }

    public class Export{
        public String music;
        public String iPod;

        public String getiPod() {
            return Setup.replaceEnvironmentVariables(iPod);
        }

        public String getMusic() {
            return Setup.replaceEnvironmentVariables(music);
        }

    }


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

    public class Import {
        public String base;
        public String filename;
    }

    public class MezzmoBase {
        public String id;
        public String description;
        public String path;
    }

    public class Mezzmo {
        public String base;
        public boolean synchronizePlaycount;
        public Playlist playlist;
        public Import importF;
        public Export export;
        public List<MezzmoBase> baseList;
        public MezzmoActiveBase activeBase;
        public Mp3Checker mp3Checker;

        public class Mp3Checker {
            public boolean check;
            public int maxNumberOfErrors;
        }
    }



    public class MezzmoActiveBase {
        public String defaultId;
        public List<MezzmoActiveBaseItem> list;
    }


    public class MezzmoActiveBaseItem {
        public String host;
        public String id;
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
