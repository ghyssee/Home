package be.home.model;

/**
 * Created by Gebruiker on 3/07/2016.
 */

import com.google.common.base.Function;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class MP3PreprocessorConfig {

    public class Pattern {

        public String id;
        public String pattern;
    }

    private List <Pattern> patterns;
    public Map<String, Pattern> patternMap = null;

    private List <Pattern> splitters;
    public Map<String, Pattern> splittersMap = null;

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

    private class PatternFunction implements Function {

        @Override
        public Object apply(Object o) {
            MP3PreprocessorConfig.Pattern p = (MP3PreprocessorConfig.Pattern) o;
            return (((MP3PreprocessorConfig.Pattern) o).id);
        }

    }


    public  Map<String, Pattern> getSplitters(){

        if (splittersMap == null) {
            splittersMap = Maps.uniqueIndex(splitters, new PatternFunction());
        }
        return splittersMap;
    }


    public  Map<String, Pattern> getPatterns(){

        if (patternMap == null) {
            patternMap = Maps.uniqueIndex(patterns, new PatternFunction());
        }
        return patternMap;
    }

 }
