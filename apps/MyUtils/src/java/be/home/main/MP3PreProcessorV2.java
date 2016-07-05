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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ghyssee on 20/02/2015.
 */
public class MP3PreProcessorV2 extends BatchJobV2 {

    private static final String VERSION = "V1.0";
    // Set Duration to null to disable the removal of the duration for the specified field
    //private static final TAGS[] FORMAT_TRACK = {TAGS.ARTIST, TAGS.TITLE};
    private static final String CD_TAG = "cd";
    private static final String ALBUM_TAG = "Album:";
    private static final String FILE = "Album.txt";

    public static ConfigTO.Config config;
    private static final Logger log = Logger.getLogger(MP3PreProcessor.class);

    public enum TAGS {

        TRACK {
            @Override
            public void method(AlbumInfo.Track track, String item, boolean duration) {
                track.track =  StringUtils.leftPad(item, 2, "0");
            }
        },
        ARTIST {
            @Override
            public void method(AlbumInfo.Track track, String item, boolean duration) {
                track.artist = item;
            }
        },
        TITLE {
            @Override
            public void method(AlbumInfo.Track track, String item, boolean duration) {
                track.title = item;
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
                             MP3PreprocessorConfig mp3Config, MP3PreprocessorConfig.ConfigItem configItem,
                             String sPattern, AtomicInteger counter){
        String tmp = MP3Helper.replaceSpecialCharacters(line.trim());
        log.info(tmp);
        if (checkCDTag(album, tmp)){
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
                if (track.track == null){
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

    private AlbumInfo.Track splitString(MP3PreprocessorConfig mp3Config, MP3PreprocessorConfig.ConfigItem configItem, String line){

        AlbumInfo albumInfo = new AlbumInfo();
        AlbumInfo.Track track = albumInfo.new Track();
        String rest = line;
        for (MP3PreprocessorConfig.ConfigRecord tmp : configItem.config){
            TAGS tag = TAGS.valueOf(tmp.type);
            if (tmp.splitter != null) {
                MP3PreprocessorConfig.Pattern split = findPattern(mp3Config.splitters, tmp.splitter);
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
