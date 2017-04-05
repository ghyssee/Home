package be.home.mezzmo.domain.model.json;

/**
 * Created by Gebruiker on 3/07/2016.
 */


import java.util.List;

public class MP3Prettifier {

    public class Word {

        public String id;
        public String oldWord;
        public String newWord;
        public boolean parenthesis;
        public boolean exactMatch;
        public int priority;
    }

    public class Global {
        public List <Word> words;
        public List <Word> filenames;
    }
    public class Artist {
        public List <Word> words;
        public List <Word> names;
    }

    public class Song {
        public List <Word> replacements;
    }

    public class ArtistSong {
        public String id;
        public String artist;
        public String oldSong;
        public String newSong;
    }


    public Global global;
    public Song song;
    public Artist artist;
    public List<ArtistSong> artistSongs;

}
