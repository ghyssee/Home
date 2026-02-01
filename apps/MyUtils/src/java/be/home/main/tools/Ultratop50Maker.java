package be.home.main.tools;

import be.home.common.mp3.MP3Utils;
import be.home.common.utils.MyFileWriter;
import be.home.common.utils.SortUtils;
import be.home.domain.model.ArtistSongItem;
import be.home.domain.model.service.MP3Exception;
import be.home.domain.model.service.MP3JAudioTaggerServiceImpl;
import be.home.domain.model.service.MP3Service;
import be.home.model.json.AlbumInfo;
import be.home.model.ConfigTO;
import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.exceptions.ApplicationException;
import be.home.common.logging.Log4GE;
import be.home.common.main.BatchJobV2;

import be.home.common.utils.JSONUtils;
import be.home.domain.model.MP3Helper;
import be.home.model.json.MP3Settings;
import com.mpatric.mp3agic.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

/**
 * Created by ghyssee on 20/02/2015.
 */
public class Ultratop50Maker extends BatchJobV2 {

    private static final String VERSION = "V1.0";
    private static final String VARIOUS = "Various Artists";

    public static Log4GE log4GE;
    public static ConfigTO.Config config;
    public static final String INPUT_FILE = Setup.getInstance().getFullPath(Constants.Path.PROCESS) + File.separator + "Album.json";
    private static final Logger log = getMainLog(Ultratop50Maker.class);
    private static final String SOURCE_FILE = "";
    private static final String SOURCE_MP3 = "T:\\My Music\\ipod\\";
    private static final String DEST_PATH = "c:\\temp\\ultratop\\";
    /* Copies all mp3 files from SOURCE_MP3 to DEST_PATH
       It takes album.json as source which contains the top 50 of a certain date (made with imacros 23_Ultratop_Top50.js)
     */

    public static void main(String args[]) {

        java.util.logging.Logger.getLogger("org.jaudiotagger").setLevel(java.util.logging.Level.OFF);

        Ultratop50Maker instance = new Ultratop50Maker();
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

    public boolean isType(String type, String typeToCheck){
        boolean feat = false;
        if (type != null) {
            type = type.toUpperCase();
            if (type.startsWith(typeToCheck.toUpperCase())){
                feat = true;
            }
        }
        return feat;
    }

    public String constructArtist(AlbumInfo.Track track){
        String artist = track.artist;
        if (track.extraArtists != null){
            for (AlbumInfo.ExtraArtist extraArtist : track.extraArtists){
                if (isType(extraArtist.type, "FEAT")){
                    String addArtist = " Feat. " + extraArtist.extraArtist;
                    String findExtraArtist = "(.*) Feat(uring)?\\.? " + extraArtist.extraArtist;
                    MP3Helper mp3Helper = MP3Helper.getInstance();
                    String[] featArtists = artist.split(" Feat\\. ");
                    if (featArtists.length == 2){
                        // check if the featuring artist already is in the artist name. If not, add it
                        String[] artists = mp3Helper.splitArtist(featArtists[1].toUpperCase());
                        String[] featArtists2 = mp3Helper.splitArtist(extraArtist.extraArtist.toUpperCase());
                        Arrays.sort(artists);
                        Arrays.sort(featArtists2);
                        if (!Arrays.equals(artists, featArtists2)){
                            artist = artist + addArtist;
                        }
                    }
                    else {
                        // add the extra artist
                        artist = artist + addArtist;
                    }
                    /*
                    if (!artist.toUpperCase().matches(findExtraArtist.toUpperCase())) {
                        artist = artist + addArtist;
                    }
                     */
                }
            }
        }
        return artist;
    }

    public String constructTitle(AlbumInfo.Track track){
        String title = track.title;
        if (track.extraArtists != null){
            for (AlbumInfo.ExtraArtist extraArtist : track.extraArtists){
                if (isType(extraArtist.type, "MIX")){
                    title = title + " (" + extraArtist.extraArtist + " Mix)";
                }
                else if (isType(extraArtist.type, "REMIX")){
                    title = checkRemix(title, extraArtist.extraArtist );
                }
            }
        }
        return title;
    }

    private String checkRemix(String title, String remixInfo){
        ArrayList<String> remixArray = new ArrayList<String>(
                Arrays.asList("Remix", "Radio Edit", "Mix"));
        Optional<String> value = remixArray
                .stream()
                .filter(a -> remixInfo.contains(a))
                .findFirst();
        if (value.isPresent()) {
            title = title + " (" + remixInfo + " Remix)";
        }
        else {
            log.info("Ignoring Remix Info: " + remixInfo);
        }
        return title;
    }

    public int getTrackSize(AlbumInfo.Config album) {
        int maxNr = 0;
        int nr = 0;
        for (AlbumInfo.Track track : album.tracks) {
            nr++;
            try {
                nr = Integer.valueOf(track.track.trim());
                if (maxNr < nr) {
                    maxNr = nr;
                }
            }
            catch (NumberFormatException ex){
                maxNr = nr;
            }
        }
        return String.valueOf(maxNr).length();
    }


    public void start() throws IOException {

        AlbumInfo.Config album = (AlbumInfo.Config) JSONUtils.openJSON(INPUT_FILE, AlbumInfo.Config.class, "UTF-8");
        MP3Settings mp3Settings = (MP3Settings) JSONUtils.openJSONWithCode(Constants.JSON.MP3SETTINGS, MP3Settings.class);
        if (album.trackSize == 0) {
            album.trackSize = getTrackSize(album);
        }
        MyFileWriter myFile = new MyFileWriter("c:\\My Data\\tmp\\Java\\MP3Processor\\Album\\test.txt", MyFileWriter.NO_APPEND);

        /* Get All mp3 files from specified location */
        String mp3Dir = SOURCE_MP3;
        log.info("Source MP3 Directory: " + mp3Dir);
        ArrayList<Path> fileTitles = new ArrayList<Path>();

        try (Stream<Path> stream = Files.walk(Paths.get(mp3Dir))) {
            stream.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".mp3"))
                    .forEach(
                            path -> {
                                fileTitles.add(path);
                                try {
                                    myFile.append(path.getFileName().toString());
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                    );
        }

        MP3Helper helper = MP3Helper.getInstance();

        album.album = helper.prettifyAlbum(album.album, album.albumArtist == null ? VARIOUS: album.albumArtist);
        for (AlbumInfo.Track track: album.tracks){

            track.artist = helper.prettifyArtist(constructArtist(track)).trim();
            track.title = helper.prettifySong(constructTitle(track)).trim();
            ArtistSongItem item = helper.prettifyRuleArtistSong(track.artist, track.title, true);
            track.artist = item.getArtist();
            track.title = item.getSong();
            String filename = helper.stripFilename(track.artist + " - " + track.title);
            myFile.append("Lookup song: " + track.track + " " + filename);
            Path fileFound = lookupFile(filename, fileTitles, helper);
            if (fileFound != null) {
                myFile.append("Found: " + fileFound.toString());
                String dest = "c:\\temp\\ultratop\\";
                copyFile(fileFound, track.track, filename);
            }
        }
        myFile.close();


        }

        public void copyFile(Path source, String track, String filename) throws IOException {
            String dest = DEST_PATH;
            File newFile = new File(dest + track + " " + filename + ".mp3");
            Files.copy(source, newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

    public Path lookupFile(String file,  ArrayList<Path> fileTitles, MP3Helper mp3Helper) {
        for (Path path : fileTitles){
             String filename = mp3Helper.stripFilename(FilenameUtils.removeExtension(path.getFileName().toString()));
             filename = filename.substring(3);
             //System.out.println(filename);
            if (filename.equalsIgnoreCase(file)){
                return path;
            }

        }
        return null;
    }
}
