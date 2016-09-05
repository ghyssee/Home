package be.home.main.mezzmo;

import be.home.common.dao.jdbc.SQLiteJDBC;
import be.home.mezzmo.domain.model.MGOFileAlbumCompositeTO;
import be.home.mezzmo.domain.model.MGOFileTO;
import be.home.mezzmo.domain.service.MezzmoServiceImpl;
import be.home.model.AlbumInfo;
import be.home.model.ConfigTO;
import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.exceptions.ApplicationException;
import be.home.common.logging.Log4GE;
import be.home.common.main.BatchJobV2;

import be.home.common.utils.JSONUtils;
import be.home.domain.model.MP3Helper;
import be.home.model.MP3Settings;
import com.mpatric.mp3agic.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import java.io.*;
import java.nio.file.*;
import java.util.*;


/**
 * Created by ghyssee on 20/02/2015.
 */
public class SynchronizeRating extends BatchJobV2 {

    private static final String VERSION = "V1.0";
    public static MezzmoServiceImpl mezzmoService = null;

    public static Log4GE log4GE;
    public static ConfigTO.Config config;
    public static final String MP3_SETTINGS = Setup.getInstance().getFullPath(Constants.Path.CONFIG) + File.separator +
            "MP3Settings.json";
    public static final String INPUT_FILE = Setup.getInstance().getFullPath(Constants.Path.PROCESS) + File.separator + "Album.json";
    private static final Logger log = Logger.getLogger(SynchronizeRating.class);

    public static void main(String args[]) {

        java.util.logging.Logger.getLogger("org.jaudiotagger").setLevel(java.util.logging.Level.OFF);
        /*
        String dynamicLog = // log directory somehow chosen...
        Properties p = new Properties( Config.ETC + "/log4j.properties" );
        p.put( "log.dir", dynamicLog ); // overwrite "log.dir"
        PropertyConfigurator.configure( p );        String currentDir = System.getProperty("user.dir");*/
        //log.info("Current Working dir: " + currentDir);



        SynchronizeRating instance = new SynchronizeRating();
        instance.printHeader("ZipFiles " + VERSION, "=");
        try {
            config = instance.init();
            SQLiteJDBC.initialize();
            instance.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

    }

    public void start() throws IOException {

        AlbumInfo.Config album = (AlbumInfo.Config) JSONUtils.openJSON(INPUT_FILE, AlbumInfo.Config.class, "UTF-8");
        MP3Settings mp3Settings = (MP3Settings) JSONUtils.openJSON(MP3_SETTINGS, MP3Settings.class);

        MP3Helper helper = MP3Helper.getInstance();
        String mp3Dir = Setup.getInstance().getFullPath(Constants.Path.ALBUM) + File.separator + mp3Settings.album;
        log.info("Album Directory: " + mp3Dir);
        List<Path> files = new ArrayList();
        Path path = Paths.get("r:\\My Music\\iPod");
        log.info ("Get List of MP3 files");
        listFiles(path, files);
        for (Path file : files){
            try {
                readMP3File(file);
            } catch (InvalidDataException e) {
                e.printStackTrace();
            } catch (UnsupportedTagException e) {
                e.printStackTrace();
            } catch (NotSupportedException e) {
                e.printStackTrace();
            }
        }

    }

    private void readMP3File(Path file) throws InvalidDataException, IOException, UnsupportedTagException, NotSupportedException {

        AudioFile f = null;
        try {
            MGOFileAlbumCompositeTO comp = new MGOFileAlbumCompositeTO();
            f = AudioFileIO.read(file.toFile());
            Tag tag = f.getTag();
            int track = Integer.parseInt(tag.getFirst(FieldKey.TRACK));
            comp.getFileTO().setTrack(track);
            comp.getFileArtistTO().setArtist(tag.getFirst(FieldKey.ARTIST));
            comp.getFileTO().setTitle(tag.getFirst(FieldKey.TITLE));
            comp.getFileAlbumTO().setName(tag.getFirst(FieldKey.ALBUM));
            int rating = convertRating(tag.getFirst(FieldKey.RATING));
            MGOFileTO fileTO = getMezzmoService().findByTitleAndAlbum(comp);
            if (fileTO == null) {
                log.error("File: " + file.toString());
                log.error("File Not Found");
            }
            else {
                int dbRating = fileTO.getRanking() == 0 ? 0 : (fileTO.getRanking());
                if (rating != dbRating){
                    if (rating < dbRating && rating == 0) {
                        log.info("File: " + file.toString());
                        log.info("Track: " + track);
                        log.info("Artist: " + comp.getFileArtistTO().getArtist());
                        log.info("Title: " + comp.getFileTO().getTitle());
                        log.info("Album: " + comp.getFileAlbumTO().getName());
                        log.info("Different Rating: " + "Rating MP3: " + rating + " / Rating DB: " + dbRating);
                        log.info(StringUtils.repeat('=', 100));
                    }
                }
            }
        } catch (CannotReadException e) {
            e.printStackTrace();
        } catch (TagException e) {
            e.printStackTrace();
        } catch (ReadOnlyFileException e) {
            e.printStackTrace();
        } catch (InvalidAudioFrameException e) {
            e.printStackTrace();
        }

    }

    private int convertRating (String rating){
        int stars = 0;
        switch (rating){
            case "1":
                stars = 1;
                break;
            case "64":
                stars = 2;
                break;
            case "128":
                stars = 3;
                break;
            case "196":
                stars = 4;
                break;
            case "255":
                stars = 5;
                break;
        }
        return stars;
    }

    private AlbumInfo.Track findMP3File(AlbumInfo.Config album, int trackNumber){
        return album.tracks.get(trackNumber-1);
    }

    public void listFiles(Path path, List<Path> files) {

        if (Files.exists(path)) {
            try (
                    DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path);
            ) {
                for (Path entry : directoryStream) {
                    if (Files.isDirectory(entry)){
                       listFiles(entry, files);
                    }
                    else {
                        String ext = FilenameUtils.getExtension(entry.getFileName().toString()).toUpperCase();
                        if ("MP3".equals(ext)) {
                            files.add(entry);
                        }
                    }
                }
                directoryStream.close();
            } catch (IOException ex) {
            }
        }
        else {
            throw new ApplicationException("MP3 Directory " + path.toString() + " does not exist!");
        }
        return;
    }

    public static MezzmoServiceImpl getMezzmoService(){

        if (mezzmoService == null) {
            return MezzmoServiceImpl.getInstance();
        }
        return mezzmoService;
    }

}
