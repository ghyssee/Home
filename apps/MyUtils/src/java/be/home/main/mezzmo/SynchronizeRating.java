package be.home.main.mezzmo;

import be.home.common.dao.jdbc.SQLiteJDBC;
import be.home.mezzmo.domain.model.Compilation;
import be.home.mezzmo.domain.model.MGOFileAlbumCompositeTO;
import be.home.mezzmo.domain.model.MGOFileTO;
import be.home.mezzmo.domain.service.MezzmoServiceImpl;
import be.home.model.ConfigTO;
import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.exceptions.ApplicationException;
import be.home.common.logging.Log4GE;
import be.home.common.main.BatchJobV2;

import be.home.common.utils.JSONUtils;
import be.home.domain.model.MP3Helper;
import be.home.model.MP3Settings;
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
import java.sql.SQLException;
import java.util.*;


/**
 * Created by ghyssee on 20/02/2015.
 */
public class SynchronizeRating extends BatchJobV2 {

    private static final String VERSION = "V1.0";
    public static MezzmoServiceImpl mezzmoService = null;

    public static Log4GE log4GE;
    public static ConfigTO.Config config;
    private static final Logger log = Logger.getLogger(SynchronizeRating.class);

    public static void main(String args[]) {

        java.util.logging.Logger.getLogger("org.jaudiotagger").setLevel(java.util.logging.Level.OFF);

        SynchronizeRating instance = new SynchronizeRating();
        instance.printHeader("SyncrhonizeRatings " + VERSION, "=");
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

        MP3Settings mp3Settings = (MP3Settings) JSONUtils.openJSON(Setup.getInstance().getFullPath(Constants.JSON.MP3SETTINGS),
                MP3Settings.class, "UTF-8");

        MP3Helper helper = MP3Helper.getInstance();
        log.info("Start Directory: " + mp3Settings.synchronizer.startDirectory);
        log.info("Update: " + mp3Settings.synchronizer.updateRating);
        List<Path> files = new ArrayList();
        Path path = Paths.get(mp3Settings.synchronizer.startDirectory);
        log.info ("Fetching MP3 Files");
        listFiles(path, files);
        log.info ("Sorting list");
        Collections.sort(files,
                (o1, o2) -> o1.toString().compareTo(o2.toString()));
        log.info ("Check Rating MP3 Files");
        for (Path file : files) {
            readMP3File(mp3Settings, file);
        }
    }

    private void readMP3File(MP3Settings settings, Path file)throws IOException {

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
            int rating = convertRating(settings, tag.getFirst(FieldKey.RATING));
            MGOFileTO fileTO = getMezzmoService().findByTitleAndAlbum(comp);
            if (fileTO == null) {
                log.error("File: " + file.toString());
                log.error("File Not Found");
            }
            else {
                int dbRating = fileTO.getRanking() == 0 ? 0 : (fileTO.getRanking());
                if (rating != dbRating){
                    log.info("File: " + file.toString());
                    log.info("Track: " + track);
                    log.info("Artist: " + comp.getFileArtistTO().getArtist());
                    log.info("Title: " + comp.getFileTO().getTitle());
                    log.info("Album: " + comp.getFileAlbumTO().getName());
                    log.info("Different Rating: " + "Rating MP3: " + rating + " / Rating DB: " + dbRating);
                    log.info(StringUtils.repeat('=', 100));
                    if (settings.synchronizer.updateRating) {
                        getMezzmoService().updateRanking(fileTO.getId(), rating);
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
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private int convertRating (MP3Settings settings, String rating){
        int stars = 0;
        if (StringUtils.isNotBlank(rating)) {
            int newRating = Integer.parseInt(rating);
            if (settings.rating.rating1 <= newRating && newRating < settings.rating.rating2){
                stars = 1;
            }
            else if (settings.rating.rating2 <= newRating && newRating < settings.rating.rating3){
                stars = 2;
            }
            else if (settings.rating.rating3 <= newRating && newRating < settings.rating.rating4){
                stars = 3;
            }
            else if (settings.rating.rating4 <= newRating && newRating < settings.rating.rating5){
                stars = 4;
            }
            else if (newRating >= settings.rating.rating5){
                stars = 5;
            }
        }
        return stars;
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
