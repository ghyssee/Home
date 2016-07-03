package be.home.model;

/**
 * Created by Gebruiker on 3/07/2016.
 */

import java.util.List;

public class MP3PreprocessorConfig {

    public class Pattern {

        public String id;
        public String pattern;
    }

    public List <Pattern> patterns;
    public List <Pattern> splitters;
    public List <ConfigRecord> config1;
    public List <ConfigRecord> config2;

    public class ConfigRecord {
        public String type;
        public String splitter;
        public boolean duration;
    }

 }
