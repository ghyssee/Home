package be.home.main;

import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.main.BatchJobV2;
import be.home.common.utils.FileUtils;
import be.home.common.utils.JSONUtils;
import be.home.domain.model.MP3Helper;
import be.home.model.ConfigTO;
import be.home.model.M3uTO;
import be.home.model.json.UltratopConfig;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ghyssee on 20/02/2015.
 */
public class M3uMakerV2 extends BatchJobV2 {

    public static ConfigTO.Config config;
    public static UltratopConfig.Config ultratopConfig;
    private static String SPLIT_SONG = " - ";
    private static final Logger log = getMainLog(M3uMakerV2.class);


    public static void main(String args[])  {
        M3uMakerV2 instance = new M3uMakerV2();
        try {
            config = instance.init();
            ultratopConfig = (UltratopConfig.Config) JSONUtils.openJSONWithCode(Constants.JSON.ULTRATOP, UltratopConfig.Config.class );
            instance.processUltratopConfigurationFile(ultratopConfig);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void run(){

    }

    public void processUltratopConfigurationFile(UltratopConfig.Config config){
        for (UltratopConfig.Year year : config.years){
            if (year.enabled) {
                log.info(StringUtils.repeat("*", 200));
                log.info("YEAR    : " + year.year);
                log.info("LISTFILE: " + year.listFile);
                log.info("ENABLED : " + year.enabled);
                log.info(StringUtils.repeat("*", 200));
                for (UltratopConfig.Month m3uMonth : year.m3uMonth) {
                    if (m3uMonth.enabled){
                        log.info("MONTH ID       : " + m3uMonth.id);
                        log.info("MONTH BASEDIR  : " + m3uMonth.baseDir);
                        log.info("MONTH INPUTFILE: " + m3uMonth.inputFile);
                        log.info("MONTH ENABLED  : " + m3uMonth.enabled);
                        log.info(StringUtils.repeat("=", 200));
                        processMonth(ultratopConfig, year, m3uMonth);
                    }
                }
            }
        }
    }


    public void processMonth(UltratopConfig.Config m3u, UltratopConfig.Year year, UltratopConfig.Month m3uMonth) {

        String baseFolder = Setup.getFullPath(Constants.Path.IPOD) + File.separator + m3uMonth.baseDir + File.separator;
        String inputFile =  baseFolder + m3uMonth.inputFile;
        List <M3uTO> songsOfUltratop = null;
        try {
            songsOfUltratop = getSongsFromUltratopListFile(inputFile);
            List <M3uTO> songsOfYearList = processYearList(year);
            List <M3uTO> baseDirList = getFileList(baseFolder);
            matchUltratop(songsOfUltratop, songsOfYearList);
            matchUltratop(songsOfUltratop, baseDirList);

            makePlayList(songsOfUltratop, baseFolder);
            boolean errorFound = false;
            for (M3uTO song : songsOfUltratop){
                if (song.getM3uSong() == null){
                    if (!errorFound){
                        errorFound = true;
                        log.error("ERRORS Found");
                        log.error(StringUtils.repeat('=', 100));
                    }
                    log.error("No Match Found: " + song.getLine());
                }
                //System.out.println("Track: " + song.getTrack() + " " + song.getM3uSong());
            }
            if (errorFound){
                log.error(StringUtils.repeat('=', 100));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String getFullPathYearListFile(String listFile) {
        String base = Setup.getInstance().getFullPath(Constants.Path.ONEDRIVE) +
                File.separator + listFile;
        return base;

    }

    private void makePlayList(List<M3uTO> ultratopList, String baseDir){
        PrintWriter writer = null;
        //OutputStreamWriter writer = null;
        File listFile = new File(baseDir);
        File m3uFile = new File(baseDir + File.separator + listFile.getName() + ".m3u");
        log.info("Making M3u File " + m3uFile);
        int counter = 0;
        try {
            Charset charset = Charset.forName("UTF-8");
            FileOutputStream stream = null;
            try {
                stream = new FileOutputStream(m3uFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            writer = new PrintWriter(new java.io.BufferedWriter(
                    new java.io.OutputStreamWriter(stream, charset)), true);
            /* add the UTF-8 BOM to the beginning of the file
               some players have trouble with special characters without this entry
             */
            writer.print('\ufeff');
            for (M3uTO m3uTO : ultratopList) {
                if (m3uTO.getM3uSong() != null) {
                    //System.out.println(m3uTO.getM3uSong());
                    writer.println(convertSongNameToM3uSongName(m3uTO.getM3uSong()));
                    counter++;
                }
                else {
                    //System.out.println("ERROR: " + m3uTO.getOriginalLine());
                }
            }
        } finally{
            if (writer != null){
                writer.close();
            }
        }
        log.info("FINISHED: " + counter + " songs added");
        log.info(StringUtils.repeat("*", 200));
        log.info("");

    }

    private String getUniqueSong(String song){
        //song = MP3Helper.getInstance().prettifySong(song);
        //song = MP3Helper.getInstance().stripFilename(song);
        song = prettifyM3uSong(song);
        song = song.toUpperCase();
        song = song.replace("CATCH & RELEASE (DEEPEND REMIX)", "CATCH & RELEASE");
        return song;
    }

    private String getUniqueAritst(String artist){
        //artist = MP3Helper.getInstance().prettifyArtist(artist);
        //artist = MP3Helper.getInstance().stripFilename(artist);
        artist = prettifyM3uArtist(artist);
        artist = artist.toUpperCase();
        //artist = removeSpecificWords(artist);
        return artist;
    }

    private String prettifyM3uArtist(String artist){
        String text = artist.replaceAll(" \\[BE\\]", "");
        text = text.replaceAll("Jebroer & DJ Paul Elstak", "Dr Phunk & Paul Elstak Feat. Jebroer");
        return text;
    }

    private String prettifyM3uSong(String song){
        String text = song.replaceAll(" \\(DJ Franxu Extended Edit\\)", "");
        text = text.replaceAll(" \\(Remix Feat. Justin Bieber\\)", "");
        text = text.replaceAll("You've Got A Friend(?: \\(Live\\))?", "You've Got A Friend (Live)");
        return text;
    }

    private String convertSongNameToM3uSongName(String song){
        String tmpSong = song.replace("\"", "''");
        return tmpSong;
    }

    private M3uTO getSongInfo(String strippedSongLine) {

        // Song is in format <Track 2 Numbers><Space><Artist><Space>-<Space><Title>
        // Minimum length is: 8
        if (StringUtils.isBlank(strippedSongLine) || strippedSongLine.length() < 8){
            throw new RuntimeException("Invalid format for line: + strippedSongLine");
        }

        String[] array = strippedSongLine.substring(3).split(SPLIT_SONG);
        if (array == null || array.length < 2){
            throw new RuntimeException("Invalid format for line: + strippedSongLine");
        }
        M3uTO song = new M3uTO();
        song.setLine(strippedSongLine);
        String track = "";
        switch (array.length) {
            case 2:
                track = strippedSongLine.substring(0,2);
                song.setArtist(array[0].trim());
                song.setSong(array[1].trim());
                song.setTrack(track);
                break;
            default:
                // join all other parts together in 1 string
                track = strippedSongLine.substring(0,2);
                song.setTrack(track);
                song.setArtist(array[0].trim());
                song.setSong(array[1].trim());
                for (int i=2; i < array.length; i++){
                    song.setSong(song.getSong()+array[i]);
                }
                break;
        }
        MP3Helper mp3Helper = MP3Helper.getInstance();
        String artist = mp3Helper.prettifyArtist(song.getArtist());
        String title = mp3Helper.prettifySong(song.getSong());
        song.setArtist(mp3Helper.prettifyArtistSong(artist, title));
        song.setSong(mp3Helper.prettifySongArtist(artist, title));
        song.setArtist(mp3Helper.stripFilename(song.getArtist()));
        song.setSong(mp3Helper.stripFilename(song.getSong()));
        return song;
    }

    private List<M3uTO> processYearList(UltratopConfig.Year year) throws IOException {

        String inputFile = getFullPathYearListFile(year.listFile);
        System.out.println("Start Year List: " + inputFile);
        List <String> lines = new ArrayList<String>();
        File myFile = new File(inputFile);
        if (myFile.isFile()) {
            lines = FileUtils.getContents(new File(inputFile), StandardCharsets.UTF_8);
        }
        List <M3uTO> songs = new ArrayList();
        for(String line: lines) {
            // ignore empty lines
            if (!StringUtils.isBlank(line)) {
                File tmpFile = new File(line);
                String strippedLine = tmpFile.getName();
                strippedLine = FilenameUtils.removeExtension(strippedLine);
                M3uTO song = getSongInfo(strippedLine);
                song.setM3uSong(line);
                songs.add(song);
            }
        }
        System.out.println("Finished Year List: " + inputFile);
        return songs;
    }

    private List<M3uTO> getSongsFromUltratopListFile(String inputFile) throws IOException {
        log.info("Start: Ultratop List + " + inputFile);
        List <String> lines = FileUtils.getContents(new File(inputFile),  StandardCharsets.UTF_8);
        List <M3uTO> songs = new ArrayList();
        for(String line: lines) {
            if (StringUtils.isBlank(line)) {
            } else {
                songs.add(getSongInfo(line));
            }
        }
        log.info("Finished: Ultratop List");
        return songs;
    }

    private List <M3uTO> getFileList(String baseDir){
        log.info("Start File List: " + baseDir);
        File folder = new File(baseDir);
        File[] listOfFiles = folder.listFiles();
        List <M3uTO> baseDirList = new ArrayList <M3uTO> ();
        for (int i=0; i < listOfFiles.length; i++){
            File file = listOfFiles[i];
            if (file.getName().toUpperCase().endsWith(".MP3")) {
                String strippedLine = file.getName();
                strippedLine = FilenameUtils.removeExtension(strippedLine);
                M3uTO info = getSongInfo(strippedLine);
                info.setM3uSong(file.getName());
                baseDirList.add(info);
            }
        }
        log.info("End File List: " + baseDir);
        return baseDirList;
    }

    private void matchUltratop(List <M3uTO> ultratopList, List <M3uTO> ListOfMP3Files){

        log.info("Start: Match Ultratop");

        for (M3uTO song: ultratopList) {
            if (song.getM3uSong() == null) {
                M3uTO foundSong = findSong(song, ListOfMP3Files);
                log.info("Lookup: " + song.getLine());
                if (foundSong != null) {
                    song.setM3uSong(foundSong.getM3uSong());
                }
            }
        }
        log.info("End: Match Ultratop");

    }

    private M3uTO findSong(M3uTO song, List <M3uTO> list ){

        String uniqueSong = getUniqueSong(song.getSong());
        String uniqueArtist = getUniqueAritst(song.getArtist());
        for (M3uTO m3uTO: list) {
            //System.out.println("Comparing 1: " + getUniqueSong(song.getSong()));
            //System.out.println("Comparing 2: " + getUniqueSong(m3uTO.getSong()));
            if (uniqueSong.equals(getUniqueSong(m3uTO.getSong()))) {
                //System.out.println("Comparing 1: " + getUniqueAritst(song.getArtist()));
                //System.out.println("Comparing 2: " + getUniqueSong(m3uTO.getArtist()));
                if (uniqueArtist.equals(getUniqueAritst(m3uTO.getArtist()))) {
                    return m3uTO;
                }
            }
        }
        return null;

    }
}

