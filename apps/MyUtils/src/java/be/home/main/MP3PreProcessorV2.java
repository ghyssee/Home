package be.home.main;

import be.home.common.mp3.Mp3Tags;
import be.home.model.json.AlbumInfo;
import be.home.model.ConfigTO;
import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.exceptions.ApplicationException;
import be.home.common.main.BatchJobV2;
import be.home.common.utils.JSONUtils;
import be.home.domain.model.MP3Helper;
import be.home.model.json.MP3PreprocessorConfig;
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
    /* if the RENUM tag is found, than the track info from the file will be ignored and the automatic counter
       will be used
    */
    private static final String RENUM_TAG = "{RENUM}";

    public static ConfigTO.Config config;
    private static final Logger log = getMainLog(MP3PreProcessorV2.class);

    public static void main(String args[]) {

        String currentDir = System.getProperty("user.dir");
        log.info("Current Working dir: " + currentDir);

        MP3PreProcessorV2 instance = new MP3PreProcessorV2();
        instance.printHeader("ZipFiles " + VERSION, "=");

        try {
           instance.start();
           // instance.startTst();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

    }

    public enum PatternType {PREFIX, SUFFIX}

    public String replacePattern(String line, String rep, PatternType patternType){
        if (rep != null) {
            String pat = "(.*)";
            switch (patternType) {
                case PREFIX:
                    pat = rep + pat;
                    break;
                case SUFFIX:
                    pat = pat + rep;
                    break;
            }
            Pattern pattern = Pattern.compile(pat);
            Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                line = matcher.replaceAll("$1");
            }
        }
        return line;
    }

    public void startTst() throws IOException {
        String prefix = "Hitexplosion - ";
        String suffix = ".mp3";
        String line = "Hitexplosion - 01 - Loona.mp3Hitexplosion -  - Badam (Edit)Hitexplosion - .mp3";
        line = replacePattern(line, prefix, PatternType.PREFIX);
        line = replacePattern(line, suffix, PatternType.SUFFIX);
        System.out.println("line: " + line);
        line = "01 - Loona.mp3Hitexplosion -  - Badam (Edit)";
        line = replacePattern(line, prefix, PatternType.PREFIX);
        line = replacePattern(line, suffix, PatternType.SUFFIX);
        line = "Album: Future Trance Vol. 77 (2016)";
        System.out.println("line: " + line);
        Pattern pattern = Pattern.compile("Album:(.*)");
        Matcher matcher2 = pattern.matcher(line);
        boolean tagFound = false;
        if (matcher2.matches()) {
            System.out.println("album found");
        }



    }

    public void start() throws IOException {
        MP3PreprocessorConfig mp3PreprocessorConfig = (MP3PreprocessorConfig) JSONUtils.openJSONWithCode(Constants.JSON.MP3PREPROCESSOR, MP3PreprocessorConfig.class);
        MP3PreprocessorConfig.ConfigItem configItem = findConfiguration(mp3PreprocessorConfig.configurations, mp3PreprocessorConfig.activeConfiguration);
        if (configItem == null){
            throw new ApplicationException("Active Configuration not found: " + mp3PreprocessorConfig.activeConfiguration);
        }
        String pattern = constructPattern(mp3PreprocessorConfig, configItem.config);
        log.info("Pattern: " + pattern);
        Path file = Paths.get(Setup.getInstance().getFullPath(Constants.FILE.ALBUM));
        BufferedReader reader2 = Files.newBufferedReader(file, Charset.forName("UTF-8"));
        String line = null;
        AlbumInfo info = new AlbumInfo();
        AlbumInfo.Config configAlbum = info.new Config();
        setAlbumTag(mp3PreprocessorConfig, configAlbum);
        configAlbum.total = 0;
        AtomicInteger counter = new AtomicInteger(0);
        while ((line = reader2.readLine()) != null) {
            if (StringUtils.isNotBlank(line)) {
                processLine(configAlbum, configAlbum.tracks, line, mp3PreprocessorConfig, configItem, pattern, counter);
            }
        }
        reader2.close();
        log.info("Renum (General): " + mp3PreprocessorConfig.renum);
        log.info("Renum (Album): " + configAlbum.renum);
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
        line = MP3Helper.getInstance().replaceSpecialCharacters(line.trim());
        log.info(line);
        if (CheckSpecialTags(album, line)){
            log.info("Special Tag Found");
            counter.set(1);
        }
        else if (checkCDTag(mp3Config, album, line)){
            log.info("CD Tag found");
            counter.set(1);
        }
        /*
        else if (checkAlbumTag(mp3Config, album, line)){
            log.info("Album Tag found");
            counter.set(1);
        }*/
        else {
            log.debug("patternString: " + sPattern);
            line = replacePattern(line, mp3Config.prefix, PatternType.PREFIX);
            line = replacePattern(line, mp3Config.suffix, PatternType.SUFFIX);
            Pattern pattern = Pattern.compile(sPattern);
            Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                AlbumInfo.Track track = splitString(mp3Config, configItem, line);
                if (album.total > 0) {
                    track.cd = String.valueOf(album.total);
                }
                if (track.track == null || album.renum || mp3Config.renum){
                    track.track = String.format("%d", counter.get());
                }
                album.trackSize = Math.max(album.trackSize, track.track.length());
                log.info("Track: " + track.track);
                log.info("Artist: " + track.artist);
                log.info("Title: " + track.title);
                tracks.add(track);
                counter.incrementAndGet();
            }
        }
    }

    private boolean checkCDTag(MP3PreprocessorConfig mp3Config, AlbumInfo.Config album, String line){
        String cd = null;
        // CD TAG followed by zero or more spaces and 1 or more numbers
        Pattern pattern = Pattern.compile(mp3Config.cdTag.toUpperCase() + "( *)[1-9]{1,}");
        Matcher matcher2 = pattern.matcher(line.toUpperCase());
        boolean cdTagFound = false;
        if (matcher2.matches()) {
            String[] array = line.toUpperCase().split(mp3Config.cdTag.toUpperCase());
            cd = array[1].trim();
            cd = cd.replaceAll("[^0-9]", "");
            album.total = Integer.parseInt(cd);
            cdTagFound = true;
        }
        return cdTagFound;
    }

    private boolean checkAlbumTag(MP3PreprocessorConfig mp3Config, AlbumInfo.Config album, String line){
        // possible problem with UTF-8 BOM, than first characters are special and
        // album is not found
        Pattern pattern = Pattern.compile(mp3Config.albumTag + "(.*)");

        Matcher matcher2 = pattern.matcher(line);
        boolean tagFound = false;
        if (matcher2.matches()) {
            String[] array = line.split(mp3Config.albumTag);
            album.album = array[1].trim();
            tagFound = true;
        }
        return tagFound;
    }


    private void setAlbumTag(MP3PreprocessorConfig mpPreprocessorConfig, AlbumInfo.Config album){
        // possible problem with UTF-8 BOM, than first characters are special and
        // album is not found
        album.album = mpPreprocessorConfig.album;
    }


    private AlbumInfo.Track splitString(MP3PreprocessorConfig mp3Config, MP3PreprocessorConfig.ConfigItem configItem, String line){

        AlbumInfo albumInfo = new AlbumInfo();
        AlbumInfo.Track track = albumInfo.new Track();
        String rest = line;
        for (MP3PreprocessorConfig.ConfigRecord tmp : configItem.config){
            Mp3Tags tag = Mp3Tags.valueOf(tmp.type);
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
