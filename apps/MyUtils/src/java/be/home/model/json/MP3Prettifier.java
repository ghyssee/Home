package be.home.model.json;

/**
 * Created by Gebruiker on 3/07/2016.
 */


import java.util.List;

public class MP3Prettifier {

    public class Word {

        public String oldWord;
        public String newWord;
        public boolean parenthesis;
    }

    public class Artist {
        public List <Word> words;
        public List <Word> names;
    }

    public class Song {
        public List <Word> replacements;
    }

    public List <Word> words;
    public Song song;
    public Artist artist;



}
