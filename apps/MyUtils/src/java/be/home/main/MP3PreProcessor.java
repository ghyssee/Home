package be.home.main;

import be.home.common.constants.Constants;
import be.home.common.logging.Log4GE;
import be.home.common.main.BatchJobV2;
import be.home.domain.model.MP3Helper;
import be.home.model.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
public class MP3PreProcessor extends BatchJobV2 {

    private static final String VERSION = "V1.0";
    private static final TAGS[] FORMAT_TRACK = {TAGS.TRACK, TAGS.TITLE, TAGS.ARTIST};
    // Set Duration to null to disable the removal of the duration for the specified field
    private static final TAGS DURATION = TAGS.ARTIST;
    private static final String CD_TAG = "CD";
    private static final String FILE = "albuminfo.txt";
    private static final String SEPERATOR_1 = "\\.";
    private static final String SEPERATOR_2 = " - ";


    public static Log4GE log4GE;
    public static ConfigTO.Config config;
    private static final Logger log = Logger.getLogger(ZipFiles.class);
    private static ParamTO PARAMS [] = {new ParamTO("-source", new String[]{"This is the source directory to start the backup", "of files and folders"},
            ParamTO.REQUIRED),
            new ParamTO("-zipFile", new String[]{"This is the name of the zipfile"},
                    ParamTO.REQUIRED)
    };

    public enum TAGS {
        TRACK, ARTIST, TITLE;
    }

    public static void main(String args[]) {

        String currentDir = System.getProperty("user.dir");
        log.info("Current Working dir: " + currentDir);


        MP3PreProcessor instance = new MP3PreProcessor();
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

        Path file = Paths.get(Constants.Path.MP3_PREPROCESSOR + File.separator + FILE);
        BufferedReader reader2 = Files.newBufferedReader(file, Charset.defaultCharset());
        StringBuilder content = new StringBuilder();
        String line = null;
        AlbumInfo info = new AlbumInfo();
        AlbumInfo.Config configAlbum = info.new Config();
        configAlbum.total = 0;
        while ((line = reader2.readLine()) != null) {
            if (StringUtils.isNotBlank(line)) {
                processLine(configAlbum, configAlbum.tracks, line);
            }
        }
        writeJsonFile(configAlbum);


    }

    private void writeJsonFile(AlbumInfo.Config album) throws IOException {
        Writer writer = new FileWriter(Constants.Path.MP3_PROCESSOR + File.separator + "Album.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        gson.toJson(album, writer);
        writer.flush();
        writer.close();
    }

    private void processLine(AlbumInfo.Config album, List <AlbumInfo.Track> tracks, String line){
        String tmp = line.trim();
        System.out.println(tmp);
        String cd = null;
        Pattern pattern = Pattern.compile(CD_TAG + "(.*)");
        Matcher matcher = pattern.matcher(tmp);
        matcher = pattern.matcher(tmp.toUpperCase());
        if (matcher.matches()) {
            String[] array = tmp.toUpperCase().split(CD_TAG);
            cd = array[1].trim();
            album.total = Integer.parseInt(cd);
        }
        pattern = Pattern.compile("[0-9]+(" + SEPERATOR_1 + ").+[" + SEPERATOR_2 + "].*");
        matcher = pattern.matcher(tmp);
        if (matcher.matches()) {
            AlbumInfo.Track track = splitString(tmp);
            if (album.total > 0) {
                track.cd = StringUtils.leftPad(String.valueOf(album.total), 2);
            }
            tracks.add(track);
        }
    }

    private AlbumInfo.Track splitString(String line){

        String[] array1= line.split(SEPERATOR_1,2);
        AlbumInfo albumInfo = new AlbumInfo();
        AlbumInfo.Track track = albumInfo.new Track();
        String[] array2 = array1[1].split(SEPERATOR_2, 2);
        fillInfo(track, array1[0], FORMAT_TRACK[0]);
        fillInfo(track, array2[0], FORMAT_TRACK[1]);
        fillInfo(track, array2[1], FORMAT_TRACK[2]);
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