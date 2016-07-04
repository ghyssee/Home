package be.home.main;

import be.home.model.AlbumInfo;
import be.home.model.ConfigTO;
import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.exceptions.ApplicationException;
import be.home.common.logging.Log4GE;
import be.home.common.main.BatchJobV2;

import be.home.common.utils.JSONUtils;
import be.home.domain.model.MP3Helper;
import com.mpatric.mp3agic.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;
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
    public static final String MP3_DIR = Setup.getInstance().getFullPath(Constants.Path.ALBUM) + File.separator +
                                         "Kidszone Zomer 2016";
    public static final String INPUT_FILE = Setup.getInstance().getFullPath(Constants.Path.PROCESS) + File.separator + "Album.json";
    private static final Logger log = Logger.getLogger(MP3Processor.class);

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
            track.artist = helper.prettifyArtist(track.artist);
            track.title = helper.prettifySong(track.title);
            helper.checkTrack(track);
        }

        //List <Path> directories = directoryList(MP3_DIR);
        //if (directories.size() > 0){

        //}
        List <Path> listOfFiles = fileList(MP3_DIR);
        if (listOfFiles.size() > 0){
            checkNrOfMP3s(album.tracks, listOfFiles.size());
            processSingleDirectory(album, listOfFiles, 1, "");
        }
        else {
            List <Path> directories = directoryList(MP3_DIR);

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

    private void readMP3File(AlbumInfo.Config album, AlbumInfo.Track track, String fileName, String prefixFileName) throws InvalidDataException, IOException, UnsupportedTagException, NotSupportedException {
        Mp3File mp3file = new Mp3File(fileName);
//        System.out.println("Length of this mp3 is: " + mp3file.getLengthInSeconds() + " seconds");
 //       System.out.println("Bitrate: " + mp3file.getBitrate() + " kbps " + (mp3file.isVbr() ? "(VBR)" : "(CBR)"));
 //       System.out.println("Sample rate: " + mp3file.getSampleRate() + " Hz");
 //       System.out.println("Has ID3v2 tag?: " + (mp3file.hasId3v2Tag() ? "YES" : "NO"));
        ID3v2 id3v2Tag;
        if (mp3file.hasId3v2Tag()) {
            id3v2Tag = mp3file.getId3v2Tag();
        }
        else {
            id3v2Tag =  new ID3v24Tag();
            mp3file.setId3v2Tag(id3v2Tag);
        }
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
        File newFile = new File(Setup.getInstance().getFullPath(Constants.Path.NEW) + File.separator + prefixFileName + originalFile.getName());
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
