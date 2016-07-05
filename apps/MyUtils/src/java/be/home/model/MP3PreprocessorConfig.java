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
    public List <ConfigItem> configurations;
    public String activeConfiguration;

    public class ConfigItem {
        public String id;
        public List <ConfigRecord> config;
    }

    public class ConfigRecord {
        public String type;
        public String splitter;
        public boolean duration;
    }

 }
