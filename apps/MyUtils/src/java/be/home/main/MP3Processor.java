package be.home.main;

import be.home.mezzmo.domain.model.Compilation;
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
import com.mpatric.mp3agic.ID3v24TagExt;
import com.mpatric.mp3agic.Mp3FileExt;
import com.mpatric.mp3agic.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;

/**
 * Created by ghyssee on 20/02/2015.
 */
public class MP3Processor extends BatchJobV2 {

    private static final String VERSION = "V1.0";

    public static Log4GE log4GE;
    public static ConfigTO.Config config;
    public static final String MP3_SETTINGS = Setup.getInstance().getFullPath(Constants.Path.CONFIG) + File.separator +
            "MP3Settings.json";
    public static final String INPUT_FILE = Setup.getInstance().getFullPath(Constants.Path.PROCESS) + File.separator + "Album.json";
    private static final Logger log = Logger.getLogger(MP3Processor.class);

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

    public void start() throws IOException {

        AlbumInfo.Config album = (AlbumInfo.Config) JSONUtils.openJSON(INPUT_FILE, AlbumInfo.Config.class, "UTF-8");
        MP3Settings mp3Settings = (MP3Settings) JSONUtils.openJSON(MP3_SETTINGS, MP3Settings.class);

        MP3Helper helper = MP3Helper.getInstance();
        String mp3Dir = Setup.getInstance().getFullPath(Constants.Path.ALBUM) + mp3Settings.album;
        log.info("Album Directory: " + mp3Dir);

        album.album = helper.prettifyAlbum(album.album);
        for (AlbumInfo.Track track: album.tracks){
            track.artist = helper.prettifyArtist(track.artist);
            track.title = helper.prettifySong(track.title);
            helper.checkTrack(track);
        }

        //List <Path> directories = directoryList(MP3_DIR);
        //if (directories.size() > 0){

        //}
        List <Path> listOfFiles = fileList(mp3Dir);
        if (listOfFiles.size() > 0){
            checkNrOfMP3s(album.tracks, listOfFiles.size());
            processSingleDirectory(album, listOfFiles, 1, "");
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
                    processSingleDirectory(album, listOfFiles, index, String.valueOf(counter));
                }
                else {
                    System.out.println("No MP3's found in directory: " + path.toString());
                }
                index += listOfFiles.size();
            }
        }

    }

    public void checkNrOfMP3s(List <AlbumInfo.Track> tracks, int nrOfMP3s){
        if (nrOfMP3s != tracks.size()){
            throw new RuntimeException("Nr of MP3 files (" + nrOfMP3s + ") does not match Nr of Track records found (" + tracks.size() + ")");
        }
    }

    public void processSingleDirectory(AlbumInfo.Config album, List <Path> listOfFiles, Integer index, String prefix){
        for (Path path : listOfFiles) {
            log.info(path.toString());
            try {
                AlbumInfo.Track track = findMP3File(album, index++);
                readMP3File(album, track, path.toString(), prefix);
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

    private void readMP3FileOld(AlbumInfo.Config album, AlbumInfo.Track track, String fileName, String prefixFileName) throws InvalidDataException, IOException, UnsupportedTagException, NotSupportedException {
        Mp3FileExt mp3file = new Mp3FileExt(fileName);
        File f = new File(fileName);
        AudioFile af = null;
        try {
            af = AudioFileIO.read(f);
            Tag tag = af.getTag();

            tag.deleteField(FieldKey.TRACK);
            tag.setField(FieldKey.TRACK, MP3Helper.getInstance().formatTrack(album, track.track));
            tag.deleteField(FieldKey.ARTIST);
            tag.setField(FieldKey.ARTIST, track.artist);
            tag.deleteField(FieldKey.TITLE);
            tag.setField(FieldKey.TITLE, track.title);
            tag.deleteField(FieldKey.ALBUM);
            tag.setField(FieldKey.ALBUM, album.album);
            tag.deleteField(FieldKey.IS_COMPILATION);
            tag.setField(FieldKey.IS_COMPILATION, Compilation.TRUE.getValue());
            tag.deleteField(FieldKey.ALBUM_ARTIST);
            tag.setField(FieldKey.ALBUM_ARTIST, "Various Artists");
            if (StringUtils.isBlank(track.cd)){
                tag.deleteField(FieldKey.DISC_NO);
            }
            else if (album.total > 1) {
                tag.setField(FieldKey.DISC_NO, track.cd);
            }
            tag.deleteField(FieldKey.COVER_ART);
            af.commit();
        } catch (CannotReadException e) {
            e.printStackTrace();
        } catch (TagException e) {
            e.printStackTrace();
        } catch (ReadOnlyFileException e) {
            e.printStackTrace();
        } catch (InvalidAudioFrameException e) {
            e.printStackTrace();
        } catch (CannotWriteException e) {
            e.printStackTrace();
        }

    }

    private void readMP3File(AlbumInfo.Config album, AlbumInfo.Track track, String fileName, String prefixFileName) throws InvalidDataException, IOException, UnsupportedTagException, NotSupportedException {
        Mp3FileExt mp3file = new Mp3FileExt(fileName);
        ID3v2 id3v2Tag;
        if (mp3file.hasId3v2Tag()) {
            id3v2Tag = mp3file.getId3v2Tag();
        }
        else {
            id3v2Tag =  new ID3v24TagExt();
            mp3file.setId3v2Tag(id3v2Tag);
        }
        System.out.println("Track: " + id3v2Tag.getTrack());
        System.out.println("NEW Track: " + MP3Helper.getInstance().formatTrack(album, track.track));
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
        // compilation cd //
        id3v2Tag.setCompilation(true);
        id3v2Tag.setAlbumArtist("Various Artists");
        id3v2Tag.setTrack(MP3Helper.getInstance().formatTrack(album, track.track));
        id3v2Tag.setArtist(track.artist);
        id3v2Tag.setTitle(track.title);
        id3v2Tag.clearAlbumImage();
        if (album.total > 1) {
            id3v2Tag.setPartOfSet(track.cd);
        }
        else {
            id3v2Tag.clearFrameSet(AbstractID3v2Tag.ID_PART_OF_SET);
        }

        mp3file.setId3v2Tag(id3v2Tag);
        File originalFile = new File(fileName);
        File newFile = new File(Setup.getInstance().getFullPath(Constants.Path.NEW) + File.separator + prefixFileName + originalFile.getName());
        System.out.println("New File " + newFile);
        //if (track.artist.contains("Λ")){
        //    id3v2Tag.setArtist(track.artist.replaceAll("Λ", "&"));
        //}
        //mp3file.save(newFile.getAbsolutePath());
        //if (originalFile.delete()){
        //   newFile.renameTo(originalFile);
        //}
        //else {
        //  System.err.println("There was a problem deleting the file " + fileName);
        //}*/
        mp3file.save(newFile.getAbsolutePath());
    }

    private AlbumInfo.Track findMP3File(AlbumInfo.Config album, int trackNumber){
        return album.tracks.get(trackNumber-1);
    }

    public static List<Path> fileList(String directory) {

        DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
            public boolean accept(Path file) throws IOException {
                String fileExt = FilenameUtils.getExtension(file.toString()).toLowerCase();
                return "mp3".equals(fileExt);
            }
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
            Collections.sort(fileNames, new Comparator<Path>() {
                @Override
                public int compare(Path f1, Path f2) {
                    return f1.toString().compareTo(f2.toString());
                }

            });
        }
        else {
            throw new ApplicationException("MP3 Directory " + directory + " does not exist!");
        }
        return fileNames;
    }

    public static List<Path> directoryList(String directory) {

        DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
            public boolean accept(Path file) throws IOException {
                String fileExt = FilenameUtils.getExtension(file.toString()).toLowerCase();
                return Files.isDirectory(file);
            }
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
            Collections.sort(fileNames, new Comparator<Path>() {
                @Override
                public int compare(Path f1, Path f2) {
                    return f1.toString().compareTo(f2.toString());
                }

            });
        }
        else {
            throw new ApplicationException("MP3 Directory " + directory + " does not exist!");
        }
        return fileNames;
    }

}
