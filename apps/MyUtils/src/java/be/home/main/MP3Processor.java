package be.home.main;

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
import java.util.regex.Pattern;

import static be.home.common.utils.SortUtils.stripAccentsIgnoreCase;

/**
 * Created by ghyssee on 20/02/2015.
 */
public class MP3Processor extends BatchJobV2 {

    private static final String VERSION = "V1.0";

    public static Log4GE log4GE;
    public static ConfigTO.Config config;
    public static final String INPUT_FILE = Setup.getInstance().getFullPath(Constants.Path.PROCESS) + File.separator + "Album.json";
    private static final Logger log = getMainLog(MP3Processor.class);

    public static void main(String args[]) {

        java.util.logging.Logger.getLogger("org.jaudiotagger").setLevel(java.util.logging.Level.OFF);

        MP3Processor instance = new MP3Processor();
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

    public void startTest() throws IOException {
        MP3Helper helper = MP3Helper.getInstance();
        AlbumInfo albumInfo = new AlbumInfo();
        AlbumInfo.Track track = albumInfo.new Track();
        track.title = "Test (Feat. Jona)";
        track.artist = "Dakota";
        helper.checkTrack(track);
        System.out.println("Title: " + track.title);
        System.out.println("Artist: " + track.artist);

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
                    if (!artist.toUpperCase().matches(findExtraArtist.toUpperCase())) {
                        artist = artist + addArtist;
                    }
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
        for (AlbumInfo.Track track : album.tracks) {
            int nr = Integer.valueOf(track.track);
            if (maxNr < nr) {
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

        MP3Helper helper = MP3Helper.getInstance();
        String mp3Dir = Setup.getInstance().getFullPath(Constants.Path.ALBUM) + File.separator + mp3Settings.album;
        log.info("Album Directory: " + mp3Dir);

        album.album = helper.prettifyAlbum(album.album, album.albumArtist);
        MyFileWriter myFile = new MyFileWriter("c:\\My Data\\tmp\\Java\\MP3Processor\\Album\\test.txt", MyFileWriter.NO_APPEND);
        for (AlbumInfo.Track track: album.tracks){
            /*
            track.artist = helper.prettifyArtist(constructArtist(track));
            track.title = helper.prettifySong(constructTitle(track));
            ArtistSongItem item = helper.prettifyRuleArtistSong(track.artist, track.title, true);
            track.artist = item.getArtist();
            track.title = item.getSong();
            */
            boolean diff = false;
            String oldArtist = replaceDefaultArtistItems(track.artist);
            String oldTitle = track.title;
            String statusArtist = "ARTIST OK";
            String statusTitle = "TITLE OK";
            helper.checkTrack(track);
            ArtistSongItem item = helper.formatSong(constructArtist(track), constructTitle(track));
            track.artist = item.getArtist();
            track.title = item.getSong();
            if (!oldArtist.equals(track.artist)) {
                statusArtist = "ARTIST DIFF";
                diff = true;
            }
            if (!oldTitle.equals(track.title)){
                statusTitle = "TITLE DIFF";
                diff = true;
            }
            if (diff) {
                myFile.append("TRACK: " + track.track);
                myFile.append("OLD: " + oldArtist + " - " + oldTitle);
                myFile.append("NEW: " + track.artist + " - " + track.title);
                if (StringUtils.isNotBlank(item.getComment())) {
                    myFile.append("COMMENT: " + item.getComment());
                }
                myFile.append("STATUS: " + statusArtist + "/ " + statusTitle);
                myFile.append(StringUtils.repeat('=', 100));
            }
        }
        myFile.close();

        //List <Path> directories = directoryList(MP3_DIR);
        //if (directories.size() > 0){

        //}
        List <Path> listOfFiles = fileList(mp3Dir);
        if (listOfFiles.size() > 0){
            checkNrOfMP3s(album.tracks, listOfFiles.size());
            processSingleDirectory(mp3Settings, album, listOfFiles, 1, "");
        }
        else {
            List <Path> directories = directoryList(mp3Dir);

            // check if Nr of MP3s found correspond with Album Info
            int nrOfMP3s = 0;
            for (Path path : directories) {
                listOfFiles = fileList(path.toString());
                nrOfMP3s += listOfFiles.size();
            }
            checkNrOfMP3s(album.tracks, nrOfMP3s);
            Integer index = 1;
            int counter = 0;
            for (Path path : directories) {
                listOfFiles = fileList(path.toString());
                if (listOfFiles.size() > 0) {
                    counter++;
                    System.out.println("Processing Directory: " + path.toString());
                    processSingleDirectory(mp3Settings, album, listOfFiles, index, String.valueOf(counter));
                }
                else {
                    System.out.println("No MP3's found in directory: " + path.toString());
                }
                index += listOfFiles.size();
            }
        }

    }

    public String replaceDefaultArtistItems(String artist){
        String newArtist = artist.replaceAll(" [F|f](?:ea)?t(?:uring)?\\.? ", " Feat. ");
        return newArtist;
    }



    public void checkNrOfMP3s(List <AlbumInfo.Track> tracks, int nrOfMP3s){
        if (nrOfMP3s != tracks.size()){
            throw new RuntimeException("Nr of MP3 files (" + nrOfMP3s + ") does not match Nr of Track records found (" + tracks.size() + ")");
        }
    }

    public void processSingleDirectory(MP3Settings mp3Settings, AlbumInfo.Config album, List <Path> listOfFiles, Integer index, String prefix){
        for (Path path : listOfFiles) {
            log.info(path.toString());
            try {
                AlbumInfo.Track track = findMP3File(album, index);
               readMP3File(album, mp3Settings, track, path.toString(), prefix, index);
                index++;
            }  catch (NotSupportedException e) {
                e.printStackTrace();
            } catch (InvalidDataException e) {
                e.printStackTrace();
            } catch (UnsupportedTagException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        log.info("Finished processing MP3s");
    }

    private void readMP3FileOld(AlbumInfo.Config album, MP3Settings mp3Settings, AlbumInfo.Track track, String fileName, String prefixFileName) throws InvalidDataException, IOException, UnsupportedTagException, NotSupportedException {
        Mp3File mp3file = new Mp3File(fileName);
        ID3v2 id3v2Tag = MP3Utils.getId3v2Tag(mp3file);

        System.out.println("Track: " + id3v2Tag.getTrack());
        System.out.println("NEW Track: " + MP3Helper.getInstance().formatTrack(album, track.track,0));
        System.out.println(StringUtils.repeat('=', 100));
        System.out.println("Artist: " + id3v2Tag.getArtist());
        System.out.println("NEW Artist: " + track.artist);
        System.out.println(StringUtils.repeat('=', 100));
        System.out.println("Title: " + id3v2Tag.getTitle());
        System.out.println("NEW Title: " + track.title);
        System.out.println(StringUtils.repeat('=', 100));
        if (StringUtils.isNotBlank(album.album)){
            id3v2Tag.setAlbum(album.album);
        }
        if (StringUtils.isBlank(mp3Settings.albumArtist)) {
            // compilation cd //
            id3v2Tag.setCompilation(true);
            id3v2Tag.setAlbumArtist("Various Artists");
        }
        else {
            id3v2Tag.setAlbumArtist(mp3Settings.albumArtist);
        }
        if (StringUtils.isNotBlank(mp3Settings.albumYear)) {
            id3v2Tag.setYear(mp3Settings.albumYear);
        }
        id3v2Tag.setTrack(MP3Helper.getInstance().formatTrack(album, track.track, 0));
        id3v2Tag.setArtist(track.artist);
        id3v2Tag.setTitle(track.title);
        cleanUpTag(id3v2Tag, id3v2Tag.getComment(), AbstractID3v2Tag.ID_COMMENT);
        cleanUpTag(id3v2Tag, id3v2Tag.getUrl(), AbstractID3v2Tag.ID_URL);
        cleanUpTag(id3v2Tag, id3v2Tag.getEncoder(), AbstractID3v2Tag.ID_ENCODER);
        cleanUpTag(id3v2Tag, id3v2Tag.getKey(), AbstractID3v2Tag.ID_KEY);
        cleanUpTag(id3v2Tag, id3v2Tag.getLyrics(), AbstractID3v2Tag.ID_TEXT_LYRICS);
        cleanUpTag(id3v2Tag, id3v2Tag.getAudioSourceUrl(), AbstractID3v2Tag.ID_AUDIOSOURCE_URL);
        cleanUpTag(id3v2Tag, id3v2Tag.getAudiofileUrl(), AbstractID3v2Tag.ID_AUDIOFILE_URL);
        cleanUpTag(id3v2Tag, id3v2Tag.getArtistUrl(), AbstractID3v2Tag.ID_ARTIST_URL);
        cleanUpTag(id3v2Tag, id3v2Tag.getCommercialUrl(), AbstractID3v2Tag.ID_COMMERCIAL_URL);
        cleanUpTag(id3v2Tag, id3v2Tag.getPaymentUrl(), AbstractID3v2Tag.ID_PAYMENT_URL);
        cleanUpTag(id3v2Tag, id3v2Tag.getPublisherUrl(), AbstractID3v2Tag.ID_PUBLISHER_URL);
        cleanUpTag(id3v2Tag, id3v2Tag.getRadiostationUrl(), AbstractID3v2Tag.ID_RADIOSTATION_URL);
        id3v2Tag.clearAlbumImage();
        if (album.total > 1) {
            id3v2Tag.setPartOfSet(track.cd);
        }
        else {
            id3v2Tag.clearFrameSet(AbstractID3v2Tag.ID_PART_OF_SET);
        }

        mp3file.setId3v2Tag(id3v2Tag);

        File newFile;
        if (mp3Settings.filename.renameEnabled){
            String newFilename = constructFilename(mp3Settings, album, track, FilenameUtils.getExtension(fileName));
            newFile = new File(Setup.getInstance().getFullPath(Constants.Path.NEW) + File.separator  + newFilename);
        }
        else {
            File originalFile = new File(fileName);
            newFile = new File(Setup.getInstance().getFullPath(Constants.Path.NEW) + File.separator + prefixFileName + originalFile.getName());
        }
        System.out.println("New File " + newFile);
        mp3file.save(newFile.getAbsolutePath());
    }


    private void readMP3File(AlbumInfo.Config album, MP3Settings mp3Settings, AlbumInfo.Track track, String fileName, String prefixFileName, int index) throws InvalidDataException, IOException, UnsupportedTagException, NotSupportedException {

        File newFile;
        File originalFile = new File(fileName);
        System.out.println("Track Number: " + index);
        if (mp3Settings.filename.renameEnabled){
            String newFilename = constructFilename(mp3Settings, album, track, FilenameUtils.getExtension(fileName));
            newFile = new File(Setup.getInstance().getFullPath(Constants.Path.NEW) + File.separator  + newFilename);
        }
        else {
            newFile = new File(Setup.getInstance().getFullPath(Constants.Path.NEW) + File.separator + prefixFileName + originalFile.getName());
        }

        Files.copy(originalFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        try {
            MP3Service mp3File = new MP3JAudioTaggerServiceImpl(newFile.getAbsolutePath());
            System.out.println("Track: " + mp3File.getTrack());
            System.out.println("NEW Track: " + MP3Helper.getInstance().formatTrack(album, track.track, index));
            System.out.println(StringUtils.repeat('=', 100));
            System.out.println("Artist: " + mp3File.getArtist());
            System.out.println("NEW Artist: " + track.artist);
            System.out.println(StringUtils.repeat('=', 100));
            System.out.println("Title: " + mp3File.getTitle());
            System.out.println("NEW Title: " + track.title);
            System.out.println(StringUtils.repeat('=', 100));

            if (StringUtils.isNotBlank(album.album)){
                mp3File.setAlbum(album.album);
            }
            if (StringUtils.isBlank(mp3Settings.albumArtist)) {
                // compilation cd //
                mp3File.setCompilation(true);
                mp3File.setAlbumArtist("Various Artists");
            }
            else {
                mp3File.setAlbumArtist(mp3Settings.albumArtist);
            }
            if (StringUtils.isNotBlank(mp3Settings.albumYear)) {
                mp3File.setYear(mp3Settings.albumYear);
            }
            prettifyGenre(mp3File);

            mp3File.setTrack(MP3Helper.getInstance().formatTrack(album, track.track,index));
            mp3File.setArtist(track.artist);
            mp3File.setTitle(track.title);
            mp3File.cleanupTags();
            mp3File.clearAlbumImage();
            if (album.total > 1) {
                mp3File.setDisc(track.cd);
            }
            else {
                mp3File.setDisc(null);
            }
            mp3File.commit();
        } catch (MP3Exception e) {
            e.printStackTrace();
        }
        System.out.println("New File " + newFile);
    }

    private void prettifyGenre(MP3Service mp3Service) throws MP3Exception {
        String genre = mp3Service.getGenre();
        if (StringUtils.isNotBlank(genre)) {
            genre = MP3Helper.getInstance().prettifyString(genre);
            mp3Service.setGenre(genre);
        }

    }

    private void cleanUpTag(ID3v2 id3v2Tag, String tagToCheck, String Key){
        final String TAG_TO_DELETE = "RJ/SNWTJE";
        if (tagToCheck != null && tagToCheck.compareToIgnoreCase(TAG_TO_DELETE) == 0){
            id3v2Tag.clearFrameSet(Key);
        }
    }

    private String constructFilename(MP3Settings mp3Settings, AlbumInfo.Config album, AlbumInfo.Track track, String ext){
        int lengthDisc = album.total > 0 ? String.valueOf(album.total).length() : 0;
        String disc = (StringUtils.isBlank(track.cd) ? "" : String.valueOf(track.cd));
        disc = StringUtils.leftPad(disc, lengthDisc, mp3Settings.filename.paddingForTrackInFilename);
        String customTrack = StringUtils.leftPad(track.track, album.trackSize, mp3Settings.filename.paddingForTrackInFilename);
        customTrack = disc + customTrack;
        String filename =  customTrack + mp3Settings.filename.trackArtistSeperator +
                           track.artist + mp3Settings.filename.artistTitleSeperator +
                           track.title;
        filename = MP3Helper.getInstance().stripFilename(filename);
        filename += "." + ext;

        return filename;
    }

    private AlbumInfo.Track findMP3File(AlbumInfo.Config album, int trackNumber){
        return album.tracks.get(trackNumber-1);
    }

    public static List<Path> fileList(String directory) {

        DirectoryStream.Filter<Path> filter = file -> {
            String fileExt = FilenameUtils.getExtension(file.toString()).toLowerCase();
            return "mp3".equals(fileExt);
        };

        List<Path> fileNames = new ArrayList<>();
        Path path = Paths.get(directory);
        if (Files.exists(path)) {
            try (
                    DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path, filter);
            ) {
                for (Path file : directoryStream) {
                    fileNames.add(file);
                }
                directoryStream.close();
            } catch (IOException ex) {
            }
            Collections.sort(fileNames, (f1, f2) -> SortUtils.stripAccentsIgnoreCase(f1.toString()).compareTo(SortUtils.stripAccentsIgnoreCase(f2.toString())));
        }
        else {
            throw new ApplicationException("MP3 Directory " + directory + " does not exist!");
        }
        return fileNames;
    }

    public static List<Path> directoryList(String directory) {

        DirectoryStream.Filter<Path> filter = file -> Files.isDirectory(file);

        List<Path> fileNames = new ArrayList<>();
        Path path = Paths.get(directory);
        if (Files.exists(path)) {
            try (
                    DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path, filter);
            ) {
                for (Path file : directoryStream) {
                    fileNames.add(file);
                }
                directoryStream.close();
            } catch (IOException ex) {
            }
            Collections.sort(fileNames, (f1, f2) -> SortUtils.stripAccentsIgnoreCase(f1.toString()).compareTo(SortUtils.stripAccentsIgnoreCase(f2.toString())));
        }
        else {
            throw new ApplicationException("MP3 Directory " + directory + " does not exist!");
        }
        return fileNames;
    }

}
