package be.home.main;

import be.home.model.AlbumInfo;
import be.home.model.ConfigTO;
import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.exceptions.ApplicationException;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ghyssee on 20/02/2015.
 */
public class MP3PreProcessorV2 extends BatchJobV2 {

    private static final String VERSION = "V1.0";
    private static final String CD_TAG = "cd";
    private static final String ALBUM_TAG = "Album:";
    /* if the RENUM tag is found, than the track info from the file will be ignored and the automatic counter
       will be used
    */
    private static final String RENUM_TAG = "{RENUM}";
    private static final String FILE = "Album.txt";

    public static ConfigTO.Config config;
    private static final Logger log = Logger.getLogger(MP3PreProcessor.class);

    public enum TAGS {

        TRACK {
            @Override
            public void method(AlbumInfo.Track track, String item, boolean duration) {
                item = removeDuration(item, duration);
                track.track =  StringUtils.leftPad(item, 2, "0");
            }
        },
        ARTIST {
            @Override
            public void method(AlbumInfo.Track track, String item, boolean duration) {

                item = removeDuration(item, duration);
                track.artist = item;
            }
        },
        TITLE {
            @Override
            public void method(AlbumInfo.Track track, String item, boolean duration) {
                item = removeDuration(item, duration);
                track.title = item;
            }
        }; // note the semi-colon after the final constant, not just a comma!
        public String removeDuration (String item, boolean duration){
            if (duration){
                item = MP3Helper.getInstance().removeDurationFromString(item);
            }
            return item;
        }


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
        MP3PreprocessorConfig mp3PreprocessorConfig = (MP3PreprocessorConfig) JSONUtils.openJSON(
                Setup.getInstance().getFullPath(Constants.Path.CONFIG) + File.separator + "MP3Preprocessor.json", MP3PreprocessorConfig.class);
        MP3PreprocessorConfig.ConfigItem configItem = findConfiguration(mp3PreprocessorConfig.configurations, mp3PreprocessorConfig.activeConfiguration);
        if (configItem == null){
            throw new ApplicationException("Active Configuration not found: " + mp3PreprocessorConfig.activeConfiguration);
        }
        String pattern = constructPattern(mp3PreprocessorConfig, configItem.config);
        log.info("Pattern: " + pattern);
        Path file = Paths.get(Setup.getInstance().getFullPath(Constants.Path.PREPROCESS) + File.separator + FILE);
        BufferedReader reader2 = Files.newBufferedReader(file, Charset.forName("UTF-8"));
        String line = null;
        AlbumInfo info = new AlbumInfo();
        AlbumInfo.Config configAlbum = info.new Config();
        configAlbum.total = 0;
        AtomicInteger counter = new AtomicInteger(0);
        while ((line = reader2.readLine()) != null) {
            if (StringUtils.isNotBlank(line)) {
                processLine(configAlbum, configAlbum.tracks, line, mp3PreprocessorConfig, configItem, pattern, counter);
            }
        }
        reader2.close();
        JSONUtils.writeJsonFile(configAlbum, Setup.getInstance().getFullPath(Constants.Path.PROCESS) + File.separator + "Album.json");

    }

    private MP3PreprocessorConfig.ConfigItem findConfiguration(List <MP3PreprocessorConfig.ConfigItem> list, String config){
        MP3PreprocessorConfig.ConfigItem foundConfig = null;
        for (MP3PreprocessorConfig.ConfigItem configItem : list){
            if (configItem.id.equals(config)){
                log.info("Active configuration found: " + config);
                foundConfig = configItem;
                break;
            }
        }
        return foundConfig;
    }

    private String constructPattern(MP3PreprocessorConfig mp3Config, List <MP3PreprocessorConfig.ConfigRecord> list){

        String pattern= "";
        for (MP3PreprocessorConfig.ConfigRecord c :list){
            MP3PreprocessorConfig.Pattern p = findPattern(mp3Config.getPatterns(), c.type);
            pattern += p.pattern;
            if (c.splitter != null){
                p = findPattern(mp3Config.getSplitters(), c.splitter);
                pattern += p.pattern;
            }
        }
        return pattern;
    }

    private MP3PreprocessorConfig.Pattern findPattern (Map <String, MP3PreprocessorConfig.Pattern> map, String id){

        MP3PreprocessorConfig.Pattern p = map.get(id);
        if (p== null) {
            throw new ApplicationException("Pattern not found:" + id);
        }
        return p;
    }

    private boolean CheckSpecialTags(AlbumInfo.Config album, String tmp){
        String tmpLine = tmp.trim();
        if (tmpLine.equals(RENUM_TAG)){
            album.renum = true;
            return true;
        }
        return false;
    }


    private void processLine(AlbumInfo.Config album, List <AlbumInfo.Track> tracks, String line,
                             MP3PreprocessorConfig mp3Config, MP3PreprocessorConfig.ConfigItem configItem,
                             String sPattern, AtomicInteger counter){
        String tmp = MP3Helper.getInstance().replaceSpecialCharacters(line.trim());
        log.info(tmp);
        if (CheckSpecialTags(album, tmp)){
            log.info("Special Tag Found");
            counter.set(1);
        }
        else if (checkCDTag(album, tmp)){
            log.info("CD Tag found");
            counter.set(1);
        }
        else if (checkAlbumTag(album, tmp)){
            log.info("Album Tag found");
            counter.set(1);
        }
        else {
            log.debug("patternString: " + sPattern);
            Pattern pattern = Pattern.compile(sPattern);
            Matcher matcher = pattern.matcher(tmp);
            if (matcher.matches()) {
                AlbumInfo.Track track = splitString(mp3Config, configItem, tmp);
                if (album.total > 0) {
                    track.cd = String.valueOf(album.total);
                }
                if (track.track == null || album.renum){
                    track.track = String.format("%02d", counter.get());
                }
                log.info("Track: " + track.track);
                log.info("Artist: " + track.artist);
                log.info("Title: " + track.title);
                tracks.add(track);
                counter.incrementAndGet();
            }
        }
    }

    private boolean checkCDTag(AlbumInfo.Config album, String line){
        String cd = null;
        // CD TAG followed by zero or more spaces and 1 or more numbers
        Pattern pattern = Pattern.compile(CD_TAG.toUpperCase() + "( *)[1-9]{1,}");
        Matcher matcher2 = pattern.matcher(line.toUpperCase());
        boolean cdTagFound = false;
        if (matcher2.matches()) {
            String[] array = line.toUpperCase().split(CD_TAG.toUpperCase());
            cd = array[1].trim();
            cd = cd.replaceAll("[^0-9]", "");
            album.total = Integer.parseInt(cd);
            cdTagFound = true;
        }
        return cdTagFound;
    }

    private boolean checkAlbumTag(AlbumInfo.Config album, String line){
        Pattern pattern = Pattern.compile(ALBUM_TAG + "(.*)");
        Matcher matcher2 = pattern.matcher(line);
        boolean tagFound = false;
        if (matcher2.matches()) {
            String[] array = line.split(ALBUM_TAG);
            album.album = array[1].trim();
            tagFound = true;
        }
        return tagFound;
    }

    private AlbumInfo.Track splitString(MP3PreprocessorConfig mp3Config, MP3PreprocessorConfig.ConfigItem configItem, String line){

        AlbumInfo albumInfo = new AlbumInfo();
        AlbumInfo.Track track = albumInfo.new Track();
        String rest = line;
        for (MP3PreprocessorConfig.ConfigRecord tmp : configItem.config){
            TAGS tag = TAGS.valueOf(tmp.type);
            if (tmp.splitter != null) {
                MP3PreprocessorConfig.Pattern split = findPattern(mp3Config.getSplitters(), tmp.splitter);
                String[] array1= rest.split(split.pattern,2);
                if (array1.length >= 2){
                    rest = "";
                    for (int i= 1; i < array1.length; i++){
                        rest += array1[i];
                    }
                }
                tag.method(track, array1[0].trim(), tmp.duration);
            }
            else {
                rest = rest.trim().replaceAll("\\.mp3$", "");
                tag.method(track, rest, tmp.duration);
            }
        }

        return track;
    }

}
