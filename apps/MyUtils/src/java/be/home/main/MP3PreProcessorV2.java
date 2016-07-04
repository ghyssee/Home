package be.home.main;

import be.home.mezzmo.domain.model.MGOFileAlbumCompositeTO;
import be.home.mezzmo.domain.model.MezzmoJavaBeanFactory;
import be.home.model.AlbumInfo;
import be.home.model.ConfigTO;
import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.exceptions.ApplicationException;
import be.home.common.logging.Log4GE;
import be.home.common.main.BatchJobV2;
import be.home.common.utils.JSONUtils;
import be.home.domain.model.MP3Helper;
import be.home.model.MP3PreprocessorConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ghyssee on 20/02/2015.
 */
public class MP3PreProcessorV2 extends BatchJobV2 {

    private static final String VERSION = "V1.0";
    // Set Duration to null to disable the removal of the duration for the specified field
    //private static final TAGS[] FORMAT_TRACK = {TAGS.ARTIST, TAGS.TITLE};
    private static final TAGS[] FORMAT_TRACK = {TAGS.TRACK, TAGS.ARTIST, TAGS.TITLE};
    private static final TAGS DURATION = null;// TAGS.ARTIST;
    private static final String CD_TAG = "Tracklist";
    private static final String ALBUM_TAG = "Album:";
    private static final String FILE = "album.txt";
    private static final String SEPERATOR_1 = "\\.";
    //private static final String SEPERATOR_3 = " - ";
    private static final String SEPERATOR_2 = "[-|–]";



    public static Log4GE log4GE;
    public static ConfigTO.Config config;
    private static final Logger log = Logger.getLogger(MP3PreProcessor.class);

    public enum TAGS {

        TRACK {
            @Override
            public void method(AlbumInfo.Track track, String item, boolean duration) {
                System.out.println("first enum constant behavior!");
                track.track =  StringUtils.leftPad(item, 2, "0");
            }
        },
        ARTIST {
            @Override
            public void method(AlbumInfo.Track track, String item, boolean duration) {
                track.artist = item;
                System.out.println("second enum constant behavior!");
            }
        },
        TITLE {
            @Override
            public void method(AlbumInfo.Track track, String item, boolean duration) {
                track.title = item;
                System.out.println("third enum constant behavior!");
            }
        }; // note the semi-colon after the final constant, not just a comma!
        public abstract void method(AlbumInfo.Track track, String item, boolean duration); // could also be in an interface that MyEnum implements
        }


    public static void main(String args[]) {

        String currentDir = System.getProperty("user.dir");
        log.info("Current Working dir: " + currentDir);


        MP3PreProcessorV2 instance = new MP3PreProcessorV2();
        instance.printHeader("ZipFiles " + VERSION, "=");

        try {
            instance.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

    }

    public void start() throws IOException {
        MP3PreprocessorConfig mp3Config = (MP3PreprocessorConfig) JSONUtils.openJSON(
                Setup.getInstance().getFullPath(Constants.Path.CONFIG) + File.separator + "MP3Preprocessor.json", MP3PreprocessorConfig.class);
        String pattern = constructPattern(mp3Config);
        log.info("Pattern: " + pattern);
        Path file = Paths.get(Setup.getInstance().getFullPath(Constants.Path.PREPROCESS) + File.separator + FILE);
        BufferedReader reader2 = Files.newBufferedReader(file, Charset.defaultCharset());
        String line = null;
        AlbumInfo info = new AlbumInfo();
        AlbumInfo.Config configAlbum = info.new Config();
        configAlbum.total = 0;
        while ((line = reader2.readLine()) != null) {
            if (StringUtils.isNotBlank(line)) {
                processLine(configAlbum, configAlbum.tracks, line, mp3Config, pattern);
            }
        }
        reader2.close();
        JSONUtils.writeJsonFile(configAlbum, Setup.getInstance().getFullPath(Constants.Path.PROCESS) + File.separator + "Album.json");

    }

    private String constructPattern(MP3PreprocessorConfig mp3Config){

        String pattern= "";
        for (MP3PreprocessorConfig.ConfigRecord c : mp3Config.config1){
            MP3PreprocessorConfig.Pattern p = findPattern(mp3Config.patterns, c.type);
            pattern += p.pattern;
            if (c.splitter != null){
                p = findPattern(mp3Config.splitters, c.splitter);
                pattern += p.pattern;
            }
        }
        return pattern;
    }

    private MP3PreprocessorConfig.Pattern findPattern (List <MP3PreprocessorConfig.Pattern> list, String id){
        for (MP3PreprocessorConfig.Pattern record : list){
            if (id.equals(record.id)){
                return record;
            }
        }
        throw new ApplicationException("Pattern not found:" + id);
    }

    private void processLine(AlbumInfo.Config album, List <AlbumInfo.Track> tracks, String line,
                             MP3PreprocessorConfig patternConfig, String sPattern){
        String tmp = line.trim();
        log.info(tmp);
        if (checkCDTag(album, tmp)){
            log.info("CD Tag found");
        }
        else if (checkAlbumTag(album, tmp)){
            log.info("Album Tag found");
        }
        else {
            System.out.println("patternString: " + sPattern);
            Pattern pattern = Pattern.compile(sPattern);
            Matcher matcher = pattern.matcher(tmp);
            if (matcher.matches()) {
                AlbumInfo.Track track = splitString(patternConfig, tmp);
                if (album.total > 0) {
                    track.cd = StringUtils.leftPad(String.valueOf(album.total), 2);
                }
                if (track.track == null){
                    track.track = String.format("%02d", album.tracks.size()+1);
                }
                tracks.add(track);
            }
        }
    }

    private boolean checkCDTag(AlbumInfo.Config album, String line){
        String cd = null;
        Pattern pattern = Pattern.compile(CD_TAG.toUpperCase() + "(.*)");
        Matcher matcher2 = pattern.matcher(line.toUpperCase());
        boolean cdTagFound = false;
        if (matcher2.matches()) {
            String[] array = line.toUpperCase().split(CD_TAG.toUpperCase());
            cd = array[1].trim();
            album.total = Integer.parseInt(cd);
            cdTagFound = true;
        }
        return cdTagFound;
    }

    private boolean checkAlbumTag(AlbumInfo.Config album, String line){
        Pattern pattern = Pattern.compile(ALBUM_TAG.toUpperCase() + "(.*)");
        Matcher matcher2 = pattern.matcher(line.toUpperCase());
        boolean tagFound = false;
        if (matcher2.matches()) {
            String[] array = line.toUpperCase().split(ALBUM_TAG.toUpperCase());
            album.album = array[1].trim();
            tagFound = true;
        }
        return tagFound;
    }

    private AlbumInfo.Track splitString(MP3PreprocessorConfig config, String line){

        AlbumInfo albumInfo = new AlbumInfo();
        AlbumInfo.Track track = albumInfo.new Track();
        String rest = line;
        for (MP3PreprocessorConfig.ConfigRecord tmp : config.config1){
            TAGS tag = TAGS.valueOf(tmp.type);
            if (tmp.splitter != null) {
                MP3PreprocessorConfig.Pattern split = findPattern(config.splitters, tmp.splitter);
                String[] array1= rest.split(split.pattern,2);
                if (array1.length >= 2){
                    rest = "";
                    for (int i= 1; i < array1.length; i++){
                        rest += array1[i];
                    }
                }
                tag.method(track, array1[0].trim(), tmp.duration);
                //fillInfo(track, array1[0], FORMAT_TRACK[0]);
            }
            else {
                tag.method(track, rest.trim(), tmp.duration);
            }
        }

        /*
            String[] array1= line.split(SEPERATOR_1,2);
        //String[] array2 = array1[1].split("[" + SEPERATOR_2 + "|" + SEPERATOR_3 + "]", 2);
        String[] array2 = array1[1].split(SEPERATOR_2, 2);
        if (array2.length < 2){
            throw new ApplicationException("Invalid format. No seperator Found: " +  SEPERATOR_2 + " For The Line " + line);
        }
        fillInfo(track, array1[0], FORMAT_TRACK[0]);
        fillInfo(track, array2[0], FORMAT_TRACK[1]);
        if (FORMAT_TRACK.length > 2){
            fillInfo(track, array2[1], FORMAT_TRACK[2]);
        }
        else {
            //fillInfo(track, "1", FORMAT_TRACK[2]);
        }*/
        System.out.println("Track: "+ track.track);
        System.out.println("Artist: "+ track.artist);
        System.out.println("Title: "+ track.title);
        return track;
    }

    private void fillInfo(AlbumInfo.Track track, String part, TAGS type){

        if (DURATION != null && DURATION.equals(type)) {
            MP3Helper helper = new MP3Helper();
            part = helper.removeDurationFromString(part);
        }
        part = part.trim();
        switch (type) {
            case TRACK:
                track.track = StringUtils.leftPad(part, 2, "0");
                break;
            case ARTIST:
                track.artist = part;
                break;
            case TITLE:
                track.title = part;
                break;
        }
    }

}
