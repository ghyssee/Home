package be.home.main;

import be.home.common.constants.Constants;
import be.home.common.logging.Log4GE;
import be.home.common.main.BatchJobV2;

import be.home.common.utils.JSONUtils;
import be.home.domain.model.MP3Helper;
import be.home.model.*;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.mpatric.mp3agic.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

/**
 * Created by ghyssee on 20/02/2015.
 */
public class MP3Processor extends BatchJobV2 {

    private static final String VERSION = "V1.0";

    public static Log4GE log4GE;
    public static ConfigTO.Config config;
    public static final String MP3_DIR = Constants.Path.MP3_ALBUM + File.separator + "Vlaamse Top Hits 2016 Vol. 1";
    public static final String INPUT_FILE = Constants.Path.MP3_PROCESSOR + File.separator + "Album.json";
    private static final Logger log = Logger.getLogger(ZipFiles.class);

    public static void main(String args[]) {

        String currentDir = System.getProperty("user.dir");
        log.info("Current Working dir: " + currentDir);


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

    public void start() throws IOException {

        //File albumInfo = new File("c:/My Programs/iMacros/output/album.txt");

        AlbumInfo.Config album = (AlbumInfo.Config) JSONUtils.openJSON(INPUT_FILE, AlbumInfo.Config.class);

        System.out.println(album.album);
        MP3Helper helper = new MP3Helper();

        album.album = helper.prettifyAlbum(album.album);
        for (AlbumInfo.Track track: album.tracks){
            track.artist = helper.prettifySong(track.artist);
            track.title = helper.prettifySong(track.title);
            helper.checkTrack(track);
        }

        List <Path> listOfFiles = fileList(MP3_DIR);
        int index = 1;
        if (listOfFiles.size() != album.tracks.size()){
            throw new RuntimeException("Nr of MP3 files (" + listOfFiles.size() + ") does not match Nr of Track records found (" + album.tracks.size() + ")");
        }
        for (Path path : listOfFiles) {
            log.info(path.toString());
            try {
                AlbumInfo.Track track = findMP3File(album, index++);
                readMP3File(album, track, path.toString());
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

    class MyFileVisitor extends SimpleFileVisitor<Path> {

        @Override
        public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs)
                throws IOException {
            if (attrs.isDirectory()) {
                throw new IllegalStateException("WAT!? Visiting directory: " + file.toAbsolutePath().toString());
            }
            log.info("Visiting file: " + file.toAbsolutePath().toString());
            return super.visitFile(file, attrs);
        }
    }

    private void readMP3File(AlbumInfo.Config album, AlbumInfo.Track track, String fileName) throws InvalidDataException, IOException, UnsupportedTagException, NotSupportedException {
        Mp3File mp3file = new Mp3File(fileName);
//        System.out.println("Length of this mp3 is: " + mp3file.getLengthInSeconds() + " seconds");
 //       System.out.println("Bitrate: " + mp3file.getBitrate() + " kbps " + (mp3file.isVbr() ? "(VBR)" : "(CBR)"));
 //       System.out.println("Sample rate: " + mp3file.getSampleRate() + " Hz");
 //       System.out.println("Has ID3v2 tag?: " + (mp3file.hasId3v2Tag() ? "YES" : "NO"));
        ID3v2 id3v2Tag = mp3file.getId3v2Tag();
        System.out.println("Track: " + id3v2Tag.getTrack());
        System.out.println("NEW Track: " + track.track);
        System.out.println(StringUtils.repeat('=', 100));
        System.out.println("Artist: " + id3v2Tag.getArtist());
        System.out.println("NEW Artist: " + track.artist);
        System.out.println(StringUtils.repeat('=', 100));
        System.out.println("Title: " + id3v2Tag.getTitle());
        System.out.println("NEW Title: " + track.title);
        System.out.println(StringUtils.repeat('=', 100));
        /*
        System.out.println("Album: " + id3v2Tag.getAlbum());
        System.out.println("Year: " + id3v2Tag.getYear());
        System.out.println("Genre: " + id3v2Tag.getGenre() + " (" + id3v2Tag.getGenreDescription() + ")");
        System.out.println("Comment: " + id3v2Tag.getComment());
        System.out.println(StringUtils.repeat('=', 100));
        System.out.println(StringUtils.repeat('=', 100));*/
        if (StringUtils.isNotBlank(album.album)){
            id3v2Tag.setAlbum(album.album);
        }
        // compilation cd //
        id3v2Tag.setCompilation(true);
        id3v2Tag.setAlbumArtist("Various Artists");
        id3v2Tag.setTrack(StringUtils.leftPad(track.track, 2, "0"));
        //EncodedText tmp = new EncodedText(track.artist);
        id3v2Tag.setArtist(track.artist);
        id3v2Tag.setTitle(track.title);
        id3v2Tag.clearAlbumImage();
        id3v2Tag.setPartOfSet(track.cd);

        mp3file.setId3v2Tag(id3v2Tag);
        File originalFile = new File(fileName);
        File newFile = new File(Constants.Path.MP3_NEW + File.separator + originalFile.getName());
        System.out.println("New File " + newFile);

        mp3file.save(newFile.getAbsolutePath());
        //if (originalFile.delete()){
         //   newFile.renameTo(originalFile);
        //}
        //else {
          //  System.err.println("There was a problem deleting the file " + fileName);
        //}*/
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
        try (
                DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(directory), filter);
        ) {
            for (Path path : directoryStream) {
                fileNames.add(path);
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
        return fileNames;
    }

}
