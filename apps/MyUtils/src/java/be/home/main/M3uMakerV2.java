package be.home.main;

import be.home.common.logging.Log4GE;
import be.home.common.main.BatchJobV2;
import be.home.common.utils.FileUtils;
import be.home.model.ConfigTO;
import be.home.model.M3uTO;
import be.home.model.UltratopConfig;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ghyssee on 20/02/2015.
 */
public class M3uMakerV2 extends BatchJobV2 {

    public static Log4GE log4GE;
    public static ConfigTO.Config config;
    public static UltratopConfig.Config ultratopConfig;
    private static String SPLIT_SONG = " - ";


    public static void main(String args[])  {
        M3uMakerV2 instance = new M3uMakerV2();
        boolean fileRenamed = false;
        try {
            config = instance.init();
            System.out.println("Full Path Config Dir = " + config.getFullPathConfigDir());
            ultratopConfig = instance.init(config.getFullPathConfigDir() + "/UltratopConfig.json");
            System.out.println(config.toString());
            instance.processUltratopConfigurationFile(ultratopConfig);

            /*
            for (ConfigTO.Parts part : config.parts){
                System.out.println(part.name);
            }*/
            //instance.run();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void run(){

    }

    public void processUltratopConfigurationFile(UltratopConfig.Config config){
        for (UltratopConfig.Year year : config.years){
            System.out.println(StringUtils.repeat("*", 200));
            System.out.println("YEAR    : " + year.year);
            System.out.println("LISTFILE: " + year.listFile);
            System.out.println("ENABLED : " + year.enabled);
            System.out.println(StringUtils.repeat("*", 200));
            if (year.enabled) {
                for (UltratopConfig.Month m3uMonth : year.m3uMonth) {
                    System.out.println("MONTH ID       : " + m3uMonth.id);
                    System.out.println("MONTH BASEDIR  : " + m3uMonth.baseDir);
                    System.out.println("MONTH INPUTFILE: " + m3uMonth.inputFile);
                    System.out.println("MONTH ENABLED  : " + m3uMonth.enabled);
                    System.out.println(StringUtils.repeat("=", 200));
                    if (m3uMonth.enabled){
                        processMonth(ultratopConfig, year, m3uMonth);
                    }
                }
            }
            System.out.println();
        }
    }


    public boolean processMonth(UltratopConfig.Config m3u, UltratopConfig.Year year, UltratopConfig.Month m3uMonth) {

        final String batchJob = "M3u";
        log4GE = new Log4GE(config.wiki.resultLog);
        log4GE.clear();
        log4GE.start("Wiki Make CSV file");
        //log.info("Batchjob " + batchJob + " started on " + new Date());
        String baseFolder = m3u.baseDir + File.separator + m3uMonth.baseDir + File.separator;
        String inputFile =  baseFolder + m3uMonth.inputFile;
        List <M3uTO> songsOfUltratop = null;
        boolean fileRenamed = false;
        try {
            songsOfUltratop = getSongsFromUltratopListFile(inputFile);
            List <M3uTO> songsOfYearList = processYearList(year);
            List <M3uTO> baseDirList = getFileList(baseFolder);
            matchUltratopWithYearList(songsOfUltratop, songsOfYearList);
            matchUltratopWithYearList(songsOfUltratop, baseDirList);

            makePlayList(songsOfUltratop, baseFolder);
            boolean errorFound = false;
            for (M3uTO song : songsOfUltratop){
                if (song.getM3uSong() == null){
                    if (!errorFound){
                        errorFound = true;
                        System.err.println("ERRORS Found");
                        System.err.println(StringUtils.repeat('=', 100));
                    }
                    System.err.println("No Match Found: " + song.getLine());
                }
                //System.out.println("Track: " + song.getTrack() + " " + song.getM3uSong());
            }
            if (errorFound){
                System.err.println(StringUtils.repeat('=', 100));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        //log.info("Batchjob " + batchJob + " ended on " + new Date());
        log4GE.end();
        return fileRenamed;

    }

    private String getFullPathYearListFile(String listFile) {
        return config.oneDriveDir + File.separator + listFile;
    }

    private void makePlayList(List<M3uTO> ultratopList, String baseDir){
        PrintWriter writer = null;
        //OutputStreamWriter writer = null;
        File listFile = new File(baseDir);
        File m3uFile = new File(baseDir + File.separator + listFile.getName() + ".m3u");
        System.out.println("Making M3u File " + m3uFile);
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
        System.out.println("FINISHED: " + counter + " songs added" );
        System.out.println(StringUtils.repeat("*", 200));
        System.out.println();

    }

    private String getUniqueSong(String song){
        String tmpSong = song.toUpperCase();
        tmpSong = tmpSong.replace(" FEAT. ", " & ").replace(" FEAT ", " & ").replace(" FT. ", " & ").replace(" FT ", " & ");
        tmpSong = tmpSong.replace("[BE]", "");
        tmpSong = tmpSong.replace(" VS. ", " VS ");
        tmpSong = tmpSong.replace("MR. ", "MR ");
        tmpSong = tmpSong.replace("?", "");
        tmpSong = tmpSong.replace("/", "&");
        tmpSong = tmpSong.replace("<3", "LOVE");
        tmpSong = tmpSong.replace("**", "UC");
        tmpSong = tmpSong.replace("* ", "");
        tmpSong = tmpSong.replace("*", "");
        tmpSong = tmpSong.replace("+", "&");
        //tmpSong = tmpSong.replace("''", "\"");
        tmpSong = tmpSong.replace("\"", "''");
        tmpSong = tmpSong.replace("\"", "");
        tmpSong = tmpSong.replace("[", "(");
        tmpSong = tmpSong.replace("]", ")");
        tmpSong = tmpSong.replace("S.O.S", "SOS");
        tmpSong = tmpSong.replace("Å", "A");
        tmpSong = tmpSong.replace("å", "a");
        tmpSong = tmpSong.replace("é", "e");
        tmpSong = tmpSong.replace("è", "e");
        tmpSong = tmpSong.replace("ë", "e");
        tmpSong = tmpSong.replace("ê", "e");
        tmpSong = tmpSong.replace("á", "a");
        tmpSong = tmpSong.replace("à", "a");
        tmpSong = tmpSong.replace("ä", "a");
        tmpSong = tmpSong.replace("â", "a");
        tmpSong = tmpSong.replace("ó", "o");
        tmpSong = tmpSong.replace("ò", "o");
        tmpSong = tmpSong.replace("ö", "o");
        tmpSong = tmpSong.replace("ô", "o");
        tmpSong = tmpSong.replace("ú", "u");
        tmpSong = tmpSong.replace("ù", "u");
        tmpSong = tmpSong.replace("ü", "u");
        tmpSong = tmpSong.replace("û", "u");
        tmpSong = tmpSong.replace("í", "i");
        tmpSong = tmpSong.replace("ì", "i");
        tmpSong = tmpSong.replace("ï", "i");
        tmpSong = tmpSong.replace("î", "i");
        tmpSong = tmpSong.replace("ý", "y");
        tmpSong = tmpSong.replace("ÿ", "y");

        tmpSong = tmpSong.replace("É", "E");
        tmpSong = tmpSong.replace("È", "E");
        tmpSong = tmpSong.replace("Ë", "E");
        tmpSong = tmpSong.replace("Ê", "E");
        tmpSong = tmpSong.replace("Á", "A");
        tmpSong = tmpSong.replace("À", "A");
        tmpSong = tmpSong.replace("Ä", "A");
        tmpSong = tmpSong.replace("Â", "A");
        tmpSong = tmpSong.replace("Ó", "O");
        tmpSong = tmpSong.replace("Ò", "O");
        tmpSong = tmpSong.replace("Ö", "O");
        tmpSong = tmpSong.replace("Ô", "O");
        tmpSong = tmpSong.replace("Ú", "U");
        tmpSong = tmpSong.replace("Ù", "U");
        tmpSong = tmpSong.replace("Ü", "U");
        tmpSong = tmpSong.replace("Û", "U");
        tmpSong = tmpSong.replace("Í", "I");
        tmpSong = tmpSong.replace("Ì", "I");
        tmpSong = tmpSong.replace("Ï", "I");
        tmpSong = tmpSong.replace("Î", "I");
        tmpSong = tmpSong.replace("Ý", "Y");
        tmpSong = tmpSong.replace(" (DEEPEND REMIX)", "");
        tmpSong = tmpSong.replace("Ø", "O");
        tmpSong = tmpSong.replace("$IGN", "SIGN");

        return tmpSong.trim();
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
        if (array.length < 2){
            throw new RuntimeException("Invalid format for line: + strippedSongLine");
        }
        M3uTO song = new M3uTO();
        song.setLine(strippedSongLine);
        //m3u.setOriginalLine(songLine);
        String track = "";
        switch (array.length) {
            case 2:
                //m3u.setStatus(M3uTO.STATUS_OK);
                track = strippedSongLine.substring(0,2);
                song.setArtist(array[0].trim());
                song.setSong(array[1].trim());
                song.setTrack(track);
                break;
            default:
                //song.setStatus(M3uTO.STATUS_OK);
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
        return song;
    }

    private List<M3uTO> processYearList(UltratopConfig.Year year) throws IOException {

        String inputFile = getFullPathYearListFile(year.listFile);
        System.out.println("Start Year List: " + inputFile);
        List <String> lines = new ArrayList<String>();
        File myFile = new File(inputFile);
        if (myFile.isFile()) {
            lines = FileUtils.getContents(new File(inputFile), "UTF-8");
        }
        List <M3uTO> songs = new ArrayList();
        //System.out.println(line);
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
        System.out.println("Start: Ultratop List + " + inputFile);
        List <String> lines = FileUtils.getContents(new File(inputFile), "UTF-8");
        List <M3uTO> songs = new ArrayList();
        for(String line: lines) {
            if (StringUtils.isBlank(line)) {
            } else {
                songs.add(getSongInfo(line));
            }
        }
        System.out.println("Finished: Ultratop List");
        return songs;
    }

    private List <M3uTO> getFileList(String baseDir){
        System.out.println("Start File List: " + baseDir);
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
        System.out.println("End File List: " + baseDir);
        return baseDirList;
    }

    private void matchUltratopWithYearList(List <M3uTO> ultratopList, List <M3uTO> ListOfMP3Files){

        System.out.println("Start: Match Ultratop With Year List");

        boolean success = true;
        for (M3uTO song: ultratopList) {
            M3uTO foundSong = findSong(song, ListOfMP3Files);
            System.out.println("Lookup: " + song.getLine());
            if (foundSong != null){
                song.setM3uSong(foundSong.getM3uSong());
                //System.out.println("FOUND: " + foundSong.getM3uSong());
            }
            else {
                System.out.println("NOT FOUND: " + song.getLine());
            }
        }
        System.out.println("End: Match Ultratop With Year List");

    }

    private M3uTO findSong(M3uTO song, List <M3uTO> list ){

        String uniqueSong = getUniqueSong(song.getSong());
        String uniqueArtist = getUniqueSong(song.getArtist());
        for (M3uTO m3uTO: list) {
            //System.out.println("Comparing 1: " + getUniqueSong(song.getSong()));
            //System.out.println("Comparing 2: " + getUniqueSong(m3uTO.getSong()));
            if (uniqueSong.equals(getUniqueSong(m3uTO.getSong()))) {
                //System.out.println("Comparing 1: " + getUniqueSong(song.getArtist()));
                //System.out.println("Comparing 2: " + getUniqueSong(m3uTO.getArtist()));
                if (uniqueArtist.equals(getUniqueSong(m3uTO.getArtist()))) {
                    return m3uTO;
                }
            }
        }
        return null;

    }



}

