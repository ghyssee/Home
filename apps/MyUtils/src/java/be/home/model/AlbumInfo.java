package be.home.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ghyssee on 20/02/2015.
 */
public class AlbumInfo {

    public class Track {
        public String track;
        public String artist;
        public String title;
        public String cd;
        public List <ExtraArtist> extraArtists = new ArrayList <ExtraArtist>();
    }

    public class ExtraArtist {
        public String type;
        public String extraArtist;
    }

    private static String getMessage(Object o){

        if (o == null){
            return "Error";
        }
        else {
            return o.toString();
        }
    }

    public class Config {
        public String album;
        public int total;
        public List <Track> tracks = new ArrayList <Track>();

        public void Config(){};

    }
}
